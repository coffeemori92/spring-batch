package com.example.demo.integration;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.List;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobExecution;
import org.springframework.batch.test.JobLauncherTestUtils;
import org.springframework.batch.test.context.SpringBatchTest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.test.context.ContextConfiguration;

import com.example.demo.BatchCsvImportApplication;
import com.example.demo.domain.model.Employee;

@SpringBatchTest
@DisplayName("CsvImportJobのIntegrationTest")
@ContextConfiguration(classes = {BatchCsvImportApplication.class})
public class CsvImportJobIntegrationTest {
	
	@Autowired
	private JobLauncherTestUtils jobLauncherTestUtils;
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	private static String SQL = "SELECT * FROM employee ORDER BY id";
	
	private RowMapper<Employee> rowMapper = new BeanPropertyRowMapper<>(Employee.class);
	
	@Test
	@DisplayName("ユーザーがインポートされていること")
	public void jobTest() throws Exception {
		JobExecution jobExecution = jobLauncherTestUtils.launchJob();
		jobExecution.getStepExecutions()
			.forEach(stepExecution -> assertThat(ExitStatus.COMPLETED).isEqualTo(stepExecution.getExitStatus()));
		assertThat(ExitStatus.COMPLETED).isEqualTo(jobExecution.getExitStatus());
		
		List<Employee> result = jdbcTemplate.query(SQL, rowMapper);
		assertThat(result.size()).isEqualTo(2);
		Employee employee1 = result.get(0);
		assertThat(employee1.getName()).isEqualTo("テストユーザー1");
		Employee employee2 = result.get(1);
		assertThat(employee2.getName()).isEqualTo("テストユーザー2");
	}
}
