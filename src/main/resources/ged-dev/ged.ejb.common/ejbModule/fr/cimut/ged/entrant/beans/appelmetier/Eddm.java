package fr.cimut.ged.entrant.beans.appelmetier;

import fr.cimut.ged.entrant.beans.TypeEntite;
import fr.cimut.ged.entrant.utils.GlobalVariable;

public class Eddm {

	public static final String DISPONIBLE = "0200";
	public static final String PURGE = "0400";

	private String cmroc;
	private String numTutelle;
	private String sAssu;
	private String insee;
	private String partId;
	private String typPartId;
	private String partNiv;
	private String identreprise;
	//private String struId;
	//private String struClass;
	//private String struSect;
	private String idSystemEntreprise;
	private TypeEntite typeEntite;
	private String libDocument;
	private String extension;
	private String utilisateur = GlobalVariable.IMPORT_USER;
	private String sens = "R";
	private String codeCanal = "";
	private String libCanal = "Courrier";
	private String etat = "0200";
	private String docId;
	private String codePostal;

	public Eddm() {

	}

	public String getDocId() {
		return docId;
	}

	public void setDocId(String docId) {
		this.docId = docId;
	}

	public String getCmroc() {
		return cmroc;
	}

	public void setCmroc(String cmroc) {
		this.cmroc = cmroc;
	}

	public String getNumTutelle() {
		return numTutelle;
	}

	public void setNumTutelle(String numTutelle) {
		this.numTutelle = numTutelle;
	}

	public String getInsee() {
		return insee;
	}

	public void setInsee(String insee) {
		this.insee = insee;
	}

	public String getSassu() {
		return sAssu;
	}

	public void setSassu(String sAssu) {
		this.sAssu = sAssu;
	}

	public String getPartId() {
		return partId;
	}

	public void setPartId(String partId) {
		this.partId = partId;
	}

	public String getPartNiv() {
		return partNiv;
	}

	public void setPartNiv(String partNiv) {
		this.partNiv = partNiv;
	}

	public String getTypPartId() {
		return typPartId;
	}

	public void setTypPartId(String typPartId) {
		this.typPartId = typPartId;
	}

	public String getIdentreprise() {
		return identreprise;
	}

	public void setIdentreprise(String identreprise) {
		this.identreprise = identreprise;
	}

	public TypeEntite getTypeEntite() {
		return typeEntite;
	}

	public void setTypeEntite(TypeEntite typeEntite) {
		this.typeEntite = typeEntite;
	}

	public String getLibDocument() {
		return libDocument;
	}

	public void setLibDocument(String libDocument) {
		this.libDocument = libDocument;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

	public String getUtilisateur() {
		return utilisateur;
	}

	public void setUtilisateur(String utilisateur) {
		this.utilisateur = utilisateur;
	}

	public String getSens() {
		return sens;
	}

	public void setSens(String sens) {
		this.sens = sens;
	}

	public String getCodeCanal() {
		return codeCanal;
	}

	public void setCodeCanal(String codeCanal) {
		this.codeCanal = codeCanal;
	}

	public String getLibCanal() {
		return libCanal;
	}

	public void setLibCanal(String libCanal) {
		this.libCanal = libCanal;
	}

	public String getEtat() {
		return etat;
	}

	public void setEtat(String etat) {
		this.etat = etat;
	}

	public String getIdSystemEntreprise() {
		return idSystemEntreprise;
	}

	public void setIdSystemEntreprise(String idSystemEntreprise) {
		this.idSystemEntreprise = idSystemEntreprise;
	}

	public void setCodePostal(String codePostal) {
		this.codePostal = codePostal;
	}

	public String getCodePostal() {
		return this.codePostal;
	}

	@Override
	public String toString() {
		return "Eddm [cmroc=" + cmroc + ", numTutelle=" + numTutelle + ", sAssu=" + sAssu + ", insee=" + insee + ", partId=" + partId + ", typPartId="
				+ typPartId + ", partNiv=" + partNiv + ", identreprise=" + identreprise + ", idSystemEntreprise=" + idSystemEntreprise
				+ ", typeDocument=" + typeEntite + ", libDocument=" + libDocument + ", extension=" + extension + ", utilisateur=" + utilisateur
				+ ", sens=" + sens + ", codeCanal=" + codeCanal + ", libCanal=" + libCanal + ", etat=" + etat + ", docId=" + docId + ", codePostal="
				+ codePostal + "]";
	}

}
