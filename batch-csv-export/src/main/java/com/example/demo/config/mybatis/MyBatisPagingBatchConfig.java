package com.example.demo.config.mybatis;

import java.util.HashMap;
import java.util.Map;

import org.apache.ibatis.session.SqlSessionFactory;
import org.mybatis.spring.batch.MyBatisPagingItemReader;
import org.mybatis.spring.batch.builder.MyBatisPagingItemReaderBuilder;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.config.BaseConfig;
import com.example.demo.domain.model.Employee;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class MyBatisPagingBatchConfig extends BaseConfig {
	
	/** SqlSessionFactory(MyBatisで必要) */
	private final SqlSessionFactory sqlSessionFactory;
	
	/** MyBatisPagingItemReader */
	@Bean
	@StepScope
	public MyBatisPagingItemReader<Employee> myBatisPagingReader() {
		
		// クエリーに渡すパラメーター
		Map<String, Object> parameterValues = new HashMap<>();
		parameterValues.put("genderParam", 1);
		
		return new MyBatisPagingItemReaderBuilder<Employee>()
				.sqlSessionFactory(sqlSessionFactory)
				.queryId("com.example.demo.repository.EmployeeMapper.findByGenderPaging")
				.parameterValues(parameterValues) // パラメーター
				.pageSize(10) // ページサイズ
				.build(); // readerの生成
	}
	
	/** MyBatisPagingItemReaderを使用するStepの生成 */
	@Bean
	public Step exportMyBatisPagingStep() {
		return stepBuilderFactory.get("ExportMyBatisPagingStep")
				.<Employee, Employee>chunk(10)
				.reader(myBatisPagingReader())
				.listener(readListener)
				.processor(genderConvertProcessor)
				.writer(csvWriter())
				.listener(writeListener)
				.build();
	}
	
	/** MyBatisPagingItemReaderを使用するJobの生成 */
	@Bean("MyBatisPagingJob")
	public Job exportMyBatisPagingJob() {
		return jobBuilderFactory.get("ExportMyBatisPagingJob")
				.incrementer(new RunIdIncrementer())
				.start(exportMyBatisPagingStep())
				.build();
	}
}
