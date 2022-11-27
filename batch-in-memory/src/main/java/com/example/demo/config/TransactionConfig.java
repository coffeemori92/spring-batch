package com.example.demo.config;

import javax.persistence.EntityManagerFactory;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Primary;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.transaction.PlatformTransactionManager;

@Configuration
public class TransactionConfig {

	@Autowired
	private EntityManagerFactory emf;
	
	@Bean
	@Primary
	public PlatformTransactionManager jpaTransactionManager() {
		return new JpaTransactionManager(emf);
	}
}
