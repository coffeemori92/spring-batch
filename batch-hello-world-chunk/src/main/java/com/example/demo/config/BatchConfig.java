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
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@EnableBatchProcessing
@RequiredArgsConstructor
public class BatchConfig {
	
	/** JobBuilderのFactoryクラス */
	private final JobBuilderFactory jobBuilderFactory;
	
	/** StepBuilderのFactoryクラス */
	private final StepBuilderFactory stepBuilderFactory;
	
	/** HelloReader */
	private final ItemReader<String> reader;
	
	/** HelloProcessor */
	private final ItemProcessor<String, String> processor;
	
	/** HelloWriter */
	private final ItemWriter<String> writer;
	
	/** ChunkのStepを生成*/
	@Bean
	public Step chunkStep() {
		return stepBuilderFactory.get("HelloChunkStep") // Builderの取得
				.<String, String>chunk(3) // チャンクの設定
				.reader(reader) // readerセット
				.processor(processor) // processorセット
				.writer(writer) // writerセット
				.build(); // Stepの生成
	}
	
	/** Jobを生成 */
	@Bean
	public Job chunkJob() {
		return jobBuilderFactory.get("HelloWorldChunkJob") // Builderの取得
				.incrementer(new RunIdIncrementer()) // IDのインクリメント
				.start(chunkStep()) // 最初のStep
				.build(); // Jobを生成
	}
}
