package fr.cimut.ged.entrant.beans.p360;

import java.util.List;

/**
 * reponse retournee par le service web Search360 lors d'une recherche agregee. <br/>
 * Cette reponse contient 3 listes, a savoir la liste d'adhérents, la liste de partenaires de sante et la liste
 * d'etablissements correspondant a la recherche effectuee
 * 
 * @author pgarel
 */
public class ReponseSearch360Agregee extends ReponseSearch360Abstract {

	private List<PersonneRORCProspect> personnes;
	private List<PartenaireSante> partenaires;
	private List<Etablissement> etablissements;

	public ReponseSearch360Agregee() {
		super();
	}

	public ReponseSearch360Agregee(String message, List<PersonneRORCProspect> personnes, List<PartenaireSante> partenaires,
			List<Etablissement> etablissements) {
		this.message = message;
		this.personnes = personnes;
		this.partenaires = partenaires;
		this.etablissements = etablissements;
	}

	public List<PersonneRORCProspect> getPersonnes() {
		return personnes;
	}

	public void setPersonnes(List<PersonneRORCProspect> personnes) {
		this.personnes = personnes;
	}

	public List<PartenaireSante> getPartenaires() {
		return partenaires;
	}

	public void setPartenaires(List<PartenaireSante> partenaires) {
		this.partenaires = partenaires;
	}

	public List<Etablissement> getEtablissements() {
		return etablissements;
	}

	public void setEtablissements(List<Etablissement> etablissements) {
		this.etablissements = etablissements;
	}

}
