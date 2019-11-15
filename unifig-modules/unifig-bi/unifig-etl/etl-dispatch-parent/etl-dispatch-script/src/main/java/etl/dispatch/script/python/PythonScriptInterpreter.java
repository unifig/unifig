package etl.dispatch.script.python;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.alibaba.fastjson.JSON;
import etl.dispatch.script.AbstractScript;
import etl.dispatch.script.ScriptBean;
import etl.dispatch.script.ScriptCallBack;

@Service("pythonScript")
public class PythonScriptInterpreter extends AbstractScript {
	private static Logger logger = LoggerFactory.getLogger(PythonScriptInterpreter.class);
	private static final String python = "C:\\ProgramData\\Anaconda3\\python.exe";

	@Override
	protected void start(ScriptBean scriptBean, ScriptCallBack callback) {
		String[] params = new String[] { JSON.toJSONString(scriptBean.getParamMap()) };
		String pythonPath = scriptBean.getScriptPath();
		String[] command = Arrays.copyOf(new String[] { python, pythonPath }, params.length + 2);
		System.arraycopy(params, 0, command, 2, params.length);
		List<String> resLines = new ArrayList<>();
		try {
			Process process = Runtime.getRuntime().exec(command, null, null);
			BufferedReader in = new BufferedReader(new InputStreamReader(process.getInputStream()));
			String line;
			while ((line = in.readLine()) != null) {
				resLines.add(line);
			}
			in.close();
			process.waitFor();
		} catch (IOException e) {
			logger.error("Java executes Runtime IOException, error：" + e.getMessage());
		} catch (InterruptedException e) {
			logger.error("Java executes Runtime InterruptedException, error：" + e.getMessage());
		}
		// 脚本结束回调状态
		super.callback(true, null, scriptBean, callback);
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
	}

}
