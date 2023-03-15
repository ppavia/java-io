package fr.cimut.ged.entrant.beans.p360;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.commons.lang.StringUtils;

import fr.cimut.indexation.model.ContratExterne;
import fr.cimut.indexation.model.Personne;

/**
 * Personne regroupant les informations provenant de RO, de RC et les informations Prospect
 * 
 * @author pgarel
 */
public class PersonneRORCProspect extends Search360DtoAbstract {

	private String nom;
	private String nomBeneficiaire;
	private String prenom;
	private String inseeAssure;
	private String inseeBeneficiaire;
	private String dtNaissance;
	private String codePostal;
	private String ville;
	private String telDomicile;
	private String telPortable;
	private String email;
	private String sexe;
	private Set<CoupleCmrocValeur<String>> numAdherents;
	private Set<String> numContrat;
	private String etatContrat;
	private String rang;
	private Set<CoupleCmrocValeur<String>> anciennesReferences;
	private Set<CoupleCmrocValeur<String>> datesFermeture;
	private boolean ro;
	private boolean rc;

	private List<ContratExterne> contratExternes = new ArrayList<ContratExterne>(0);;

	public PersonneRORCProspect() {
		super();
	}

	/**
	 * Cree une {@link PersonneRORCProspect} a partir d'une {@link Personne} MongoDB
	 * 
	 * @param personne
	 *            {@link Personne} MongoDB
	 * @return une {@link PersonneRORCProspect}
	 */
	public static PersonneRORCProspect createFrom(Personne personne) {
		PersonneRORCProspect personneRORCProspect = new PersonneRORCProspect();

		personneRORCProspect.setNom(personne.getnom());
		personneRORCProspect.setNomBeneficiaire(personne.getnomBeneficiaire());
		personneRORCProspect.setPrenom(personne.getprenom());
		personneRORCProspect.setInseeAssure(personne.getinseeAssure());
		personneRORCProspect.setInseeBeneficiaire(personne.getinseeBeneficiaire());
		personneRORCProspect.setDtNaissance(personne.getdtNaissance());
		personneRORCProspect.setCodePostal(personne.getcodePostal());
		personneRORCProspect.setVille(personne.getville());
		personneRORCProspect.setTelDomicile(personne.gettelDomicile());
		personneRORCProspect.setTelPortable(personne.gettelPortable());
		personneRORCProspect.setEmail(personne.getemail());
		personneRORCProspect.setSexe(personne.getsexe());

		if (personne.getnumAdherent() != null && !personne.getnumAdherent().isEmpty()) {
			Set<CoupleCmrocValeur<String>> numAdherents = new HashSet<CoupleCmrocValeur<String>>();
			personneRORCProspect.setNumAdherents(numAdherents);
			numAdherents.add(new CoupleCmrocValeur<String>(personne.getcmroc(), personne.getnumAdherent()));
		}

		personneRORCProspect.setNumContrat(personne.getnumContrat());
		personneRORCProspect.setEtatContrat(personne.getetatContrat());

		if (personne.hasContratExterne()) {
			// on positionne l'information cmroc sur le contrat
			ContratExterne contrat = personne.getcontratExterne();
			contrat.setpartenaire(personne.getcmroc());
			personneRORCProspect.getContratExternes().add(contrat);
		}

		personneRORCProspect.setRang(personne.getrang());

		if (personne.getancienneReference() != null && !personne.getancienneReference().isEmpty()) {
			Set<CoupleCmrocValeur<String>> anciennesReferences = new HashSet<CoupleCmrocValeur<String>>();
			personneRORCProspect.setAnciennesReferences(anciennesReferences);
			anciennesReferences.add(new CoupleCmrocValeur<String>(personne.getcmroc(), personne.getancienneReference()));
		}
		if (personne.getdtFermeture() != null && !personne.getdtFermeture().isEmpty()) {
			Set<CoupleCmrocValeur<String>> datesFermeture = new HashSet<CoupleCmrocValeur<String>>();
			personneRORCProspect.setDatesFermeture(datesFermeture);
			datesFermeture.add(new CoupleCmrocValeur<String>(personne.getcmroc(), personne.getdtFermeture()));
		}

		personneRORCProspect.setRoRc(personne.getcmroc());

		return personneRORCProspect;
	}

	/**
	 * Fusionne cette {@link PersonneRORCProspect} avec la {@link Personne} passee en parametre. Seuls les champs nuls
	 * sont valorisés par les champs non nuls de {@link Personne}.
	 * 
	 * @param personne
	 *            La {@link Personne} a fusionner avec {@code this}
	 */
	public void mergeFrom(Personne personne) {
		// Nom
		if (StringUtils.isBlank(getNom()) && StringUtils.isNotBlank(personne.getnom())) {
			setNom(personne.getnom());
		}
		// Nom beneficiaire
		if (StringUtils.isBlank(getNomBeneficiaire()) && StringUtils.isNotBlank(personne.getnomBeneficiaire())) {
			setNomBeneficiaire(personne.getnomBeneficiaire());
		}
		// Prenom
		if (StringUtils.isBlank(getPrenom()) && StringUtils.isNotBlank(personne.getprenom())) {
			setPrenom(personne.getprenom());
		}
		// INSEE Assure
		if (StringUtils.isBlank(getInseeAssure()) && StringUtils.isNotBlank(personne.getinseeAssure())) {
			setInseeAssure(personne.getinseeAssure());
		}
		// INSEE beneficiaire
		if (StringUtils.isBlank(getInseeBeneficiaire()) && StringUtils.isNotBlank(personne.getinseeBeneficiaire())) {
			setInseeBeneficiaire(personne.getinseeBeneficiaire());
		}
		// Date de naissance
		if (StringUtils.isBlank(getDtNaissance()) && StringUtils.isNotBlank(personne.getdtNaissance())) {
			setDtNaissance(personne.getdtNaissance());
		}
		// Code Postal
		if (StringUtils.isBlank(getCodePostal()) && StringUtils.isNotBlank(personne.getcodePostal())) {
			setCodePostal(personne.getcodePostal());
		}
		// Ville
		if (StringUtils.isBlank(getVille()) && StringUtils.isNotBlank(personne.getville())) {
			setVille(personne.getville());
		}
		// teléphone Domicile
		if (StringUtils.isBlank(getTelDomicile()) && StringUtils.isNotBlank(personne.gettelDomicile())) {
			setTelDomicile(personne.gettelDomicile());
		}
		// teléphone Portable
		if (StringUtils.isBlank(getTelPortable()) && StringUtils.isNotBlank(personne.gettelPortable())) {
			setTelPortable(personne.gettelPortable());
		}
		// Email
		if (StringUtils.isBlank(getEmail()) && StringUtils.isNotBlank(personne.getemail())) {
			setEmail(personne.getemail());
		}
		// Sexe
		if (StringUtils.isBlank(getSexe()) && StringUtils.isNotBlank(personne.getsexe())) {
			setSexe(personne.getsexe());
		}
		// Numero adhérent
		if (StringUtils.isNotBlank(personne.getnumAdherent())) {
			Set<CoupleCmrocValeur<String>> numAdherents = getNumAdherents();
			if (numAdherents == null) {
				numAdherents = new HashSet<CoupleCmrocValeur<String>>();
				setNumAdherents(numAdherents);
			}
			numAdherents.add(new CoupleCmrocValeur<String>(personne.getcmroc(), personne.getnumAdherent()));
		}
		// Numero Contrat
		if (null == getNumContrat() && null != personne.getnumContrat()) {
			setNumContrat(personne.getnumContrat());
		}
		// etat Contrat
		if (StringUtils.isBlank(getEtatContrat()) && StringUtils.isNotBlank(personne.getetatContrat())) {
			setEtatContrat(personne.getetatContrat());
		}

		// Rang
		if (StringUtils.isBlank(getRang()) && StringUtils.isNotBlank(personne.getrang())) {
			setRang(personne.getrang());
		}
		// Ancienne reference
		if (StringUtils.isNotBlank(personne.getancienneReference())) {
			Set<CoupleCmrocValeur<String>> anciennesReferences = getAnciennesReferences();
			if (anciennesReferences == null) {
				anciennesReferences = new HashSet<CoupleCmrocValeur<String>>();
				setAnciennesReferences(anciennesReferences);
			}
			anciennesReferences.add(new CoupleCmrocValeur<String>(personne.getcmroc(), personne.getancienneReference()));
		}
		// Date Fermeture
		if (StringUtils.isNotBlank(personne.getdtFermeture())) {
			Set<CoupleCmrocValeur<String>> datesFermeture = getDatesFermeture();
			if (datesFermeture == null) {
				datesFermeture = new HashSet<CoupleCmrocValeur<String>>();
				setDatesFermeture(datesFermeture);
			}
			datesFermeture.add(new CoupleCmrocValeur<String>(personne.getcmroc(), personne.getdtFermeture()));
		}

		if (personne.hasContratExterne()) {
			ContratExterne contrat = personne.getcontratExterne();
			contrat.setpartenaire(personne.getcmroc());
			getContratExternes().add(contrat);
		}

		// RO / RC
		setRoRc(personne.getcmroc());
	}

	/**
	 * Positionne les flags RO et RC en fonction du CMROC passé en parametre
	 * 
	 * @param cmroc
	 *            CMROC de la personne
	 */
	private void setRoRc(String cmroc) {
		Regime regime = Regime.getRegime(cmroc);
		if (regime != null) {
			switch (regime) {
			case RO:
				setRo(true);
				break;
			case RC:
				setRc(true);
				break;
			}
		}
	}

	public List<ContratExterne> getContratExternes() {
		return contratExternes;
	}

	public void setContratExternes(List<ContratExterne> contratExternes) {
		this.contratExternes = contratExternes;
	}

	@Override
	public String getCompareString() {
		return (nom == null ? "" : nom) + (prenom == null ? "" : prenom);
	}

	public Set<String> getNumContrat() {
		return numContrat;
	}

	public void setNumContrat(Set<String> numContrat) {
		this.numContrat = numContrat;
	}

	public String getNom() {
		return nom;
	}

	public void setNom(String nom) {
		this.nom = nom;
	}

	public String getNomBeneficiaire() {
		return nomBeneficiaire;
	}

	public void setNomBeneficiaire(String nomBeneficiaire) {
		this.nomBeneficiaire = nomBeneficiaire;
	}

	public String getPrenom() {
		return prenom;
	}

	public void setPrenom(String prenom) {
		this.prenom = prenom;
	}

	public String getInseeAssure() {
		return inseeAssure;
	}

	public void setInseeAssure(String inseeAssure) {
		this.inseeAssure = inseeAssure;
	}

	public String getInseeBeneficiaire() {
		return inseeBeneficiaire;
	}

	public void setInseeBeneficiaire(String inseeBeneficiaire) {
		this.inseeBeneficiaire = inseeBeneficiaire;
	}

	public String getDtNaissance() {
		return dtNaissance;
	}

	public void setDtNaissance(String dtNaissance) {
		this.dtNaissance = dtNaissance;
	}

	public String getCodePostal() {
		return codePostal;
	}

	public void setCodePostal(String codePostal) {
		this.codePostal = codePostal;
	}

	public String getVille() {
		return ville;
	}

	public void setVille(String ville) {
		this.ville = ville;
	}

	public String getTelDomicile() {
		return telDomicile;
	}

	public void setTelDomicile(String telDomicile) {
		this.telDomicile = telDomicile;
	}

	public String getTelPortable() {
		return telPortable;
	}

	public void setTelPortable(String telPortable) {
		this.telPortable = telPortable;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getSexe() {
		return sexe;
	}

	public void setSexe(String sexe) {
		this.sexe = sexe;
	}

	public Set<CoupleCmrocValeur<String>> getNumAdherents() {
		return numAdherents;
	}

	public void setNumAdherents(Set<CoupleCmrocValeur<String>> numAdherents) {
		this.numAdherents = numAdherents;
	}

	public String getEtatContrat() {
		return etatContrat;
	}

	public void setEtatContrat(String etatContrat) {
		this.etatContrat = etatContrat;
	}

	public String getRang() {
		return rang;
	}

	public void setRang(String rang) {
		this.rang = rang;
	}

	public Set<CoupleCmrocValeur<String>> getAnciennesReferences() {
		return anciennesReferences;
	}

	public void setAnciennesReferences(Set<CoupleCmrocValeur<String>> anciennesReferences) {
		this.anciennesReferences = anciennesReferences;
	}

	public Set<CoupleCmrocValeur<String>> getDatesFermeture() {
		return datesFermeture;
	}

	public void setDatesFermeture(Set<CoupleCmrocValeur<String>> datesFermeture) {
		this.datesFermeture = datesFermeture;
	}

	public boolean isRo() {
		return ro;
	}

	public void setRo(boolean ro) {
		this.ro = ro;
	}

	public boolean isRc() {
		return rc;
	}

	public void setRc(boolean rc) {
		this.rc = rc;
	}

}
