
package fr.cimut.ged.entrant.beans.db;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.*;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.joda.time.LocalDate;

import fr.cimut.ged.entrant.beans.mongo.DocumentMongo;
import fr.cimut.ged.entrant.beans.starwebdao.DocumentInfoComp;
import fr.cimut.ged.entrant.utils.DateHelper;

/**
 * 
 * <p>
 * The persistent class for the ERD_REFDOC database table.
 * </p>
 * 
 * @author gyclon
 * 
 */

@Entity
@Table(name = "ERD_REFDOC")
//@formatter:off 
@NamedQueries({ 
	@NamedQuery(name = "Document.findByEddocId", query =
			" select d from Document d " + 
			" where d.idstar = :idstar and d.tsstar = :tsstar"),
	@NamedQuery(name = "Document.deleteByEddocId", query =
			" delete from Document d " + 
			" where d.idstar = :idstar and d.tsstar = :tsstar"),
	@NamedQuery(name = "Document.countDocByIdType", query =
			" select count(d) from Document d " + 
			" where d.type.id = :idType "),
	
	@NamedQuery(name = "Document.findByEddocIds",	query = 
			" select d from Document d " +
			" where d.idstar in (:idstarList) " +
			" and d.tsstar in (:tsstarList) " +
			" order by d.dtcreate desc"),

	@NamedQuery(name = "Document.findByEddocIdstar",	query =
			" select d from Document d " +
					" where d.idstar in (:idstarList) " +
					" order by d.dtcreate desc")
})

//@formatter:on 
public class Document implements Serializable {
	private static final long serialVersionUID = 1L;

	@Transient
	private DocumentMongo docMongo;

	@Id
	@Column(name = "ERD_LBNMFD")
	private String id;

	@ManyToOne
	@JoinColumn(name = "ERD_REFTYPE_FK", nullable = true)
	private Type type;

	@Column(name = "ERD_CDCONF")
	private int profilEditique;

	@Column(name = "ERD_CDCORG")
	private String cmroc;

	@Column(name = "ERD_CDPAPI")
	private String typepapier;

	@Column(name = "ERD_CDSERV")
	private String service;

	@Column(name = "ERD_CDUSCS")
	private String usercons;

	@Column(name = "ERD_CDUSSL")
	private String userdeleted;

	@Column(name = "ERD_DTARCHDC")
	private long dtarchdc;

	@Column(name = "ERD_DTCONSDC")
	private long dtconsdc;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ERD_DTCRDC")
	private Date dtcreate;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ERD_DTCSDC")
	private Date dtcons;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "ERD_DTSLDC")
	private Date dtdeleted;

	@Column(name = "ERD_IDSTAR")
	private long idstar;

	@Column(name = "ERD_STATUT")
	private String status;

	@Column(name = "ERD_MIMETYPE")
	private String mimeType;

	@Column(name = "ERD_LBCHFP")
	private String lbchfp;

	@Column(name = "ERD_LBDCTP")
	private String libelle;

	@Column(name = "ERD_LBNMFF")
	private String lbnmff;

	@Column(name = "ERD_LBORDC")
	private String origine;

	@Column(name = "ERD_LBSITE")
	private String site;

	@Column(name = "ERD_NBCSDC")
	private long nbconsultation;

	@Column(name = "ERD_NBEXDC")
	private int nbexdc;

	@Column(name = "ERD_NBPGDC")
	private int nbpage;

	@Column(name = "ERD_NODCTP")
	private String typeDocument;

	@Column(name = "ERD_ORAERR")
	private String oraerr;

	@Column(name = "ERD_PRCPAR")
	private String isArchivage;

	@Column(name = "ERD_PRFDPG")
	private String isFonddepage;

	@Column(name = "ERD_SIDSTAR")
	private String sidstar;

	@Column(name = "ERD_TSSTAR")
	private long tsstar;

	@Column(name = "ERD_SEFAS")
	private Boolean sefas;

	@Column(name = "ERD_VISU_EXT")
	protected Boolean visibleExtranet;
	
	@Column(name = "ERD_IDEXT")
	protected String identifiantExterne;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="ERD_DTDE")
	private Date dtDebutValidite;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name="ERD_DTFI")
	private Date dtFinValidite;

	private transient DocumentInfoComp infoStarweb;

	@OneToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, fetch = FetchType.EAGER)
	@PrimaryKeyJoinColumn
	private Json json;

	public Document() {
		this.docMongo = new DocumentMongo();
		this.json = new Json();
	}

	@JsonIgnore
	public DocumentMongo getDocMongo() {
		return docMongo;
	}

	public void setDocMongo(DocumentMongo docMongo) {
		this.docMongo = docMongo;
	}

	public String getId() {
		return this.id;
	}

	public String getEddocId() {
		StringBuilder eddocId = new StringBuilder();
		eddocId.append(this.idstar).append('_').append(this.tsstar);
		return eddocId.toString();
	}

	public DocumentInfoComp getInfoStarweb() {
		return infoStarweb;
	}

	public void setInfoStarweb(DocumentInfoComp infoStarweb) {
		this.infoStarweb = infoStarweb;
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

	public void setTypeDocument(String typedocument) {
		this.typeDocument = typedocument;
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

	@JsonIgnore
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
	
	public String getIdentifiantExterne() {
		return identifiantExterne;
	}

	public void setIdentifiantExterne(String identifiantExterne) {
		this.identifiantExterne = identifiantExterne;
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

	@JsonIgnore
	@Deprecated
	public boolean isVisibleExtranetConsideringTypeAndDateOld() {
		return (!Boolean.FALSE.equals(visibleExtranet) && (Boolean.TRUE.equals(visibleExtranet) || !Boolean.FALSE.equals(type.isVisibleExtranet()))
				&& (null == dtcreate || null == type.getDureeVisibleExtranet()
						|| LocalDate.now().isBefore(DateHelper.dateToLocalDate(dtcreate).plusDays(type.getDureeVisibleExtranet()))));
	}
	
	@JsonIgnore
	@Deprecated
	public boolean isVisibleExtranetConsideringTypeAndDate() {
		return (
				(Boolean.TRUE.equals(visibleExtranet) || (type != null && !Boolean.FALSE.equals(type.isVisibleExtranet())))
				&& (
						null == dtcreate || 
						null == type.getDureeVisibleExtranet() || 
						LocalDate.now().isBefore(DateHelper.dateToLocalDate(dtcreate).plusDays(type.getDureeVisibleExtranet()))
					)	
				);
	}

	public static Document clone(Document document) {
		Document clone = new Document();
		clone.setDocMongo(document.getDocMongo());
		clone.setId(document.getId());
		clone.setType(document.getType());
		clone.setProfilEditique(document.getProfilEditique());
		clone.setCmroc(document.getCmroc());
		clone.setTypepapier(document.getTypepapier());
		clone.setService(document.getService());
		clone.setUsercons(document.getUsercons());
		clone.setUserdeleted(document.getUserdeleted());
		clone.setDtarchdc(document.getDtarchdc());
		clone.setDtconsdc(document.getDtconsdc());
		clone.setDtcreate(document.getDtcreate());
		clone.setDtcons(document.getDtcons());
		clone.setDtdeleted(document.getDtdeleted());
		clone.setIdstar(document.getIdstar());
		clone.setStatus(document.getStatus());
		clone.setMimeType(document.getMimeType());
		clone.setLbchfp(document.getLbchfp());
		clone.setLibelle(document.getLibelle());
		clone.setLbnmff(document.getLbnmff());
		clone.setOrigine(document.getOrigine());
		clone.setSite(document.getSite());
		clone.setNbconsultation(document.getNbconsultation());
		clone.setNbexdc(document.getNbexdc());
		clone.setNbpage(document.getNbpage());
		clone.setTypeDocument(document.getTypeDocument());
		clone.setOraerr(document.getOraerr());
		clone.setIsArchivage(document.getIsArchivage());
		clone.setIsFonddepage(document.getIsFonddepage());
		clone.setSidstar(document.getSidstar());
		clone.setTsstar(document.getTsstar());
		clone.setSefas(document.getSefas());
		clone.setVisibleExtranet(document.getVisibleExtranet());
		clone.setIdentifiantExterne(document.getIdentifiantExterne());
		clone.setInfoStarweb(document.getInfoStarweb());
		clone.setJson(document.getJson());
		clone.setDtDebutValidite(document.getDtDebutValidite());
		clone.setDtFinValidite(document.getDtFinValidite());
		return clone;
	}

}