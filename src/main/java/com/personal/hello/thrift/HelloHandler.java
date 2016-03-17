package com.personal.hello.thrift;

import java.util.HashMap;
import java.util.Map;

import org.apache.thrift.TException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;
import org.springframework.jdbc.core.JdbcTemplate;

import com.personal.hello.thrift.Hello.Iface;

/**
 * 实际的业务层
 * @author liuquan
 *
 */
public class HelloHandler implements Hello.Iface {
	
	private ClassPathXmlApplicationContext context = null;
	
	private JdbcTemplate jdbcTemplate = null;
	
	private static final Logger log = LoggerFactory.getLogger(HelloHandler.class);
	
	public HelloHandler(){
		context = new ClassPathXmlApplicationContext(new String[]{"classpath:applicationContext.xml"});
		context.start();
		jdbcTemplate = context.getBean(JdbcTemplate.class);
	}
	
	@Override
	public String helloString(String para) throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int helloInt(int para) throws TException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean helloBoolean(boolean para) throws TException {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void helloVoid() throws TException {
		// TODO Auto-generated method stub

	}

	@Override
	public String helloNull() throws TException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Map<Integer, String> helloMap(String u) throws TException {
		log.info("开始处理helloMap..");
		Map<Integer, String> map = new HashMap<Integer, String>();
		map.put(1, u);
		int count = jdbcTemplate.queryForObject("select  count(1) from `manager-user`", Integer.class);
		log.info("处理完毕！"+count);
		return map;
	}

}
