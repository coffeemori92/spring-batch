package com.example.demo.config;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.EnableBatchProcessing;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.flow.FlowExecutionStatus;
import org.springframework.batch.core.job.flow.JobExecutionDecider;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.listener.TaskletStepListener;

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
	@Qualifier("SuccessTasklet")
	private Tasklet successTasklet;
	
	@Autowired
	@Qualifier("FailTasklet")
	private Tasklet failTasklet;
	
	@Autowired
	@Qualifier("TaskletStepListener")
	private TaskletStepListener taskletStepListener;
	
	@Autowired
	@Qualifier("RandomTasklet")
	private Tasklet randomTasklet;
	
	@Autowired
	private JobExecutionDecider sampleDecider;
	
	/** FirstStepの生成 */
	@Bean
	public Step firstStep() {
		return stepBuilderFactory.get("FirstStep")
				.tasklet(firstTasklet)
				.listener(taskletStepListener)
				.build();
	}
	
	/** SuccessStepの生成 */
	@Bean
	public Step successStep() {
		return stepBuilderFactory.get("SuccessStep")
				.tasklet(successTasklet)
				.build();
	}
	
	/** FailStepを生成 */
	@Bean
	public Step failStep() {
		return stepBuilderFactory.get("FailStep")
				.tasklet(failTasklet)
				.build();
	}
	
	/** RandomStepを生成 */
	@Bean
	public Step randomStep() {
		return stepBuilderFactory.get("RandomStep")
				.tasklet(randomTasklet)
				.listener(taskletStepListener)
				.build();
	}
	
	/** Taskletの分岐Jobを生成 */
	@Bean
	public Job taskletBranchJob() {
		return jobBuilderFactory.get("TaskletBranchJob")
				.incrementer(new RunIdIncrementer())
				.start(firstStep())
				.on(ExitStatus.COMPLETED.getExitCode())
				.to(successStep())
				.from(firstStep())
				.on(ExitStatus.FAILED.getExitCode())
				.to(failStep())
				.end()
				.build();
	}
	
	/** RandomTaskletの分岐Jobを生成 */
	@Bean
	public Job randomTaskletBranchJob() {
		return jobBuilderFactory.get("RandomTaskletBranchJob")
				.incrementer(new RunIdIncrementer())
				.start(randomStep())
				.next(sampleDecider)
				.from(sampleDecider)
				.on(FlowExecutionStatus.COMPLETED.getName())
				.to(successStep())
				.from(sampleDecider)
				.on(FlowExecutionStatus.FAILED.getName())
				.to(failStep())
				.end()
				.build();
	}
	
}
