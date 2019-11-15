package etl.dispatch.util.ip;

import java.net.InetAddress;

import javax.servlet.http.HttpServletRequest;

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
}