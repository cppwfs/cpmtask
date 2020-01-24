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

package io.spring.bananaalert;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;

@Configuration
public class BananaAlertConfiguration {

	@Autowired
	private BananaAlertProperties bananaAlertProperties;

	@StreamListener(Sink.INPUT)
	public void process(SensorData sensorData) {
		RestTemplate restTemplate  = new RestTemplate();
		String alertMessage = String.format("{\"text\":\"Alert Customer `%s` to have house evaluated, because banana count was: %s\"}", sensorData.getUnitId(), sensorData.getBananas());
		if(Double.valueOf(sensorData.getBananas()) > this.bananaAlertProperties.getMaxBananas()) {
			restTemplate.postForEntity(this.bananaAlertProperties.getSlackURL(), alertMessage, null);
		}
	}

}
