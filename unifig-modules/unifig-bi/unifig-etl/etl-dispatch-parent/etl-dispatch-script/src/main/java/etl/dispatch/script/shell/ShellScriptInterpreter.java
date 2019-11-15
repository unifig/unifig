package etl.dispatch.script.shell;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import etl.dispatch.script.AbstractScript;
import etl.dispatch.script.ScriptCallBack;
import etl.dispatch.script.ScriptBean;

@Service("shellScript")
public class ShellScriptInterpreter extends AbstractScript {
	private static Logger logger = LoggerFactory.getLogger(ShellScriptInterpreter.class);

	@Override
	protected void start(ScriptBean scriptBean, ScriptCallBack callback) {
		// TODO Auto-generated method stub

		// 脚本结束回调状态
		super.callback(true, null, scriptBean, callback);
	}

	@Override
	public void stop() {
		// TODO Auto-generated method stub
	}

}
