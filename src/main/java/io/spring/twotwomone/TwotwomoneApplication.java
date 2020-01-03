package io.spring.twotwomone;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableTask
public class TwotwomoneApplication {

	@Autowired
	private SensorFlows sensorFlows;


	public static void main(String[] args) {
		SpringApplication.run(TwotwomoneApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner() {
		return new CommandLineRunner() {
			@Override
			public void run(String... args) throws Exception {
				//sensorFlows.generateFakeData();
				sensorFlows.processCPM();
			}
		};
	}
}
