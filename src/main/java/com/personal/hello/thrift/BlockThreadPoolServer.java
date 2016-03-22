package com.personal.hello.thrift;

import org.apache.thrift.TException;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;

/**
 * 注册服务端
 * 一个请求一个线程
 *     线程池服务模型，使用标准的阻塞式IO，预先创建一组线程处理请求
 */
public class BlockThreadPoolServer {
    // 注册端口
    public static final int SERVER_PORT = 8081;

    public static void main(String[] args) throws TException {
        TProcessor tprocessor = new Hello.Processor<Hello.Iface>(new HelloHandler());
        // 阻塞IO
        TServerSocket serverTransport = new TServerSocket(SERVER_PORT);
        // 多线程服务模型
        TThreadPoolServer.Args tArgs = new TThreadPoolServer.Args(serverTransport);
        tArgs.processor(tprocessor);
        // 客户端协议要一致
        tArgs.protocolFactory(new TBinaryProtocol.Factory());
         // 线程池服务模型，使用标准的阻塞式IO，预先创建一组线程处理请求。
        TServer server = new TThreadPoolServer(tArgs);
        System.out.println("Hello TThreadPoolServer....");
        server.serve(); // 启动服务
    }
}
