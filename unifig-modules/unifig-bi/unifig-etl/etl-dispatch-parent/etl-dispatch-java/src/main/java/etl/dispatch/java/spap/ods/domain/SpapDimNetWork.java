package etl.dispatch.java.spap.ods.domain;

import java.io.Serializable;

/**
 * 网络类型
 *
 */
public class SpapDimNetWork implements Serializable {
	private static final long serialVersionUID = -6704487614598739125L;
	private short id;
	private String name;

	public SpapDimNetWork() {

	}

	public SpapDimNetWork(short id, String name) {
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
			SpapDimNetWork cloned = (SpapDimNetWork) super.clone();
			return cloned;
		} catch (CloneNotSupportedException e) {
			return new SpapDimNetWork(this.id, this.name);
		}
	}
}
