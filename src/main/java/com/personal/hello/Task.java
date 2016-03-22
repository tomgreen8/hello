/**
 * 
 */
package com.personal.hello;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * @author liuquan
 *
 */

@Component
public class Task {
	
	private final Logger log = LoggerFactory.getLogger(getClass());
	
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Scheduled(cron= "* 2 1 * * ?")
	public void execute(){
		int count = jdbcTemplate.queryForObject("select count(1) from msgids", Integer.class);
		log.info("hello!count:"+count);
	}

}
