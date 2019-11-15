package etl.dispatch.java.spap.ods.domain;

import java.io.Serializable;

/**
 * 互联网服务提供商(IP)
 *
 *
 */
public class SpapDimIsp implements Serializable {
	private static final long serialVersionUID = -2576772118722160066L;

	private int id;

	private String name;

	public SpapDimIsp() {

	}
	public SpapDimIsp(int id, String name) {
		this.id = id;
		this.name = name;
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		try {
			SpapDimIsp cloned = (SpapDimIsp) super.clone();
			return cloned;
		} catch (CloneNotSupportedException e) {
			return new SpapDimIsp(this.id, this.name);
		}
	}
}
