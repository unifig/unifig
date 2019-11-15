package com.tools.plugin.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.Socket;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TcpConnectUtil {
	private static Logger logger = LoggerFactory.getLogger(TcpConnectUtil.class);
	private static final int timeout = 5000;

	public static boolean isReachable(InetAddress remoteInetAddr, int port, int timeout) {
		boolean isReachable = false;
		Socket socket = null;
		try {
			socket = new Socket();
			InetSocketAddress endpointSocketAddr = new InetSocketAddress(remoteInetAddr, port);
			socket.connect(endpointSocketAddr, timeout);
			isReachable = true;
			logger.debug("SUCCESS - connection established! remote: " + remoteInetAddr.getHostAddress() + " port:" + port);
		} catch (IOException ex) {
			logger.error("FAILRE - CAN not connect! remote: " + remoteInetAddr.getHostAddress() + " port:" + port + "; ");
		} finally {
			if (socket != null) {
				try {
					socket.close();
				} catch (IOException e) {
					logger.error("Error occurred while closing socket..");
				}
			}
		}
		return isReachable;
	}

	public static void main(String[] args) throws IOException {
		InetAddress address = InetAddress.getByName("103.6.222.234");
		System.out.println(isReachable(address, 9968, timeout));
	}
}
