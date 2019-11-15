package com.tools.plugin.utils.system;

import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.StringTokenizer;

 /**
 * IP地址及Mac地址帮助类
 *
 *
 */

public class UtilityUtil {

	/**
	 * 得到本地的ip地址
	 * @return
	 */
	public static String getLocalhostIP() {
		String str = "";
		try {
			str = InetAddress.getLocalHost().getHostAddress();
		} catch (Exception e) {
			//....
		}
		return str;

	}

	/**
	 * 得到当前操作系统
	 * @return
	 */
	public static String getOperatingSystem() {
		return System.getProperty("os.name");
	}

	/**
	 * 获取机器的MAC地址
	 * @return
	 */
	public static String getAllMacAddress() {
		String os = getOperatingSystem();
		String macStr = "";
		try {
			if (os.startsWith("Windows")) {
				macStr = getWindowsMACAddress();
			} else if (os.startsWith("Linux")) {
				macStr = getUnixMACAddress();
			}
		} catch (Exception ex) {
			//....
		}

		return macStr;
	}

	/**
	 * 获取机器第一张网卡的MAC地址
	 * @return
	 */
	public static String getMacAddress() {
		String os = getOperatingSystem();
		String macStr = "";
		try {
			if (os.startsWith("Windows")) {
				macStr = getWindowsMACAddress().split("#")[0];
			} else if (os.startsWith("Linux")) {
				macStr = getUnixMACAddress();
			}
		} catch (Exception ex) {
			//....
		}
		return macStr;
	}

	/**
	 * 获取unix网卡的mac地址. 非windows的系统默认调用本方法获取.如果有特殊系统请继续扩充新的取mac地址方法.
	 * @return mac地址
	 */
	public static String getUnixMACAddress() {
		StringBuffer mac = new StringBuffer();
		int index = 0;
		//      有时要用到这个命令，权限的问题: /sbin/ifconfig
		String ipConfig = runConsoleCommand("ifconfig eth0");// linux下的命令，一般取eth0作为本地主网卡
		// 显示信息中包含有mac地址信息
		StringTokenizer tokenizer = new StringTokenizer(ipConfig, "\n");
		while (tokenizer.hasMoreTokens()) {
			// 转为小写,去掉前后空格
			String line = tokenizer.nextToken().toLowerCase().trim();
			index = line.indexOf("hwaddr");// 寻找标示字符串[hwaddr]
			if (index >= 0) {// 找到了
				index = line.indexOf(":");// 寻找":"的位置
				if (index >= 0) {
					mac.append(line.substring(index + 1).trim());// 取出mac地址并去除2边空格
				}
				break;
			}
		}
		return mac.toString();
	}

	/**
	 * 获取widnows网卡的mac地址,多个网卡默认以#隔开
	 * @return 小写格式字符串的mac地址
	 */
	public static String getWindowsMACAddress() {
		return getWindowsMACAddress("#");
	}

	/**
	 * 获取widnows网卡的mac地址
	 * @param split 分隔符,用来分隔多个网卡的mac地址
	 * @return 小写格式字符串的mac地址
	 */
	public static String getWindowsMACAddress(String split) {
		StringBuffer mac = new StringBuffer();
		int index = 0;
		String ipConfig = runConsoleCommand("ipconfig /all");// windows下的命令，显示信息中包含有mac地址信息
		StringTokenizer tokenizer = new StringTokenizer(ipConfig, "\n");
		while (tokenizer.hasMoreTokens()) {
			// 转为小写,去掉前后空格
			String line = tokenizer.nextToken().toLowerCase().trim();
			index = line.indexOf("physical address");// 寻找标示字符串[physical
			// address]
			if (index >= 0) {// 找到了
				index = line.indexOf(":");// 寻找":"的位置
				if (index >= 0) {
					mac.append(line.substring(index + 1).trim() + split);// 取出mac地址并去除2边空格
				}
			}
		}
		return mac.subSequence(0, mac.length() - 1).toString();
	}

	/**
	 * 执行本地命令
	 * @param command 命令
	 * @return
	 */
	public static String runConsoleCommand(String command) {
		StringBuffer buffer = new StringBuffer();
		InputStream inputStream = null;
		Process p = null;
		try {
			p = Runtime.getRuntime().exec(command);
			inputStream = new BufferedInputStream(p.getInputStream());
			while (true) {
				int c = inputStream.read();
				if (c == -1) {
					break;
				}
				buffer.append((char) c);
			}
		} catch (IOException ex) {
			//			ex.printStackTrace();
		} finally {
			if (inputStream != null) {
				try {
					inputStream.close();
				} catch (IOException e) {
					//					e.printStackTrace();
				}
			}
			if (p != null) {
				p.destroy();
				p = null;
			}
		}
		return buffer.toString();
	}
}
