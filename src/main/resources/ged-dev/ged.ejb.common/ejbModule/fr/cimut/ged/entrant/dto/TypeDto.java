package fr.cimut.ged.entrant.dto;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import java.io.Serializable;
import java.util.Date;

@JsonIgnoreProperties(ignoreUnknown = true)
public class TypeDto implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7332243159359576540L;

	private long id;

	private String code;

	private String libelle;

	protected boolean visibleExtranet;

	protected Integer dureeVisibleExtranet;

	protected boolean notifExtranet;

	protected Integer dureeArchivage;

	protected Integer dureePurge;

	protected boolean integrableIdoc;

	private String userMaj;

	private Date dateMaj;

	private String paramsUserMaj;

	private Date paramsDateMaj;

	private CategoryDto categorie;

	private String typeEntrantSortant;

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
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

	public boolean isVisibleExtranet() {
		return visibleExtranet;
	}

	public void setVisibleExtranet(boolean visibleExtranet) {
		this.visibleExtranet = visibleExtranet;
	}

	public Integer getDureeVisibleExtranet() {
		return dureeVisibleExtranet;
	}

	public void setDureeVisibleExtranet(Integer dureeVisibleExtranet) {
		this.dureeVisibleExtranet = dureeVisibleExtranet;
	}

	public boolean isNotifExtranet() {
		return notifExtranet;
	}

	public void setNotifExtranet(boolean notifExtranet) {
		this.notifExtranet = notifExtranet;
	}

	public Integer getDureeArchivage() {
		return dureeArchivage;
	}

	public void setDureeArchivage(Integer dureeArchivage) {
		this.dureeArchivage = dureeArchivage;
	}

	public Integer getDureePurge() {
		return dureePurge;
	}

	public void setDureePurge(Integer dureePurge) {
		this.dureePurge = dureePurge;
	}

	public boolean isIntegrableIdoc() {
		return integrableIdoc;
	}

	public void setIntegrableIdoc(boolean integrableIdoc) {
		this.integrableIdoc = integrableIdoc;
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

	public String getParamsUserMaj() {
		return paramsUserMaj;
	}

	public void setParamsUserMaj(String paramsUserMaj) {
		this.paramsUserMaj = paramsUserMaj;
	}

	public Date getParamsDateMaj() {
		return paramsDateMaj;
	}

	public void setParamsDateMaj(Date paramsDateMaj) {
		this.paramsDateMaj = paramsDateMaj;
	}

	public CategoryDto getCategorie() { return categorie; }

	public void setCategorie(CategoryDto categorie) { this.categorie = categorie; }

	public String getTypeEntrantSortant() {
		return typeEntrantSortant;
	}

	public void setTypeEntrantSortant(String typeEntrantSortant) {
		this.typeEntrantSortant = typeEntrantSortant;
	}


}
