package fr.cimut.ged.entrant.beans.mongo;

import java.io.Serializable;
import java.util.Arrays;

import org.apache.commons.lang3.StringUtils;

public class RuleDa implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String type;
	private String categorie;
	private String support;
	private String sujet;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSupport() {
		return support;
	}

	public void setSupport(String support) {
		this.support = support;
	}

	public String getSujet() {
		return sujet;
	}

	public void setSujet(String sujet) {
		this.sujet = sujet;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		RuleDa other = (RuleDa) obj;

		if (id != null && other.id == null) {
			return false;
		} else if (id == null && other.id != null) {
			return false;
		} else if (id == null && other.id == null) {
			return true;
		}

		if (!id.equals(other.id)) {
			return false;
		}
		if (!name.equals(other.name)) {
			return false;
		}
		if (!support.equals(other.support)) {
			return false;
		}
		if (!sujet.equals(other.sujet)) {
			return false;
		}
		if (!type.equals(other.type)) {
			return false;
		}
		if (!StringUtils.equals(categorie, other.categorie)) {
			return false;
		}
		return true;
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(new String[] { id, name, type, categorie, support, sujet });
	}

	public String getCategorie() {
		return categorie;
	}

	public void setCategorie(String categorie) {
		this.categorie = categorie;
	}

}
