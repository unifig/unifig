package etl.dispatch.java.ods.domain;

import java.io.Serializable;

/**
 * 工作行业
 *
 *
 */
public class DimIndustry  implements Serializable {
	private static final long serialVersionUID = 8458212009848123947L;
	private short id;
	private String name;

	public DimIndustry() {

	}

	public DimIndustry(short id, String name) {
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
			DimIndustry cloned = (DimIndustry) super.clone();
			return cloned;
		} catch (CloneNotSupportedException e) {
			return new DimIndustry(this.id, this.name);
		}
	}
}
