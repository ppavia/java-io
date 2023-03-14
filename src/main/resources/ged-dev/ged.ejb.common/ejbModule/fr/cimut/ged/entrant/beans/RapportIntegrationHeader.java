package fr.cimut.ged.entrant.beans;

// XXX Ajouter champs obligatoire true/false
public enum RapportIntegrationHeader {
	NOM_DOCUMENT(
			"Nom du fichier",
			0,
			true),
	TYPE_DOCUMENT(
			"Type de document",
			1,
			true),
	LIBELLE(
			"Libellé",
			2,
			false),
	TYPE_RATTACHEMENT(
			"Type d’entité de rattachement",
			3,
			true),
	ADHERENT_NUM(
			"PERSONNE\nnuméro adhérent ou insee",
			4,
			false),
	PARTENAIRE_NUM_INTERNE(
			"PARTENAIRE\nnuméro interne",
			5,
			false),
	PARTENAIRE_TYPE_PART(
			"type de partenaire",
			6,
			false),
	PARTENAIRE_NIVEAU(
			"niveau",
			7,
			false),
	ENTREPRISE_NUM_INTERNE(
			"ENTREPRISE\nnuméro interne",
			8,
			false),
	ENTREPRISE_CLASS_ETAB(
			"classe établissement",
			9,
			false),
	SECTION_NUM_INTERNE(
			"SECTION\nnumero interne",
			10,
			false),
	SECTION_CLASSE_ETAB(
			"classe établissement",
			11,
			false),
	SECTION_CODE_SECTION(
			"code section",
			12,
			false),
	PACK_CODE_PRODUIT(
			"PACK\ncode produit commercial",
			13,
			false),
	PACK_CODE_PACK(
			"code pack",
			14,
			false),
	GARANTIE_CODE_GARANTIE(
			"GARANTIE\ncode garantie",
			15,
			false),
	SUDE_IDENTIFIANT(
			"SUDE\nidentifiant",
			16,
			false),
	DATE_DEBUT_VALIDITE(
			"date de début de validité",
			17,
			false),
	DATE_FIN_VALIDITE(
			"date de fin de validité",
			18,
			false),
	STATUT_INTEGRATION(
			"STATUT INTEGRATION",
			19,
			false),
	DETAIL_INTEGRATION(
			"DETAIL INTEGRATION",
			20,
			false);

	private String libelle;
	private int indexInTemplate;
	private boolean isMandatory;

	public static final int ROWLASTINDEX = 20;

	RapportIntegrationHeader(String libelle, int index, boolean isMandatory) {
		this.libelle = libelle;
		this.indexInTemplate = index;
		this.setMandatory(isMandatory);
	}

	public String getLibelle() {
		return libelle;
	}

	public int getIndex() {
		return indexInTemplate;
	}

	public boolean isMandatory() {
		return isMandatory;
	}

	public void setMandatory(boolean isMandatory) {
		this.isMandatory = isMandatory;
	}

	public static RapportIntegrationHeader getByCellIndex(int cellIndex) {
		for (RapportIntegrationHeader element : RapportIntegrationHeader.values()) {
			if (element.getIndex() == cellIndex) {
				return element;
			}
		}
		return null;
	}

}
