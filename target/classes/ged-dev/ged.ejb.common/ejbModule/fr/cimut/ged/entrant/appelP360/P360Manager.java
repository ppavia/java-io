package fr.cimut.ged.entrant.appelP360;

import java.util.List;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;
import org.apache.log4j.Logger;
import org.jboss.resteasy.client.ClientRequest;
import org.jboss.resteasy.client.ClientResponse;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

import fr.cimut.ged.entrant.appelmetier.EddmManager;
import fr.cimut.ged.entrant.beans.TypeEntite;
import fr.cimut.ged.entrant.beans.appelmetier.Alph;
import fr.cimut.ged.entrant.beans.appelmetier.Stru;
import fr.cimut.ged.entrant.beans.mongo.Departement;
import fr.cimut.ged.entrant.beans.mongo.DocumentMongo;
import fr.cimut.ged.entrant.beans.mongo.Region;
import fr.cimut.ged.entrant.beans.p360.CoupleCmrocValeur;
import fr.cimut.ged.entrant.beans.p360.Etablissement;
import fr.cimut.ged.entrant.beans.p360.PartenaireSante;
import fr.cimut.ged.entrant.beans.p360.PersonneRORCProspect;
import fr.cimut.ged.entrant.beans.p360.ReponseSearch360Agregee;
import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.exceptions.CimutDocumentException;
import fr.cimut.ged.entrant.exceptions.CimutMetierException;
import fr.cimut.ged.entrant.service.Metier;
import fr.cimut.ged.entrant.utils.GlobalVariable;
import fr.cimut.ged.entrant.utils.OrganismeHelper;

public class P360Manager {

	private static final Logger LOGGER = Logger.getLogger(P360Manager.class);

	/**
	 * Appel le portail 360 avec l'email
	 * 
	 * @param email
	 * @param cmroc
	 * @return
	 * @throws CimutConfException
	 * @throws CimutMetierException
	 */
	public ReponseSearch360Agregee get(String email, String cmroc) throws CimutConfException, CimutMetierException {
		String stringigyJob = "term=" + email;

		String keyP360 = GlobalVariable.getP360KeyForCmroc(cmroc);
		if (StringUtils.isBlank(keyP360)) {
			// si pas de clé d'appel, pas la peine de faire une requete vers le portail 360 il nous jettera
			String errorMsg = "Appel au portail 360 en echec, pas de clé d'appel au portail 360 pour le cmroc " + cmroc;
			LOGGER.error(errorMsg);
			throw new CimutMetierException(errorMsg);
		}

		ClientRequest request = new ClientRequest(GlobalVariable.getUrlP360());
		request.accept("application/json");
		request.body("application/x-www-form-urlencoded", stringigyJob);
		request.header("key", keyP360);

		ReponseSearch360Agregee entity = null;
		try {
			ClientResponse<ReponseSearch360Agregee> req = request.post();
			if (req.getStatus() != 200) {
				throw new CimutMetierException(
						"Appel au portail 360 en echec; http status " + req.getStatus() + " => " + req.getEntity(String.class));
			} else {
				String json = req.getEntity(String.class);
				ObjectMapper mapper = new ObjectMapper();
				mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
				entity = mapper.readValue(json, ReponseSearch360Agregee.class);
				Logger.getLogger(EddmManager.class).info(json);
			}
		} catch (Exception e1) {
			LOGGER.error("Appel au portail 360 en echec ", e1);
			throw new CimutMetierException("Appel au portail 360 en echec", e1);
		} finally {
			if (request != null) {
				request.clear();
			}
		}

		return entity;
	}

	private DocumentMongo setNoAffectation(DocumentMongo json, String cmroc) throws CimutConfException {
		json.setTypeEntiteRattachement(null);
		json.setStatus(GlobalVariable.STATUS_NOAFFECT);
		json.setTutelle(cmroc);
		json.setCmroc(OrganismeHelper.getOrganisme(cmroc));
		return json;
	}

	/**
	 * recupere sous form de json l'affectation determiner depuis le webservice portail 360
	 * 
	 * @param sender
	 * @param cmroc
	 * @param metier
	 * @param environnement
	 * @return
	 * @throws CimutConfException
	 * @throws CimutMetierException
	 * @throws CimutDocumentException
	 */
	public DocumentMongo getAffectation(String sender, String cmroc, Metier metier, String environnement)
			throws CimutConfException, CimutMetierException, CimutDocumentException {

		fr.cimut.ged.entrant.beans.mongo.DocumentMongo json = new fr.cimut.ged.entrant.beans.mongo.DocumentMongo();

		boolean foundOccurence = false;

		try {

			ReponseSearch360Agregee response = get(sender, cmroc);

			if (response.getMessage() == null) {

				if (response.getPartenaires() != null && !response.getPartenaires().isEmpty()) {

					if (response.getPartenaires().size() == 1) {
						PartenaireSante partenaire = response.getPartenaires().get(0);
						json.setTypeEntiteRattachement(TypeEntite.PARTENAIRE);
						json.setTutelle(cmroc);
						json.setCmroc(OrganismeHelper.getOrganisme(cmroc));
						json.setCodePostal(partenaire.getCodePostal());

						if (partenaire.getCodePostal() != null) {
							json.setRegion(Region.getRegion(partenaire.getCodePostal()));
							json.setDepartement(Departement.getDepartement(partenaire.getCodePostal()));
						}

						json.setVille(partenaire.getCommune());
						json.setNom(partenaire.getNom());
						json.setPrenom(partenaire.getPrenom());
						json.getAttributes().put("telephone", partenaire.getTelephone());
						json.getAttributes().put("email", partenaire.getEmail());
						json.setIdProf(partenaire.getFiness() + "|PS|0000");
						foundOccurence = true;
					} else {
						throw new CimutMetierException("Plusieurs occurences trouvées");
					}
				}

				if (response.getEtablissements() != null && !response.getEtablissements().isEmpty()) {
					if (response.getEtablissements().size() == 1) {
						if (foundOccurence) {
							throw new CimutMetierException("Plusieurs occurences trouvées");
						} else {
							foundOccurence = true;
						}
						Etablissement entreprise = response.getEtablissements().get(0);

						// attention, j'ai besoin de l'identifiant systeme de l'etreprise pour realisé l'affecation EDDM.
						// du coup, il me faut faire un appel STRU !

						json.setTypeEntiteRattachement(TypeEntite.ENTREPRISE);
						json.setTutelle(cmroc);
						json.setCmroc(OrganismeHelper.getOrganisme(cmroc));
						json.setCodePostal(entreprise.getCodePostal());
						if (entreprise.getCodePostal() != null) {
							json.setRegion(Region.getRegion(entreprise.getCodePostal()));
							json.setDepartement(Departement.getDepartement(entreprise.getCodePostal()));
						}
						json.setVille(entreprise.getCommune());
						json.setNomEntreprise(entreprise.getRaisonSociale());
						json.getAttributes().put("telephone", entreprise.getTelephone());
						json.getAttributes().put("email", entreprise.getEmail());
						json.getAttributes().put("codeContrat", entreprise.getCodeContrat());
						json.setIdEntreprise(entreprise.getSiren() + "|" + entreprise.getNic());
						foundOccurence = true;
					}
				}

				if (response.getPersonnes() != null && !response.getPersonnes().isEmpty()) {

					List<PersonneRORCProspect> personnes = response.getPersonnes();
					for (PersonneRORCProspect result : personnes) {

						Set<CoupleCmrocValeur<String>> adherents = result.getNumAdherents();
						if (adherents != null && !adherents.isEmpty()) {
							CoupleCmrocValeur<String> adherent = null;

							for (CoupleCmrocValeur<String> adh : adherents) {
								String organisme = OrganismeHelper.getOrganisme(adh.getCmroc());
								// TODO vu avec johann, (sensibilisé) verifier le truc des organismes/tutelles 
								// plutot choisir le  :
								// if (OrganismeHelper.getTutelles(cmroc).contains(adh.getCmroc()))
								if (cmroc.equals(organisme)) {
									adherent = adh;
									if (foundOccurence) {
										throw new CimutMetierException("Plusieurs occurences trouvées");
									} else {
										foundOccurence = true;
									}
								}
							}

							if (foundOccurence) {
								json.setTypeEntiteRattachement(TypeEntite.PERSONNE);
								json.setRegime(adherent.getRegime().toString());
								json.setAssuInsee(result.getInseeAssure());
								json.setNumAdherent(adherent.getValeur());
								json.setRang(result.getRang());
								json.setTutelle(adherent.getCmroc());
								json.setCmroc(OrganismeHelper.getOrganisme(adherent.getCmroc()));
								json.setCodePostal(result.getCodePostal());
								if (result.getCodePostal() != null) {
									json.setRegion(Region.getRegion(result.getCodePostal()));
									json.setDepartement(Departement.getDepartement(result.getCodePostal()));
								}
								json.setVille(result.getVille());
								json.setNom(result.getNom());
								json.setPrenom(result.getPrenom());
							}
						}
					}
				}
			} else {
				throw new CimutMetierException(response.getMessage());
			}
			if (!foundOccurence) {
				throw new CimutMetierException("Aucune affectation trouvée");
			} else {
				json = valideP360Affectation(json, metier, environnement);
			}

		} catch (Exception e) {
			// TODO : on ne devrait pas logger une stack trace lorsqu'aucune affectation n'est trouvée
			LOGGER.error(e.getMessage() + " (" + sender + "," + cmroc + ")", e);
			json = setNoAffectation(json, cmroc);
		}
		return json;
	}

	public fr.cimut.ged.entrant.beans.mongo.DocumentMongo valideP360Affectation(fr.cimut.ged.entrant.beans.mongo.DocumentMongo docMongo, Metier metier,
			String environnement)
			throws CimutMetierException, CimutConfException {

		TypeEntite typeDocument = docMongo.getTypeEntiteRattachement();

		if (typeDocument == TypeEntite.PERSONNE && docMongo.getNumAdherent() != null && !docMongo.getNumAdherent().isEmpty()) {
			try {
				Alph alph = metier.getAlphManager().get(docMongo.getAssuInsee(), docMongo.getCmroc(), environnement);
				if (docMongo.getNumAdherent() != null && alph.getsAssu() != null && docMongo.getNumAdherent().matches("\\d+")
						&& alph.getsAssu().matches("\\d+")) {
					Long numAdherant1 = Long.valueOf(docMongo.getNumAdherent());
					Long numAdherant2 = Long.valueOf(alph.getsAssu());
					if (!numAdherant1.equals(numAdherant2)) {
						LOGGER.warn(docMongo.getNumAdherent() + " != " + alph.getsAssu());
					}
					docMongo.setNumAdherent(alph.getsAssu());
				} else {
					LOGGER.warn(docMongo.getNumAdherent() + " != " + alph.getsAssu());
				}
			} catch (Exception e) {
				LOGGER.warn("Erreur ALPH : " + e.getMessage());
				throw new CimutMetierException(e);
			}
		}
		if (typeDocument == TypeEntite.PERSONNE && docMongo.getNumAdherent() != null && !docMongo.getNumAdherent().isEmpty()) {
			if (docMongo.getAssuInsee() == null || docMongo.getAssuInsee().isEmpty()) {
				try {
					Alph alph = metier.getAlphManager().get(docMongo.getNumAdherent(), docMongo.getCmroc(), environnement);
					docMongo.setAssuInsee(alph.getInsee());
				} catch (Exception e) {
					LOGGER.warn("Erreur ALPH : " + e.getMessage());
					throw new CimutMetierException(e);
				}
			}
		} else if (typeDocument == TypeEntite.PERSONNE && docMongo.getAssuInsee() != null && !docMongo.getAssuInsee().isEmpty()) {
			if (docMongo.getNumAdherent() != null && !docMongo.getNumAdherent().isEmpty()) {
				try {
					Alph alph = metier.getAlphManager().get(docMongo.getAssuInsee(), docMongo.getCmroc(), environnement);
					docMongo.setNumAdherent(alph.getsAssu());
				} catch (Exception e) {
					LOGGER.warn("Erreur ALPH : " + e.getMessage());
					throw new CimutMetierException(e);
				}
			}
		} else if (typeDocument == TypeEntite.ENTREPRISE && docMongo.getIdEntreprise() != null && !docMongo.getIdEntreprise().isEmpty()) {
			if (docMongo.getIdSystemEntreprise() == null || docMongo.getIdSystemEntreprise().isEmpty()) {
				try {
					Stru stru = metier.getStruManager().get(docMongo.getIdEntreprise(), docMongo.getCmroc(), environnement);
					docMongo.setIdSystemEntreprise(stru.getId());
					if (docMongo.getIdEntreprise() != null && docMongo.getIdEntreprise().matches("^\\d{9}\\|\\d{5}$")) {
						docMongo.setIdEntreprise(stru.getNumInterne() + "|" + stru.getClasse());
					}
				} catch (Exception e) {
					LOGGER.warn("Erreur STRU : " + e.getMessage());
					throw new CimutMetierException(e);
				}
			}
		} else if (typeDocument == TypeEntite.PARTENAIRE && docMongo.getIdProf() != null && !docMongo.getIdProf().isEmpty()) {
			try {
				metier.getPartManager().get(docMongo.getIdProf(), docMongo.getCmroc(), environnement);
			} catch (Exception e) {
				LOGGER.warn("Erreur PART : " + e.getMessage());
				throw new CimutMetierException(e);
			}
		} else {
			throw new CimutMetierException("Aucune affectation possible pour typeDocument [" + typeDocument + "], " + " json.getIdProf() ["
					+ docMongo.getIdProf() + "], " + " json.getIdEntreprise() [" + docMongo.getIdEntreprise() + "], " + " json.getNumAdherent() ["
					+ docMongo.getNumAdherent() + "], " + " json.getAssuInsee() [" + docMongo.getAssuInsee() + "], ");
		}
		return docMongo;
	}

}
