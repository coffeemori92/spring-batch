package com.example.demo.config.jdbc;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import com.example.demo.config.BaseConfig;
import com.example.demo.domain.model.Employee;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class JdbcCursorBatchConfig extends BaseConfig {

	/** DataSource(JDBCで必要) */
	private final DataSource dataSource;
	
	/** SELECT用のSQL */
	private static final String SELECT_EMPLOYEE_SQL = "SELECT * FROM employee WHERE gender = ?";
	
	/** JdbcCurosrItemReader */
	@Bean
	@StepScope
	public JdbcCursorItemReader<Employee> jdbcCursorReader() {
		// クエリーに渡すパラメーター
		Object[] params = new Object[] {1};
		
		// RowMapper
		BeanPropertyRowMapper<Employee> rowMapper = new BeanPropertyRowMapper<>(Employee.class);
		
		return new JdbcCursorItemReaderBuilder<Employee>() // Builderの生成
				.dataSource(dataSource) // DataSourceのセット
				.name("jdbcCursorItemReader") // 名前のセット
				.sql(SELECT_EMPLOYEE_SQL) // SQLのセット
				.queryArguments(params) // パラメーター
				.rowMapper(rowMapper) // rowMapperのセット
				.build(); // readerの生成
	}
	
	/** Stepの生成 */
	@Bean
	public Step exportJdbcCursorStep() {
		return stepBuilderFactory.get("ExportJdbcCursorStep")
				.<Employee, Employee>chunk(10)
				.reader(jdbcCursorReader())
				.listener(readListener)
				.processor(genderConvertProcessor)
				.writer(csvWriter())
				.listener(writeListener)
				.build();
	}
	
	/** Jobの生成 */
	@Bean("JdbcCursorJob")
	public Job exportJdbcCursorJob() {
		return jobBuilderFactory.get("ExportJdbcCursorJob")
				.incrementer(new RunIdIncrementer())
				.start(exportJdbcCursorStep())
				.build();
	}
}
