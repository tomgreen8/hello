/**
 * 
 */
package com.personal.hello.thrift;

import java.io.FileInputStream;
import java.util.Properties;

import org.apache.thrift.TProcessor;
import org.apache.thrift.TProcessorFactory;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;

import com.personal.hello.cluster.Registry;

/**
 * @author liuquan
 *
 */
public class NonBlockSelectorServer {

	private static HelloHandler handler;
	public static final String servers = "/helloservers";
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

			TProcessor tprocessor = new Hello.Processor<Hello.Iface>(handler);
			// 传输通道 - 非阻塞方式
			TNonblockingServerSocket serverTransport = new TNonblockingServerSocket(port);
			TThreadedSelectorServer.Args tArgs = new TThreadedSelectorServer.Args(serverTransport);
			tArgs.processor(tprocessor);
			TFramedTransport.Factory f = new TFramedTransport.Factory();
			tArgs.transportFactory(f);
			// 二进制协议
			tArgs.protocolFactory(new TCompactProtocol.Factory());
			// 多线程非阻塞模型
			TServer server = new TThreadedSelectorServer(tArgs);
			System.out.println("Hello TThreadedSelectorServer....");
			server.serve(); // 启动服务

			// 注册服务，实现集群
			Registry.add(zkServer, parent, name);
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

}
