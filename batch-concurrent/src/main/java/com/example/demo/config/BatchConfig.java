package com.example.demo.config;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.core.job.flow.support.SimpleFlow;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;

@Configuration
@EnableBatchProcessing
public class BatchConfig {
	
	@Autowired
	private StepBuilderFactory stepBuilderFactory;
	
	@Autowired
	private JobBuilderFactory jobBuilderFactory;
	
	@Autowired
	@Qualifier("FirstTasklet")
	private Tasklet firstTasklet;
	
	@Autowired
	@Qualifier("SecondTasklet")
	private Tasklet secondTasklet;
	
	@Autowired
	@Qualifier("ThirdTasklet")
	private Tasklet thirdTasklet;
	
	@Bean
	public Step firstStep() {
		return stepBuilderFactory.get("FirstStep")
				.tasklet(firstTasklet)
				.build();
	}
	
	@Bean
	public Step secondStep() {
		return stepBuilderFactory.get("SecondStep")
				.tasklet(secondTasklet)
				.build();
	}
	
	@Bean
	public Step thirdStep() {
		return stepBuilderFactory.get("ThirdStep")
				.tasklet(thirdTasklet)
				.build();
	}
	
	/** FirstStepのFlowを生成 */
	@Bean
	public Flow firstFlow() {
		return new FlowBuilder<SimpleFlow>("FirstFlow")
				.start(firstStep())
				.build();
	}
	
	/** SecondStepのFlowを生成 */
	@Bean
	public Flow secondFlow() {
		return new FlowBuilder<SimpleFlow>("SecondFlow")
				.start(secondStep())
				.build();
	}
	
	/** ThirdStepのFlowを生成 */
	@Bean
	public Flow thirdFlow() {
		return new FlowBuilder<SimpleFlow>("ThirdFlow")
				.start(thirdStep())
				.build();
	}
	
	/** 非同期実行のTaskExecutor */
	@Bean
	public TaskExecutor asyncTaskExecutor() {
		return new SimpleAsyncTaskExecutor("concurrent_");
	}
	
	/** Flow分割 */
	@Bean
	public Flow splitFlow() {
		return new FlowBuilder<SimpleFlow>("splitFlow")
				.split(asyncTaskExecutor())
				.add(secondFlow(), thirdFlow())
				.build();
	}
	
	/** Jobを生成 */
	@Bean
	public Job concurrentJob() {
		return jobBuilderFactory.get("ConcurrentJob")
				.incrementer(new RunIdIncrementer())
				.start(firstFlow())
				.next(splitFlow())
				.build()
				.build();
	}
}
