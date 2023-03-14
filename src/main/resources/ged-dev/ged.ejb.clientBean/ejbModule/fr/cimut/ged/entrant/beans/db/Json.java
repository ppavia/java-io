
package fr.cimut.ged.entrant.beans.db;

import java.io.Serializable;

/**
 * 
 * <p>
 * The persistent class for the ERD_REFDOC database table.
 * </p>
 * 
 * @author gyclon
 * 
 */

public class Json implements Serializable {
	private static final long serialVersionUID = 1L;

	private String id;

	private String data;

	private String organisme;

	public Json() {

	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getData() {
		return data;
	}

	public void setData(String data) {
		this.data = data;
	}

	public String getOrganisme() {
		return organisme;
	}

	public void setOrganisme(String organisme) {
		this.organisme = organisme;
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		Json cloned = (Json) super.clone();
		return cloned;
	}

}