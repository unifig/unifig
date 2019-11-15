package etl.dispatch.java.ods.enums;

public enum EngineMessageTypes {

	/**
	 * 文本
	 */
	Text(1,"Text"),
	/**
	 * 文件
	 */
	File(2,"File"),
	/**
	 * 图片
	 */
	Image(3,"Image"),
	/**
	 * 短语音
	 */
	VoiceClip(4,"voiceClip"),
	/**
	 * 短视频
	 */
	VideoClip(5,"videoClip"),
	/**
	 * 卡片消息
	 */
	Card(6,"Card"),
	/**
	 * 历史消息
	 */
	History(7,"History"),
	/**
	 * 富文本消息
	 */
	Rich(8,"Rich");
	
	
	private int code;
    private String desc;
    
	public int getCode() {
		return code;
	}
	public void setCode(int code) {
		this.code = code;
	}
	public String getDesc() {
		return desc;
	}
	public void setDesc(String desc) {
		this.desc = desc;
	}
    
	EngineMessageTypes(int code, String desc) {
        this.code = code;
        this.desc = desc;
    }
	
}
