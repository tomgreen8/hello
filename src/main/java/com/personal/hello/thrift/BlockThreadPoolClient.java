package com.personal.hello.thrift;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
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
        TTransport transport = new TSocket(SERVER_IP, SERVER_PORT, TIMEOUT);
        // 协议要和服务端一致
        // 使用二进制协议 
        TProtocol protocol = new TBinaryProtocol(transport);
        // 创建Client
        Hello.Client client = new Hello.Client(protocol);
        transport.open();
        String result = client.helloString("thrift");
        System.out.println("result : " + result);
        // 关闭资源
        transport.close();
    }
}
