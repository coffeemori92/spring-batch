package com.example.demo.config;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.domain.model.Employee;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class JdbcImportBatchConfig extends BaseConfig {

	/** DataSource(JDBCで必要) */
	private final DataSource dataSource;
	
	/** insert-sql(JDBC用) */
	private static final String INSERT_EMPLOYEE_SQL = ""
			+ "INSERT INTO employee (id, name, age, gender)"
			+ " VALUES (:id, :name, :age, :gender)";
	
	/** Writer(JDBC) */
	@Bean
	@StepScope
	public JdbcBatchItemWriter<Employee> jdbcWriter() {
		// Provider生成
		BeanPropertyItemSqlParameterSourceProvider<Employee> provider = 
				new BeanPropertyItemSqlParameterSourceProvider<>();
		
		// 設定
		return new JdbcBatchItemWriterBuilder<Employee>() // Builderの生成
				.itemSqlParameterSourceProvider(provider) // provider
				.sql(INSERT_EMPLOYEE_SQL) // SQLのセット
				.dataSource(dataSource) // DataSourceのセット
				.build(); // writerの生成
	}
	
	/** Stepの生成(JDBC) */
	@Bean
	public Step csvImportjdbcStep() {
		return stepBuilderFactory.get("CsvImportJdbcStep")
				.<Employee, Employee>chunk(10)
				.reader(csvReader())
				.listener(readListener)
//				.processor(genderConvertProcessor)
				.processor(compositeProcessor())
				.listener(processListener)
				.writer(jdbcWriter())
				.listener(writeListener)
				.build();
	}
	
	/** Jobの生成(JDBC) */
	@Bean("JdbcJob")
	public Job csvImportJdbcJob() {
		return jobBuilderFactory.get("CsvImportJdbcJob")
				.incrementer(new RunIdIncrementer())
				.start(csvImportjdbcStep())
				.build();
	}
}
