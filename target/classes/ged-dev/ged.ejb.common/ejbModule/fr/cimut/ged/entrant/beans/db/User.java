
package fr.cimut.ged.entrant.beans.db;

import java.io.Serializable;
import java.util.List;

/**
 * 
 * <p>
 * The persistent class for the ERD_REFDOC database table.
 * </p>
 * 
 * @author gyclon
 * 
 */

public class User implements Serializable {
	private static final long serialVersionUID = 1L;

	private String login;
	private String cmroc;
	private List<String> cmrocs;
	private String droitCmsi;
	private String levelConfidentiel;

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getCmroc() {
		return cmroc;
	}

	public void setCmroc(String cmroc) {
		this.cmroc = cmroc;
	}

	public List<String> getCmrocs() {
		return cmrocs;
	}

	public void setCmrocs(List<String> cmrocs) {
		this.cmrocs = cmrocs;
	}

	public String getDroitCmsi() {
		return droitCmsi;
	}

	public void setDroitCmsi(String droitCmsi) {
		this.droitCmsi = droitCmsi;
	}

	public int getLevelConfidentiel() {
		int level = 0;
		try {
			level = Integer.parseInt(levelConfidentiel);
		} catch (Exception e) {
		}
		return level;
	}

	public void setLevelConfidentiel(String levelConfidentiel) {
		this.levelConfidentiel = levelConfidentiel;
	}

}