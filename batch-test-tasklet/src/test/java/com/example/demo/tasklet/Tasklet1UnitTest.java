package com.example.demo.tasklet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.scope.context.StepContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.test.MetaDataInstanceFactory;

import com.example.demo.component.SampleComponent;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@DisplayName("Tasklet1のUnitTest")
@ExtendWith(MockitoExtension.class)
public class Tasklet1UnitTest {

	@InjectMocks
	private Tasklet1 tasklet1;
	
	@Mock
	private SampleComponent component;
	
	@BeforeAll
	public static void initAll() {
		log.info("=== Tasklet1 UnitTest Start ===");
	}
	
	@AfterAll
	public static void tearDownAll() {
		log.info("=== Tasklet1 UnitTest End ===");
	}
	
	@Test
	@DisplayName("RepeatStatusがFINISHEDで終了すること")
	public void checkRepeatStatus() throws Exception {
		StepContribution contribution = getStepContribution();
		RepeatStatus repeatStatus = tasklet1.execute(contribution, getChunkContext());
		assertThat(repeatStatus).isEqualTo(RepeatStatus.FINISHED);
	}
	
	public JobExecution getJobExecution() {
		JobParameters params = new JobParametersBuilder().addString("param", "paramTest").toJobParameters();
		JobExecution execution = MetaDataInstanceFactory.createJobExecution("UnitTestJob", 1L, 1L, params);
		execution.getExecutionContext().putString("jobKey", "jobValue");
		return execution;
	}
	
	public StepExecution getStepExecution() {
		StepExecution execution = new StepExecution("stepName", getJobExecution());
		execution.getExecutionContext().putString("stepKey", "stepValue");
		return execution;
	}
	
	public StepContribution getStepContribution() {
		StepExecution execution = getStepExecution();
		StepContribution contribution = execution.createStepContribution();
		return contribution;
	}
	
	public ChunkContext getChunkContext() {
		StepExecution execution = getStepExecution();
		StepContext stepContext = new StepContext(execution);
		ChunkContext chunkContext = new ChunkContext(stepContext);
		return chunkContext;
	}
}
