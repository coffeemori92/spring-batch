package com.example.demo.config.jpa;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.database.JpaPagingItemReader;
import org.springframework.batch.item.database.builder.JpaPagingItemReaderBuilder;
import org.springframework.batch.item.database.orm.JpaNativeQueryProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.config.BaseConfig;
import com.example.demo.domain.model.Employee;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class JpaPagingBatchConfig extends BaseConfig {
	
	/** EntityManagerFactory(JPAで必要) */
	private final EntityManagerFactory emf;
	
	/** JpaPagingItemReader */
	@Bean
	@StepScope
	public JpaPagingItemReader<Employee> jpaPagingReader() {
		// SQL
		String sql = "SELECT * FROM employee WHERE gender = :genderParam ORDER BY id";
		
		// クエリーの設定
		JpaNativeQueryProvider<Employee> queryProvider = new JpaNativeQueryProvider<>();
		queryProvider.setSqlQuery(sql);
		queryProvider.setEntityClass(Employee.class);
		
		// クエリーに渡すパラメーター
		Map<String, Object> parameterValues = new HashMap<>();
		parameterValues.put("genderParam", 1);
		
		return new JpaPagingItemReaderBuilder<Employee>()
				.entityManagerFactory(emf)
				.name("jpaPagingItemReader")
				.queryProvider(queryProvider)
				.parameterValues(parameterValues)
				.pageSize(5)
				.build();
	}
	
	/** JpaPagingItemReaderを使用するStepの生成 */
	@Bean
	public Step exportJpaPagingStep() {
		return stepBuilderFactory.get("ExportJpaPagingStep")
				.<Employee, Employee>chunk(10)
				.reader(jpaPagingReader())
				.listener(readListener)
				.processor(genderConvertProcessor)
				.writer(csvWriter())
				.listener(writeListener)
				.build();
	}

}
