package fr.cimut.ged.entrant.interrogation.ejb;

import java.util.List;
import java.util.Map;

import javax.ejb.Remote;

import fr.cimut.ged.entrant.beans.mongo.Rule;
import fr.cimut.ged.entrant.beans.mongo.RuleHistorique;

@Remote
@Deprecated
public interface CrdeRemote {

	// ===========================================================================
	// Anciennes méthodes ne comportant par l'environnement dans leur signature
	// (gardées pour rétro-compatibilité, mais devront être supprimées)
	// L'environnement utilisé est le premier environnement configuré de la liste
	// ===========================================================================
	@Deprecated
	public Rule insert(String cmroc, Rule rule);

	@Deprecated
	public Rule get(String cmroc, String id);

	@Deprecated
	public List<Rule> list(String cmroc, Map<String, List<String>> parameters);

	@Deprecated
	public void remove(String cmroc, String id);

	@Deprecated
	public void update(String cmroc, Rule rule);

	@Deprecated
	public List<RuleHistorique> getHist(String cmroc, Rule rule);

	/**
	 * Insère une nouvelle règle
	 * 
	 * @param environnement
	 * @param cmroc
	 * @param rule
	 * @return
	 */
	public Rule insert(String environnement, String cmroc, Rule rule);

	/**
	 * Récupère une règle
	 * 
	 * @param environnement
	 * @param cmroc
	 * @param id
	 * @return
	 */
	public Rule get(String environnement, String cmroc, String id);

	/**
	 * Liste toutes les règles
	 * 
	 * @param environnement
	 * @param cmroc
	 * @param parameters
	 * @return
	 */
	public List<Rule> list(String environnement, String cmroc, Map<String, List<String>> parameters);

	/**
	 * Efface une règle
	 * 
	 * @param environnement
	 * @param cmroc
	 * @param id
	 */
	public void remove(String environnement, String cmroc, String id);

	/**
	 * Met a jour une règle
	 * 
	 * @param environnement
	 * @param cmroc
	 * @param rule
	 */
	public void update(String environnement, String cmroc, Rule rule);

	/**
	 * Récupère la liste des historiques
	 * 
	 * @param environnement
	 * @param cmroc
	 * @param rule
	 * @return
	 */
	public List<RuleHistorique> getHist(String environnement, String cmroc, Rule rule);

}