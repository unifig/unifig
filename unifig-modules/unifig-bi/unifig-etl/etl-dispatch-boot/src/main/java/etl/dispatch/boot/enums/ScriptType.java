package etl.dispatch.boot.enums;
/**
 * shell、python、java等
 *
 *
 */
public enum ScriptType {
	SHELL(1, "shell"), PYTHON(2, "python"), JAVA(3, "java");
	public int code;
	public String value;

	ScriptType(int code, String value) {
		this.code = code;
		this.value = value;
	}

	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}
}
