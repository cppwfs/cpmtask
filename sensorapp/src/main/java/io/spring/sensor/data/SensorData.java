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

package io.spring.sensor.data;

import java.util.Date;

public class SensorData {
	private Integer countsPerMinute;
	private Double microSieverts;
	private Double bananas;
	private String unitId;
	private Date timestamp;

	public SensorData(Integer countsPerMinute, Double microSieverts, Double bananas, String unitId, Date timestamp) {
		this.countsPerMinute = countsPerMinute;
		this.microSieverts = microSieverts;
		this.bananas = bananas;
		this.unitId = unitId;
		this.timestamp = timestamp;
	}

	public SensorData(Integer countsPerMinute, Double microSieverts, String unitId) {
		this.countsPerMinute = countsPerMinute;
		this.microSieverts = microSieverts;
		this.unitId = unitId;
		this.bananas = microSieverts /.1;
		this.timestamp = new Date();
	}

	public Integer getCountsPerMinute() {
		return countsPerMinute;
	}

	public void setCountsPerMinute(Integer countsPerMinute) {
		this.countsPerMinute = countsPerMinute;
	}

	public Double getMicroSieverts() {
		return microSieverts;
	}

	public void setMicroSieverts(Double microSieverts) {
		this.microSieverts = microSieverts;
	}

	public Double getBananas() {
		return bananas;
	}

	public void setBananas(Double bananas) {
		this.bananas = bananas;
	}

	public String getUnitId() {
		return unitId;
	}

	public void setUnitId(String unitId) {
		this.unitId = unitId;
	}

	public Date getTimestamp() {
		return timestamp;
	}

	public void setTimestamp(Date timestamp) {
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
