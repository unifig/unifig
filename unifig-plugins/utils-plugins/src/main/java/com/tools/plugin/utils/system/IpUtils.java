package com.tools.plugin.utils.system;

import java.math.BigInteger;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.http.HttpServletRequest;

import com.tools.plugin.utils.UncString;

public class IpUtils {
	public static String getIpAddr(HttpServletRequest request) {
		String ip = request.getHeader("X-Forwarded-For");
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("WL-Proxy-Client-IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_CLIENT_IP");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getHeader("HTTP_X_FORWARDED_FOR");
		}
		if (ip == null || ip.length() == 0 || "unknown".equalsIgnoreCase(ip)) {
			ip = request.getRemoteAddr();
		}
		return ip;
	}

	public static String getHostAddress() {
		try {
			InetAddress addr = InetAddress.getLocalHost();
			if (null != addr) {
				String ip = addr.getHostAddress().toString();// 获得本机IP　　
				String address = addr.getHostName().toString();// 获得本机名称
				return ip + ":" + address;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static String getIPAddress() {
		try {
			InetAddress addr = InetAddress.getLocalHost();
			if (null != addr) {
				String ip = addr.getHostAddress().toString();// 获得本机IP　　
				return ip ;
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	

	public static int ip2int(String ip) {
		if(ip == null || ip.length() == 0)
			return 0;
		
		String[] parts = ip.split("\\.");
		if(parts.length != 4) {
			return 0;
		}
		
		try {
			int ip1 = Integer.parseInt(parts[0]);
			int ip2 = Integer.parseInt(parts[1]);
			int ip3 = Integer.parseInt(parts[2]);
			int ip4 = Integer.parseInt(parts[3]);
			
			return (ip1 << 24) + (ip2 << 16) + (ip3 << 8) + ip4;
		} catch(Throwable t) {
			return 0;
		}
	}
	
	public static String convertIp(int ipval) {
		int ip1 = (ipval & 0xFF000000) >> 24;
		int ip2 = (ipval & 0xFF0000) >> 16;
		int ip3 = (ipval & 0xFF00) >> 8;
		int ip4 = (ipval & 0xFF);
		if(ip1 < 0) ip1 += 256;
		if(ip2 < 0) ip2 += 256;
		if(ip3 < 0) ip3 += 256;
		if(ip4 < 0) ip4 += 256;
		return ip1 + "." + ip2 + "." + ip3 + "." + ip4;
	}
	
	public static BigInteger ip2BigInt(String ip) {
		try {
			InetAddress addr = InetAddress.getByName(ip);
			return new BigInteger(addr.getAddress());
		} catch (UnknownHostException e) {
			return null;
		}
	}
	
	/**
	 * 检查是否合法IP
	 * @return
	 */
	public static boolean isValidIp(String ip) {
		if(ip == null || ip.length() == 0)
			return false;
		
		char[] chars = ip.toCharArray();
		for(int i = 0; i < chars.length; ++i) {
			switch(chars[i]) {
				case '0' :
				case '1' :
				case '2' :
				case '3' :
				case '4' :
				case '5' :
				case '6' :
				case '7' :
				case '8' :
				case '9' :
				case '.' :
				case '*' :
					break;
				default :
					// invalid char
					return false;
			}
		}
		
		return true;
	}
	
	/**
	 * 获取真实IP，如果可以获取到公网IP，则返回公网IP
	 * @param request
	 * @return
	 */
	private static String[] PROXY_HEADER_NAMES = new String[] {
		"X-Forwarded-For", "Proxy-Client-IP", "WL-Proxy-Client-IP"
	};
	
	public static String getRemoteIp(HttpServletRequest request) {
		String ip = null;
		for(String proxyHeaderName : PROXY_HEADER_NAMES) {
			String proxyIp = request.getHeader(proxyHeaderName);
			if(proxyIp != null && !"unknown".equals(proxyIp.toLowerCase())) {
				ip = proxyIp;
				break;
			}
		}
		
		if(ip == null) {
			ip = request.getRemoteAddr();
		} else {
			if(ip.indexOf(',') != -1 || ip.indexOf(' ') != -1) {
				// proxy ip list
				String[] ipList = UncString.tokenizeToStringArray(ip, ", ");
				String wanIp = null;
				for(String proxyIp : ipList) {
					if(!isLanIp(proxyIp)) {
						wanIp = proxyIp;
						break;
					}
				}
				
				ip = (wanIp == null ? ipList[0] : wanIp);
			}
		}
		
		return ip;
	}
	
	public static boolean isLanIp(String ip){
		if(ip == null)
			return false;
		
		/*
	  		保留IP地址范围如下所示。 
			10.0.0.0 - 10.255.255.255 
			172.16.0.0 - 172.31.255.255 
			192.168.0.0 - 192.168.255.555
			127.0.0.0 - 127.255.255.255
			0.0.0.0 - 0.255.255.255
		 */	
		//if(Global.SHOULD_EXCLUDE_PRIVATE_IP) {		
		String[] parts = UncString.tokenizeToStringArray(ip, ".");
		if(parts.length != 4) {
			// invalid ip
			return false;
		}
		
		int ip1 = Integer.parseInt(parts[0]);
		int ip2 = Integer.parseInt(parts[1]);
		
		if (ip1 == 192 && ip2 == 168)
			return true;
		if (ip1 == 10)
			return true;
		if (ip1 == 172 && ip2 >= 16 && ip2 <= 31)
			return true;
		if (ip1 == 127)
			return true;
		if (ip1 == 0)
			return true;

    	return false;
	}

}