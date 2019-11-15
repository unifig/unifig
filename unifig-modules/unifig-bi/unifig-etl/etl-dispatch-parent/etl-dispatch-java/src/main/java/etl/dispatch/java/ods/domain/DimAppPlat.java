package etl.dispatch.java.ods.domain;

import java.io.Serializable;

/**
 * 平台应用类型
 * web、android、ios、macos、windows
 *
 */
public class DimAppPlat implements Serializable {
	private static final long serialVersionUID = -9001714109731044942L;
	private short id;
	private String name;

	public DimAppPlat() {

	}

	public DimAppPlat(short id, String name) {
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
			DimAppPlat cloned = (DimAppPlat) super.clone();
			return cloned;
		} catch (CloneNotSupportedException e) {
			return new DimAppPlat(this.id, this.name);
		}
	}
}
