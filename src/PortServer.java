/**
 * 
 */


/**
 * @author liuquan
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
public class PortServer {
	public static void main(String args[]) {

		int count = 0;
		int fail = 0;

		// 创建一个socket对象
		for (int i = 10000; i < 65535; i++) {
			try {
				ServerSocket s = new ServerSocket(i);
				count++;
				System.out.println("端口"+i+"打开成功!");
			} catch (Exception e) {
				fail++;
				e.printStackTrace();
			}

		}
		System.out.println("总共创建" + count + "个服务端,失败" + fail + "个");

	}
}
