package etl.dispatch.java.spap.ods.domain;

import java.io.Serializable;

/**
 * 终端操作系统
 *
 */
public class SpapDimOs implements Serializable {
	private static final long serialVersionUID = -6704487614598739125L;
	private short id;
	private String name;

	public SpapDimOs() {

	}

	public SpapDimOs(short id, String name) {
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
			SpapDimOs cloned = (SpapDimOs) super.clone();
			return cloned;
		} catch (CloneNotSupportedException e) {
			return new SpapDimOs(this.id, this.name);
		}
	}
}
