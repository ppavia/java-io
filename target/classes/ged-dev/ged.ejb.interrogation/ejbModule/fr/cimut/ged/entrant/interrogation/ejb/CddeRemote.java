package fr.cimut.ged.entrant.interrogation.ejb;

import java.util.List;
import java.util.Map;

import javax.ejb.Remote;

import fr.cimut.ged.entrant.beans.db.Document;
import fr.cimut.ged.entrant.beans.db.Historique;
import fr.cimut.ged.entrant.beans.db.User;
import fr.cimut.ged.entrant.beans.mongo.Parameter;

@Remote
@Deprecated
public interface CddeRemote {

	// ===========================================================================
	// Anciennes méthodes ne comportant par l'environnement dans leur signature
	// (gardées pour rétro-compatibilité, mais devront être supprimées)
	// L'environnement utilisé est le premier environnement configuré de la liste
	// ===========================================================================
	@Deprecated
	public List<fr.cimut.ged.entrant.beans.mongo.DocumentMongo> listIndexDocument(Map<String, String> parameters, User user);

	@Deprecated
	public String listString(Map<String, String> parameters, User user);

	@Deprecated
	public String stats(Map<String, String> parameters, User user);

	@Deprecated
	public Document update(Map<String, String> parameters, User user);

	@Deprecated
	public Document status(Map<String, String> map, User user);

	@Deprecated
	public Parameter getParameter(String cmroc, Map<String, String> search);

	@Deprecated
	public void deleteSude(List<String> eddocIds, User user);

	@Deprecated
	public void updateStatusSude(List<String> eddocIds, User user);

	/**
	 * liste les beans documents présents en base d'indexation mongo
	 * 
	 * @param parameters
	 *            la map des clés à rechercher en base mongo
	 * @param user
	 *            l'utilisateur
	 * @return les list des documents
	 */
	public List<fr.cimut.ged.entrant.beans.mongo.DocumentMongo> listIndexDocument(String environnement, Map<String, String> parameters, User user);

	/**
	 * liste les documents sous forme de string json
	 * 
	 * @param parameters
	 * @param user
	 * @return
	 */
	public String listString(String environnement, Map<String, String> parameters, User user);

	public Document get(String id, User user);

	public String stats(String environnement, Map<String, String> parameters, User user);

	public Document update(String environnement, Map<String, String> parameters, User user);

	public Document status(String environnement, Map<String, String> map, User user);

	public List<Historique> getHistoriques(String id, User user);

	public Parameter getParameter(String environnement, String cmroc, Map<String, String> search);

	public void deleteSude(String environnement, List<String> eddocIds, User user);

	public void updateStatusSude(String environnement, List<String> eddocIds, User user);
}
