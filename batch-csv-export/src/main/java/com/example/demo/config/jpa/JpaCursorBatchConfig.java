package com.example.demo.config.jpa;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.batch.item.database.orm.JpaNativeQueryProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.config.BaseConfig;
import com.example.demo.domain.model.Employee;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class JpaCursorBatchConfig extends BaseConfig {
	
	/** EntityManagerFactory(JPAで必要) */
	private final EntityManagerFactory emf;
	
	/** JpaCursorItemReader */
	@Bean
	@StepScope
	public JpaCursorItemReader<Employee> jpaCursorReader() {
		// SQL
		String sql = "SELECT * FROM employee WHERE gender = :genderParam";
		
		// クエリーの設定
		JpaNativeQueryProvider<Employee> queryProvider = new JpaNativeQueryProvider<>();
		queryProvider.setSqlQuery(sql);
		queryProvider.setEntityClass(Employee.class);
		
		// クエリーに渡すパラメーター
		Map<String, Object> parameterValues = new HashMap<>();
		parameterValues.put("genderParam", 1);
		return new JpaCursorItemReaderBuilder<Employee>()
				.entityManagerFactory(emf)
				.name("jpaCursorItemReader")
				.queryProvider(queryProvider)
				.parameterValues(parameterValues)
				.build();
	}
	
	/** JpaCursorItemReaderを使用するStepの生成 */
	@Bean
	public Step exportJpaCursorStep() {
		return stepBuilderFactory.get("ExportJpaCursorStep")
				.<Employee, Employee>chunk(10)
				.reader(jpaCursorReader())
				.listener(readListener)
				.processor(genderConvertProcessor)
				.writer(csvWriter())
				.listener(writeListener)
				.build();
	}
	
	/** JpaCursorItemReaderを使用するJobの生成 */
	@Bean
	public Job exportJpaCursorJob() {
		return jobBuilderFactory.get("ExportJpaCursorJob")
				.incrementer(new RunIdIncrementer())
				.start(exportJpaCursorStep())
				.build();
	}
}
