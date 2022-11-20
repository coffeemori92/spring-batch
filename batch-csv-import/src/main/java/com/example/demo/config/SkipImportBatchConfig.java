package com.example.demo.config;

import org.mybatis.spring.batch.MyBatisBatchItemWriter;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.SkipListener;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.example.demo.domain.model.Employee;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SkipImportBatchConfig extends BaseConfig {
	
	/** Listener */
	private final SkipListener<Employee, Employee> employeeSkipListener;
	
	private final MyBatisBatchItemWriter<Employee> myBatisWriter;
	
	/** Stepの生成(Skip) */
	@Bean
	public Step csvImportSkipStep() {
		return stepBuilderFactory.get("CsvImportSkipStep")
				.<Employee, Employee>chunk(10)
				.reader(csvReader())
				.listener(readListener)
				.processor(genderConvertProcessor)
				.listener(processListener)
				.writer(myBatisWriter)
				.faultTolerant() // FaultTolerant
				.skipLimit(Integer.MAX_VALUE) // 最大件数
				.skip(RuntimeException.class) // 例外クラス
				.listener(employeeSkipListener) // listener
				.build();
	}
	
	/** Jobの生成(Skip) */
	@Bean("SkipJob")
	public Job csvImportSkipJob() {
		return jobBuilderFactory.get("CsvImportSkipJob")
				.incrementer(new RunIdIncrementer())
				.start(csvImportSkipStep())
				.build();
	}
}
