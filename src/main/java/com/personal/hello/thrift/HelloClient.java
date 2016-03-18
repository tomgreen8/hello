package com.personal.hello.thrift;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.Properties;

import org.apache.thrift.TException;
import org.apache.thrift.transport.TSSLTransportFactory;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TSSLTransportFactory.TSSLTransportParameters;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.personal.hello.cluster.ServiceConsumer;

public class HelloClient {
	
	private static final Logger log =LoggerFactory.getLogger(HelloClient.class);
	
	private static final ServiceConsumer consumer = new ServiceConsumer();
	
	public static String getServer(){
		return consumer.consume();
	}
	
	public static void main(String[] args) throws URISyntaxException, FileNotFoundException, IOException {
		try {
			boolean secure = false;
			TTransport transport;
			log.info(""+args.length);
			Properties p = new Properties();
			String config = HelloServer.class.getResource("/").getPath() + "system.properties";
			p.load(new FileInputStream(config));
			String zkServer = p.getProperty("zookeeper");
			consumer.init(zkServer, HelloServer.servers);
			String server = getServer();
			log.info("订阅服务："+server);
			int port = Integer.parseInt(server.split(":")[1]);
			if (args.length == 0 || args[0].contains("simple")) {
				transport = new TSocket(server.split(":")[0], port);
				transport.open();
			} else {
				secure = true;
				/*
				 * Similar to the server, you can use the parameters to setup
				 * client parameters or use the default settings. On the client
				 * side, you will need a TrustStore which contains the trusted
				 * certificate along with the public key. For this example it's
				 * a self-signed cert.
				 */
				TSSLTransportParameters params = new TSSLTransportParameters();
				String path = HelloClient.class.getResource(".").toURI().getPath()+"kclient.keystore";
				System.out.println(path);
				params.setTrustStore(path, "thrift", "SunX509", "JKS");
				/*
				 * Get a client transport instead of a server transport. The
				 * connection is opened on invocation of the factory method, no
				 * need to specifically call open()
				 */
				transport = TSSLTransportFactory.getClientSocket(server.split(":")[0], port, 0, params);
			}

			TProtocol protocol = new TBinaryProtocol(transport);
			Hello.Client client = new Hello.Client(protocol);
			perform(client,secure);
			transport.close();
		} catch (TException x) {
			x.printStackTrace();
		}
	}

	private static void perform(Hello.Client client,boolean secure) throws TException {
		Map<Integer, String> result = client.helloMap("bill!");
		log.info("secure:"+secure+",获取到远端处理结果:" + result.toString());

	}
}
