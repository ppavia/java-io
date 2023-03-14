
package fr.cimut.ged.entrant.beans.db;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Lob;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.Table;

/**
 * 
 * <p>
 * The persistent class for the ERD_REFDOC database table.
 * </p>
 * 
 * @author gyclon
 * 
 */

@Entity
@Table(name = "ERD_REFJSONGED")
@NamedQueries({ @NamedQuery(name = "Json.rebuild", query = "select e from  Json e where  e.organisme = :organisme") })
public class Json implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "ERD_LBNMFD")
	private String id;

	@Lob
	@Column(name = "ERD_JSON")
	private String data;

	@Column(name = "ERD_ORGA")
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