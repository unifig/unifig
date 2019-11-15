package etl.dispatch.java.ods.domain;

import java.io.Serializable;

/**
 * 终端平台厂商
 *
 */
public class DimManufacturer implements Serializable {
	private static final long serialVersionUID = 183998304256122183L;
	private int id;
	private String name;
	private String nameLowercase;

	public DimManufacturer() {

	}

	public DimManufacturer(int id, String name) {
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
			DimManufacturer cloned = (DimManufacturer) super.clone();
			return cloned;
		} catch (CloneNotSupportedException e) {
			return new DimManufacturer(this.id, this.name);
		}
	}

}
