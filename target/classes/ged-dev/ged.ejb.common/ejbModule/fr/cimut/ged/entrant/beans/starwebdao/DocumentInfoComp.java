package fr.cimut.ged.entrant.beans.starwebdao;

import java.io.Serializable;

/**
 * la classe avec les informations compléementaire de document récupérées dans les différentes tables starweb
 * 
 * @author jlebourgocq
 *
 */
public class DocumentInfoComp implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 4369246273332276485L;

	private String rangRattachement;

	private String libelleStar;

	private String creePar;

	private String etat;

	private String etatLib;

	private String sens;

	private String codeCanal;

	private String valeurCanal;

	private String typeFichier;

	private Boolean typeVisibleExtranet;

	private Integer typeDureeVisibleExtranet;

	private Boolean typeIntegIdoc;

	public DocumentInfoComp() {
		super();
	}

	public String getRangRattachement() {
		return rangRattachement;
	}

	public void setRangRattachement(String rangRattachement) {
		this.rangRattachement = rangRattachement;
	}

	public String getLibelleStar() {
		return libelleStar;
	}

	public void setLibelleStar(String libelleStar) {
		this.libelleStar = libelleStar;
	}

	public String getCreePar() {
		return creePar;
	}

	public void setCreePar(String creePar) {
		this.creePar = creePar;
	}

	public String getEtat() {
		return etat;
	}

	public void setEtat(String etat) {
		this.etat = etat;
	}

	public String getEtatLib() {
		return etatLib;
	}

	public void setEtatLib(String etatLib) {
		this.etatLib = etatLib;
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

	public String getValeurCanal() {
		return valeurCanal;
	}

	public void setValeurCanal(String valeurCanal) {
		this.valeurCanal = valeurCanal;
	}

	public String getTypeFichier() {
		return typeFichier;
	}

	public void setTypeFichier(String typeFichier) {
		this.typeFichier = typeFichier;
	}

	public Boolean getTypeVisibleExtranet() {
		return typeVisibleExtranet;
	}

	public void setTypeVisibleExtranet(Boolean typeVisibleExtranet) {
		this.typeVisibleExtranet = typeVisibleExtranet;
	}

	public Integer getTypeDureeVisibleExtranet() {
		return typeDureeVisibleExtranet;
	}

	public void setTypeDureeVisibleExtranet(Integer typeDureeVisibleExtranet) {
		this.typeDureeVisibleExtranet = typeDureeVisibleExtranet;
	}

	public Boolean getTypeIntegIdoc() {
		return typeIntegIdoc;
	}

	public void setTypeIntegIdoc(Boolean typeIntegIdoc) {
		this.typeIntegIdoc = typeIntegIdoc;
	}
}
