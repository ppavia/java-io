package fr.cimut.ged.entrant.beans.p360;

/**
 * Partenaire de sante tel que retourne par le service de recherche 360
 * 
 * @author pgarel
 */
public class PartenaireSante extends Search360DtoAbstract {

	private String civilite;
	private String nom;
	private String prenom;
	private String finess;
	private String telephone;
	private String email;
	private String commune;
	private String codePostal;

	public PartenaireSante() {
		super();
	}

	@Override
	public String getCompareString() {
		return (nom == null ? "" : nom) + (prenom == null ? "" : prenom);
	}

	public String getCivilite() {
		return civilite;
	}

	public void setCivilite(String civilite) {
		this.civilite = civilite;
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

	public String getFiness() {
		return finess;
	}

	public void setFiness(String finess) {
		this.finess = finess;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCommune() {
		return commune;
	}

	public void setCommune(String commune) {
		this.commune = commune;
	}

	public String getCodePostal() {
		return codePostal;
	}

	public void setCodePostal(String codePostal) {
		this.codePostal = codePostal;
	}

}
