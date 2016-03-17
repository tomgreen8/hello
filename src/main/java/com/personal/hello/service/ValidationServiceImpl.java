/**
 * 
 */
package com.personal.hello.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.personal.hello.service.validate.ValidationParameter;

/**
 * @author liuquan
 *
 */
public class ValidationServiceImpl implements ValidationService {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	/* 
	 * 
	 * @see com.personal.hello.service.validate.ValidationService#save(com.personal.hello.service.validate.ValidationParameter)
	 */
	@Override
	public void save(ValidationParameter parameter) {
		String sql = "insert into `test_name`(name,value) values(?,?)";
		jdbcTemplate.update(sql, parameter.getName(),parameter.getEmail());
	}

	/* (non-Javadoc)
	 * @see com.personal.hello.service.validate.ValidationService#delete(int)
	 */
	@Override
	public void delete(int id) {
		String sql = "delete from `test_name` where id = ?";
		jdbcTemplate.update(sql,id);
	}

}
