/*
 * Copyright 2020 the original author or authors.
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
import java.text.SimpleDateFormat;
import java.util.Random;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fazecast.jSerialComm.SerialPort;
import io.spring.twotwomone.configuration.SensorProperties;
import io.spring.twotwomone.data.SensorData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

public class SensorFlows {

	Logger logger = LoggerFactory.getLogger(SensorFlows.class);

	private WebClient webClient;
	private File backupFile;
	private OutputStream outputStream;
	private String unitId;
	private SensorProperties sensorProperties;
	private ObjectMapper mapper;

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
		this.mapper = new ObjectMapper();
	}

	public void processSensorFlows() throws Exception{
		if(this.sensorProperties.isGenerateSampleData()) {
			generateFakeData();
		}
		else {
			processCPM();
		}
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
			//System.out.print("CPM:");
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
		String sensorData = null;
		try {
			sensorData = calculateSensorData(cpm);
		}
		catch (Exception exception) {
			exception.printStackTrace();
			return;
		}
		try {
			this.webClient.post()
					.body(BodyInserters.fromObject(sensorData))
					.exchange().block()
					.bodyToMono(String.class)
					.block();
			logger.info(">>>>>" + cpm);
		}
		catch (Exception e) {
			this.outputStream.write(("=====>" + sensorData + "\n").getBytes() );
			logger.info("====>" + sensorData);
		}
	}

	private String calculateSensorData(String cpm) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss");
		int countsPerMinute = Integer.valueOf(cpm);
		Double microSieverts = countsPerMinute / this.sensorProperties.getCpmSievertConverstion();
		SensorData sensorData = new SensorData(countsPerMinute, microSieverts, unitId);
		return this.mapper.writeValueAsString(sensorData);
	}
}