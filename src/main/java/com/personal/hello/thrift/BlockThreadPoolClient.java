package com.personal.hello.thrift;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.personal.hello.cluster.ServiceConsumer;

/**
 * 客户端调用 阻塞
 */
public class BlockThreadPoolClient {

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
		consumer.init(zkServer, BlockThreadPoolServer.servers);
		
		test();
		

	}
	
	public static void test1(){
		// 设置传输通道
				ExecutorService executor = Executors.newCachedThreadPool();
				int size = 300;
				for (int i = 0; i < size; i++) {
					executor.execute(new Runnable() {
						@Override
						public void run() {
							test();
						}
					});
				}
				// executor.shutdown();
	}
	
	public static void test(){
		String server = getServer();
		log.info("订阅服务：" + server);
		int port = Integer.parseInt(server.split(":")[1]);
		String ip = server.split(":")[0];
		for (int i = 0; i < 100000; i++) {
			TTransport transport = null;
			try {
				transport = new TSocket(ip, port, TIMEOUT);
				transport.open();
				//handle(transport);
				multiHandle(transport);
				Thread.sleep(3500);
			} catch (Exception e) {
				server = getServer();
				port = Integer.parseInt(server.split(":")[1]);
				ip = server.split(":")[0];
				e.printStackTrace();
			}finally{
				if(transport!=null){
					// 关闭资源
					transport.close();
				}
			}
		}
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
	
	/**
	 * 当服务端使用TMultiplexedProcessor时候，客户端要使用TMultiplexedProtocol，否则调不通
	 * @param transport
	 * @throws TException
	 */
	@Deprecated
	public static void handle(TTransport transport) throws TException {
		// 协议要和服务端一致
		// 使用二进制协议
		TProtocol protocol = new TBinaryProtocol(transport);
		Hello.Client client = new Hello.Client(protocol);
	
		String result = client.helloString("thrift");
		System.out.println("result=============:"+result);
	}

}
