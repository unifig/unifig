package etl.dispatch.java.spap.ods.domain;

import java.io.Serializable;

/**
 * 终端设备型号
 *
 */
public class SpapDimManufacturerModel implements Serializable{
	private static final long serialVersionUID = -7570774350002126014L;
	private int id;
	private int manufacturerId;
	private String name;
	private String nameLowercase;

	public SpapDimManufacturerModel() {

	}

	public SpapDimManufacturerModel(int id, int manufacturerId, String name) {
		this.id = id;
		this.manufacturerId = manufacturerId;
		this.name = name;
		this.nameLowercase = (name == null ? null : name.toLowerCase());
	}
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getManufacturerId() {
		return manufacturerId;
	}

	public void setManufacturerId(int manufacturerId) {
		this.manufacturerId = manufacturerId;
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
			SpapDimManufacturerModel cloned = (SpapDimManufacturerModel) super.clone();
			return cloned;
		} catch (CloneNotSupportedException e) {
			return new SpapDimManufacturerModel(this.id, this.manufacturerId, this.name);
		}
	}
}
