package com.personal.server;
/**
 * 
 */



import java.io.*;
import java.net.*;

/**
 * 服务器端程序
 * 
 * @author Hongten
 * 
 * @time 2012-4-29 2012
 */
public class MyServer {
    public static void main(String args[]) {
        try {
        	int count = 0;
            // 创建一个socket对象
        	String port = "8888";
        	if(args.length > 0){
        		port = args[0];
        	}
            ServerSocket s = new ServerSocket(Integer.parseInt(port));
            // socket对象调用accept方法，等待连接请求
            System.out.println("Server listenning "+port+" start(s->ServerSocket,s1->Socket)");
            while(true){
	            Socket s1 = s.accept();
	            System.out.println("Server accept!");
	            System.out.println("s1(socket).getRemoteSocketAddress():"+s1.getRemoteSocketAddress().toString());
	            System.out.println("socket getPort:"+s1.getPort()+",getLocalPort:"+s1.getLocalPort()+",getInetAddress"+s1.getInetAddress().toString()+",getLocalAddress:"+s1.getLocalAddress());
	
	            // =========服务器端，在这里应该先打开输出流，在打开输入流，
	            // =========因为服务器端执行的操作是先听，再说，听，说，听，说.....
	

	            // 打开输入流
	            InputStream is = s1.getInputStream();
	            // 封装输入流
	            DataInputStream dis = new DataInputStream(is);
	            // 创建并启用两个线程
	            new MyServerReader(dis).start();
	            // 打开输出流
	            OutputStream os = s1.getOutputStream();
	            // 封装输出流
	            DataOutputStream dos = new DataOutputStream(os);
	            new MyServerWriter(dos).start();
	            System.out.println(++count+".新连接:"+s1.getInetAddress()+":"+s1.getPort());
	           
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}



// 接受并打印客户端传过来的信息
class MyServerReader extends Thread {
    private DataInputStream dis;

    public MyServerReader(DataInputStream dis) {
        this.dis = dis;
    }

    public void run() {
        String info;
        try {
            while (true) {
                // 如果对方，即客户端没有说话，那么就会阻塞到这里，
                // 这里的阻塞并不会影响到其他线程
                info = dis.readUTF();
                // 如果状态有阻塞变为非阻塞，那么就打印接受到的信息
                System.out.println("对方说: " + info);
                if (info.equals("bye")) {
                    System.out.println("对方下线，程序退出!");
                   // System.exit(0);
                    break;//退出当前线程，socket要等超时退出
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

// 从键盘获得输入流并写入信息到客户端
class MyServerWriter extends Thread {
    private DataOutputStream dos;

    public MyServerWriter(DataOutputStream dos) {
        this.dos = dos;
    }

    public void run() {
        // 读取键盘输入流
        InputStreamReader isr = new InputStreamReader(System.in);
        // 封装键盘输入流
        BufferedReader br = new BufferedReader(isr);
        String info;
        try {
            while (true) {
                info = br.readLine();
                dos.writeUTF(info);
                if (info.equals("bye")) {
                    System.out.println("自己下线，程序退出!");
                    break;
                   // System.exit(0);
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
