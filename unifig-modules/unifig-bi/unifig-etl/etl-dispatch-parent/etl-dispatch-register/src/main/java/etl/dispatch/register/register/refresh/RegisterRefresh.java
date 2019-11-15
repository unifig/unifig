package etl.dispatch.register.register.refresh;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import etl.dispatch.register.listener.AppRegisterListener;
import etl.dispatch.register.register.RegisterListener;

public class RegisterRefresh {
    //每60秒注册一次服务状态
 	private static int flushTime = 60000;

	public static void refresh() {
		final Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			public void run() {
				try {
					registerRefresh();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}, 0, flushTime);
	}

	/**
	 * 注册状态刷新
	 * @throws IOException
	 */
	public static void registerRefresh() throws IOException {
		RegisterListener.register(getRegisterInfo());
	}
	
	/**
	 * 状态注册数据
	 * @return
	 */
	public static Map<String, Object> getRegisterInfo() {
		Map<String, Object> messageMap = new HashMap<String, Object>();
		messageMap.put("serverIp", AppRegisterListener.serverIp);
		messageMap.put("serverPort", AppRegisterListener.serverPort);
		messageMap.put("activeTime", new Date().getTime());
		return messageMap;
	}

}
