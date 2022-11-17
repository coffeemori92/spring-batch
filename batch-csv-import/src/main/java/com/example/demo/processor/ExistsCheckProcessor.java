package com.example.demo.processor;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import com.example.demo.domain.model.Employee;
import com.example.demo.repository.EmployeeJdbcRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@StepScope
@RequiredArgsConstructor
@Component("ExistsCheckProcessor")
public class ExistsCheckProcessor implements ItemProcessor<Employee, Employee> {
	
	private final EmployeeJdbcRepository employeeRepository;

	/** 従業員が存在するかチェックする */
	@Override
	public Employee process(Employee item) throws Exception {
		boolean exists = employeeRepository.exists(item.getId());
		if (exists) {
			log.info("Skip because it already exists: {}", item);
			return null;
		}
		return item;
	}
}
