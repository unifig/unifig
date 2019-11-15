package etl.dispatch.script;

import java.util.Map;

import etl.dispatch.script.ScriptCallBack;

/**
 * 脚本Etl调度执行
 *
 */
public interface IScriptService {

	public void init(Map<String, Object> tasksMap);

	public void start(long startTimes, String groupId, ScriptCallBack callback);

	public void stop();
}
