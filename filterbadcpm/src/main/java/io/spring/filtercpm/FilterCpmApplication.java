package io.spring.filtercpm;

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
	public SensorData convertCPM(SensorData sensorData) {
		SensorData result = null;
		if(Integer.valueOf(sensorData.getCountsPerMinute() )> 0) {
			result = sensorData;
		}
		return result;
	}
}
