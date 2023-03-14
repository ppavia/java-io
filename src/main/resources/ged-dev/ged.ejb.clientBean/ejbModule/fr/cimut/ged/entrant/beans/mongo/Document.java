package fr.cimut.ged.entrant.beans.mongo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.joda.time.DateTime;

public class Document implements Serializable, GenericMgDbBean {

	/**
	 * 
	 */

	private static final long serialVersionUID = 1L;

	private String _id;

	private String erreurEddm;

	private String erreurDa;

	private Map<String, String> attributes = new HashMap<String, String>();

	private Object attributs;

	private String id;

	private String typeDossier;

	private String cmroc;

	private String tutelle;

	private String typeDocument;

	private String priorite;

	private String status;

	private String da;

	private String eddocId;

	private List<String> eddocIds = new ArrayList<String>();

	private String codePostal;

	private String departement;

	private String region;

	private String mailed;

	private DateTime dtCreate;

	private DateTime dtIntegration;

	private String idEntreprise;

	private String nomEntreprise;

	private String numEntreprise;

	private String idProf;

	private String numAdherent;

	private String ville;

	private String nom;

	private String prenom;

	private String commentaire;

	private String assuInsee;

	/**
	 * attribut pour la règle d'affichage, mis en place pour les documents Courtage et ne devant pas remonter dans
	 * starweb
	 */
	private String showRule;

	public void handleUnknown(String key, String value) {
		this.addAttribute(key, value);
	}

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

	public String getTypeDossier() {
		return typeDossier;
	}

	public void setTypeDossier(String typeDossier) {
		this.typeDossier = typeDossier;
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getDa() {
		return this.da;
	}

	public String getEddocId() {
		return this.eddocId;
	}

	public void setEddocId(String eddocId) {
		this.eddocId = eddocId;
	}

	public void setDa(String da) {
		this.da = da;
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

	public String getNomEntreprise() {
		return this.nomEntreprise;
	}

	public void setNomEntreprise(String nomEntreprise) {
		this.nomEntreprise = nomEntreprise;
	}

	public void setIdEntreprise(String idEntreprise) {
		this.idEntreprise = idEntreprise;
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

	public String getNumEntreprise() {
		return numEntreprise;
	}

	public void setNumEntreprise(String numEntreprise) {
		this.numEntreprise = numEntreprise;
	}

	public String getMailed() {
		return this.mailed;
	}

	public void setMailed(String mailed) {
		this.mailed = mailed;
	}

	/**
	 * @return the eddocIds
	 */
	public List<String> getEddocIds() {
		return eddocIds;
	}

	/**
	 * @param eddocIds
	 *            the eddocIds to set
	 */
	public void setEddocIds(List<String> eddocIds) {
		this.eddocIds = eddocIds;
	}

}
