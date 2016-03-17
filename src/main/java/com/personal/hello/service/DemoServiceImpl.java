package com.personal.hello.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import com.alibaba.dubbo.config.annotation.Service;

@Service
public class DemoServiceImpl implements DemoService {
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	 
    public String sayHello(String name) {
    	String sql = "select count(1) from msgids";
    	int count = jdbcTemplate.queryForObject(sql, Integer.class);
        return "Hello " + name+",总共有"+count+"条";
    }
 
}