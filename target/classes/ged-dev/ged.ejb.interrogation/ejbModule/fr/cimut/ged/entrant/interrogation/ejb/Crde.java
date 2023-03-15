package fr.cimut.ged.entrant.interrogation.ejb;

import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.ejb.LocalBean;
import javax.ejb.Stateless;
import javax.ejb.TransactionAttribute;
import javax.ejb.TransactionAttributeType;

import org.apache.log4j.Logger;

import fr.cimut.ged.entrant.beans.mongo.Rule;
import fr.cimut.ged.entrant.beans.mongo.RuleHistorique;
import fr.cimut.ged.entrant.service.Rules;
import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.utils.EnvironnementHelper;

/**
 * Session Bean implementation class Crde
 */

@Stateless(mappedName = "Crde")
@LocalBean
@TransactionAttribute(value = TransactionAttributeType.NEVER)
@Deprecated
public class Crde implements CrdeRemote {

	@EJB
	Rules crde;

	private Logger logger = Logger.getLogger(this.getClass());

	/**
	 * Default constructor.
	 */

	public Crde() {

	}

	/**
	 * Vérifie la validité de l'environnement fourni
	 * 
	 * @param environnement
	 *            L'environnement à vérifier
	 * @return L'environnement à utiliser
	 * @throws RuntimeException
	 *             Levée si l'environnement fourni est invalide ou si une {@link CimutConfException} est levée
	 * @see EnvironnementHelper#determinerEnvironnement
	 */
	private String checkEnvironnement(String environnement) throws RuntimeException {
		String envirGere = null;
		try {
			envirGere = EnvironnementHelper.determinerEnvironnement(environnement);
		} catch (CimutConfException e) {
			logger.fatal("Erreur de configuration : " + e.getMessage(), e);
			throw new RuntimeException(e);
		}
		if (envirGere == null) {
			logger.fatal("Environnement invalide (non géré) : " + environnement);
			throw new RuntimeException("Environnement invalide (non géré) : " + environnement);
		}
		return envirGere;
	}

	@Override
	public Rule insert(String environnement, String cmroc, Rule rule) {
		// Vérification de la validité de l'environnement
		environnement = checkEnvironnement(environnement);

		// Vérification que le CMROC est défini dans la Rule
		if (rule.getCmroc() == null || rule.getCmroc().isEmpty()) {
			rule.setCmroc(cmroc);
		}

		return crde.insert(environnement, cmroc, rule);
	}

	@Override
	public Rule get(String environnement, String cmroc, String id) {
		// Vérification de la validité de l'environnement
		environnement = checkEnvironnement(environnement);

		return crde.get(environnement, cmroc, id);
	}

	@Override
	public List<Rule> list(String environnement, String cmroc, Map<String, List<String>> parameters) {
		// Vérification de la validité de l'environnement
		environnement = checkEnvironnement(environnement);

		return crde.list(environnement, cmroc, parameters);
	}

	@Override
	public void remove(String environnement, String cmroc, String id) {
		// Vérification de la validité de l'environnement
		environnement = checkEnvironnement(environnement);

		crde.remove(environnement, cmroc, id);
	}

	@Override
	public void update(String environnement, String cmroc, Rule rule) {
		// Vérification de la validité de l'environnement
		environnement = checkEnvironnement(environnement);

		// Vérification que le CMROC est défini dans la Rule
		if (rule.getCmroc() == null || rule.getCmroc().isEmpty()) {
			rule.setCmroc(cmroc);
		}

		crde.update(environnement, cmroc, rule);
	}

	@Override
	public List<RuleHistorique> getHist(String environnement, String cmroc, Rule rule) {
		// Vérification de la validité de l'environnement
		environnement = checkEnvironnement(environnement);

		return crde.getHistorique(environnement, cmroc, rule.getId());
	}

	// ===========================================================================
	// Anciennes méthodes ne comportant par l'environnement dans leur signature
	// (gardées pour rétro-compatibilité, mais devront être supprimées)
	// L'environnement utilisé est le premier environnement configuré de la liste
	// ===========================================================================

	@Deprecated
	private String getEnvironnementParDefaut() {
		String environnement = null;
		try {
			environnement = EnvironnementHelper.getEnvironnements().get(0);
		} catch (CimutConfException e) {
		}
		return environnement;
	}

	@Override
	@Deprecated
	public Rule insert(String cmroc, Rule rule) {
		return insert(getEnvironnementParDefaut(), cmroc, rule);
	}

	@Override
	@Deprecated
	public Rule get(String cmroc, String id) {
		return get(getEnvironnementParDefaut(), cmroc, id);
	}

	@Override
	@Deprecated
	public List<Rule> list(String cmroc, Map<String, List<String>> parameters) {
		return list(getEnvironnementParDefaut(), cmroc, parameters);
	}

	@Override
	@Deprecated
	public void remove(String cmroc, String id) {
		remove(getEnvironnementParDefaut(), cmroc, id);
	}

	@Override
	@Deprecated
	public void update(String cmroc, Rule rule) {
		update(getEnvironnementParDefaut(), cmroc, rule);
	}

	@Override
	@Deprecated
	public List<RuleHistorique> getHist(String cmroc, Rule rule) {
		return getHist(getEnvironnementParDefaut(), cmroc, rule);
	}

}