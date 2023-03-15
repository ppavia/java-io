
package fr.cimut.ged.entrant.beans.db;

import java.io.Serializable;
import java.util.Date;

public class Document implements Serializable {
	private static final long serialVersionUID = 1L;

	private String id;

	private Type type;

	private int profilEditique;

	private String cmroc;

	private String typepapier;

	private String service;

	private String usercons;

	private String userdeleted;

	private long dtarchdc;

	private long dtconsdc;

	private Date dtcreate;

	private Date dtcons;

	private Date dtdeleted;

	private long idstar;

	private String status;

	private String mimeType;

	private String lbchfp;

	private String libelle;

	private String lbnmff;

	private String origine;

	private String site;

	private long nbconsultation;

	private int nbexdc;

	private int nbpage;

	private String typeDocument;

	private String oraerr;

	private String isArchivage;

	private String isFonddepage;

	private String sidstar;

	private long tsstar;

	private boolean sefas;

	protected Boolean visibleExtranet;

	private Json json;

	private String eddocId;

	private String infoStarweb;
	
	private Date dtDebutValidite;
	
	private Date dtFinValidite;

	public Document() {
		this.json = new Json();
	}

	public String getId() {
		return this.id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		this.type = type;
	}

	public int getProfilEditique() {
		return this.profilEditique;
	}

	public void setProfilEditique(int profilEditique) {
		this.profilEditique = profilEditique;
	}

	public String getCmroc() {
		return this.cmroc;
	}

	public void setCmroc(String cmroc) {
		this.cmroc = cmroc;
	}

	public String getTypepapier() {
		return this.typepapier;
	}

	public void setTypepapier(String typepapier) {
		this.typepapier = typepapier;
	}

	public String getService() {
		return this.service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public String getUsercons() {
		return this.usercons;
	}

	public void setUsercons(String usercons) {
		this.usercons = usercons;
	}

	public String getUserdeleted() {
		return this.userdeleted;
	}

	public void setUserdeleted(String userdeleted) {
		this.userdeleted = userdeleted;
	}

	public long getDtarchdc() {
		return this.dtarchdc;
	}

	public void setDtarchdc(long dtarchdc) {
		this.dtarchdc = dtarchdc;
	}

	public long getDtconsdc() {
		return this.dtconsdc;
	}

	public void setDtconsdc(long dtconsdc) {
		this.dtconsdc = dtconsdc;
	}

	public Date getDtcreate() {
		return this.dtcreate;
	}

	public void setDtcreate(Date dtcreate) {
		this.dtcreate = dtcreate;
	}

	public Date getDtcons() {
		return this.dtcons;
	}

	public void setDtcons(Date dtcons) {
		this.dtcons = dtcons;
	}

	public Date getDtdeleted() {
		return this.dtdeleted;
	}

	public void setDtdeleted(Date dtdeleted) {
		this.dtdeleted = dtdeleted;
	}

	public long getIdstar() {
		return this.idstar;
	}

	public void setIdstar(long idstar) {
		this.idstar = idstar;
	}

	public String getMimeType() {
		return this.mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public String getLibelle() {
		return this.libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public String getLbnmff() {
		return this.lbnmff;
	}

	public void setLbnmff(String lbnmff) {
		this.lbnmff = lbnmff;
	}

	public String getOrigine() {
		return this.origine;
	}

	public void setOrigine(String origine) {
		this.origine = origine;
	}

	public String getSite() {
		return this.site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public long getNbconsultation() {
		return this.nbconsultation;
	}

	public void setNbconsultation(long nbconsultation) {
		this.nbconsultation = nbconsultation;
	}

	public int getNbexdc() {
		return this.nbexdc;
	}

	public void setNbexdc(int nbexdc) {
		this.nbexdc = nbexdc;
	}

	public int getNbpage() {
		return this.nbpage;
	}

	public void setNbpage(int nbpage) {
		this.nbpage = nbpage;
	}

	public String getTypeDocument() {
		return this.typeDocument;
	}

	public void setTypeDocument(String typeDocument) {
		this.typeDocument = typeDocument;
	}

	public String getOraerr() {
		return this.oraerr;
	}

	public void setOraerr(String oraerr) {
		this.oraerr = oraerr;
	}

	public String getIsArchivage() {
		return this.isArchivage;
	}

	public void setIsArchivage(String isArchivage) {
		this.isArchivage = isArchivage;
	}

	public String getIsFonddepage() {
		return this.isFonddepage;
	}

	public void setIsFonddepage(String isFonddepage) {
		this.isFonddepage = isFonddepage;
	}

	public String getSidstar() {
		return this.sidstar;
	}

	public void setSidstar(String sidstar) {
		this.sidstar = sidstar;
	}

	public long getTsstar() {
		return this.tsstar;
	}

	public void setTsstar(long tsstar) {
		this.tsstar = tsstar;
	}

	public Json getJson() {
		return this.json;
	}

	public void setJson(Json json) {
		this.json = json;
	}

	public String getLbchfp() {
		return this.lbchfp;
	}

	public void setLbchfp(String lbchfp) {
		this.lbchfp = lbchfp;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public boolean isSefas() {
		return sefas;
	}

	public void setSefas(boolean sefas) {
		this.sefas = sefas;
	}

	public Boolean getVisibleExtranet() {
		return visibleExtranet;
	}

	public void setVisibleExtranet(Boolean visibleExtranet) {
		this.visibleExtranet = visibleExtranet;
	}

	public String getEddocId() {
		return eddocId;
	}

	public void setEddocId(String eddocId) {
		this.eddocId = eddocId;
	}

	public String getInfoStarweb() {
		return infoStarweb;
	}

	public void setInfoStarweb(String infoStarweb) {
		this.infoStarweb = infoStarweb;
	}

	public Date getDtDebutValidite() {
		return dtDebutValidite;
	}

	public void setDtDebutValidite(Date dtDebutValidite) {
		this.dtDebutValidite = dtDebutValidite;
	}

	public Date getDtFinValidite() {
		return dtFinValidite;
	}

	public void setDtFinValidite(Date dtFinValidite) {
		this.dtFinValidite = dtFinValidite;
	}
}