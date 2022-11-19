package com.example.demo.config.mybatis;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisCursorItemReader;
import org.mybatis.spring.batch.builder.MyBatisCursorItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.config.BaseConfig;
import com.example.demo.domain.model.Employee;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class MyBatisCursorBatchConfig extends BaseConfig {

	/** SqlSessionFactory(Mybatisで必要) */
	private final SqlSessionFactory sqlSessionFactory;
	
	/** MyBatisCursorItemReader */
	@Bean
	@StepScope
	public MyBatisCursorItemReader<Employee> myBatisCursorReader() {
		// クエリーに渡すパラメーター
		Map<String, Object> parameterValues = new HashMap<>();
		parameterValues.put("genderParam", 1);
		
		return new MyBatisCursorItemReaderBuilder<Employee>() // Builder生成
				.sqlSessionFactory(sqlSessionFactory)
				.queryId("com.example.demo.repository.EmployeeMapper.findByGender")
				.parameterValues(parameterValues) // パラメーター
				.build(); // readerの生成
	}
	
	/** MybatisCursorItemReaderを使用するStepの生成 */
	@Bean
	public Step exportMyBatisCursorStep() {
		return stepBuilderFactory.get("ExportMyBatisCursorStep")
				.<Employee, Employee>chunk(10)
				.reader(myBatisCursorReader())
				.listener(readListener)
				.processor(genderConvertProcessor)
				.writer(csvWriter())
				.listener(writeListener)
				.build();
	}
	
	/** MyBatisCursorItemReaderを使用するJobの生成 */
	@Bean("MyBatisCursorJob")
	public Job exportMyBatisCursorJob() {
		return jobBuilderFactory.get("ExportMyBatisCursorJob")
				.incrementer(new RunIdIncrementer())
				.start(exportMyBatisCursorStep())
				.build();
	}
}
