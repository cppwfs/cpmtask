package io.spring.transformcpm;

import java.text.SimpleDateFormat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Processor;
import org.springframework.messaging.handler.annotation.SendTo;

@SpringBootApplication
@EnableBinding(Processor.class)
public class TransformCpmApplication {

	public static void main(String[] args) {
		SpringApplication.run(TransformCpmApplication.class, args);
	}

	@StreamListener(Processor.INPUT)
	@SendTo(Processor.OUTPUT)
	public SensorData convertCPM(String cpm) {
		int countsPerMinute = Integer.valueOf(cpm);
		Double microSieverts = countsPerMinute /  123.14;;
		SensorData sensorData = new SensorData(countsPerMinute, microSieverts, "Cloud App");
		return sensorData;
	}
}
