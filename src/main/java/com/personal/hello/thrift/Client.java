package com.personal.hello.thrift;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;
import java.util.concurrent.atomic.AtomicInteger;

import org.apache.thrift.TApplicationException;
import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.async.TAsyncMethodCall;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingSocket;
import org.apache.thrift.transport.TNonblockingTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;

import com.personal.hello.thrift.Hello.AsyncClient.helloMap_call;
import com.personal.hello.thrift.Hello.AsyncClient.helloString_call;
import com.personal.hello.thrift.Hello.helloString_result;

/**
 * 客户端调用 ，可以连接到Server与NonBlockSelectorServer,
 * 不能连接BlockThreadPoolServer
 */
public class Client {
	public static final String address = "127.0.0.1";
	public static final int port = 8080;
	public static final int clientTimeout = 30000;

	public static void main_syn() {
		TTransport transport = new TFramedTransport(new TSocket(address, port, clientTimeout));
		TProtocol protocol = new TCompactProtocol(transport);
		Hello.Client client = new Hello.Client(protocol);

		try {
			transport.open();
			System.out.println("syn:"+client.helloString("larry"));
			System.out.println("syn int:"+client.helloInt(123));
		} catch (TApplicationException e) {
			System.out.println(e.getMessage() + " " + e.getType());
		} catch (TTransportException e) {
			e.printStackTrace();
		} catch (TException e) {
			e.printStackTrace();
		}finally{
			transport.close();
		}
		
	}
	
	static AtomicInteger count = new AtomicInteger(0);
	

	/**
	 * 
	 *1.一个连接(Hello.AsyncClient)可以执行多个方法，但是注意等待所有的同步客户端都完成后关闭连接，否则后端会出现关闭异常
	 *2.如果没有使用等待机制，就要休眠足够长的时间，否则主线程停止后，请求结果还没有到来,日志是不会打印。
	 *4.异步调用后，服务端总是会有一个"远程主机强迫关闭了一个现有的连接"的IOException,这是因为不是在所有客户端的onComplete方法执行完毕后执行transport.close()
	 *造成的
	 *
	 * @throws Exception
	 */
	public static void main_asy() throws Exception {
		try {
			final TAsyncClientManager clientManager = new TAsyncClientManager();
			final TNonblockingTransport transport = new TNonblockingSocket(address, port, clientTimeout);
			TProtocolFactory protocol = new TCompactProtocol.Factory();
			final Hello.AsyncClient asyncClient = new Hello.AsyncClient(protocol, clientManager, transport);
			System.out.println("Client calls .....");
			AsyncMethodCallback<helloMap_call> callback = new AsyncMethodCallback<com.personal.hello.thrift.Hello.AsyncClient.helloMap_call>() {

				@Override
				public void onComplete(com.personal.hello.thrift.Hello.AsyncClient.helloMap_call response) {
					Hello.AsyncClient.helloMap_call res = (Hello.AsyncClient.helloMap_call) response;
					try {
						System.out.println("onComplete:" + res.getResult().toString());
						count.addAndGet(1);
					} catch (TException e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onError(Exception exception) {
					System.out.println("error:");
					//transport.close();
				}
			};
			asyncClient.helloMap("larry", callback);
			
			Thread.sleep(500);
	
		//	Hello.AsyncClient asyncClient2 = new Hello.AsyncClient(protocol, clientManager, transport);
			asyncClient.helloString("hello", new AsyncMethodCallback<helloString_call>() {

				@Override
				public void onComplete(helloString_call response) {
					try {
						System.out.println("onComplete hello:" + response.getResult());
						count.addAndGet(1);
					} catch (TException e) {
						e.printStackTrace();
					}
					
				}

				@Override
				public void onError(Exception exception) {
					System.out.println("error hello:");					
				}
			});
			
			///关键代码，关闭连接
			while(count.get()<2){//等待两个结果均返回
				
			}
			transport.finishConnect();
			transport.close();
///			Thread.sleep(5000);//必须要休眠，否则主线程停止后，请求结果还没有到来,日志是不会打印。
		} catch (IOException e) {
			e.printStackTrace();
		}finally{
			
		}
	}

	/**
	 * 客户端同步调用和异步调用最后不要在一块使用，目前会有问题，没有找到解决办法！不知道是什么原因!
	 * 大概是由于同一时刻只有一个客户端连接正在处理？好像不是(有可能是两个transport实际上指向的是一个
	 * 地址导致异步调用使用了同步调用关掉的连接造成的)
	 * @param args
	 * @throws Exception
	 */
	public static void main(String[] args) throws Exception {
		main_asy();
	
		main_syn();
	//	Thread.sleep(1000);//保证异步方法的日志能够打印出来
	}
}
