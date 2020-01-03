/*
 * Copyright 2019 the original author or authors.
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *          http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package io.spring.twotwomone;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.nio.channels.WritableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

import com.fazecast.jSerialComm.SerialPort;
import io.spring.twotwomone.configuration.SensorProperties;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

public class SensorFlows {

	private WebClient webClient;
	private File backupFile;
	private OutputStream outputStream;
	private String unitId;
	private SensorProperties sensorProperties;

	public SensorFlows(SensorProperties sensorProperties) {
		this.webClient = WebClient
				.builder()
				.baseUrl(sensorProperties.getUrl())
				.defaultHeader(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
				.build();
		this.backupFile = new File(sensorProperties.getBackUpFileName());
		try {
			backupFile.createNewFile();
			this.outputStream = new FileOutputStream(backupFile, true);
		}
		catch (IOException e) {
			e.printStackTrace();
		}
		unitId = sensorProperties.getUnitId();
		this.sensorProperties = sensorProperties;
	}

	public void generateFakeData() throws Exception {
		Random r = new Random();
		for (int x = 0; x < 100000; x++) {
			int cpm =  r.nextInt((30 - 15) + 1) + 15;
			postData(Integer.valueOf(cpm).toString());
			Thread.sleep(1000);
		}
	}

	public void processCPM() {

		SerialPort comPort = SerialPort.getCommPorts()[this.sensorProperties.getComPort()];
		comPort.openPort();
		comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
		InputStream in = comPort.getInputStream();
		try
		{
			System.out.print("CPM:");
			String data = "";
			for (int j = 0; j < 100000; ++j) {
				char c = (char) in.read();
				if (c != '\r' && c!= '\n') {
					data += c;
				}
//				System.out.print(c);
				if(c == '\n') {
					postData(data);
					data = "";
				}
			}
			in.close();
		} catch (Exception e) { e.printStackTrace(); }
		comPort.closePort();
	}


	public void postData(String cpm) throws IOException{
		try {
			this.webClient.post()
					.body(BodyInserters.fromObject(getStringFormat(cpm)))
					.exchange().block()
					.bodyToMono(String.class)
					.block();
			System.out.println(">>>>>" + cpm);
		}
		catch (Exception e) {
			this.outputStream.write(("=====>" + getStringFormat(cpm) + "\n").getBytes() );
			System.out.println("====>" + getStringFormat(cpm));
		}
	}

	private String getStringFormat(String cpm) {
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss");
		Date date = new Date();
		return String.format("{\"unitid\":\"%s\",\"timestamp\":\"%s\",\"cpm\":\"%s\"}", unitId, sdf.format(date), cpm);
	}
}
