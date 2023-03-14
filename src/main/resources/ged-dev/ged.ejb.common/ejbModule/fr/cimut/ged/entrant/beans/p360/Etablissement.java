package fr.cimut.ged.entrant.beans.p360;

/**
 * Etablissement tel que retourne par le service de recherche 360
 * 
 * @author pgarel
 */
public class Etablissement extends Search360DtoAbstract {

	private String raisonSociale;
	private String telephone;
	private String siren;
	private String nic;
	private String email;
	private String codeContrat;
	private String commune;
	private String codePostal;

	public Etablissement() {
		super();
	}

	@Override
	public String getCompareString() {
		return raisonSociale;
	}

	public String getRaisonSociale() {
		return raisonSociale;
	}

	public void setRaisonSociale(String raisonSociale) {
		this.raisonSociale = raisonSociale;
	}

	public String getTelephone() {
		return telephone;
	}

	public void setTelephone(String telephone) {
		this.telephone = telephone;
	}

	public String getSiren() {
		return siren;
	}

	public void setSiren(String siren) {
		this.siren = siren;
	}

	public String getNic() {
		return nic;
	}

	public void setNic(String nic) {
		this.nic = nic;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCodeContrat() {
		return codeContrat;
	}

	public void setCodeContrat(String codeContrat) {
		this.codeContrat = codeContrat;
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
