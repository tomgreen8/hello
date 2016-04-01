package com.personal.hello.thrift;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import org.apache.thrift.TException;
import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;

import com.personal.hello.cluster.Registry;

/**
 * 支持服务端集群
 * 注册服务端 一个请求一个线程 线程池服务模型，使用标准的阻塞式IO，预先创建一组线程处理请求
 */
public class BlockThreadPoolServer {

	public static final String servers = "/BlockThreadPoolServer";

	public static void main(String[] args) throws TException, FileNotFoundException, IOException {
		int port = 8080;
		// /读取配置
		Properties p = new Properties();
		String config = HelloServer.class.getResource("/").getPath() + "system.properties";
		p.load(new FileInputStream(config));
		String zkServer = p.getProperty("zookeeper");
		String port1 = p.getProperty("thrift.server.port");
		String ip = p.getProperty("thrift.server.ip");// 当前thirftserver的ip
		if (port1 != null) {
			port = Integer.parseInt(port1);
		}
		if(args.length==1){
			port = Integer.parseInt(args[0]);
		}
		String name = ip + ":" + port;
		TMultiplexedProcessor processor = new TMultiplexedProcessor();
		processor.registerProcessor("hello", new Hello.Processor<Hello.Iface>(new HelloHandler()));
		processor.registerProcessor("calculator", new Calculator.Processor<Calculator.Iface>(new CalculatorHandler()));
		// 阻塞IO
		TServerSocket serverTransport = new TServerSocket(port);
		// 多线程服务模型
		TThreadPoolServer.Args tArgs = new TThreadPoolServer.Args(serverTransport);
		tArgs.processor(processor);
		// 客户端协议要一致
		tArgs.protocolFactory(new TBinaryProtocol.Factory());
		// 线程池服务模型，使用标准的阻塞式IO，预先创建一组线程处理请求。
		TServer server = new TThreadPoolServer(tArgs);
		System.out.println("Hello、calculator TThreadPoolServer start....");
		// 注册服务，实现集群
		Registry.add(zkServer, servers, name);
		server.serve(); // 启动服务

	}
}
