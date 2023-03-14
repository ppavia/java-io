package fr.cimut.ged.entrant.dto;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 * Requête de modification d'un type de document
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TypeModificationDto {

	private TypeModificationCategoryDto categorie;
	private String code;
	private String libelle;
	private Boolean integrableIdoc;
	private Integer dureePurge;

	public TypeModificationDto() {
	}

	public TypeModificationCategoryDto getCategorie() {
		return categorie;
	}

	public void setCategorie(TypeModificationCategoryDto categorie) {
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

	public Boolean getIntegrableIdoc() {
		return integrableIdoc;
	}

	public void setIntegrableIdoc(Boolean integrableIdoc) {
		this.integrableIdoc = integrableIdoc;
	}

	public Integer getDureePurge() {
		return dureePurge;
	}

	public void setDureePurge(Integer dureePurge) {
		this.dureePurge = dureePurge;
	}
}
