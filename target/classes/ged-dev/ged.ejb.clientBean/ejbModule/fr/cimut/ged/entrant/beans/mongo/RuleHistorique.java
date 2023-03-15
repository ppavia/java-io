package fr.cimut.ged.entrant.beans.mongo;

import java.io.Serializable;
import java.util.Date;

public class RuleHistorique implements Serializable, GenericMgDbBean {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String _id;

	private String id;

	private String cmroc;

	private String user;

	private Date dateModif;

	private String object;

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	public String getCmroc() {
		return cmroc;
	}

	public void setCmroc(String cmroc) {
		this.cmroc = cmroc;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public Date getDateModif() {
		return dateModif;
	}

	public void setDateModif(Date dateModif) {
		this.dateModif = dateModif;
	}

	public String getObject() {
		return object;
	}

	public void setObject(String object) {
		this.object = object;
	}

	public String get_id() {
		return _id;
	}

	public void set_id(String id) {
		this._id = id;
	}

}
