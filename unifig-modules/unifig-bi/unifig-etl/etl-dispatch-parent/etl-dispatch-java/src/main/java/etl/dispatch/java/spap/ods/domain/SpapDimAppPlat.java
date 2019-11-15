package etl.dispatch.java.spap.ods.domain;

import java.io.Serializable;

/**
 * 平台应用类型
 * web、android、ios、macos、windows
 *
 */
public class SpapDimAppPlat implements Serializable {
	private static final long serialVersionUID = -9001714109731044942L;
	private short id;
	private String name;

	public SpapDimAppPlat() {

	}

	public SpapDimAppPlat(short id, String name) {
		this.id = id;
		this.name = name;
	}

	public short getId() {
		return id;
	}

	public void setId(short id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@Override
	public Object clone() {
		try {
			SpapDimAppPlat cloned = (SpapDimAppPlat) super.clone();
			return cloned;
		} catch (CloneNotSupportedException e) {
			return new SpapDimAppPlat(this.id, this.name);
		}
	}
}
