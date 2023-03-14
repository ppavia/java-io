package fr.cimut.ged.entrant.dto;

import java.util.Date;

import fr.cimut.ged.entrant.beans.db.Type;

public class EddocDto {

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

    private Boolean sefas;

    protected Boolean visibleExtranet;

    private Date dtDebutValidite;

    private Date dtFinValidite;

    private DocumentInfoCompDto infoStarweb;

    public String getId() {
        return id;
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
        return profilEditique;
    }

    public void setProfilEditique(int profilEditique) {
        this.profilEditique = profilEditique;
    }

    public String getCmroc() {
        return cmroc;
    }

    public void setCmroc(String cmroc) {
        this.cmroc = cmroc;
    }

    public String getTypepapier() {
        return typepapier;
    }

    public void setTypepapier(String typepapier) {
        this.typepapier = typepapier;
    }

    public String getService() {
        return service;
    }

    public void setService(String service) {
        this.service = service;
    }

    public String getUsercons() {
        return usercons;
    }

    public void setUsercons(String usercons) {
        this.usercons = usercons;
    }

    public String getUserdeleted() {
        return userdeleted;
    }

    public void setUserdeleted(String userdeleted) {
        this.userdeleted = userdeleted;
    }

    public long getDtarchdc() {
        return dtarchdc;
    }

    public void setDtarchdc(long dtarchdc) {
        this.dtarchdc = dtarchdc;
    }

    public long getDtconsdc() {
        return dtconsdc;
    }

    public void setDtconsdc(long dtconsdc) {
        this.dtconsdc = dtconsdc;
    }

    public Date getDtcreate() {
        return dtcreate;
    }

    public void setDtcreate(Date dtcreate) {
        this.dtcreate = dtcreate;
    }

    public Date getDtcons() {
        return dtcons;
    }

    public void setDtcons(Date dtcons) {
        this.dtcons = dtcons;
    }

    public Date getDtdeleted() {
        return dtdeleted;
    }

    public void setDtdeleted(Date dtdeleted) {
        this.dtdeleted = dtdeleted;
    }

    public long getIdstar() {
        return idstar;
    }

    public void setIdstar(long idstar) {
        this.idstar = idstar;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getMimeType() {
        return mimeType;
    }

    public void setMimeType(String mimeType) {
        this.mimeType = mimeType;
    }

    public String getLbchfp() {
        return lbchfp;
    }

    public void setLbchfp(String lbchfp) {
        this.lbchfp = lbchfp;
    }

    public String getLibelle() {
        return libelle;
    }

    public void setLibelle(String libelle) {
        this.libelle = libelle;
    }

    public String getLbnmff() {
        return lbnmff;
    }

    public void setLbnmff(String lbnmff) {
        this.lbnmff = lbnmff;
    }

    public String getOrigine() {
        return origine;
    }

    public void setOrigine(String origine) {
        this.origine = origine;
    }

    public String getSite() {
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public long getNbconsultation() {
        return nbconsultation;
    }

    public void setNbconsultation(long nbconsultation) {
        this.nbconsultation = nbconsultation;
    }

    public int getNbexdc() {
        return nbexdc;
    }

    public void setNbexdc(int nbexdc) {
        this.nbexdc = nbexdc;
    }

    public int getNbpage() {
        return nbpage;
    }

    public void setNbpage(int nbpage) {
        this.nbpage = nbpage;
    }

    public String getTypeDocument() {
        return typeDocument;
    }

    public void setTypeDocument(String typeDocument) {
        this.typeDocument = typeDocument;
    }

    public String getOraerr() {
        return oraerr;
    }

    public void setOraerr(String oraerr) {
        this.oraerr = oraerr;
    }

    public String getIsArchivage() {
        return isArchivage;
    }

    public void setIsArchivage(String isArchivage) {
        this.isArchivage = isArchivage;
    }

    public String getIsFonddepage() {
        return isFonddepage;
    }

    public void setIsFonddepage(String isFonddepage) {
        this.isFonddepage = isFonddepage;
    }

    public String getSidstar() {
        return sidstar;
    }

    public void setSidstar(String sidstar) {
        this.sidstar = sidstar;
    }

    public long getTsstar() {
        return tsstar;
    }

    public void setTsstar(long tsstar) {
        this.tsstar = tsstar;
    }

    public Boolean getSefas() {
        return sefas;
    }

    public void setSefas(Boolean sefas) {
        this.sefas = sefas;
    }

    public Boolean getVisibleExtranet() {
        return visibleExtranet;
    }

    public void setVisibleExtranet(Boolean visibleExtranet) {
        this.visibleExtranet = visibleExtranet;
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

    public DocumentInfoCompDto getInfoStarweb() {
        return infoStarweb;
    }

    public void setInfoStarweb(DocumentInfoCompDto infoStarweb) {
        this.infoStarweb = infoStarweb;
    }
    
    public String getEddocId() {
		StringBuilder eddocId = new StringBuilder();
		eddocId.append(this.idstar).append('_').append(this.tsstar);
		return eddocId.toString();
	}
}
