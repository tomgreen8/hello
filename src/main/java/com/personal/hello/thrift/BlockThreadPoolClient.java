package com.personal.hello.thrift;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

/**
 * 客户端调用
 * 阻塞
 */
public class BlockThreadPoolClient {
    public static final String SERVER_IP = "127.0.0.1";
    public static final int SERVER_PORT = 8081;
    public static final int TIMEOUT = 30000;

    public static void main(String[] args) throws TException {
        // 设置传输通道
        ExecutorService executor = Executors.newCachedThreadPool();
        int size = 300;
        for (int i = 0; i < size; i++) {
        	executor.execute(new Runnable() {
				@Override
				public void run() {
					try {
						 final TTransport transport = new TSocket(SERVER_IP, SERVER_PORT, TIMEOUT);
					     transport.open();
						 for (int i = 0; i < 100000; i++) {
							 multiHandle(transport);
						 }
						 // 关闭资源
					      transport.close();
					} catch (TException e) {
						e.printStackTrace();
					}
				}
			});
		}
      //  executor.shutdown();
        
       
    }
    
    public static void multiHandle(TTransport transport) throws TException{
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
        System.out.println("calculator:"+client2.add(1, 5));
    }
    
}
