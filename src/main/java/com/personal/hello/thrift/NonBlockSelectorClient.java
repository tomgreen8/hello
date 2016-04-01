package com.personal.hello.thrift;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Map;
import java.util.Properties;

import org.apache.thrift.TException;
import org.apache.thrift.async.AsyncMethodCallback;
import org.apache.thrift.async.TAsyncClientManager;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingSocket;
import org.apache.thrift.transport.TNonblockingTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;

import com.personal.hello.thrift.Hello.AsyncClient.helloMap_call;

/**
 * 客户端调用
 * 非阻塞
 */
public class NonBlockSelectorClient {
    public static final String SERVER_IP = "127.0.0.1";
    public static int port = 8080;
    public static final int TIMEOUT = 30000;

    public static void main(String[] args) throws Exception {
        // 设置传输通道，对于非阻塞服务，需要使用TFramedTransport，它将数据分块发送  
    	Properties p = new Properties();
		String config = HelloServer.class.getResource("/").getPath() + "system.properties";
		p.load(new FileInputStream(config));
		String port1 = p.getProperty("thrift.server.port");
		String ip = p.getProperty("thrift.server.ip");
		if (port1 != null) {
			port = Integer.parseInt(port1);
		}
        TTransport transport = new TFramedTransport(new TSocket(SERVER_IP, port, TIMEOUT));
        // 协议要和服务端一致
        //HelloTNonblockingServer
        // 使用高密度二进制协议 
        TProtocol protocol = new TCompactProtocol(transport);
        // 使用二进制协议 
        //TProtocol protocol = new TBinaryProtocol(transport);
        Hello.Client client = new Hello.Client(protocol);
        transport.open();
        Map<Integer,String> result = client.helloMap("jacks");
        System.out.println("result : " + result.toString());
        // 关闭资源
        transport.close();
		main_asy();
    }
    
    
    /**
     * 异步调用,与同步调用不要放在一块，目前有些问题,见Client类
     * @throws Exception
     */
    public static void main_asy() throws Exception {
    	TNonblockingTransport transport = null;
        try {  
            TAsyncClientManager clientManager = new TAsyncClientManager();  
            transport = new TNonblockingSocket(SERVER_IP, port, 3000);  
            TProtocolFactory protocol = new TCompactProtocol.Factory();  
            Hello.AsyncClient asyncClient = new Hello.AsyncClient(protocol, clientManager, transport);  
            System.out.println("Client calls .....");  
           
            AsyncMethodCallback<helloMap_call> callback = new AsyncMethodCallback<com.personal.hello.thrift.Hello.AsyncClient.helloMap_call>() {

				@Override
				public void onComplete(com.personal.hello.thrift.Hello.AsyncClient.helloMap_call response) {
					Hello.AsyncClient.helloMap_call res = (Hello.AsyncClient.helloMap_call)response;
		    		try {
						System.out.println("onComplete:"+res.getResult().toString());
						//TAsyncMethodCall t =null;
					} catch (TException e) {
						e.printStackTrace();
					}
				}

				@Override
				public void onError(Exception exception) {
					System.out.println("error:");
				}
			};
            asyncClient.helloMap("zhangsan", callback);
        } catch (IOException e) {  
            e.printStackTrace();  
        }finally{
        	if(	transport != null){
        		transport.close();
        	}
        }
    } 
    
}

 
