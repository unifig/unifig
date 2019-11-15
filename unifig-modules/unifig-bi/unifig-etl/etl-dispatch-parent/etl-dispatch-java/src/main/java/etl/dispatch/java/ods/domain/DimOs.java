package etl.dispatch.java.ods.domain;

import java.io.Serializable;

/**
 * 终端操作系统
 *
 */
public class DimOs implements Serializable {
	private static final long serialVersionUID = -6704487614598739125L;
	private short id;
	private String name;

	public DimOs() {

	}

	public DimOs(short id, String name) {
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

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		try {
			DimOs cloned = (DimOs) super.clone();
			return cloned;
		} catch (CloneNotSupportedException e) {
			return new DimOs(this.id, this.name);
		}
	}
}
