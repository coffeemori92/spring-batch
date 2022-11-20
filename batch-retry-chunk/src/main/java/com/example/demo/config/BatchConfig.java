package com.example.demo.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryListener;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {
	
	private final StepBuilderFactory stepBuilderFactory;
	private final JobBuilderFactory jobBuilderFactory;
	private final ItemReader<String> reader;
	private final ItemProcessor<String, String> processor;
	private final ItemWriter<String> writer;
	private final RetryListener retryListener;
	
	@Value("${retry.num}")
	private Integer retryNum;
	
	@Bean
	public Step retryChunkstep() {
		return stepBuilderFactory.get("RetryChunkStep")
				.<String, String>chunk(10)
				.reader(reader)
				.processor(processor)
				.writer(writer)
				.faultTolerant()
				.retryLimit(retryNum)
				.retry(Exception.class)
				.listener(retryListener)
				.build();
	}
	
	@Bean
	public Job retryChunkJob() {
		return jobBuilderFactory.get("RetryChunkJob")
				.incrementer(new RunIdIncrementer())
				.start(retryChunkstep())
				.build();
	}

}
