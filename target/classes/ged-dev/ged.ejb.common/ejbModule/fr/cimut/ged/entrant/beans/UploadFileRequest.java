package fr.cimut.ged.entrant.beans;

import javax.ws.rs.FormParam;

public class UploadFileRequest {

	@FormParam("attachment")
	private byte[] fileData;

	@FormParam("typeDest")
	private String typeDest;

	@FormParam("destInsee")
	private String destInsee;

	@FormParam("rang")
	private String rang;

	@FormParam("partId")
	private String partId;

	@FormParam("partType")
	private String partType;

	@FormParam("partNiv")
	private String partNiv;

	@FormParam("struNum")
	private String struNum;

	@FormParam("struClasse")
	private String struClasse;

	@FormParam("struSec")
	private String struSec;

	@FormParam("numContrat")
	private String numContrat;

	@FormParam("garantieCode")
	private String garantieCode;

	@FormParam("produitComCode")
	private String produitComCode;

	@FormParam("packCode")
	private String packCode;

	@FormParam("categorieDoc")
	private String categorieDoc;

	@FormParam("typeDoc")
	private String typeDoc;

	@FormParam("dtDebutValidite")
	private String dtDebutValidite;

	@FormParam("dtFinValidite")
	private String dtFinValidite;

	@FormParam("label")
	private String label;

	public byte[] getFileData() {
		return fileData;
	}

	public void setFileData(byte[] fileData) {
		this.fileData = fileData;
	}

	public String getTypeDest() {
		return typeDest;
	}

	public void setTypeDest(String typeDest) {
		this.typeDest = typeDest;
	}

	public String getDestInsee() {
		return destInsee;
	}

	public void setDestInsee(String destInsee) {
		this.destInsee = destInsee;
	}

	public String getRang() {
		return rang;
	}

	public void setRang(String rang) {
		this.rang = rang;
	}

	public String getPartId() {
		return partId;
	}

	public void setPartId(String partId) {
		this.partId = partId;
	}

	public String getPartType() {
		return partType;
	}

	public void setPartType(String partType) {
		this.partType = partType;
	}

	public String getPartNiv() {
		return partNiv;
	}

	public void setPartNiv(String partNiv) {
		this.partNiv = partNiv;
	}

	public String getStruNum() {
		return struNum;
	}

	public void setStruNum(String struNum) {
		this.struNum = struNum;
	}

	public String getStruClasse() {
		return struClasse;
	}

	public void setStruClasse(String struClasse) {
		this.struClasse = struClasse;
	}

	public String getStruSec() {
		return struSec;
	}

	public void setStruSec(String struSec) {
		this.struSec = struSec;
	}

	public String getNumContrat() {
		return numContrat;
	}

	public void setNumContrat(String numContrat) {
		this.numContrat = numContrat;
	}

	public String getGarantieCode() {
		return garantieCode;
	}

	public void setGarantieCode(String garantieCode) {
		this.garantieCode = garantieCode;
	}

	public String getProduitComCode() {
		return produitComCode;
	}

	public void setProduitComCode(String produitComCode) {
		this.produitComCode = produitComCode;
	}

	public String getPackCode() {
		return packCode;
	}

	public void setPackCode(String packCode) {
		this.packCode = packCode;
	}

	public String getCategorieDoc() {
		return categorieDoc;
	}

	public void setCategorieDoc(String categorieDoc) {
		this.categorieDoc = categorieDoc;
	}

	public String getTypeDoc() {
		return typeDoc;
	}

	public void setTypeDoc(String typeDoc) {
		this.typeDoc = typeDoc;
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

	public String getLabel() {return label;}

	public void setLabel(String label) {this.label = label;}
}