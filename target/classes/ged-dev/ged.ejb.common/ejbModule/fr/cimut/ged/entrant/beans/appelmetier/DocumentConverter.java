package fr.cimut.ged.entrant.beans.appelmetier;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.EnumSet;
import java.util.List;

import fr.cimut.ged.entrant.utils.GedeIdHelper;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;
import org.jsoup.select.Evaluator.IsEmpty;

import fr.cimut.ged.entrant.beans.TypeEntite;
import fr.cimut.ged.entrant.beans.db.Document;
import fr.cimut.ged.entrant.beans.db.Type;
import fr.cimut.ged.entrant.beans.mongo.DocumentMongo;
import fr.cimut.ged.entrant.beans.mongo.RuleDa;
import fr.cimut.ged.entrant.exceptions.CimutDocumentException;
import fr.cimut.ged.entrant.exceptions.CimutFileException;
import fr.cimut.ged.entrant.exceptions.CimutMetierException;
import fr.cimut.ged.entrant.utils.DocumentHelper;
import fr.cimut.ged.entrant.utils.FileHelper;
import fr.cimut.ged.entrant.utils.GlobalVariable;

public class DocumentConverter {

	public DocumentConverter() {

	}

	/**
	 * Filiere repertoire
	 * 
	 * @param ruleDa
	 * @param docMongo
	 * @return
	 * @throws CimutMetierException
	 * @throws CimutFileException
	 * @throws CimutDocumentException
	 */
	public Sude toSude(RuleDa ruleDa, DocumentMongo docMongo) throws CimutMetierException, CimutFileException, CimutDocumentException {

		// avoid duplicate code by json transforming associated doc within a seprated list
		Document doc = new Document();
		doc.setId(docMongo.getId());
		doc.setLibelle(doc.getId());
		GedeIdHelper.setIdstarTstar(doc, docMongo.getEddocId());

		final String separator = "&nbsp;-&nbsp;";

		// Dans le cas de l'integration par repertoire, on veut dans la note de la SUDE,
		// l'affectation intiale.
		if (docMongo.getTypeEntiteRattachement() != null) {

			// on ajoute a la note sude, les elements qui pourrait leur permettre de retrouver
			// l adherant, partenaire entreprise car on a pas pu verifier depuis le metier que l'affectation etait correcte
			StringBuilder str = new StringBuilder("Type: " + docMongo.getTypeEntiteRattachement() + "<br>");
			if (docMongo.getTypeEntiteRattachement() == TypeEntite.PERSONNE) {
				if (StringUtils.isNotBlank(docMongo.getNumAdherent())) {
					str.append(separator + "Identifiant adhérent: " + docMongo.getNumAdherent() + "<br>");
				}
				if (StringUtils.isNotBlank(docMongo.getAssuInsee())) {
					str.append(separator + "Numero insee: " + docMongo.getAssuInsee() + "<br>");
				}
				if (StringUtils.isNotBlank(docMongo.getNom())) {
					str.append(separator + "Nom: " + docMongo.getNom() + "<br>");
				}
				if (StringUtils.isNotBlank(docMongo.getPrenom())) {
					str.append(separator + "Prénom: " + docMongo.getPrenom() + "<br>");
				}
			} else if (docMongo.getTypeEntiteRattachement() == TypeEntite.PARTENAIRE) {
				if (StringUtils.isNotBlank(docMongo.getIdProf())) {
					str.append(separator + "Identifiant Partenaire : " + docMongo.getIdProf() + "<br>");
				}
				if (StringUtils.isNotBlank(docMongo.getNomEntreprise())) {
					str.append(separator + "Raison social: " + docMongo.getNomEntreprise() + "<br>");
				}
				if (StringUtils.isNotBlank(docMongo.getNumAdherent())) {
					str.append(separator + "Identifiant adhérent: " + docMongo.getNumAdherent() + "<br>");
				}
				if (StringUtils.isNotBlank(docMongo.getAssuInsee())) {
					str.append(separator + "Numero insee: " + docMongo.getAssuInsee() + "<br>");
				}
				if (StringUtils.isNotBlank(docMongo.getNom())) {
					str.append(separator + "Nom: " + docMongo.getNom() + "<br>");
				}
				if (StringUtils.isNotBlank(docMongo.getPrenom())) {
					str.append(separator + "Prénom: " + docMongo.getPrenom() + "<br>");
				}
			} else if (docMongo.getTypeEntiteRattachement() == TypeEntite.ENTREPRISE) {
				if (StringUtils.isNotBlank(docMongo.getIdEntreprise())) {
					str.append(separator + "Identifiant entreprise: " + docMongo.getIdEntreprise() + "<br>");
				}
				if (StringUtils.isNotBlank(docMongo.getNomEntreprise())) {
					str.append(separator + "Raison social: " + docMongo.getNomEntreprise() + "<br>");
				}
			} else {
				if (StringUtils.isNotBlank(docMongo.getIdProf())) {
					str.append(separator + "Identifiant Partenaire : " + docMongo.getIdProf() + "<br>");
				}
				if (StringUtils.isNotBlank(docMongo.getNomEntreprise())) {
					str.append(separator + "Raison social: " + docMongo.getNomEntreprise() + "<br>");
				}
				if (StringUtils.isNotBlank(docMongo.getIdEntreprise())) {
					str.append(separator + "Identifiant entreprise: " + docMongo.getIdEntreprise() + "<br>");
				}
				if (StringUtils.isNotBlank(docMongo.getNomEntreprise())) {
					str.append(separator + "Raison social: " + docMongo.getNomEntreprise() + "<br>");
				}
				if (StringUtils.isNotBlank(docMongo.getNumAdherent())) {
					str.append(separator + "Identifiant adhérent: " + docMongo.getNumAdherent() + "<br>");
				}
				if (StringUtils.isNotBlank(docMongo.getAssuInsee())) {
					str.append(separator + "Numero insee: " + docMongo.getAssuInsee() + "<br>");
				}
				if (StringUtils.isNotBlank(docMongo.getNom())) {
					str.append(separator + "Nom: " + docMongo.getNom() + "<br>");
				}
				if (StringUtils.isNotBlank(docMongo.getPrenom())) {
					str.append(separator + "Prénom: " + docMongo.getPrenom() + "<br>");
				}
			}

			// on rajoute si disponible quelque element supplementaire ici ...
			if (StringUtils.isNotBlank(docMongo.getCodePostal())) {
				str.append(separator + "Code postal: " + docMongo.getCodePostal() + "<br>");
			}
			if (StringUtils.isNotBlank(docMongo.getVille())) {
				str.append(separator + "Ville: " + docMongo.getVille() + "<br>");
			}

			// l'erreur EDDM de l'appel retour metier ou des validateurs internes.
			//			if (StringUtils.isNotBlank(json.getErreurEddm())) {
			//				str.append(separator + "Erreur: " + json.getErreurEddm());
			//			}

			// le commentaire n'est pas sauvgrader ici.
			//Juste en lecture seul.
			// Utiliser ulterieurement pour setter le texte de la note.
			docMongo.setCommentaire(str.toString());
		}
		// on skip l'affectation pour des personnes/partenaire/entreprise avec la Da
		// car on as deja eu l'erreur d'affectation.

		return toSude(ruleDa, docMongo, Arrays.asList(doc));
	}

	/**
	 * Filiere mail.
	 * 
	 * @param ruleDa
	 * @param docMongo
	 * @param listDocument
	 * @return
	 * @throws CimutMetierException
	 * @throws CimutFileException
	 * @throws CimutDocumentException
	 */
	public Sude toSude(RuleDa ruleDa, DocumentMongo docMongo, List<Document> listDocument)
			throws CimutMetierException, CimutFileException, CimutDocumentException {

		// Sude n'est pas capable de gerer plus de 20 documents
		// le probleme c'est que certain mail en ont plus de 20.
		// A traité manuellement depuis le reprtoire d'erreur de la boite.
		if (docMongo.getEddocIds() != null && !docMongo.getEddocIds().isEmpty() && docMongo.getEddocIds().size() > 20) {
			throw new CimutFileException("pas plus de 20 documents pas NOTE ! : " + docMongo.getEddocIds().size());
		}

		Sude sude = new Sude();

		if (docMongo.getTypeEntiteRattachement() != null) {
			if (docMongo.getTypeEntiteRattachement() == TypeEntite.PERSONNE) {
				if (docMongo.getNumAdherent() != null) {
					sude.setAssure(docMongo.getNumAdherent());
				}
				if (docMongo.getAssuInsee() != null) {
					sude.setInsee(docMongo.getAssuInsee());
				}
			} else if (docMongo.getTypeEntiteRattachement() == TypeEntite.PARTENAIRE) {
				sude.setPartenaire(docMongo.getIdProf());
				if (docMongo.getNumAdherent() != null) {
					sude.setAssure(docMongo.getNumAdherent());
				}
				if (docMongo.getAssuInsee() != null) {
					sude.setInsee(docMongo.getAssuInsee());
				}
			} else if (docMongo.getTypeEntiteRattachement() == TypeEntite.ENTREPRISE) {
				sude.setEntreprise(docMongo.getIdEntreprise());
			}
		}
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");

		// specificité mail entrant
		if (Type.CODE_MAIL_DEMATERIALISE.equals(docMongo.getTypeDocument())) {
			String accuseReception = docMongo.getAttribute(GlobalVariable.ACCUSE_RECEPTION);
			if (accuseReception != null && "1".equals(accuseReception)) {
				sude.setAccuseReceptionDone("1");
				sude.setAccuseReceptionDate(sdf.format(new Date()));
				sude.setAccuseReceptionCanal(GlobalVariable.CANAL_MEL);
			}
			sude.setDateReception(sdf.format(docMongo.getDtCreate().toDate()));
		} else {
			sude.setDateReception(sdf.format(new Date()));
		}

		sude.setDateDemande(sdf.format(docMongo.getDtCreate().toDate()));
		if (GlobalVariable.CANAL_LER.equals(docMongo.getCanal())) {
			sude.setReferentielCourrier((docMongo.getReferentielCourrier() != null
					&& !docMongo.getReferentielCourrier().isEmpty() ) ? docMongo.getReferentielCourrier() : "X");
		}

		SudeNote daNote = new SudeNote();
		daNote.setLibelle(docMongo.getCommentaire());
		daNote.setSens("N");
		daNote.setType("INI");

		if (listDocument != null) {
			for (Document docs : listDocument) {

				String extension = FileHelper.getExtension(docs.getId()).substring(1);
				SudeNoteDocument daNoteDocument = new SudeNoteDocument();
				daNoteDocument.setEddocId(GedeIdHelper.getEddocIdFromIds(docs.getIdstar(), docs.getTsstar()));
				//si le libelle comprend l'extension
				if (docs.getLibelle().indexOf(".") > 0 && docs.getLibelle().length() > 3 ) {
					daNoteDocument.setFileName(docs.getLibelle().substring(0, docs.getLibelle().length() - extension.length() - 1));
				} else {
					daNoteDocument.setFileName(docs.getLibelle());
				}
				
				daNoteDocument.setExtension(extension);
				daNote.getDocuments().add(daNoteDocument);
			}
		}

		sude.setNote(daNote);
		sude.setOrganisme(docMongo.getCmroc());

		// je vois pas l'interet de ceci actuellement ...
		String key = "ADRESSE";
		int counter = 1;
		String attributeValue = docMongo.getAttribute(key + counter);
		while (attributeValue != null && !attributeValue.isEmpty()) {
			if (counter == 1) {
				sude.setAddress1(attributeValue);
			} else if (counter == 2) {
				sude.setAddress2(attributeValue);
			} else if (counter == 3) {
				sude.setAddress3(attributeValue);
			} else if (counter == 4) {
				sude.setAddress4(attributeValue);
			} else if (counter == 5) {
				sude.setAddress5(attributeValue);
			} else if (counter == 6) {
				sude.setAddress6(attributeValue);
			} else if (counter == 7) {
				sude.setAddress7(attributeValue);
			}
			counter++;
			attributeValue = docMongo.getAttribute(key + counter);
		}

		sude.setPriorite("2");

		// from rule stored in mongo Rules collection
		if (ruleDa.getSujet() != null && ruleDa.getSujet().length() > 80) {
			sude.setObjDemande(ruleDa.getSujet().substring(0, 80));
		} else {
			sude.setObjDemande(ruleDa.getSujet());
		}
		sude.setEntiteId(ruleDa.getId());
		sude.setEntiteNom(ruleDa.getName());
		if(docMongo.getCanal() != null) {
			sude.setdTypeSupId(docMongo.getCanal());
		}else {
			sude.setdTypeSupId(ruleDa.getSupport());
		}
		sude.setTypeDmaId(ruleDa.getType());
		sude.setReclamType(ruleDa.getCategorie());
		return sude;
	}

	/**
	 * Valide le format des données pour l'association avant de faire l'appel metier. Pas la peine d'aller plus loin si
	 * cela ne passe pas .
	 * 
	 * @param docMongo
	 * @throws CimutMetierException
	 */
	private void validateAssociation(DocumentMongo docMongo) throws CimutMetierException {
		TypeEntite typeEntite = docMongo.getTypeEntiteRattachement();

		// du point de vue métier section = entreprise
		if (typeEntite == TypeEntite.SECTION) {
			typeEntite = TypeEntite.ENTREPRISE;
		}

		// si pas d'affecation demander, on laisse passer.
		if (typeEntite == null) {
			return;
		}

		if (!EnumSet.of(TypeEntite.PERSONNE, TypeEntite.ENTREPRISE, TypeEntite.PARTENAIRE).contains(typeEntite)) {
			throw new CimutMetierException("Le type d'entité n'est pas supporté,  valeurs possibles : " + TypeEntite.PERSONNE + ","
					+ TypeEntite.ENTREPRISE + "," + TypeEntite.PARTENAIRE);
		}

		if (typeEntite == TypeEntite.ENTREPRISE) {
			if (docMongo.getIdEntreprise() == null || !docMongo.getIdEntreprise().matches("^[0-9A-Za-z\\|\\s]+$")
					|| docMongo.getIdEntreprise().matches("^0+$")) {
				throw new CimutMetierException(GlobalVariable.ATTR_ID_ENTREPRISE + " n'est pas au bon format : " + docMongo.getIdEntreprise());
			}
		} else if (typeEntite == TypeEntite.PARTENAIRE) {
			if (docMongo.getIdProf() == null || !docMongo.getIdProf().matches("^[0-9A-Za-z\\|]+$")) {
				throw new CimutMetierException(GlobalVariable.ATTR_ID_PROF + " n'est pas au bon format : " + docMongo.getIdProf());
			}
			if (docMongo.getNumAdherent() != null) {
				if (!docMongo.getNumAdherent().matches("^[0-9a-zA-Z]{1,12}$")
						|| docMongo.getNumAdherent().matches("^0+$")) {
					throw new CimutMetierException(GlobalVariable.ATTR_NUM_ADHERENT + " n'est pas au bon format (1 à 12 characteres alpha numerique) : "
							+ docMongo.getNumAdherent() + "; ");
				}
			}
			if (docMongo.getAssuInsee() != null) {
				if ( !docMongo.getAssuInsee().matches("^\\d{13}$")) {
					throw new CimutMetierException(GlobalVariable.ATTR_ASSU_INSEE + " n'est pas au bon format (13 chiffres) : " + docMongo.getAssuInsee());
				}
			}	
		} else if (typeEntite == TypeEntite.PERSONNE) {
			boolean fail = false;
			String message = "";
			if (docMongo.getNumAdherent() == null || !docMongo.getNumAdherent().matches("^[0-9a-zA-Z]{1,12}$")
					|| docMongo.getNumAdherent().matches("^0+$")) {
				message += GlobalVariable.ATTR_NUM_ADHERENT + " n'est pas au bon format (1 à 12 characteres alpha numerique) : "
						+ docMongo.getNumAdherent() + "; ";
				if (docMongo.getAssuInsee() == null || !docMongo.getAssuInsee().matches("^\\d{13}$")) {
					message += GlobalVariable.ATTR_ASSU_INSEE + " n'est pas au bon format (13 chiffres) : " + docMongo.getAssuInsee();
					fail = true;
				}
			}

			if (fail) {
				throw new CimutMetierException(message);
			}
		}
	}

	public Eddm toEddm(Document document, boolean affect) throws CimutMetierException {
		return toEddm(document, affect, GlobalVariable.IMPORT_USER, null);
	}

	public Eddm toEddm(Document document, boolean affect, String demandeur, String infoCanal) throws CimutMetierException {
		Eddm eddm = new Eddm();
		eddm.setUtilisateur(demandeur);
		if (infoCanal != null) {
			eddm.setLibCanal(infoCanal);
		}

		DocumentMongo docMongo;
		try {
			// old way  
			// TODO refondre toutes les valorisations du type document.setJson() pour passer sur du document.getDocMongo() 
			docMongo = DocumentHelper.getDocMongoFromJson(document);
		} catch (CimutDocumentException e1) {
			// new way 
			docMongo = document.getDocMongo();
			// on vérifie que l'on a bien récupéré un docMongo utilisatable
			if (docMongo.getTypeEntiteRattachement() == null) {
				throw new CimutMetierException("Impossible de récupérer un documentMongo utilisatble.", e1);
			}
		}

		try {
			eddm.setExtension(FileHelper.getExtension(document.getId()));
		} catch (CimutFileException e) {
			Logger.getLogger(DocumentConverter.class).error("commentaire manquant", e);
			eddm.setExtension("");
		}

		if (StringUtils.isNotBlank(docMongo.getCmroc())) {
			eddm.setCmroc(docMongo.getCmroc());
		} else {
			eddm.setCmroc(document.getCmroc());
		}

		if (StringUtils.isNotBlank(docMongo.getTutelle())) {
			eddm.setNumTutelle(docMongo.getTutelle());
		} else {
			eddm.setNumTutelle(document.getCmroc());
		}

		// document.getTypepapier() can be null when taking from mail or directory
		String codeCanal = document.getTypepapier();
		if (codeCanal == null || codeCanal.isEmpty()) {
			codeCanal = " ";
		}

		switch (codeCanal.toUpperCase().charAt(0)) {
		//EXTRANET
		case 'E':
			eddm.setCodeCanal("E");
			break;
		//MAIL
		case 'M':
			eddm.setCodeCanal("M");
			break;
		//FAX
		case 'F':
			eddm.setCodeCanal("F");
			break;
		//SMS
		case 'S':
			eddm.setCodeCanal("S");
			break;
		default:
			eddm.setCodeCanal("");
			break;
		}

		TypeEntite typeEntite = docMongo.getTypeEntiteRattachement();

		// du point de vue métier section = entreprise
		if (typeEntite == TypeEntite.SECTION) {
			typeEntite = TypeEntite.ENTREPRISE;
		}

		eddm.setLibDocument(((typeEntite != null) ? typeEntite + " - " : "") + document.getTypeDocument());
		eddm.setTypeEntite(typeEntite);

		// trick pour sude 2 : il velulent prendre la main sur le libelle d'eddm et sur le sens de la fleche
		if (docMongo.getAttribute("EDDM_LIBELLE") != null) {
			// attention ici; mal defini, l'appel metier part en timeout
			eddm.setLibDocument(DocumentHelper.sanitize(docMongo.getAttribute("EDDM_LIBELLE")));
		} else {
			eddm.setLibDocument(DocumentHelper.sanitize(document.getLibelle()));
		}

		if (StringUtils.isNotBlank(docMongo.getAttribute("EDDM_SENS"))) {
			eddm.setSens(docMongo.getAttribute("EDDM_SENS"));
		} else {
			eddm.setSens("E");
		}

		if (affect) {

			validateAssociation(docMongo);

			if (typeEntite == TypeEntite.PERSONNE) {
				if (docMongo.getNumAdherent() != null && !docMongo.getNumAdherent().isEmpty()) {
					eddm.setSassu(docMongo.getNumAdherent());
				}
				if (docMongo.getAssuInsee() != null && !docMongo.getAssuInsee().isEmpty()) {
					eddm.setInsee(docMongo.getAssuInsee());
				}
			} else if (typeEntite == TypeEntite.PARTENAIRE) {
				String[] splitted = docMongo.getIdProf().split("\\|");
				if (splitted.length > 0) {
					eddm.setPartId(splitted[0]);
				}
				if (splitted.length > 1) {
					eddm.setTypPartId(splitted[1]);
				}
				if (splitted.length > 2) {
					eddm.setPartNiv(splitted[2]);
				}
				if (docMongo.getNumAdherent() != null && !docMongo.getNumAdherent().isEmpty()) {
					eddm.setSassu(docMongo.getNumAdherent());
				}
				if (docMongo.getAssuInsee() != null && !docMongo.getAssuInsee().isEmpty()) {
					eddm.setInsee(docMongo.getAssuInsee());
				}
			} else if (typeEntite == TypeEntite.ENTREPRISE) {

				// faut pas garder le dernier |
				if (docMongo.getIdEntreprise().endsWith("|")) {
					docMongo.setIdEntreprise(docMongo.getIdEntreprise().replaceAll("\\|$", ""));
				}
				if (docMongo.getIdSystemEntreprise() != null) {
					eddm.setIdSystemEntreprise(docMongo.getIdSystemEntreprise());
				}
				eddm.setIdentreprise(docMongo.getIdEntreprise());
			}
		}

		if (docMongo.getEddocId() != null && docMongo.getEddocId().matches("^\\d+_\\d+$")) {
			eddm.setDocId(docMongo.getEddocId());
		}

		return eddm;
	}

}
