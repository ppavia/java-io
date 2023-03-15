package fr.cimut.ged.entrant.beans.starwebdao;

public class ResponseTypeDto {

	private long idType;
	private ResponseCategoryDto category;
	private String code;
	private String libelle;
	private int dureePurge;
	private Integer dureeArchivage;
	private String typeEntrantSortant;
	private ResponseTypeParamsDto params;
	private String userMaj;
	private String dateMaj;

	public ResponseTypeDto() {
	}

	public ResponseTypeParamsDto getParams() {
		return params;
	}

	public void setParams(ResponseTypeParamsDto params) {
		this.params = params;
	}

	public ResponseCategoryDto getCategory() {
		return category;
	}

	public void setCategory(ResponseCategoryDto category) {
		this.category = category;
	}

	public long getIdType() {
		return idType;
	}

	public void setIdType(long idType) {
		this.idType = idType;
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

	public int getDureePurge() {
		return dureePurge;
	}

	public void setDureePurge(int dureePurge) {
		this.dureePurge = dureePurge;
	}

	public String getUserMaj() {
		return userMaj;
	}

	public void setUserMaj(String userMaj) {
		this.userMaj = userMaj;
	}

	public String getDateMaj() {
		return dateMaj;
	}

	public void setDateMaj(String dateMaj) {
		this.dateMaj = dateMaj;
	}

	public Integer getDureeArchivage() {
		return dureeArchivage;
	}

	public void setDureeArchivage(Integer dureeArchivage) {
		this.dureeArchivage = dureeArchivage;
	}

	public String getTypeEntrantSortant() {
		return typeEntrantSortant;
	}

	public void setTypeEntrantSortant(String typeEntrantSortant) {
		this.typeEntrantSortant = typeEntrantSortant;
	}
}
