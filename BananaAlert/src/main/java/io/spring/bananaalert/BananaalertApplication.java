package io.spring.bananaalert;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.stream.annotation.EnableBinding;
import org.springframework.cloud.stream.annotation.StreamListener;
import org.springframework.cloud.stream.messaging.Sink;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableBinding(Sink.class)
public class BananaalertApplication {

	public static void main(String[] args) {
		SpringApplication.run(BananaalertApplication.class, args);
	}

}
