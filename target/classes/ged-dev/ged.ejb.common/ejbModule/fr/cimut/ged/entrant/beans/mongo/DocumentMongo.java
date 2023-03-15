package fr.cimut.ged.entrant.beans.mongo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonAnySetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;

import fr.cimut.ged.entrant.beans.TypeEntite;
import fr.cimut.ged.entrant.utils.BsonDateDeserializer;
import fr.cimut.ged.entrant.utils.BsonDateSerializer;
import fr.cimut.ged.entrant.utils.GlobalVariable;
import fr.cimut.ged.entrant.utils.NoObjectIdSerializer;

@JsonInclude(Include.NON_NULL)
public class DocumentMongo implements Serializable, GenericMgDbBean {

	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;

	@JsonProperty(GlobalVariable.ATTR_REGIME)
	private String regime;

	@org.jongo.marshall.jackson.oid.ObjectId
	@JsonSerialize(using = NoObjectIdSerializer.class)
	private String _id;

	@JsonProperty(GlobalVariable.ATTR_ERROR_EDDM)
	private String erreurEddm;

	@JsonProperty(GlobalVariable.ATTR_ERROR_DA)
	private String erreurDa;

	@JsonIgnore
	private Map<String, String> attributes = new HashMap<String, String>();

	@JsonProperty(GlobalVariable.ATTR_ATTRIBUTS)
	private Object attributs;

	@JsonProperty(GlobalVariable.ATTR_ID)
	private String id;

	@JsonProperty(GlobalVariable.ATTR_TYPE_DOSSIER)
	private String typeDocument;

	@JsonProperty(GlobalVariable.ATTR_ID_ORGANISME)
	private String cmroc;

	@JsonProperty(GlobalVariable.ATTR_TUTELLE)
	private String tutelle;

	@JsonProperty(GlobalVariable.ATTR_TYPEDOC)
	private TypeEntite typeEntiteRattachement;

	@JsonProperty(GlobalVariable.ATTR_PRIORITE)
	private String priorite;

	@JsonProperty(GlobalVariable.ATTR_STATUS)
	private String status;

	@JsonProperty(GlobalVariable.ATTR_DA_ID)
	private String sudeId;

	@JsonProperty(GlobalVariable.ATTR_EDDOC_ID)
	private String eddocId;

	@JsonProperty(GlobalVariable.ATTR_EDDOC_IDS)
	private List<String> eddocIds = new ArrayList<String>();

	@JsonProperty(GlobalVariable.ATTR_CODE_POSTAL)
	private String codePostal;

	@JsonProperty(GlobalVariable.ATTR_DEPARTEMENT)
	private String departement;

	@JsonProperty(GlobalVariable.ATTR_REGION)
	private String region;

	@JsonProperty(GlobalVariable.ATTR_MAILED)
	private String mailed;

	@JsonSerialize(using = BsonDateSerializer.class, as = DateTime.class)
	@JsonDeserialize(using = BsonDateDeserializer.class, as = DateTime.class)
	@JsonProperty(GlobalVariable.ATTR_DTCREATE)
	private DateTime dtCreate;

	@JsonSerialize(using = BsonDateSerializer.class, as = DateTime.class)
	@JsonDeserialize(using = BsonDateDeserializer.class, as = DateTime.class)
	@JsonProperty(GlobalVariable.ATTR_DTINTEGRATION)
	private DateTime dtIntegration;

	@JsonProperty(GlobalVariable.ATTR_ID_ENTREPRISE)
	private String idEntreprise;

	@JsonProperty(GlobalVariable.ATTR_ID_SYSENTREPRISE)
	private String idSystemEntreprise;

	@JsonProperty(GlobalVariable.ATTR_NOM_ENTREPRISE)
	private String nomEntreprise;

	@JsonProperty(GlobalVariable.ATTR_NUM_ENTREPRISE)
	private String numEntreprise;

	@JsonProperty(GlobalVariable.ATTR_ID_PROF)
	private String idProf;

	@JsonProperty(GlobalVariable.ATTR_NUM_ADHERENT)
	private String numAdherent;

	@JsonProperty(GlobalVariable.ATTR_VILLE)
	private String ville;

	@JsonProperty(GlobalVariable.ATTR_NOM_DE_FAMILLE)
	private String nom;

	@JsonProperty(GlobalVariable.ATTR_PRENOM)
	private String prenom;

	@JsonProperty(GlobalVariable.ATTR_COMMENTAIRES)
	private String commentaire;

	@JsonProperty(GlobalVariable.ATTR_ASSU_INSEE)
	private String assuInsee;
	

	/**
	 * attribut pour la règle d'affichage, mis en place pour les documents Courtage et ne devant pas remonter dans
	 * starweb
	 */
	@JsonProperty(GlobalVariable.ATTR_SHOW_RULE)
	private String showRule;

	@JsonProperty(GlobalVariable.ATTR_ASSU_RANG)
	private String rang;

	@JsonProperty(GlobalVariable.ATTR_IDENTIFIANT_PACK)
	private String codePack;

	/**
	 * Attributs pour les rapport d'intégration de masse IDOC
	 */
	@JsonProperty(GlobalVariable.ATTR_INTEGRATIONS_TOT)
	private Long integrationsTotale;

	@JsonProperty(GlobalVariable.ATTR_INTEGRATIONS_OK)
	private Long integrationsOK;

	@JsonProperty(GlobalVariable.ATTR_INTEGRATIONS_KO)
	private Long integrationsKO;

	@JsonProperty(GlobalVariable.ATTR_USER_INTEGRATION)
	private String user;

	@JsonProperty(GlobalVariable.ATTR_ZIP_NAME)
	private String zipName;

	@JsonProperty(GlobalVariable.ATTR_CODE_PRODUIT)
	private String codeProduit;

	@JsonProperty(GlobalVariable.ATTR_CODE_GARANTIE)
	private String codeGarantie;
	
	@JsonProperty(GlobalVariable.ATTR_CANAL)
	private String canal;
	
	@JsonProperty(GlobalVariable.ATTR_REFERENCE_COURRIER)
	private String referentielCourrier;

	@JsonAnySetter
	public void handleUnknown(String key, String value) {
		this.addAttribute(key, value);
	}

	@JsonAnyGetter
	public Map<String, String> any() {
		return attributes;
	}

	@Override
	public String getId() {
		return this.id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	public String getTypeDocument() {
		return typeDocument;
	}

	public void setTypeDocument(String typeDocument) {
		this.typeDocument = typeDocument;
	}

	public String getCmroc() {
		return cmroc;
	}

	public void setCmroc(String cmroc) {
		this.cmroc = cmroc;
	}

	public TypeEntite getTypeEntiteRattachement() {
		return typeEntiteRattachement;
	}

	public void setTypeEntiteRattachement(TypeEntite typeEntite) {
		this.typeEntiteRattachement = typeEntite;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getSudeId() {
		return this.sudeId;
	}

	public void setSudeId(String sudeId) {
		this.sudeId = sudeId;
	}

	public String getEddocId() {
		return this.eddocId;
	}

	public void setEddocId(String eddocId) {
		this.eddocId = eddocId;
	}

	public Map<String, String> getAttributes() {
		return attributes;
	}

	public void setAttributes(Map<String, String> attributes) {
		this.attributes = attributes;
	}

	public String getAttribute(String key) {
		return attributes.get(key);
	}

	public void addAttribute(String key, String value) {
		this.attributes.put(key, value);
	}

	public String getCodePostal() {
		return codePostal;
	}

	public void setCodePostal(String codePostal) {
		this.codePostal = codePostal;
	}

	public String getShowRule() {
		return showRule;
	}

	public void setShowRule(String showRule) {
		this.showRule = showRule;
	}

	public String getDepartement() {
		return departement;
	}

	public void setDepartement(String departement) {
		this.departement = departement;
	}

	public String getRegion() {
		return region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public DateTime getDtCreate() {
		return dtCreate;
	}

	public void setDtCreate(DateTime dtCreate) {
		this.dtCreate = dtCreate;
	}

	public DateTime getDtIntegration() {
		return dtIntegration;
	}

	public void setDtIntegration(DateTime dtIntegration) {
		this.dtIntegration = dtIntegration;
	}

	public String getIdEntreprise() {
		return idEntreprise;
	}

	public void setIdEntreprise(String idEntreprise) {
		this.idEntreprise = idEntreprise;
	}

	public String getNomEntreprise() {
		return this.nomEntreprise;
	}

	public void setNomEntreprise(String nomEntreprise) {
		this.nomEntreprise = nomEntreprise;
	}

	public String getIdSystemEntreprise() {
		return idSystemEntreprise;
	}

	public void setIdSystemEntreprise(String idSystemEntreprise) {
		this.idSystemEntreprise = idSystemEntreprise;
	}

	public String getNumEntreprise() {
		return numEntreprise;
	}

	public void setNumEntreprise(String numEntreprise) {
		this.numEntreprise = numEntreprise;
	}

	public String getIdProf() {
		return idProf;
	}

	public void setIdProf(String idProf) {
		this.idProf = idProf;
	}

	public String getNumAdherent() {
		return numAdherent;
	}

	public void setNumAdherent(String numAdherent) {
		this.numAdherent = numAdherent;
	}

	public String getTutelle() {
		return tutelle;
	}

	public void setTutelle(String tutelle) {
		this.tutelle = tutelle;
	}

	/**
	 * useless
	 * 
	 * @deprecated
	 * @return
	 */
	@Deprecated
	public Object getAttributs() {
		return attributs;
	}

	/**
	 * useless
	 * 
	 * @deprecated
	 * @return
	 */
	@Deprecated
	public void setAttributs(Object attributs) {
		this.attributs = attributs;
	}

	public String getVille() {
		return ville;
	}

	public void setVille(String ville) {
		this.ville = ville;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public String getCommentaire() {
		return commentaire;
	}

	public void setCommentaire(String commentaire) {
		this.commentaire = commentaire;
	}

	public String getPriorite() {
		return priorite;
	}

	public void setPriorite(String priorite) {
		this.priorite = priorite;
	}

	public void setAssuInsee(String assuInsee) {
		this.assuInsee = assuInsee;
	}

	public String getAssuInsee() {
		return this.assuInsee;
	}

	public String getErreurDa() {
		return erreurDa;
	}

	public void setErreurDa(String erreurDa) {
		this.erreurDa = erreurDa;
	}

	public String getErreurEddm() {
		return erreurEddm;
	}

	public void setErreurEddm(String erreurEddm) {
		this.erreurEddm = erreurEddm;
	}

	public String getMailed() {
		return this.mailed;
	}

	public void setMailed(String mailed) {
		this.mailed = mailed;
	}

	public String getRang() {
		return this.rang;
	}

	public void setRang(String rang) {
		this.rang = rang;
	}

	public String getRegime() {
		return this.regime;
	}

	public void setRegime(String regime) {
		this.regime = regime;
	}

	public List<String> getEddocIds() {
		return eddocIds;
	}

	public void setEddocIds(List<String> eddocIds) {
		this.eddocIds = eddocIds;
	}

	public String getCodePack() {
		return codePack;
	}

	public void setCodePack(String id) {
		this.codePack = id;
	}

	public Long getIntegrationsTotale() {
		return integrationsTotale;
	}

	public void setIntegrationsTotale(Long integrationsTotale) {
		this.integrationsTotale = integrationsTotale;
	}

	public Long getIntegrationsOK() {
		return integrationsOK;
	}

	public void setIntegrationsOK(Long integrationsOK) {
		this.integrationsOK = integrationsOK;
	}

	public Long getIntegrationsKO() {
		return integrationsKO;
	}

	public void setIntegrationsKO(Long integrationsKO) {
		this.integrationsKO = integrationsKO;
	}

	public String getCodeProduit() {
		return codeProduit;
	}

	public void setCodeProduit(String codeProduit) {
		this.codeProduit = codeProduit;
	}

	public String getCodeGarantie() {
		return codeGarantie;
	}

	public void setCodeGarantie(String codeGarantie) {
		this.codeGarantie = codeGarantie;
	}

	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}

	public String getZipName() {
		return zipName;
	}

	public void setZipName(String zipName) {
		this.zipName = zipName;
	}
	
	public String getCanal() {
		return canal;
	}

	public void setCanal(String canal) {
		this.canal = canal;
	}

	public String getReferentielCourrier() {
		return referentielCourrier;
	}

	public void setReferentielCourrier(String referentielCourrier) {
		this.referentielCourrier = referentielCourrier;
	}
	
	

}
