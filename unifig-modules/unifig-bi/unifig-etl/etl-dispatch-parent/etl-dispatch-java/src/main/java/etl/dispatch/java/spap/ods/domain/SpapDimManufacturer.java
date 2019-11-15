package etl.dispatch.java.spap.ods.domain;

import java.io.Serializable;

/**
 * 终端平台厂商
 *
 */
public class SpapDimManufacturer implements Serializable {
	private static final long serialVersionUID = 183998304256122183L;
	private int id;
	private String name;
	private String nameLowercase;

	public SpapDimManufacturer() {

	}

	public SpapDimManufacturer(int id, String name) {
		this.id = id;
		this.name = name;
		this.nameLowercase = (name == null ? null : name.toLowerCase());
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

	public String getNameLowercase() {
		return nameLowercase;
	}

	public void setNameLowercase(String nameLowercase) {
		this.nameLowercase = nameLowercase;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#clone()
	 */
	@Override
	public Object clone() {
		try {
			SpapDimManufacturer cloned = (SpapDimManufacturer) super.clone();
			return cloned;
		} catch (CloneNotSupportedException e) {
			return new SpapDimManufacturer(this.id, this.name);
		}
	}

}
