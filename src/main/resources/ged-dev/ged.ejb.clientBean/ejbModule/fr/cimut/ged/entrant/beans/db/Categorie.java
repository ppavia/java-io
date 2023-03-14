
package fr.cimut.ged.entrant.beans.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 
 * <p>
 * les categories de documents - pouvant contenir des regroupements de type
 * </p>
 * 
 * @author jlebourgocq
 * 
 */

public class Categorie implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4194914970737821175L;

	private long id;

	private String code;

	private String libelle;

	private String userMaj;

	private Date dateMaj;

	private List<Type> types = new ArrayList<Type>();

	public Categorie() {
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
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

	public List<Type> getTypes() {
		return types;
	}

	public void setTypes(List<Type> types) {
		this.types = types;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}