
package fr.cimut.ged.entrant.beans.db;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * 
 * <p>
 * les types de documents
 * </p>
 * 
 * @author jlebourgocq
 * 
 */

public class Type implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -4194914970737821175L;

	private long id;

	private Categorie categorie;

	private String code;

	private String libelle;

	private TypeEntrantSortant typeEntrantSortant;

	protected boolean visibleExtranet;

	protected Integer dureeVisibleExtranet;

	protected boolean notifExtranet;

	protected Integer dureeArchivage;

	protected Integer dureePurge;

	protected boolean integrableIdoc;

	private String userMaj;

	private Date dateMaj;

	/** les documents erd ref doc. */
	private List<Document> documents = new ArrayList<Document>();

	/** les surcharges. */
	private List<Document> typesSurcharge = new ArrayList<Document>();

	public Type() {
	}

	public Categorie getCategorie() {
		return categorie;
	}

	public void setCategorie(Categorie categorie) {
		this.categorie = categorie;
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

	public TypeEntrantSortant getTypeEntrantSortant() {
		return typeEntrantSortant;
	}

	public void setTypeEntrantSortant(TypeEntrantSortant typeEntrantSortant) {
		this.typeEntrantSortant = typeEntrantSortant;
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

	public List<Document> getDocuments() {
		return documents;
	}

	public void setDocuments(List<Document> documents) {
		this.documents = documents;
	}

	public List<Document> getTypesSurcharge() {
		return typesSurcharge;
	}

	public void setTypesSurcharge(List<Document> typesSurcharge) {
		this.typesSurcharge = typesSurcharge;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}