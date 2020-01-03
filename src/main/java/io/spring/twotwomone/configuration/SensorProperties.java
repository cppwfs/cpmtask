/*
 * Copyright 2017 the original author or authors.
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

package io.spring.twotwomone.configuration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties
public class SensorProperties {

	/**
	 * The location to send the CPM data.
	 */
	private String url = "http://localhost:9000";

	/**
	 * The name of the file that will be used to stor data that was not transmitted.
	 */
	private String backUpFileName = "sensorData.txt";

	/**
	 * The unique id for the sensor.
 	 */
	private String unitId = "A1";

	/**
	 * The commport that the cpm data will be read from.
	 */
	private int comPort = 2;

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getBackUpFileName() {
		return backUpFileName;
	}

	public void setBackUpFileName(String backUpFileName) {
		this.backUpFileName = backUpFileName;
	}

	public String getUnitId() {
		return unitId;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	public int getComPort() {
		return comPort;
	}

	public void setComPort(int comPort) {
		this.comPort = comPort;
	}
}
