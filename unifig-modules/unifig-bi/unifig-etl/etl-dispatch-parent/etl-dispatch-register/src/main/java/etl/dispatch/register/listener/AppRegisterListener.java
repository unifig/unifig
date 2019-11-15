package etl.dispatch.register.listener;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.context.embedded.EmbeddedServletContainerInitializedEvent;
import org.springframework.context.ApplicationListener;

import com.alibaba.fastjson.JSON;

import etl.dispatch.register.register.RegisterListener;
import etl.dispatch.register.register.refresh.RegisterRefresh;
import etl.dispatch.util.Exceptions;
import etl.dispatch.util.StringUtil;

public class AppRegisterListener implements ApplicationListener<EmbeddedServletContainerInitializedEvent> {
	private static Logger logger = LoggerFactory.getLogger(AppRegisterListener.class);
	public static String serverIp;
	public static int serverPort;

	@Override
	public void onApplicationEvent(EmbeddedServletContainerInitializedEvent event) {
		// 获取Ip和端口
		String logicNodeLogStr = this.getPortByMBean(event);
		if (!StringUtil.isNullOrEmpty(logicNodeLogStr)) {
			if (logicNodeLogStr.indexOf(":") > -1) {
				AppRegisterListener.serverIp = logicNodeLogStr.split(":")[0];
				AppRegisterListener.serverPort = Integer.parseInt(logicNodeLogStr.split(":")[1]);
			}
		} else {
			AppRegisterListener.serverIp = "127.0.0.1";
			AppRegisterListener.serverPort = 8080;
		}
		try {
			logger.info("服务启动，进入ServerRegisterListener，注册服务器集群状态。");
			Map<String, Object> registerMap = RegisterRefresh.getRegisterInfo();
			RegisterListener.register(registerMap);
			RegisterRefresh.refresh();
			logger.info("注册服务器集群状态完成。：" + JSON.toJSONString(registerMap));
		} catch (IOException ex) {
			logger.error("服务启动，进入ServerRegisterListener，注册服务器集群状态失败。error:" + Exceptions.getStackTraceAsString(ex));
		}

	}

	private String getPortByMBean(EmbeddedServletContainerInitializedEvent event) {
		InetAddress addr = null;
		String ip = "";
		int port = -1;
		try {
			addr = InetAddress.getLocalHost();
			ip = addr.getHostAddress().toString();
			port = event.getEmbeddedServletContainer().getPort();
			return String.format("%s:%s", ip, port);
		} catch (UnknownHostException e1) {
			e1.printStackTrace();
		}
		return null;
	}

}
