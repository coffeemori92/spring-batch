package com.example.demo.chunk;

import java.util.List;

import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.ItemWriter;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@StepScope
@Component
public class HelloWriter implements ItemWriter<String> {
	
	@Override
	public void write(List<? extends String> items) throws Exception {
		log.info("writer: {}", items);
		log.info("=================");
	}
}
