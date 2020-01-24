package io.spring.filtercpm;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.handler.annotation.SendTo;

@SpringBootApplication
@EnableBinding(Processor.class)
public class FilterCpmApplication {

	public static void main(String[] args) {
		SpringApplication.run(FilterCpmApplication.class, args);
	}

	@StreamListener(Processor.INPUT)
	@SendTo(Processor.OUTPUT)
	public SensorData convertCPM(String sensorDataString) {
		SensorData result = null;
		ObjectMapper mapper = new ObjectMapper();
		SensorData sensorData = null;
		try {
			sensorData = mapper.readValue(sensorDataString, SensorData.class);
		}
		catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		if(Integer.valueOf(sensorData.getCountsPerMinute() )> 0) {
			result = sensorData;
		}
		return result;
	}
}
