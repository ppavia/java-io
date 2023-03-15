package fr.cimut.ged.entrant.appelmetier;

import fr.cimut.ged.entrant.beans.TypeEntite;
import fr.cimut.ged.entrant.beans.appelmetier.*;
import fr.cimut.ged.entrant.beans.db.Document;
import fr.cimut.ged.entrant.dao.starwebdao.TechnicalRestDao;
import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.exceptions.CimutDocumentException;
import fr.cimut.ged.entrant.exceptions.CimutMetierException;
import fr.cimut.ged.entrant.exceptions.GedeCommonException;
import fr.cimut.ged.entrant.service.Metier;
import fr.cimut.ged.entrant.utils.GedeIdHelper;
import fr.cimut.ged.entrant.utils.GlobalVariable;
import fr.cimut.mos.MapDataMesl;
import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;

public class EddmManager extends MetierManager {

	public static final String GENERATING = "0100";
	public static final String INTEGRATING = "0110";
	public static final String AVAILABLE = "0200";
	public static final String SENDING = "0210";
	public static final String SENT = "0220";

	public EddmManager() throws CimutConfException {
		super("IHM_EDDM");
	}

	/**
	 * * Permet de creer un enregistrement EDDM bidon
	 * 
	 * @return eddm avec just un identifiant généré
	 */
	public Eddm createEmpty() {
		Eddm eddm = new Eddm();
		eddm.setDocId(GedeIdHelper.getNewEddmId());
		return eddm;
	}

	/**
	 * permet de creer un eddm sans affectation
	 * 
	 * @throws CimutConfException
	 * @throws GedeCommonException
	 */
	public Eddm createEmpty(Document document, String environnement) throws CimutMetierException, CimutDocumentException, GedeCommonException {
		return createEmpty(document, environnement, AVAILABLE, null, null);
	}

	/**
	 * permet de creer un eddm sans affectation et avec rattachement à une sude
	 * 
	 * @param document
	 * @param environnement
	 * @param metier
	 * @param demandeur
	 * @throws CimutConfException
	 * @throws GedeCommonException
	 * 
	 */
	public Eddm createNLinkToSude(Document document, String environnement, Metier metier, String demandeur)
			throws CimutMetierException, CimutDocumentException, GedeCommonException {
		return createEmpty(document, environnement, AVAILABLE, metier, demandeur);
	}

	public Eddm createEmpty(Document document, String environnement, String etatDoc, Metier metier, String demandeur)
			throws CimutMetierException, CimutDocumentException, GedeCommonException {
		DocumentConverter converter = new DocumentConverter();
		Eddm eddm = converter.toEddm(document, false);

		MapDataMesl request = new MapDataMesl();
		request.addMesl("EDDOC_CMROC", eddm.getNumTutelle()); // ! ici on met la tutelle 
		request.addMesl("COURRIER_ID", eddm.getLibDocument());
		request.addMesl("EDDOC_INTIT", eddm.getLibDocument());
		request.addMesl("EDDOC_UTI_DEM", eddm.getUtilisateur());
		request.addMesl("EDDOC_DT_PRVPURGE", "");
		request.addMesl("EDDOC_DT_PRVARCH", "");
		request.addMesl("EDDOC_CD_SENS", eddm.getSens());
		request.addMesl("EDDOC_CD_CANAL", eddm.getCodeCanal());
		request.addMesl("EDDOC_LIB_CANAL", eddm.getLibCanal());
		request.addMesl("ETATDOC_ID", etatDoc);
		request.addMesl("EDDOC_TYPE_FICH", eddm.getExtension());
		// SOL-342 : rattachement par déffaut au rang 1 vu que le rang n'était pas renseigné ici
		request.addMesl("BENE-JUM", document.getDocMongo().getRang());

		// Appel du service metier
		MapDataMesl mapMesl = this.call(request, "GC", eddm.getCmroc(), environnement, true);
		eddm.setDocId(mapMesl.getMesl("EDDOC_ID"));
		GedeIdHelper.setIdstarTstar(document, eddm.getDocId());

		if (metier != null && document.getDocMongo().getTypeEntiteRattachement() == TypeEntite.SUDE) {
			try {
				metier.getSudeManager().addNoteAndPj(environnement, document.getCmroc(), "", document);
			} catch (Exception e) {
				remove(eddm.getDocId(), document.getCmroc(), environnement);
				throw new GedeCommonException(e.getMessage(), e);
			}
		}
		return eddm;
	}

	/**
	 * Permet de cree un enregistrement EDDM à l'état 200(dispo), demandeur _import_ged, pas une notif, pas de
	 * destinataire, pas de ligne d'archivage avec l'origine avec valorisation de l'idstar et tsstar du document
	 * 
	 * @param document
	 * @param metier
	 * @param environnement
	 * @return
	 * @throws CimutMetierException
	 * @throws CimutConfException
	 * @throws CimutDocumentException
	 */
	public Eddm create(Document document, Metier metier, String environnement) throws CimutMetierException, CimutDocumentException {
		return create(document, metier, environnement, AVAILABLE, GlobalVariable.IMPORT_USER, false, null, null);
	}

	/**
	 * Permet de cree un enregistrement EDDM à l'état 200(dispo), -demandeur- , pas une notif, pas de destinataire, pas
	 * de ligne d'archivage avec l'origine avec valorisation de l'idstar et tsstar du document
	 * 
	 * @throws CimutMetierException
	 * @throws CimutDocumentException
	 */
	public Eddm create(Document document, Metier metier, String environnement, String demandeur) throws CimutMetierException, CimutDocumentException {
		return create(document, metier, environnement, AVAILABLE, demandeur, false, null, null);
	}

	public Eddm createWithArchivage(Document document, Metier metier, String environnement, String libelleOrigineArchivage)
			throws CimutMetierException, CimutDocumentException {
		return create(document, metier, environnement, AVAILABLE, GlobalVariable.IMPORT_USER, false, null, libelleOrigineArchivage);
	}

	/**
	 * Permet de cree un enregistrement EDDM avec valorisation de l'idstar et tsstar du document !!! attention a fournir
	 * un document dont la propriété json est valorisée sinon plouf
	 * 
	 * @param document
	 * @param metier
	 * @param environnement
	 * @param etatDoc
	 * @param demandeur
	 * @param isNotif
	 * @param destinataire
	 * @param libelleOrigineArchivage,
	 *            active la création d'une ligne d'archivage dans le dossier starweb
	 * @return
	 * @throws CimutMetierException
	 * @throws CimutDocumentException
	 */
	public Eddm create(Document document, Metier metier, String environnement, String etatDoc, String demandeur, boolean isNotif, String destinataire,
			String libelleOrigineArchivage)
			throws CimutMetierException, CimutDocumentException {

		DocumentConverter converter = new DocumentConverter();
		Eddm eddm = converter.toEddm(document, true, demandeur, destinataire);

		MapDataMesl request = new MapDataMesl();
		request.addMesl("EDDOC_CMROC", eddm.getNumTutelle()); // ! ici on met la tutelle 
		request.addMesl("COURRIER_ID", eddm.getLibDocument());
		request.addMesl("EDDOC_INTIT", eddm.getLibDocument());
		request.addMesl("EDDOC_UTI_DEM", eddm.getUtilisateur());
		request.addMesl("EDDOC_DT_PRVPURGE", "");
		request.addMesl("EDDOC_DT_PRVARCH", "");
		request.addMesl("EDDOC_CD_SENS", eddm.getSens());
		request.addMesl("EDDOC_CD_CANAL", eddm.getCodeCanal());
		request.addMesl("EDDOC_LIB_CANAL", eddm.getLibCanal());
		request.addMesl("ETATDOC_ID", etatDoc);
		request.addMesl("EDDOC_TYPE_FICH", eddm.getExtension());
		request.addMesl("EDDOC_NOTIFICATION", (isNotif) ? "O" : "");
		if (libelleOrigineArchivage != null) {
			request.addMesl("EDDOC-ID-ORIGINE", libelleOrigineArchivage);
		}

		TypeEntite typeEntite = eddm.getTypeEntite();

		if (typeEntite == TypeEntite.PERSONNE && eddm.getSassu() != null && !eddm.getSassu().isEmpty()) {

			if (eddm.getInsee() == null || eddm.getInsee().isEmpty()) {
				try {
					Alph alph = metier.getAlphManager().get(eddm.getSassu(), eddm.getCmroc(), environnement);
					eddm.setInsee(alph.getInsee());
					eddm.setCodePostal(alph.getCodePostal());
				} catch (Exception e) {
					Logger.getLogger(EddmManager.class).warn("Erreur ALPH : " + e.getMessage());
					throw new CimutMetierException(e);
				}
			}
			request.addMesl("ASSU_S_ASSURE", eddm.getSassu());

		} else if (typeEntite == TypeEntite.PERSONNE && eddm.getInsee() != null && !eddm.getInsee().isEmpty()) {

			if (eddm.getSassu() == null || eddm.getSassu().isEmpty()) {
				try {
					Alph alph = metier.getAlphManager().get(eddm.getInsee(), eddm.getCmroc(), environnement);
					eddm.setSassu(alph.getsAssu());
					eddm.setCodePostal(alph.getCodePostal());
				} catch (Exception e) {
					Logger.getLogger(EddmManager.class).warn("Erreur ALPH : " + e.getMessage());
					throw new CimutMetierException(e);
				}
			}
			request.addMesl("ASSU_S_ASSURE", eddm.getSassu());

		} else if (typeEntite == TypeEntite.ENTREPRISE && eddm.getIdentreprise() != null && !eddm.getIdentreprise().isEmpty()) {

			if (eddm.getIdSystemEntreprise() == null || eddm.getIdSystemEntreprise().isEmpty()) {
				try {
					Stru stru = metier.getStruManager().get(eddm.getIdentreprise(), eddm.getCmroc(), environnement);
					eddm.setIdSystemEntreprise(stru.getId());
				} catch (Exception e) {
					Logger.getLogger(EddmManager.class).warn("Erreur STRU : " + e.getMessage());
					throw new CimutMetierException(e);
				}
			}

			if (eddm.getIdentreprise().matches(".*\\|.*\\|.*")) {
				request.addMesl("CTSAN_S_SECTION", eddm.getIdSystemEntreprise());
				request.addMesl("ENTRPRIS_ID", "");
			} else {
				request.addMesl("ENTRPRIS_ID", eddm.getIdSystemEntreprise());
				request.addMesl("CTSAN_S_SECTION", "");
			}

		} else if (typeEntite == TypeEntite.PARTENAIRE && StringUtils.isNotBlank(eddm.getPartId())) {
			try {
				Part part = metier.getPartManager().get(eddm.getPartId() + "|" + eddm.getTypPartId() + "|" + eddm.getPartNiv(), eddm.getCmroc(),
						environnement);
				eddm.setCodePostal(part.getCodePostal());
			} catch (Exception e) {
				Logger.getLogger(EddmManager.class).warn("Erreur PART : " + e.getMessage());
				throw new CimutMetierException(e);
			}
			request.addMesl("PART_ID", eddm.getPartId());
			request.addMesl("PART_NIV", eddm.getPartNiv());
			request.addMesl("TYPPART_ID", eddm.getTypPartId());
			
			if (eddm.getInsee() != null && !eddm.getInsee().isEmpty()) {
				if (eddm.getSassu() == null || eddm.getSassu().isEmpty()) {
					try {
						Alph alph = metier.getAlphManager().get(eddm.getInsee(), eddm.getCmroc(), environnement);
						eddm.setSassu(alph.getsAssu());
						eddm.setCodePostal(alph.getCodePostal());
					} catch (Exception e) {
						Logger.getLogger(EddmManager.class).warn("Erreur ALPH : " + e.getMessage());
						throw new CimutMetierException(e);
					}
				}
				request.addMesl("ASSU_S_ASSURE", eddm.getSassu());
			}
			if( eddm.getSassu() != null && !eddm.getSassu().isEmpty()) {
				if (eddm.getInsee() == null || eddm.getInsee().isEmpty()) {
					try {
						Alph alph = metier.getAlphManager().get(eddm.getSassu(), eddm.getCmroc(), environnement);
						eddm.setInsee(alph.getInsee());
						eddm.setCodePostal(alph.getCodePostal());
					} catch (Exception e) {
						Logger.getLogger(EddmManager.class).warn("Erreur ALPH : " + e.getMessage());
						throw new CimutMetierException(e);
					}
				}
				request.addMesl("ASSU_S_ASSURE", eddm.getSassu());
			}	
		} else {
			throw new CimutMetierException("Aucune affectation possible pour l'entité [" + typeEntite + "], eddm : " + eddm.toString());
		}

		boolean affectationFailed = false;
		String message = "";
		// Appel du service metier
		MapDataMesl mapMesl = this.call(request, "GC", eddm.getCmroc(), environnement, true);
		eddm.setDocId(mapMesl.getMesl("EDDOC_ID"));
		GedeIdHelper.setIdstarTstar(document, eddm.getDocId());

		if (typeEntite == TypeEntite.PERSONNE) {

			if (eddm.getSassu() != null && !eddm.getSassu().isEmpty() && mapMesl.getMesl("ASSU_S_ASSURE") != null
					&& mapMesl.getMesl("ASSU_S_ASSURE").replaceAll("^0+", "").equals(eddm.getSassu().replaceAll("^0+", ""))) {
			} else if (eddm.getInsee() != null && !eddm.getInsee().isEmpty() && mapMesl.getMesl("ASSU_INSEE") != null
					&& mapMesl.getMesl("ASSU_INSEE").equals(eddm.getInsee())) {
			} else {
				if (eddm.getSassu() != null) {
					message += "ASSU_S_ASSURE : " + eddm.getSassu();
				} else if (eddm.getInsee() != null) {
					message += "ASSU_INSEE : " + eddm.getInsee();
				}
				affectationFailed = true;
			}

		} else if (typeEntite == TypeEntite.PARTENAIRE) {

			if (!mapMesl.getMesl("PART_ID").equals(eddm.getPartId())) {
				affectationFailed = true;
				message += "PART_ID : " + eddm.getPartId();
			}
		} else if (typeEntite == TypeEntite.ENTREPRISE) {

			if (Integer.parseInt(mapMesl.getMesl("ENTRPRIS_ID")) != Integer.parseInt(eddm.getIdSystemEntreprise())
					&& Integer.parseInt(mapMesl.getMesl("CTSAN_S_SECTION")) != Integer.parseInt(eddm.getIdSystemEntreprise())) {
				affectationFailed = true;
				message += "ENTRPRIS_ID : " + eddm.getIdSystemEntreprise();
			}
		}

		if (affectationFailed) {
			eddm.setEtat("L'affectation du document pour l'entite " + typeEntite + " a echoué : " + message);
			throw new CimutMetierException("L'affectation du document pour l'entite " + typeEntite + " a echoué : " + message);
		}

		return eddm;
	}

	/**
	 * Efface un enregistrement EDDM
	 * 
	 * @param id
	 * @param cmroc
	 * @param environnement
	 * @throws CimutMetierException
	 */
	public void remove(String id, String cmroc, String environnement) throws CimutMetierException {
		if (id != null && !id.contains("_99")) {
			MapDataMesl request = new MapDataMesl();
			request.addMesl("EDDOC_ID", id);
			this.call(request, "S", cmroc, environnement, true);
		}
	}

	/**
	 * Met a jour un enregistrement EDDM
	 * 
	 * @param document
	 * @param environnement
	 * @throws CimutMetierException
	 */
	public void update(Document document, String environnement) throws CimutMetierException {
		//Declaration des variables

		DocumentConverter converter = new DocumentConverter();
		Eddm eddm = converter.toEddm(document, true);
		MapDataMesl request = new MapDataMesl();
		request.addMesl("EDDOC_TYPE_FICH", eddm.getExtension());
		request.addMesl("EDDOC_ID", eddm.getDocId());
		request.addMesl("ETATDOC_ID", INTEGRATING);
		// TODO faire une demande d'evol du cobol
		this.call(request, "GM", eddm.getCmroc(), environnement, false);
		request.addMesl("ETATDOC_ID", AVAILABLE);
		this.call(request, "GM", eddm.getCmroc(), environnement, true);
	}

}
