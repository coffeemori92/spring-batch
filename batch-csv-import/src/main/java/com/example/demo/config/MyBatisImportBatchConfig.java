package com.example.demo.config;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.mybatis.spring.batch.builder.MyBatisBatchItemWriterBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.domain.model.Employee;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class MyBatisImportBatchConfig extends BaseConfig {

	/** SqlSessionFactory(MyBatisで必要) */
	private final SqlSessionFactory sqlSessionFactory;
	
	/** Writer(MyBatis) */
	@Bean
	public MyBatisBatchItemWriter<Employee> mybatisWriter() {
		return new MyBatisBatchItemWriterBuilder<Employee>()
				.sqlSessionFactory(sqlSessionFactory)
				.statementId("com.example.demo.repository.EmployeeMapper.insertOne")
				.build();
	}
	
	/** Stepの生成(MyBatis) */
	@Bean
	public Step csvImportMybatisStep() {
		return stepBuilderFactory.get("CsvImportMybatisStep")
				.<Employee, Employee>chunk(10)
				.reader(csvReader())
				.listener(readListener)
				.processor(compositeProcessor())
				.listener(processListener)
				.writer(mybatisWriter())
				.listener(writeListener)
				.build();
	}
	
	/** Jobの生成(MyBatis) */
	@Bean("MybatisJob")
	public Job csvImportMybatisJob() {
		return jobBuilderFactory.get("CsvImportMybatisJob")
				.incrementer(new RunIdIncrementer())
				.start(csvImportMybatisStep())
				.build();
	}
}
