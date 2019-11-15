package etl.dispatch.config.entity;

import etl.dispatch.config.bean.BaseManagedEntity;

public class ConfInfoPythonScriptEntity extends BaseManagedEntity {
	private static final long serialVersionUID = -6875696092411584663L;

	private Integer pkId;

	private String scriptName;

	private String presetParam;

	private String scriptPath;

	private String personal;

	public Integer getPkId() {
		return pkId;
	}

	public void setPkId(Integer pkId) {
		this.pkId = pkId;
	}

	public String getScriptName() {
		return scriptName;
	}

	public void setScriptName(String scriptName) {
		this.scriptName = scriptName;
	}

	public String getPresetParam() {
		return presetParam;
	}

	public void setPresetParam(String presetParam) {
		this.presetParam = presetParam;
	}

	public String getScriptPath() {
		return scriptPath;
	}

	public void setScriptPath(String scriptPath) {
		this.scriptPath = scriptPath;
	}

	public String getPersonal() {
		return personal;
	}

	public void setPersonal(String personal) {
		this.personal = personal;
	}

}
