package com.example.demo.repository;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import lombok.RequiredArgsConstructor;

@Repository
@RequiredArgsConstructor
public class EmployeeJdbcRepository {
	
	private final JdbcTemplate jdbc;
	
	private static final String EXISTS_SQL = "SELECT EXISTS (SELECT * FROM employee WHERE id = ?)";
	
	/** SQL実行 */
	public boolean exists(Integer id) {
		boolean result = jdbc.queryForObject(EXISTS_SQL, Boolean.class, id);
		return result;
	}
}
