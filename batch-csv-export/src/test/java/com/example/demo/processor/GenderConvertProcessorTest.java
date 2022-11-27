package com.example.demo.processor;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import com.example.demo.domain.model.Employee;

@DisplayName("Task1のUnitTest")
@ExtendWith(MockitoExtension.class)
public class GenderConvertProcessorTest {
	
	@InjectMocks
	private GenderConvertProcessor processor;
	
	@Test
	@DisplayName("1が男性という文字列に変換されていること")
	public void convertMale() throws Exception {
		Employee employee = new Employee();
		employee.setGender(1);
		Employee result = processor.process(employee);
		assertThat(result.getGenderString()).isEqualTo("男性");
	}
	
	@Test
	@DisplayName("2が女性という文字列に変換されていること")
	public void convertFeMale() throws Exception {
		Employee employee = new Employee();
		employee.setGender(2);
		Employee result = processor.process(employee);
		assertThat(result.getGenderString()).isEqualTo("女性");
	}
	
	@Test
	@DisplayName("変換が失敗して例外が発生すること")
	public void convertFail() throws Exception {
		Employee employee = new Employee();
		Employee result = processor.process(employee);
		assertThat(result).isNull();
	}
}
