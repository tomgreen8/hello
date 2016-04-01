package com.personal.hello.thrift;
import org.apache.thrift.TProcessorFactory;  
import org.apache.thrift.protocol.TCompactProtocol;  
import org.apache.thrift.server.THsHaServer;  
import org.apache.thrift.server.TServer;  
import org.apache.thrift.transport.TFramedTransport;  
import org.apache.thrift.transport.TNonblockingServerSocket;  
import org.apache.thrift.transport.TTransportException;  
  
/** 
 * @author 吕桂强 
 * @email larry.lv.word@gmail.com 
 * @version 创建时间：2012-4-24 下午8:14:50 
 */  
public class Server {  
    public final static int PORT = 8080;  
  
    @SuppressWarnings({ "rawtypes", "unchecked" })  
    private void start() {  
        try {  
            TNonblockingServerSocket socket = new TNonblockingServerSocket(PORT);  
            final Hello.Processor processor = new Hello.Processor(new HelloHandler());  
            THsHaServer.Args arg = new THsHaServer.Args(socket);  
            // 高效率的、密集的二进制编码格式进行数据传输  
            // 使用非阻塞方式，按块的大小进行传输，类似于 Java 中的 NIO  
            arg.protocolFactory(new TCompactProtocol.Factory());  
            arg.transportFactory(new TFramedTransport.Factory());  
            arg.processorFactory(new TProcessorFactory(processor));  
            TServer server = new THsHaServer(arg);  
            System.out.println("#服务启动-使用:非阻塞&高效二进制编码");
            server.serve();  
        } catch (TTransportException e) {  
            e.printStackTrace();  
        } catch (Exception e) {  
            e.printStackTrace();  
        }  
    }  
  
    public static void main(String args[]) {  
        Server srv = new Server();  
        srv.start();  
    }  
}  