/**
 * 
 */
package com.personal.hello.thrift;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TServerSocket;

import com.personal.hello.cluster.Registry;

/**
 * @author liuquan
 *
 */
public class ThrfitServer {
	
	private static HelloHandler handler;
	public static final String servers = "/ThrfitServer";
	private static int port = 8080;

	public static void main(String[] args) {
		try {
			// /读取配置
			Properties p = new Properties();
			String config = HelloServer.class.getResource("/").getPath() + "system.properties";
			p.load(new FileInputStream(config));
			String zkServer = p.getProperty("zookeeper");
			String port1 = p.getProperty("thrift.server.port");
			String ip = p.getProperty("thrift.server.ip");
			if (port1 != null) {
				port = Integer.parseInt(port1);
			}
			String parent = servers;
			String name = ip + ":" + port;

			handler = new HelloHandler();
			
	/*		
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
			TServer server = new TThreadPoolServer(tArgs);*/

			TMultiplexedProcessor processor = new TMultiplexedProcessor();
			processor.registerProcessor("hello", new Hello.Processor<Hello.Iface>(new HelloHandler()));
			processor.registerProcessor("calculator", new Calculator.Processor<Calculator.Iface>(new CalculatorHandler()));
			// 传输通道 - 非阻塞方式
			TNonblockingServerSocket serverTransport = new TNonblockingServerSocket(port);
			TThreadedSelectorServer.Args tArgs = new TThreadedSelectorServer.Args(serverTransport);
			tArgs.processor(processor);
			TFramedTransport.Factory f = new TFramedTransport.Factory();
			tArgs.transportFactory(f);
			// 二进制协议
			tArgs.protocolFactory(new TCompactProtocol.Factory());
			// 多线程非阻塞模型
			TServer server = new TThreadedSelectorServer(tArgs);
			System.out.println("Hello TMultiplexedProcessor TThreadedSelectorServer....");
			// 注册服务，实现集群
			Registry.add(zkServer, parent, name);
			server.serve(); // 启动服务

		} catch (Exception x) {
			x.printStackTrace();
		}
	}

}
