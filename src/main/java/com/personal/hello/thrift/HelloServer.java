/**
 * 
 */
package com.personal.hello.thrift;

import java.io.FileInputStream;
import java.net.InetAddress;
import java.util.Properties;

import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TServer.Args;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.support.ClassPathXmlApplicationContext;

import com.personal.hello.cluster.Registry;

/**
 * 
 * 单线程阻塞方式处理客户端请求
 * @author liuquan
 *
 */
public class HelloServer {

	private static HelloHandler handler;
	public static final String servers = "/helloservers";
	private static final Logger log = LoggerFactory.getLogger(HelloServer.class);
	private static int port = 9090;

	private static Hello.Processor processor;

	public static void main(String[] args) {
		try {
			// /读取配置
			Properties p = new Properties();
			String config = HelloServer.class.getResource("/").getPath() + "system.properties";
			p.load(new FileInputStream(config));
			String zkServer = p.getProperty("zookeeper");
			String port1 = p.getProperty("thrift.server.port");
			String ip = p.getProperty("thrift.server.ip");//当前thirftserver的ip
			if (port1 != null) {
				port = Integer.parseInt(port1);
			}
			String parent = servers;
			String name = ip+":"+port;
			
			handler = new HelloHandler();
			processor = new Hello.Processor(handler);

			Runnable simple = new Runnable() {
				public void run() {
					simple(processor);
				}
			};
			Runnable secure = new Runnable() {
				public void run() {
					secure(processor);
				}
			};

			new Thread(simple).start();
			new Thread(secure).start();

			// 注册服务，实现集群
			Registry.add(zkServer, parent, name);
			log.info("thrift启动地址:"+name+",注册的zookeeper："+zkServer);;
		} catch (Exception x) {
			x.printStackTrace();
		}
	}

	public static void simple(Hello.Processor processor) {
		try {
			TServerTransport serverTransport = new TServerSocket(port);
			TServer server = new TSimpleServer(new Args(serverTransport).processor(processor));

			// Use this for a multithreaded server
			// TServer server = new TThreadPoolServer(new
			// TThreadPoolServer.Args(serverTransport).processor(processor));
			System.out.println("Starting the simple server...");
			server.serve();
			System.out.println("server.serve() has called!");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void secure(Hello.Processor processor) {
		try {
			/*
			 * Use TSSLTransportParameters to setup the required SSL parameters.
			 * In this example we are setting the keystore and the keystore
			 * password. Other things like algorithms, cipher suites, client
			 * auth etc can be set.
			 */
			TSSLTransportParameters params = new TSSLTransportParameters();
			// The Keystore contains the private key
			String path = HelloServer.class.getResource(".").toURI().getPath() + "kserver.keystore";
			System.out.println(path);
			params.setKeyStore(path, "thrift", null, null);

			/*
			 * Use any of the TSSLTransportFactory to get a server transport
			 * with the appropriate SSL configuration. You can use the default
			 * settings if properties are set in the command line. Ex:
			 * -Djavax.net.ssl.keyStore=.keystore and
			 * -Djavax.net.ssl.keyStorePassword=thrift
			 * 
			 * Note: You need not explicitly call open(). The underlying server
			 * socket is bound on return from the factory class.
			 */
			TServerTransport serverTransport = TSSLTransportFactory.getServerSocket(port + 1, 0, null, params);
			TServer server = new TSimpleServer(new Args(serverTransport).processor(processor));

			// Use this for a multi threaded server
			// TServer server = new TThreadPoolServer(new
			// TThreadPoolServer.Args(serverTransport).processor(processor));

			System.out.println("Starting the secure server...");
			server.serve();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
