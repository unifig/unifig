package etl.dispatch.util.ip;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class PingUtils {

	private static final int timeOut = 3000; // 超时应该在3钞以上

	public static boolean isPingInetAddress(String ip) {
		boolean status = false;
		if (ip != null) {
			try {
				status = InetAddress.getByName(ip).isReachable(timeOut);
			} catch (UnknownHostException e) {

			} catch (IOException e) {

			}
		}
		return status;
	}

	public static boolean isPingRuntime(String ip) {
		Runtime runtime = Runtime.getRuntime(); // 获取当前程序的运行进对象
		Process process = null; // 声明处理类对象
		String line = null; // 返回行信息
		InputStream is = null; // 输入流
		InputStreamReader isr = null; // 字节流
		BufferedReader br = null;
		boolean res = false;// 结果
		try {
			process = runtime.exec("ping " + ip); // PING
			is = process.getInputStream(); // 实例化输入流
			isr = new InputStreamReader(is);// 把输入流转换成字节流
			br = new BufferedReader(isr);// 从字节中读取文本
			while ((line = br.readLine()) != null) {
				if (line.contains("TTL")) {
					res = true;
					break;
				}
			}
			is.close();
			isr.close();
			br.close();
			if (res) {
				return true;
			} else {
				return false;
			}
		} catch (IOException e) {
			System.out.println(e);
			runtime.exit(1);
		}
		return false;
	}

	public static void main(String arges[]) {
		//String ip = "192.168.88.84";
		//System.out.println("ip:" + ip + " , ping 1 result " + isPingRuntime(ip));
		//System.out.println("ip:" + ip + " , ping 2 result " + isPingInetAddress(ip));

		for (int i = 85; i < 255; i++) {
			String ip = "192.168.88." + i;
			System.out.println("ip:" + ip + " , ping result " + isPingRuntime(ip));
		}

	}

}
