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

package io.spring.sensor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;
import java.util.UUID;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pi4j.io.gpio.GpioController;
import com.pi4j.io.gpio.GpioFactory;
import com.pi4j.io.gpio.GpioPinDigitalInput;
import com.pi4j.io.gpio.Pin;
import com.pi4j.io.gpio.PinPullResistance;
import com.pi4j.io.gpio.PinState;
import com.pi4j.io.gpio.RaspiPin;
import com.pi4j.io.gpio.event.GpioPinDigitalStateChangeEvent;
import com.pi4j.io.gpio.event.GpioPinListenerDigital;
import io.spring.sensor.configuration.SensorProperties;
import io.spring.sensor.data.SensorData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;


public class SensorFlows {

	Logger logger = LoggerFactory.getLogger(SensorFlows.class);

	private RestTemplate restTemplate;
	private File backupFile;
	private OutputStream outputStream;
	private String unitId;
	private SensorProperties sensorProperties;
	private ObjectMapper mapper;

	public int cpm;

	public SensorFlows(SensorProperties sensorProperties) {


		SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();

		factory.setConnectTimeout(3000);
		factory.setReadTimeout(3000);
		restTemplate = new RestTemplate(factory);
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

	public void processCPM() throws Exception{
		System.out.println("<--Pi4J--> GPIO Listen Example ... started.");

		// create gpio controller
		final GpioController gpio = GpioFactory.getInstance();

		addListener(gpio, RaspiPin.GPIO_02);

		System.out.println(" ... complete the GPIO #02 circuit and see the listener feedback here in the console.");

		Date date = new Date();
		// keep program running until user aborts (CTRL-C)
		while (true) {
			Date currentDate = new Date();
			long diff = currentDate.getTime() - date.getTime();
			long diffSeconds = diff / 1000 % 60;
			if( diffSeconds == 0 ) {
				try {
					postData(String.valueOf(this.cpm));
					this.cpm = 0;
					date = currentDate;
				}
				catch(IOException ioException) {
					ioException.printStackTrace();
				}
			}
			Thread.sleep(1000);
		}

		// stop all GPIO activity/threads by shutting down the GPIO controller
		// (this method will forcefully shutdown all GPIO monitoring threads and scheduled tasks)
		// gpio.shutdown();   <--- implement this method call if you wish to terminate the Pi4J GPIO controller
	}


	private void addListener(GpioController gpio, Pin pin) {
		final GpioPinDigitalInput myButton = gpio.provisionDigitalInputPin(pin, PinPullResistance.OFF);

		// set shutdown state for this input pin
		myButton.setShutdownOptions(true);

		// create and register gpio pin listener
		myButton.addListener(new GpioPinListenerDigital() {
			@Override
			public void handleGpioPinDigitalStateChangeEvent(GpioPinDigitalStateChangeEvent event) {
				// display pin state on console
				if(event.getState().equals(PinState.LOW)) {
					System.out.println(" --> GPIO PIN STATE CHANGE: " + event.getPin() + " = " + event.getState());
					cpm++;
				}
			}

		});
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
			this.restTemplate.postForEntity(sensorProperties.getUrl(),sensorData, SensorData.class);
			logger.info(">>>>>" + cpm);
			resetWriter();
		}
		catch (Exception e) {
			writeToFile(sensorData);
		}
	}

	private String calculateSensorData(String cpm) throws Exception{
		SimpleDateFormat sdf = new SimpleDateFormat("YYYY-MM-dd hh:mm:ss");
		int countsPerMinute = Integer.valueOf(cpm);
		Double microSieverts = countsPerMinute / this.sensorProperties.getCpmSievertConverstion();
		SensorData sensorData = new SensorData(countsPerMinute, microSieverts, unitId);
		return this.mapper.writeValueAsString(sensorData);
	}

	private void resetWriter() {
		if (outputStream != null) {
			try {
				outputStream.close();
				File file = new File(this.backupFile.getName());
				file.renameTo(new File(sensorProperties.getBackUpFileName() + UUID.randomUUID()));
				outputStream = null;
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	private void writeToFile(String sensorData) throws IOException {
		if (outputStream == null) {
			try {
				this.backupFile = new File(sensorProperties.getBackUpFileName() + "-current");
				backupFile.createNewFile();
				this.outputStream = new FileOutputStream(backupFile, true);
			}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		this.outputStream.write(("=====>" + sensorData + "\n").getBytes());
		logger.info("====>" + sensorData);
	}
}
