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

package io.spring.logbananaaverage.configuration;

import java.util.List;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.annotation.BeforeStep;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cloud.task.configuration.EnableTask;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@EnableTask
@EnableBatchProcessing
@Configuration
public class BananaAverageConfiguration {

	@Autowired
	public JobBuilderFactory jobBuilderFactory;

	@Autowired
	public StepBuilderFactory stepBuilderFactory;

	@Autowired
	public DataSource dataSource;

	@Bean
	public JobCompletionNotificationListener jobCompletionNotificationListener() {
		return new JobCompletionNotificationListener();
	};


	@Bean
	public Job importUserJob(JobCompletionNotificationListener listener, Step step1) {
		return jobBuilderFactory.get("bananaAverageJob")
				.incrementer(new RunIdIncrementer())
				.listener(listener)
				.flow(step1)
				.end()
				.build();
	}

	@Bean
	public Step step1(ItemReader<String> reader, ItemWriter<String> writer) {
		return stepBuilderFactory.get("step1")
				.<String, String> chunk(10)
				.reader(reader)
				.writer(writer)
				.build();
	}

	@Bean
	public JdbcCursorItemReader<String> reader() {
		return new JdbcCursorItemReaderBuilder<String>()
				.dataSource(this.dataSource)
				.name("fooReader")
				.sql("SELECT bananas FROM bananas")
				.rowMapper((rs, rowNum) -> {
					return rs.getString("bananas");
				})
				.build();
	}

	@Bean
	public ItemWriter<String> processor() {
		return new ItemWriter<String>() {
			private JobExecution jobExecution;
			private int count;
			private double sum;

			@Override
			public void write(List<? extends String> list) throws Exception {
				for(String bananas : list) {
					count++;
					sum = sum + Double.valueOf(bananas);
					jobExecution.getExecutionContext().put("bananaaverage", sum/count);
//					System.out.println("Processing -> " + bananas + "->" + sum + "->" + count + "->" + sum/count);
				}
			}
			@BeforeStep
			public void beforeStep(StepExecution stepExecution) {
				jobExecution = stepExecution.getJobExecution();
			}
		};
	}
}
