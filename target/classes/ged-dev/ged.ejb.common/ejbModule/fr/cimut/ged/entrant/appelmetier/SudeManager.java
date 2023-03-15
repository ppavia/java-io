package fr.cimut.ged.entrant.appelmetier;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import fr.cimut.ged.entrant.utils.GedeIdHelper;
import org.apache.log4j.Logger;

import fr.cimut.ged.entrant.beans.appelmetier.DocumentConverter;
import fr.cimut.ged.entrant.beans.appelmetier.Sude;
import fr.cimut.ged.entrant.beans.appelmetier.SudeNote;
import fr.cimut.ged.entrant.beans.appelmetier.SudeNoteDocument;
import fr.cimut.ged.entrant.beans.db.Document;
import fr.cimut.ged.entrant.beans.mongo.DocumentMongo;
import fr.cimut.ged.entrant.beans.mongo.RuleDa;
import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.exceptions.CimutDocumentException;
import fr.cimut.ged.entrant.exceptions.CimutFileException;
import fr.cimut.ged.entrant.exceptions.CimutMetierException;
import fr.cimut.ged.entrant.exceptions.GedeCommonException;
import fr.cimut.ged.entrant.utils.DocumentHelper;
import fr.cimut.ged.entrant.utils.GlobalVariable;
import fr.cimut.mos.MapDataMesl;

/**
 * Session Bean implementation class Manager
 */

public class SudeManager extends MetierManager {

	private static final int LONGUEUR_MAX_COBOL_PER_LINE = 800;
	private static final int MAX_LINE_PER_NOTE = 8;

	private static final Logger LOGGER = Logger.getLogger(SudeManager.class);

	/**
	 * Default constructor.
	 * 
	 * @throws CimutConfException
	 */

	public SudeManager() throws CimutConfException {
		super("IHM_SUDE");
	}

	/**
	 * Cree une sude
	 * 
	 * @param ruleDa
	 * @param documentMongo
	 * @param environnement
	 * @return
	 * @throws CimutMetierException
	 * @throws CimutFileException
	 */
	public Sude create(RuleDa ruleDa, DocumentMongo documentMongo, String environnement) throws CimutMetierException, CimutFileException {
		DocumentConverter converter = new DocumentConverter();
		Sude sude = null;
		try {
			sude = converter.toSude(ruleDa, documentMongo);
		} catch (CimutDocumentException e1) {
			throw new CimutMetierException(e1);
		}
		MapDataMesl retourService = null;
		try {
			retourService = this.call(getMesl(sude), "C", sude.getOrganisme(), environnement, true);
		} catch (Exception e) {
			throw new CimutMetierException("Erreur lors de la creation de la DA : " + e.getMessage(), e);
		}
		sude.setId(retourService.getMesl("RECLAM_ID"));
		return sude;
	}

	/**
	 * Cree une demande SUDE avec ratachement de documents
	 * 
	 * @param ruleDa
	 * @param documentMongo
	 * @param docs
	 * @param environnement
	 * @return
	 * @throws CimutMetierException
	 */
	public Sude create(RuleDa ruleDa, DocumentMongo documentMongo, List<Document> docs, String environnement) throws CimutMetierException {

		DocumentConverter converter = new DocumentConverter();

		Sude sude = null;
		try {
			sude = converter.toSude(ruleDa, documentMongo, docs);
		} catch (Exception e1) {
			throw new CimutMetierException(e1);
		}

		try {
			MapDataMesl retourService = this.call(getMesl(sude), "C", sude.getOrganisme(), environnement, true);
			sude.setId(retourService.getMesl("RECLAM_ID"));
		} catch (Exception e) {
			throw new CimutMetierException("Erreur lors de la creation de la DA : " + e.getMessage(), e);
		}
		return sude;
	}

	/**
	 * Efface une demande SUDE
	 * 
	 * @param idSude
	 * @param cmroc
	 * @param environnement
	 * @throws CimutMetierException
	 */
	public void remove(String idSude, String cmroc, String environnement) throws CimutMetierException {
		MapDataMesl mapData = new MapDataMesl();
		mapData.addMesl("RECLAM_ID", idSude);
		try {
			this.call(mapData, "S", cmroc, environnement, true);
		} catch (Exception e) {
			throw new CimutMetierException("Erreur lors de la suppression de la DA : " + e.getMessage(), e);
		}
	}

	/**
	 * recupere les info d'une SUDE depuis son id
	 * 
	 * @param idSude
	 * @param cmroc
	 * @param environnement
	 * @return
	 * @throws CimutMetierException
	 */
	public Sude get(String idSude, String cmroc, String environnement) throws CimutMetierException {
		Sude sude = new Sude();
		sude.setId(idSude);
		MapDataMesl datas = getSude(idSude, cmroc, environnement);
		String typeDme = datas.getMesl("TYPDME_ID");
		if ("0".equals(typeDme)) {
			sude.setInsee(datas.getMesl("ASSU_INSEE"));
		} else if ("4".equals(typeDme)) {
			sude.setEntreprise(datas.getMesl("STRU_NO") + "|" + datas.getMesl("STRU_CLASS"));
		} else if ("5".equals(typeDme)) {
			sude.setEntreprise(datas.getMesl("STRU_NO") + "|" + datas.getMesl("STRU_CLASS") + "|" + datas.getMesl("STRU_SECT"));
		} else if ("2".equals(typeDme)) {
			sude.setPartenaire(datas.getMesl("EMETPART_ID") + "|" + datas.getMesl("EMETTYPPART_ID") + "|" + datas.getMesl("EMETPART_NIV"));
		} else if ("9".equals(typeDme) && datas.getMesl("STRU_NO") != null) {
			sude.setEntreprise(datas.getMesl("STRU_NO"));
		} else {
			throw new CimutMetierException("Affection pour ce type de demandeur non supporté ! " + typeDme);
		}

		// Tous les champs ne sont pas nécessairement obligatoires, mais on va faire les chose bien, ça peut servir !
		sude.setAccuseReceptionCanal(datas.getMesl("CARTYPSUP_ID"));
		sude.setAccuseReceptionDate(datas.getMesl("RECLAM_DT_AR"));
		sude.setAccuseReceptionDone(datas.getMesl("RECLAM_ACC_RECEPT"));
		sude.setAddress1(datas.getMesl("RECLAM_ADRES1"));
		sude.setAddress2(datas.getMesl("RECLAM_ADRES2"));
		sude.setAddress3(datas.getMesl("RECLAM_ADRES3"));
		sude.setAddress4(datas.getMesl("RECLAM_ADRES4"));
		sude.setAddress5(datas.getMesl("RECLAM_ADRES5"));
		sude.setAddress6(datas.getMesl("RECLAM_ADRES6"));
		sude.setAddress7(datas.getMesl("RECLAM_ADRES7"));
		sude.setAssure(datas.getMesl("ASSU_NOM" + " " + datas.getMesl("ASSU_PRENOM")));
		//sude.setCompteUid(compteUid);
		sude.setDateDemande(datas.getMesl("RECLAM_DT_DEM"));
		sude.setDateReception(datas.getMesl("RECLAM_DT_RECEPTIO"));
		sude.setDateReponse(datas.getMesl("RECLAM_DT_REPONSE"));
		sude.setEntiteId(datas.getMesl("ENTITE_ID"));
		sude.setEtatDadhId(datas.getMesl("ETATDADH_ID"));
		sude.setdTypeSupId(datas.getMesl("DTYPSUP_ID"));
		sude.setEntiteId(datas.getMesl("ENTITE_ID"));
		sude.setEntiteNom(datas.getMesl("ENTITE_NOM"));
		sude.setId(datas.getMesl("RECLAM_ID"));
		sude.setJustifie(datas.getMesl("RECLAM_JUSTIFIE"));
		sude.setMail(datas.getMesl("MAIL"));
		sude.setObjDemande(datas.getMesl("RECLAM_OBJ_DEM"));
		sude.setObjReponse(datas.getMesl("RECLAM_OBJET"));
		//sude.setOrganisme(organisme);
		sude.setPriorite(datas.getMesl("RECLAM_PRIORITE"));
		sude.setReclamType(datas.getMesl("RECLAM_TYPE"));
		sude.setReferenceCourrierRecommandeReponse(datas.getMesl("RECLAM_REF_LER_REP"));
		sude.setReferenceCourrierRecommandeDemande(datas.getMesl("RECLAM_REF_LER_DEM"));
		sude.setReponseTypeSupport(datas.getMesl("RTYPSUP_ID"));
		sude.setTypeDemandeur(typeDme);
		sude.setTypeDmaId(datas.getMesl("TYPDMA_ID"));
		sude.setStatus(("1".equals(datas.getMesl("RECLAM_TRAITE"))) ? GlobalVariable.STATUS_TRAITE : GlobalVariable.STATUS_A_TRAITER);

		// Tout un tas d'info pour Reclamation, a transmettre telle qu'elle
		sude.setRECLAM_RECLAMATION(datas.getMesl("RECLAM_RECLAMATION"));
		sude.setRECLAM_JUSTIFIE(datas.getMesl("RECLAM_JUSTIFIE"));
		sude.setRECLAM_RESP_OC(datas.getMesl("RECLAM_RESP_OC"));
		sude.setR_SMOTRE_PRINC_ID(datas.getMesl("R_SMOTRE_PRINC_ID"));
		sude.setR_SCAURE_PRINC_ID(datas.getMesl("R_SCAURE_PRINC_ID"));
		sude.setRECLAM_DT_RA(datas.getMesl("RECLAM_DT_RA"));
		sude.setRECLAM_REP_ATT(datas.getMesl("RECLAM_REP_ATT"));
		sude.setCRATYPSUP_ID(datas.getMesl("CRATYPSUP_ID"));
		sude.setRECLAM_RECOUR(datas.getMesl("RECLAM_RECOUR"));
		sude.setRECLAM_DT_ENV_REC(datas.getMesl("RECLAM_DT_ENV_REC"));
		sude.setRECLAM_DT_REP_REC(datas.getMesl("RECLAM_DT_REP_REC"));
		sude.setRECLAM_DEC_REC(datas.getMesl("RECLAM_DEC_REC"));
		sude.setRECLAM_DEROG(datas.getMesl("RECLAM_DEROG"));
		sude.setRECLAM_DT_ENV_DER(datas.getMesl("RECLAM_DT_ENV_DER"));
		sude.setRECLAM_DT_REP_DER(datas.getMesl("RECLAM_DT_REP_DER"));
		sude.setRECLAM_DEC_DER(datas.getMesl("RECLAM_DEC_DER"));
		sude.setRECLAM_MEDIA(datas.getMesl("RECLAM_MEDIA"));
		sude.setRECLAM_DT_ENV_MED(datas.getMesl("RECLAM_DT_ENV_MED"));
		sude.setRECLAM_DT_REP_MED(datas.getMesl("RECLAM_DT_REP_MED"));
		sude.setRECLAM_DEC_MED(datas.getMesl("RECLAM_DEC_MED"));
		sude.setRECLAM_ACTION(datas.getMesl("RECLAM_ACTION"));
		sude.setR_SMOTRE_SEC_ID(datas.getMesl("R_SMOTRE_SEC_ID"));
		sude.setR_SCAURE_SEC_ID(datas.getMesl("R_SCAURE_SEC_ID"));
		sude.setRECLAM_RESP(datas.getMesl("RECLAM_RESP"));
		sude.setRECLAM_REITER(datas.getMesl("RECLAM_REITER"));
		sude.setRECLAM_TRANS_EXP(datas.getMesl("RECLAM_TRANS_EXP"));
		sude.setRECLAM_DT_ENV_EXP(datas.getMesl("RECLAM_DT_ENV_EXP"));
		sude.setRECLAM_DT_REP_EXP(datas.getMesl("RECLAM_DT_REP_EXP"));
		sude.setRECLAM_TEXTE_1(datas.getMesl("RECLAM_TEXTE_1"));
		sude.setRECLAM_TEXTE_2(datas.getMesl("RECLAM_TEXTE_2"));
		sude.setRECLAM_DT_RELAN(datas.getMesl("RECLAM_DT_RELAN"));
		sude.setRECLAM_DECISION(datas.getMesl("RECLAM_DECISION"));
		sude.setRECLAM_ORIGINE(datas.getMesl("RECLAM_ORIGINE"));

		return sude;
	}

	/**
	 * Rattache un document par son sudeId à une sude existante. Le document doit avoir un eddmId, avec ou sans
	 * rattachement : eddmManager.create ou eddmManager.createEmpty
	 * 
	 * Pour ceux qui ont un doute, eddmId et eddocId on parle de la même chose (pour toute la vérité allez voir la table
	 * DIEDDM_EDDOC bdd starweb). eddmId = doc.idstar_doc.tsstar
	 * 
	 * Attention cela ne reprend pas les documents existant déjà sur la SUDE avant cet ajout. Cela n'est pas génant sur
	 * une SUDE n'ayant pas de document. Il faudrait reprendre toutes les info récupérés lors de l'intérogation afin de
	 * les renvoyer lors de la modification car il me semble qu'il n'est pas possible de demander au cobol de ne faire
	 * qu'un ajout au lieu de tout modifier.
	 * 
	 * @param environnement
	 * @param cmroc
	 * @param libelleNote
	 * @param document
	 * @return
	 * @throws CimutMetierException
	 * @throws CimutFileException
	 * @throws GedeCommonException
	 * @throws CimutDocumentException
	 */
	public void addNoteAndPj(String environnement, String cmroc, String libelleNote, Document document)
			throws CimutMetierException, GedeCommonException {

		if (document.getIdstar() == 0) {
			throw new GedeCommonException("Impossible de ratacher un document sans eddmId à une SUDE.");
		}

		Sude sude = get(DocumentHelper.getIdentifantEntite(document), cmroc, environnement);

		SudeNote sudeNote = new SudeNote();
		sudeNote.setLibelle(libelleNote);
		sudeNote.setSens("N");
		sudeNote.setType("INT");

		SudeNoteDocument sudeNoteDocument = new SudeNoteDocument();
		sudeNoteDocument.setEddocId(GedeIdHelper.getEddocId(document));
		String[] filenameExtension = document.getId().split("\\.(?=[^\\.]+$)");
		sudeNoteDocument.setFileName(filenameExtension[0]);
		sudeNoteDocument.setExtension(filenameExtension[1]);

		sudeNote.getDocuments().add(sudeNoteDocument);

		sude.setNote(sudeNote);
		try {
			this.call(getMesl(sude), "M", cmroc, environnement, true);
		} catch (Exception e) {
			throw new CimutMetierException("Erreur lors de l'ajout d'une note à une SUDE : " + e.getMessage(), e);
		}
	}

	/**
	 * Format le message sous forme de tableau de string (Evolution SUDE)
	 * 
	 * @param content
	 * @return
	 */
	public static HashMap<Integer, Map<String, String>> getNotLib(String content) {
		// Evolution SUDE T2.2016 : on envoie le contenu de la note via un tableau de String
		int j = 1;
		final HashMap<Integer, Map<String, String>> occ2 = new HashMap<Integer, Map<String, String>>();

		if (content != null) {
			//content = content.replaceAll("[^\\x00-\\x7F]", "");
		}

		StringBuilder dummy = new StringBuilder();

		content = escape(content);

		if (content.length() < LONGUEUR_MAX_COBOL_PER_LINE) {
			final Map<String, String> map1 = new HashMap<String, String>();
			map1.put("LLIB_TEXTE", unscape(content));
			occ2.put(1, map1);
		} else {
			int counter = 1;
			Pattern datePatt = Pattern.compile("^(.{1,4})\\$(AMP|GTH|LTH|XDD)\\$.*");
			while (content.length() > LONGUEUR_MAX_COBOL_PER_LINE && counter < MAX_LINE_PER_NOTE) {
				counter++;
				final Map<String, String> map1 = new HashMap<String, String>();

				// Evite de couper en deux une balise que je doit "unescape" derriere
				int coupure = LONGUEUR_MAX_COBOL_PER_LINE;
				if (content.substring(coupure - 4, coupure).indexOf("$") > -1) {
					String current = content.substring(coupure - 5, Math.min(content.length(), coupure + 4));
					Matcher m = datePatt.matcher(current);
					if (m.matches()) {
						coupure -= (5 - m.group(1).length());
					}
				}
				dummy.append(unscape(content.substring(0, coupure)) + "\\n");
				map1.put("LLIB_TEXTE", unscape(content.substring(0, coupure)));
				occ2.put(j, map1);
				content = content.substring(coupure);

				j++;
			}
			final Map<String, String> map1 = new HashMap<String, String>();
			if (content.length() > LONGUEUR_MAX_COBOL_PER_LINE) {
				content = content.substring(0, LONGUEUR_MAX_COBOL_PER_LINE - 50) + " [...]<br>";
			}
			dummy.append(unscape(content));
			map1.put("LLIB_TEXTE", unscape(content));
			occ2.put(j, map1);
		}

		return occ2;
	}

	/**
	 * recupere une demande SUDE sous orme de message logique depuis son id et son cmroc
	 * 
	 * @param idSude
	 * @param cmroc
	 * @return
	 * @throws CimutMetierException
	 */
	private MapDataMesl getSude(String idSude, String cmroc, String environnement) throws CimutMetierException {

		MapDataMesl mapData = new MapDataMesl();
		mapData.addMesl("RECLAM_ID", idSude);
		MapDataMesl datas = null;
		try {
			datas = this.call(mapData, "I", cmroc, environnement, true);
		} catch (Exception e) {
			throw new CimutMetierException("Erreur lors de l\'obtention de la DA  [id " + idSude + "," + cmroc + "] : " + e.getMessage(), e);
		}
		return datas;
	}

	private MapDataMesl getMesl(Sude sude) throws CimutMetierException {

		MapDataMesl mapData = new MapDataMesl();

		try {

			if (sude.getPartenaire() != null) {
				String[] pieceOfPart = sude.getPartenaire().split("\\|");
				if (sude.getInsee() != null) {
					mapData.addMesl("ASSU_INSEE", sude.getInsee());
				}
				if (pieceOfPart.length < 3) {
					// cas sans affectation gerer par SUDE
					mapData.addMesl("TYPDME_ID", "9");
				} else {
					mapData.addMesl("EMETPART_ID", pieceOfPart[0]);
					mapData.addMesl("EMETTYPPART_ID", pieceOfPart[1]);
					mapData.addMesl("EMETPART_NIV", pieceOfPart[2]);
					if ("ETAB".equals(pieceOfPart[1])) {
						mapData.addMesl("TYPDME_ID", "3");
					} else {
						mapData.addMesl("TYPDME_ID", "2");
					}
					
				}
			} else if (sude.getEntreprise() != null) {
				if (sude.getEntreprise().indexOf("|") > -1) {
					String[] pieceOfPart = sude.getEntreprise().split("\\|");
					mapData.addMesl("STRU_NO", pieceOfPart[0]);
					if (!pieceOfPart[1].trim().isEmpty()) {
						mapData.addMesl("STRU_CLASS", pieceOfPart[1]);
					}
					if (pieceOfPart.length == 3) {
						mapData.addMesl("STRU_SECT", pieceOfPart[2]);
						mapData.addMesl("TYPDME_ID", "5");
					} else {
						mapData.addMesl("TYPDME_ID", "4");
					}
				} else {
					// cas sans affectation gerer par SUDE
					mapData.addMesl("STRU_NO", sude.getEntreprise());
					mapData.addMesl("TYPDME_ID", "4");
				}
			} else if (sude.getInsee() != null) {
				mapData.addMesl("ASSU_INSEE", sude.getInsee());
				mapData.addMesl("TYPDME_ID", "0");
			} else {
				// cas sans affectation gerer par SUDE
				mapData.addMesl("TYPDME_ID", "9");
			}

		} catch (Exception e) {
			// on reset l'affectation a inconnu
			LOGGER.warn(e.getMessage(), e);
			mapData = new MapDataMesl();
			mapData.addMesl("TYPDME_ID", "9");
		}

		mapData.addMesl("RECLAM_DT_RECEPTIO", sude.getDateReception());
		mapData.addMesl("RECLAM_DT_DEM", sude.getDateDemande());
		mapData.addMesl("TYPDMA_ID", sude.getTypeDmaId());
		mapData.addMesl("DTYPSUP_ID", sude.getdTypeSupId());
		mapData.addMesl("RECLAM_OBJ_DEM", sude.getObjDemande());
		mapData.addMesl("ENTITE_ID", sude.getEntiteId());
		mapData.addMesl("ENTITE_NOM", sude.getEntiteNom());
		mapData.addMesl("RECLAM_TYPE", sude.getReclamType());
		mapData.addMesl("RECLAM_ADRES1", sude.getAddress1());
		mapData.addMesl("RECLAM_ADRES2", sude.getAddress2());
		mapData.addMesl("RECLAM_ADRES3", sude.getAddress3());
		mapData.addMesl("RECLAM_ADRES4", sude.getAddress4());
		mapData.addMesl("RECLAM_ADRES5", sude.getAddress5());
		mapData.addMesl("RECLAM_ADRES6", sude.getAddress6());
		mapData.addMesl("RECLAM_ADRES7", sude.getAddress7());
		mapData.addMesl("RECLAM_JUSTIFIE", sude.getJustifie());
		mapData.addMesl("RECLAM_PRIORITE", sude.getPriorite());
		mapData.addMesl("RECLAM_DT_REPONSE", sude.getDateReponse());
		mapData.addMesl("RTYPSUP_ID", sude.getReponseTypeSupport());
		mapData.addMesl("RECLAM_OBJET", sude.getObtReponse());
		mapData.addMesl("RECLAM_REF_LER_REP", sude.getReferenceCourrierRecommandeReponse());
		mapData.addMesl("MAIL", sude.getMail());
		//dans le cadre de l'intégration de document si la reference du courier pour le canal LER est renseigné
		if(sude.getReferentielCourrier() != null && !sude.getReferentielCourrier().isEmpty()) {
			mapData.addMesl("RECLAM_REF_LER_DEM", (sude.getReferentielCourrier()));
		}else {
			mapData.addMesl("RECLAM_REF_LER_DEM", sude.getReferenceCourrierRecommandeDemande());
		}
		// Tout un tas d'info pour Reclamation, a transmettre telle qu'elle
		mapData.addMesl("RECLAM_RECLAMATION", sude.getRECLAM_RECLAMATION());
		mapData.addMesl("RECLAM_JUSTIFIE", sude.getRECLAM_JUSTIFIE());
		mapData.addMesl("RECLAM_RESP_OC", sude.getRECLAM_RESP_OC());
		mapData.addMesl("R_SMOTRE_PRINC_ID", sude.getR_SMOTRE_PRINC_ID());
		mapData.addMesl("R_SCAURE_PRINC_ID", sude.getR_SCAURE_PRINC_ID());
		mapData.addMesl("RECLAM_ACTION", sude.getRECLAM_ACTION());
		mapData.addMesl("R_SMOTRE_SEC_ID", sude.getR_SMOTRE_SEC_ID());
		mapData.addMesl("R_SCAURE_SEC_ID", sude.getR_SCAURE_SEC_ID());
		mapData.addMesl("RECLAM_RESP", sude.getRECLAM_RESP());
		mapData.addMesl("RECLAM_REITER", sude.getRECLAM_REITER());
		mapData.addMesl("RECLAM_TEXTE_1", sude.getRECLAM_TEXTE_1());
		mapData.addMesl("RECLAM_TEXTE_2", sude.getRECLAM_TEXTE_2());
		mapData.addMesl("RECLAM_DT_RELAN", sude.getRECLAM_DT_RELAN());
		mapData.addMesl("RECLAM_DECISION", sude.getRECLAM_DECISION());
		mapData.addMesl("RECLAM_ORIGINE", sude.getRECLAM_ORIGINE());

		mapData.addMesl("RECLAM_TRANS_EXP", sude.getRECLAM_TRANS_EXP());
		if (!"0".equals(sude.getRECLAM_TRANS_EXP())) {
			mapData.addMesl("RECLAM_DT_ENV_EXP", sude.getRECLAM_DT_ENV_EXP());
			mapData.addMesl("RECLAM_DT_REP_EXP", sude.getRECLAM_DT_REP_EXP());
		}

		mapData.addMesl("RECLAM_MEDIA", sude.getRECLAM_MEDIA());
		if (!"0".equals(sude.getRECLAM_MEDIA())) {
			mapData.addMesl("RECLAM_DT_ENV_MED", sude.getRECLAM_DT_ENV_MED());
			mapData.addMesl("RECLAM_DT_REP_MED", sude.getRECLAM_DT_REP_MED());
			mapData.addMesl("RECLAM_DEC_MED", sude.getRECLAM_DEC_MED());
		}

		mapData.addMesl("RECLAM_DEROG", sude.getRECLAM_DEROG());
		if (!"0".equals(sude.getRECLAM_DEROG())) {
			mapData.addMesl("RECLAM_DT_ENV_DER", sude.getRECLAM_DT_ENV_DER());
			mapData.addMesl("RECLAM_DT_REP_DER", sude.getRECLAM_DT_REP_DER());
			mapData.addMesl("RECLAM_DEC_DER", sude.getRECLAM_DEC_DER());
		}

		mapData.addMesl("RECLAM_RECOUR", sude.getRECLAM_RECOUR());
		if (!"0".equals(sude.getRECLAM_RECOUR())) {
			mapData.addMesl("RECLAM_DT_ENV_REC", sude.getRECLAM_DT_ENV_REC());
			mapData.addMesl("RECLAM_DT_REP_REC", sude.getRECLAM_DT_REP_REC());
			mapData.addMesl("RECLAM_DEC_REC", sude.getRECLAM_DEC_REC());
		}

		mapData.addMesl("RECLAM_REP_ATT", sude.getRECLAM_REP_ATT());
		if (!"0".equals(sude.getRECLAM_REP_ATT())) {
			mapData.addMesl("RECLAM_DT_RA", sude.getRECLAM_DT_RA());
			mapData.addMesl("CRATYPSUP_ID", sude.getCRATYPSUP_ID());
		}

		if ("1".equals(sude.getAccuseReceptionDone())) {
			mapData.addMesl("RECLAM_ACC_RECEPT", sude.getAccuseReceptionDone());
			mapData.addMesl("RECLAM_DT_AR", sude.getAccuseReceptionDate());
			mapData.addMesl("CARTYPSUP_ID", sude.getAccuseReceptionCanal());
		}
		// gestion des PJ pieces jointes
		if (sude.getNote() != null) {

			SudeNote noteDa = sude.getNote();
			// piece jointe de la partie note
			if ("N".equals(noteDa.getSens())) {

				mapData.addMesl("TYPNOT_ID", noteDa.getType());
				if (noteDa.getLibelle() != null) {
					mapData.addOcc("LIB_NOTE_OCC", getNotLib(noteDa.getLibelle()));
				}

				if (sude.getNote().getDocuments() != null && !sude.getNote().getDocuments().isEmpty()) {

					mapData.addMesl("RECLAM_FLAG_EDDOC", "N");
					mapData.addMesl("NOTE_FLAG_EDDOC", "O");
					int i = 1;
					final HashMap<Integer, Map<String, String>> occ1 = new HashMap<Integer, Map<String, String>>();
					for (SudeNoteDocument occurence : noteDa.getDocuments()) {
						final Map<String, String> map1 = new HashMap<String, String>();
						map1.put("NEDDOC_ID", occurence.getEddocId());
						map1.put("NEDDOC_INTIT", occurence.getFileName());
						map1.put("NEDDOC_TYPE_FICH", occurence.getExtension());
						occ1.put(i, map1);
						i++;
					}
					final String occurenceName = "EDDOC_NOTE_OCC";
					mapData.addOcc(occurenceName, occ1);
				} else {
					mapData.addMesl("RECLAM_FLAG_EDDOC", "N");
					mapData.addMesl("NOTE_FLAG_EDDOC", "N");
				}
			} else if ("R".equals(noteDa.getSens())) {
				if (sude.getNote().getDocuments() != null && !sude.getNote().getDocuments().isEmpty()) {
					mapData.addMesl("RECLAM_FLAG_EDDOC", "O");
					mapData.addMesl("NOTE_FLAG_EDDOC", "N");
					int i = 1;
					final HashMap<Integer, Map<String, String>> occ1 = new HashMap<Integer, Map<String, String>>();
					for (SudeNoteDocument occurence : noteDa.getDocuments()) {
						final Map<String, String> map1 = new HashMap<String, String>();
						map1.put("NEDDOC_ID", occurence.getEddocId());
						map1.put("NEDDOC_INTIT", occurence.getFileName());
						map1.put("NEDDOC_TYPE_FICH", occurence.getExtension());
						occ1.put(i, map1);
						i++;
					}
					final String occurenceName = "EDDOC_REPO_OCC";
					mapData.addOcc(occurenceName, occ1);
				} else {
					mapData.addMesl("RECLAM_FLAG_EDDOC", "N");
					mapData.addMesl("NOTE_FLAG_EDDOC", "N");
				}
			} else {
				mapData.addMesl("RECLAM_FLAG_EDDOC", "N");
				mapData.addMesl("NOTE_FLAG_EDDOC", "N");
			}
		}

		if (sude.getId() != null) {
			mapData.addMesl("RECLAM_ID", sude.getId());
		}
		if (sude.getEtatDadhId() != null) {
			mapData.addMesl("ETATDADH_ID", sude.getEtatDadhId());
		}

		return mapData;
	}

	private static String escape(String content) {
		content = content.replaceAll("&", "\\$AMP\\$");
		content = content.replaceAll(">", "\\$GTH\\$");
		content = content.replaceAll("<", "\\$LTH\\$");
		content = content.replaceAll("\\n", "\\$XDD\\$");
		return content;
	}

	private static String unscape(String content) {
		content = content.replaceAll("\\$AMP\\$", "&");
		content = content.replaceAll("\\$GTH\\$", ">");
		content = content.replaceAll("\\$LTH\\$", "<");
		content = content.replaceAll("\\$XDD\\$", "\n");
		return content;
	}

}
