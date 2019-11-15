package etl.dispatch.script;

public interface ScriptCallBack {

	public void setSign(boolean isSuccess, String errorMsg, ScriptBean scriptBean);
}
