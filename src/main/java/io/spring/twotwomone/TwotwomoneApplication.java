package io.spring.twotwomone;

import java.io.InputStream;

import com.fazecast.jSerialComm.SerialPort;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.cloud.task.listener.annotation.AfterTask;
import org.springframework.cloud.task.listener.annotation.BeforeTask;
import org.springframework.cloud.task.listener.annotation.FailedTask;
import org.springframework.cloud.task.repository.TaskExecution;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
@EnableTask
public class TwotwomoneApplication {

	public static void main(String[] args) {
		SpringApplication.run(TwotwomoneApplication.class, args);
	}

	@Bean
	public CommandLineRunner commandLineRunner() {
		return new CommandLineRunner() {
			@Override
			public void run(String... args) throws Exception {
//				System.out.println("hello world");
//				throw new IllegalStateException("hi");
				SerialPort comPort = SerialPort.getCommPorts()[2];
				SerialPort ports[] = SerialPort.getCommPorts();
				comPort.openPort();
				comPort.setComPortTimeouts(SerialPort.TIMEOUT_READ_SEMI_BLOCKING, 0, 0);
				InputStream in = comPort.getInputStream();
				try
				{
					System.out.print("CPM:");
					for (int j = 0; j < 100000; ++j) {
						char c = (char) in.read();
						System.out.print(c);
						if(c == '\n')
							System.out.print("CPM:");
					}
					in.close();
				} catch (Exception e) { e.printStackTrace(); }
				comPort.closePort();
			}
		};
	}


	@BeforeTask
	public void beforeTask(TaskExecution taskExecution) {
		System.out.println("Before Task");
	}
	@AfterTask
	public void afterTask(TaskExecution taskExecution) {
		System.out.println("After Task");
	}
	@FailedTask
	public void failedTask(TaskExecution taskExecution, Throwable throwme) {
		System.out.println("Failed Task");
	}
}
