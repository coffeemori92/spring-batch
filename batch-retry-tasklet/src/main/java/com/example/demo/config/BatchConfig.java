package com.example.demo.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	private Tasklet retryTasklet;
	
	@Bean
	public Step retryTaskletStep() {
		return stepBuilderFactory.get("RetryTaskletStep")
				.tasklet(retryTasklet)
				.build();
	}
	
	@Bean
	public Job retryTaskletJob() {
		return jobBuilderFactory.get("RetryTaskletJob")
				.incrementer(new RunIdIncrementer())
				.start(retryTaskletStep())
				.build();
	}

}
