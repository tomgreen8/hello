/**
 * 
 */
package com.personal.hello.thrift;

import java.util.ArrayList;
import java.util.List;

import org.apache.thrift.TException;

/**
 * @author liuquan
 *
 */
public class CalculatorHandler implements Calculator.Iface{

	@Override
	public String ping(String para) throws TException {
		return para+"-ping";
	}

	@Override
	public List<String> getMembers(int pid) throws TException {
		List<String> members = new ArrayList<>();
		members.add("hello");
		members.add("zhansan");
		members.add("lisi");
		return members;
	}

	@Override
	public int add(int a, int b) throws TException {
		return a+b;
	}

}
