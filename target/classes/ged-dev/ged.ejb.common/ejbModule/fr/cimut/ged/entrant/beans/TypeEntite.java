package fr.cimut.ged.entrant.beans;

import com.fasterxml.jackson.annotation.JsonValue;

import fr.cimut.ged.entrant.utils.GlobalVariable;

public enum TypeEntite {
	PERSONNE(
			GlobalVariable.TYPE_PERSONNE),
	ENTREPRISE(
			GlobalVariable.TYPE_ENTREPRISE),
	SECTION(
			GlobalVariable.TYPE_SECTION),
	PARTENAIRE(
			GlobalVariable.TYPE_PARTENAIRE),
	PACK(
			GlobalVariable.TYPE_PACK),
	GARANTIE(
			GlobalVariable.TYPE_GARANTIE),
	SUDE(
			GlobalVariable.TYPE_SUDE),
	INCONNU(
			GlobalVariable.TYPE_INCONNU),
	COURTIER(
			GlobalVariable.TYPE_COURTIER),
	RESEAUCOURTIERS(
			GlobalVariable.TYPE_RESEAUCOURTIERS),
	INTERNE(
			GlobalVariable.TYPE_INTERNE),
	RAPPORT_INTEGRATION_IDOC(
			GlobalVariable.TYPE_RAPPORT_INTEGRATION_IDOC);

	private final String text;

	@JsonValue
	public String getText() {
		return text;
	}

	TypeEntite(String text) {
		this.text = text;
	}

	public static TypeEntite fromString(String text) {
		for (TypeEntite typeEntite : TypeEntite.values()) {
			if (typeEntite.text.equalsIgnoreCase(text)) {
				return typeEntite;
			}
		}
		throw new IllegalArgumentException("Pas de type d'entité correspondant à la valeur " + text);
	}

}
