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

package io.spring.bananaalert;

import java.util.Date;

public class SensorData {
	private String countsPerMinute;
	private String microSieverts;
	private String bananas;
	private String unitId;
	private String timestamp;

	public SensorData() {
	}

	public SensorData(String countsPerMinute, String microSieverts, String unitId) {
		this.countsPerMinute = countsPerMinute;
		this.microSieverts = microSieverts;
		this.unitId = unitId;
		Double bananas =  Double.valueOf(microSieverts) /.1;
		this.bananas = String.valueOf(bananas);
		this.timestamp = String.valueOf(new Date());
	}

	public String getCountsPerMinute() {
		return countsPerMinute;
	}

	public void setCountsPerMinute(String countsPerMinute) {
		this.countsPerMinute = countsPerMinute;
	}

	public String getMicroSieverts() {
		return microSieverts;
	}

	public void setMicroSieverts(String microSieverts) {
		this.microSieverts = microSieverts;
	}

	public String getBananas() {
		return bananas;
	}

	public void setBananas(String bananas) {
		this.bananas = bananas;
	}

	public String getUnitId() {
		return unitId;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	public String getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(String timestamp) {
		this.timestamp = timestamp;
	}

	@Override
	public String toString() {
		return "SensorData{" +
				"countsPerMinute=" + countsPerMinute +
				", microSieverts=" + microSieverts +
				", bananas=" + bananas +
				", unitId='" + unitId + '\'' +
				", timestamp=" + timestamp +
				'}';
	}
}
