
package fr.cimut.ged.entrant.beans.db;

import java.io.Serializable;
import java.util.Date;

/**
 * 
 * <p>
 * The persistent class for the ERD_REFDOC database table.
 * </p>
 * 
 * @author gyclon
 * 
 */

public class Historique implements Serializable {
	private static final long serialVersionUID = 1L;

	private long id;

	private String docId;

	private String modification;

	private String userMaj;

	private Date dateMaj;

	public Historique() {

	}

	public long getId() {
		return this.id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getModification() {
		return modification;
	}

	public void setModification(String modification) {
		this.modification = modification;
	}

	public String getUserMaj() {
		return userMaj;
	}

	public void setUserMaj(String userMaj) {
		this.userMaj = userMaj;
	}

	public Date getDateMaj() {
		return dateMaj;
	}

	public void setDateMaj(Date dateMaj) {
		this.dateMaj = dateMaj;
	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

}