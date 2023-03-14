package fr.cimut.ged.entrant.dto;

public class DocumentRestDto {

	private String id;

    private String cmroc;

    private String typeDocument;

    private String dtcreate;
    
    private String libelle;
    
    private String origine;
    
    private int nbpage;
    
    private String typepapier;
    
    private String isFonddepage;

    private String service;

    private long nbconsultation;

    private String isArchivage;

    private int nbexdc;

    private String site;

    private String lbchfp;

    private String lbnmff;

    private int profilEditique;

    private long idstar;

    private long tsstar;
    
    private long dtconsdc;
    
    private long dtarchdc;
    
    private String sidstar;
    
    private String mimeType;
    
    private JsonDto json;
    
    private Boolean sefas;
    
    private String dtDebutValidite;
    
    private String dtFinValidite;
    
    private String dtcons;
    private String userdeleted;
    private String dtdeleted;
    private String status;
    private String usercons;
    private String oraerr;
    protected Boolean visibleExtranet;
    private DocumentInfoCompDto infoStarweb;
  
   	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getCmroc() {
		return cmroc;
	}

	public void setCmroc(String cmroc) {
		this.cmroc = cmroc;
	}

	public String getTypeDocument() {
		return typeDocument;
	}

	public void setTypeDocument(String typeDocument) {
		this.typeDocument = typeDocument;
	}

	public String getDtcreate() {
		return dtcreate;
	}

	public void setDtcreate(String dtcreate) {
		this.dtcreate = dtcreate;
	}

	public String getLibelle() {
		return libelle;
	}

	public void setLibelle(String libelle) {
		this.libelle = libelle;
	}

	public String getOrigine() {
		return origine;
	}

	public void setOrigine(String origine) {
		this.origine = origine;
	}

	public int getNbpage() {
		return nbpage;
	}

	public void setNbpage(int nbpage) {
		this.nbpage = nbpage;
	}

	public String getTypepapier() {
		return typepapier;
	}

	public void setTypepapier(String typepapier) {
		this.typepapier = typepapier;
	}

	public String getIsFonddepage() {
		return isFonddepage;
	}

	public void setIsFonddepage(String isFonddepage) {
		this.isFonddepage = isFonddepage;
	}

	public String getService() {
		return service;
	}

	public void setService(String service) {
		this.service = service;
	}

	public long getNbconsultation() {
		return nbconsultation;
	}

	public void setNbconsultation(long nbconsultation) {
		this.nbconsultation = nbconsultation;
	}

	public String getIsArchivage() {
		return isArchivage;
	}

	public void setIsArchivage(String isArchivage) {
		this.isArchivage = isArchivage;
	}



	public int getNbexdc() {
		return nbexdc;
	}

	public void setNbexdc(int nbexdc) {
		this.nbexdc = nbexdc;
	}

	public String getSite() {
		return site;
	}

	public void setSite(String site) {
		this.site = site;
	}

	public String getLbchfp() {
		return lbchfp;
	}

	public void setLbchfp(String lbchfp) {
		this.lbchfp = lbchfp;
	}

	public String getLbnmff() {
		return lbnmff;
	}

	public void setLbnmff(String lbnmff) {
		this.lbnmff = lbnmff;
	}

	public int getProfilEditique() {
		return profilEditique;
	}

	public void setProfilEditique(int profilEditique) {
		this.profilEditique = profilEditique;
	}

	public long getIdstar() {
		return idstar;
	}

	public void setIdstar(long idstar) {
		this.idstar = idstar;
	}

	public long getTsstar() {
		return tsstar;
	}

	public void setTsstar(long tsstar) {
		this.tsstar = tsstar;
	}

	public long getDtconsdc() {
		return dtconsdc;
	}

	public void setDtconsdc(long dtconsdc) {
		this.dtconsdc = dtconsdc;
	}

	public long getDtarchdc() {
		return dtarchdc;
	}

	public void setDtarchdc(long dtarchdc) {
		this.dtarchdc = dtarchdc;
	}

	public String getSidstar() {
		return sidstar;
	}

	public void setSidstar(String sidstar) {
		this.sidstar = sidstar;
	}

	public String getMimeType() {
		return mimeType;
	}

	public void setMimeType(String mimeType) {
		this.mimeType = mimeType;
	}

	public JsonDto getJson() {
		return json;
	}

	public void setJson(JsonDto json) {
		this.json = json;
	}

	public Boolean getSefas() {
		return sefas;
	}

	public void setSefas(Boolean sefas) {
		this.sefas = sefas;
	}

	public String getDtDebutValidite() {
		return dtDebutValidite;
	}

	public void setDtDebutValidite(String dtDebutValidite) {
		this.dtDebutValidite = dtDebutValidite;
	}

	public String getDtFinValidite() {
		return dtFinValidite;
	}

	public void setDtFinValidite(String dtFinValidite) {
		this.dtFinValidite = dtFinValidite;
	}

	public String getDtcons() {
		return dtcons;
	}

	public void setDtcons(String dtcons) {
		this.dtcons = dtcons;
	}

	public String getUserdeleted() {
		return userdeleted;
	}

	public void setUserdeleted(String userdeleted) {
		this.userdeleted = userdeleted;
	}

	public String getDtdeleted() {
		return dtdeleted;
	}

	public void setDtdeleted(String dtdeleted) {
		this.dtdeleted = dtdeleted;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public String getUsercons() {
		return usercons;
	}

	public void setUsercons(String usercons) {
		this.usercons = usercons;
	}

	public String getOraerr() {
		return oraerr;
	}

	public void setOraerr(String oraerr) {
		this.oraerr = oraerr;
	}

	public Boolean getVisibleExtranet() {
		return visibleExtranet;
	}

	public void setVisibleExtranet(Boolean visibleExtranet) {
		this.visibleExtranet = visibleExtranet;
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
