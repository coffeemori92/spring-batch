package com.example.demo.config;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JpaItemWriter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.domain.model.Employee;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class JpaImportBatchConfig extends BaseConfig {

	/** EntityManagerFactory(JPAで必須) */
	private final EntityManagerFactory emf;
	
	/** Writer(JPA) */
	@Bean
	public JpaItemWriter<Employee> jpaWriter() {
		JpaItemWriter<Employee> writer = new JpaItemWriter<>();
		writer.setEntityManagerFactory(emf);
		return writer;
	}
	
	/** Stepの生成(JPA) */
	@Bean
	public Step csvImportJpaStep() {
		return stepBuilderFactory.get("CsvImportJpaStep")
				.<Employee, Employee>chunk(10)
				.reader(csvReader())
				.listener(readListener)
				.processor(compositeProcessor())
				.listener(processListener)
				.writer(jpaWriter())
				.listener(writeListener)
				.build();
	}
	
	/** Jobの生成(JPA) */
	@Bean("JpaJob")
	public Job csvImportJpaJob() {
		return jobBuilderFactory.get("CsvImportJpaJob")
				.incrementer(new RunIdIncrementer())
				.start(csvImportJpaStep())
				.build();
	}
}
