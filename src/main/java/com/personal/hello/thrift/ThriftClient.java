package com.personal.hello.thrift;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.personal.hello.cluster.ServiceConsumer;

public class ThriftClient {
	private static final Logger log = LoggerFactory.getLogger(HelloClient.class);

	public static final int TIMEOUT = 30000;

	private static final ServiceConsumer consumer = new ServiceConsumer();

	public static String getServer() {
		return consumer.consume();
	}

	public static void main(String[] args) throws TException, FileNotFoundException, IOException {
		log.info("" + args.length);
		Properties p = new Properties();
		String config = HelloServer.class.getResource("/").getPath() + "system.properties";
		p.load(new FileInputStream(config));
		String zkServer = p.getProperty("zookeeper");
		consumer.init(zkServer, ThrfitServer.servers);
		
		String port1 = p.getProperty("thrift.server.port");
		String ip = p.getProperty("thrift.server.ip");
		int port = 8080;
		if (port1 != null) {
			port = Integer.parseInt(port1);
		}
		TTransport transport = new TFramedTransport(new TSocket(ip, port, TIMEOUT));

		// 使用二进制协议
		// TProtocol protocol = new TBinaryProtocol(transport);
		// 协议要和服务端一致
		// HelloTNonblockingServer
		// 使用高密度二进制协议
		TProtocol protocol = new TCompactProtocol(transport);
		// 使用二进制协议
		// TProtocol protocol = new TBinaryProtocol(transport);
		Hello.Client client = new Hello.Client(new TMultiplexedProtocol(protocol, "hello"));
		transport.open();
		Map<Integer, String> result = client.helloMap("jacks");
		System.out.println("result : " + result.toString());
		// 关闭资源
		transport.close();

	}

	public static void multiHandle(TTransport transport) throws TException {
		// 协议要和服务端一致
		// 使用二进制协议
		TProtocol protocol = new TBinaryProtocol(transport);
		TMultiplexedProtocol mpHello = new TMultiplexedProtocol(protocol, "hello");
		// 创建Client
		Hello.Client client = new Hello.Client(mpHello);
		String result = client.helloString("thrift");
		System.out.println("hello result : " + result);
		TMultiplexedProtocol mpCalculator = new TMultiplexedProtocol(protocol, "calculator");
		Calculator.Client client2 = new Calculator.Client(mpCalculator);
		System.out.println("calculator:" + client2.add(1, 5));
	}

}
