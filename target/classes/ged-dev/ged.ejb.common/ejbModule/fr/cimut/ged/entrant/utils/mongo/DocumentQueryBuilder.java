package fr.cimut.ged.entrant.utils.mongo;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import com.mongodb.AggregationOutput;
import com.mongodb.BasicDBList;
import com.mongodb.BasicDBObject;
import com.mongodb.DBCursor;
import com.mongodb.DBObject;

import fr.cimut.ged.entrant.beans.mongo.Rule;
import fr.cimut.ged.entrant.beans.mongo.RuleCriteres;
import fr.cimut.ged.entrant.exceptions.CimutMongoDBException;
import fr.cimut.ged.entrant.utils.GlobalVariable;

public class DocumentQueryBuilder {

	//attributs techniques
	private static Logger logger = Logger.getLogger(DocumentQueryBuilder.class);

	private DocumentQueryBuilder() {

	}

	/**
	 * Retourne le nombre de document a retourner
	 * 
	 * @param parameters
	 * @return
	 * @throws CimutMongoDBException
	 */
	public static int getLimit(Map<String, String> parameters) throws CimutMongoDBException {

		int limit = GlobalVariable.DEFAULT_PAGESIZE;
		if (parameters.containsKey(GlobalVariable.ATTR_PAGESIZE)) {
			try {
				limit = Integer.parseInt(parameters.get(GlobalVariable.ATTR_PAGESIZE));
			} catch (NumberFormatException exception) {
				throw new CimutMongoDBException("Erreur de parsing [" + GlobalVariable.ATTR_PAGESIZE + "]");
			}
		}
		return limit;
	}

	/**
	 * Retourne le nombre de document a skipper
	 * 
	 * @param parameters
	 * @return
	 * @throws CimutMongoDBException
	 */
	public static int getSkip(Map<String, String> parameters) throws CimutMongoDBException {

		int skip = GlobalVariable.DEFAULT_PAGENUM;
		if (parameters.containsKey(GlobalVariable.ATTR_PAGENUM)) {
			try {
				skip = Integer.parseInt(parameters.get(GlobalVariable.ATTR_PAGENUM));
			} catch (NumberFormatException exception) {
				throw new CimutMongoDBException("Erreur de parsing [" + GlobalVariable.ATTR_PAGESIZE + "][" + GlobalVariable.ATTR_PAGENUM + "]");
			}
		}
		return skip;
	}

	public static DBObject getSort(Map<String, String> parameters) {
		DBObject obj = new BasicDBObject();
		if (parameters.containsKey(GlobalVariable.ATTR_SORTING) && !parameters.get(GlobalVariable.ATTR_SORTING).isEmpty()) {
			obj.put(GlobalVariable.ATTR_DATE_CREATION, (parameters.get(GlobalVariable.ATTR_SORTING).equals("desc")) ? -1 : 1);
		} else {
			obj.put(GlobalVariable.ATTR_DATE_CREATION, (GlobalVariable.DEFAULT_SORTING.equals("desc")) ? -1 : 1);
		}
		return obj;
	}

	/**
	 * recupere la query mongo pour recuperer les EDDMs à traiter
	 * 
	 * @return BasicDBObject
	 */
	public static BasicDBObject getQueryForFailedEDDM() {
		BasicDBObject queryDoc = new BasicDBObject();
		queryDoc.put(GlobalVariable.ATTR_STATUS, fr.cimut.ged.entrant.utils.GlobalVariable.STATUS_A_TRAITER);
		queryDoc.put(GlobalVariable.ATTR_EDDOC_ID, new BasicDBObject("$exists", false));
		queryDoc.put(GlobalVariable.ATTR_SHOW_RULE, new BasicDBObject("$exists", false));
		queryDoc.put(GlobalVariable.ATTR_DESTINATAIRE, new BasicDBObject("$exists", false));
		return queryDoc;
	}

	/**
	 * recupere la query mongo pour une regle, en vue d'obtenir les DAs a cree
	 * 
	 * @param rule
	 *            BasicDBObject
	 * @return
	 */
	public static BasicDBObject getQueryFromRuleForDA(Rule rule) {

		BasicDBObject queryDoc = new BasicDBObject();
		queryDoc.put(GlobalVariable.ATTR_STATUS, fr.cimut.ged.entrant.utils.GlobalVariable.STATUS_A_TRAITER);
		queryDoc.put(GlobalVariable.ATTR_DA_ID, new BasicDBObject("$exists", false));
		DBObject eddocIdExist = new BasicDBObject(GlobalVariable.ATTR_EDDOC_ID, new BasicDBObject("$exists", true));  
		DBObject EddocsIdsExistAndNotEmpty = new BasicDBObject(GlobalVariable.ATTR_EDDOC_IDS, 
				new BasicDBObject("$exists", true).append("$not", 
						new BasicDBObject("$size", 0)));    
		BasicDBList idOrids = new BasicDBList();
		idOrids.add(eddocIdExist);
		idOrids.add(EddocsIdsExistAndNotEmpty);
		queryDoc.put("$or", idOrids);
		queryDoc.put(GlobalVariable.ATTR_ERROR_DA, new BasicDBObject("$exists", false));
		List<RuleCriteres> criteres = rule.getCriteres();
		for (RuleCriteres ruleCriteres : criteres) {
			List<String> list = ruleCriteres.getParameters();
			queryDoc.put(ruleCriteres.getId(), new BasicDBObject("$in", list));
		}

		// Bon, ici on ne traite pas ce qui vient d'autre filiere (ie : COURTAGE, etc) .
		queryDoc.put(GlobalVariable.ATTR_SHOW_RULE, new BasicDBObject("$exists", false));

		// Ajout du CMROC
		queryDoc.put(GlobalVariable.ATTR_ORGANISME, rule.getCmroc());

		logger.debug("QUERY : " + queryDoc);
		return queryDoc;
	}

	/**
	 * Pour ne lister que les documents qui n ont pas ete deja mailer et qui ne sont pas des mail entrant.
	 * 
	 * @param rule
	 * @return
	 */
	public static BasicDBObject getQueryFromRuleForMail(Rule rule) {
		BasicDBObject queryDoc = getQueryFromRule(rule);
		// pas déjà mailer
		queryDoc.put(GlobalVariable.ATTR_MAILED, new BasicDBObject("$exists", false));
		// Bon, ici on ne traite pas ce qui vient des EMAIL.
		queryDoc.put(GlobalVariable.ATTR_DESTINATAIRE, new BasicDBObject("$exists", false));
		logger.debug("QUERY : " + queryDoc);
		return queryDoc;
	}

	/**
	 * Ne list que les doc qui match la regle + cmroc. ON evite aussi les document avec le ATTR_SHOW_RULE
	 * 
	 * @param rule
	 * @return
	 */
	public static BasicDBObject getQueryFromRule(Rule rule) {
		BasicDBObject queryDoc = new BasicDBObject();

		List<RuleCriteres> criteres = rule.getCriteres();
		for (RuleCriteres ruleCriteres : criteres) {
			queryDoc.put(ruleCriteres.getId(), new BasicDBObject("$in", ruleCriteres.getParameters()));
		}

		// on ne veux pas les document de type courtage ou autre que l'on n'a specifiquement demander de ne pas etre lister.
		queryDoc.put(GlobalVariable.ATTR_SHOW_RULE, new BasicDBObject("$exists", false));

		// Ajout du CMROC
		queryDoc.put(GlobalVariable.ATTR_ORGANISME, rule.getCmroc());

		logger.debug("QUERY : " + queryDoc);
		return queryDoc;
	}

	/**
	 * Construit une requete mongodb a partir des parametres transmis
	 * 
	 * @param parameters
	 * @return
	 * @throws CimutMongoDBException
	 */
	public static BasicDBObject getSearchQuery(Map<String, String> parameters) throws CimutMongoDBException {
		//initialisation des variables
		BasicDBObject basicDBObject = new BasicDBObject();

		final SimpleDateFormat df = new SimpleDateFormat("ddMMyyyy");

		//gestion de la plage de date
		try {
			if (parameters.containsKey(GlobalVariable.ATTR_DATE_CREATION_D) && !parameters.get(GlobalVariable.ATTR_DATE_CREATION_D).isEmpty()) {
				BasicDBObject dateRange = new BasicDBObject();
				dateRange.put("$gte", df.parse(parameters.get(GlobalVariable.ATTR_DATE_CREATION_D)));
				if (parameters.containsKey(GlobalVariable.ATTR_DATE_CREATION_F) && !parameters.get(GlobalVariable.ATTR_DATE_CREATION_F).isEmpty()) {
					dateRange.append("$lte", df.parse(parameters.get(GlobalVariable.ATTR_DATE_CREATION_F)));
				}
				basicDBObject.put(GlobalVariable.ATTR_DATE_CREATION, dateRange);
			}
		} catch (ParseException pException) {
			throw new CimutMongoDBException("Format DATE_CREATION incorrect");
		}

		// Ajout du CMROC
		String cmroc = parameters.get(GlobalVariable.ATTR_ORGANISME);
		if (cmroc == null) {
			throw new CimutMongoDBException("Parametre obligatoire non present [" + GlobalVariable.ATTR_ORGANISME + "]");
		}
		basicDBObject.put(GlobalVariable.ATTR_ORGANISME, cmroc);

		List<String> avoidList = Arrays.asList(GlobalVariable.ATTR_DATE_CREATION_D, GlobalVariable.ATTR_DATE_CREATION_F, GlobalVariable.ATTR_PAGESIZE,
				GlobalVariable.ATTR_SORTING, GlobalVariable.ATTR_PAGENUM);

		// attributs
		// Intersection des attributs "recherchables" avec ceux recherchés => supprimé par johann		
		for (Entry<String, String> entry : parameters.entrySet()) {
			String attr = entry.getKey();

			if (avoidList.contains(attr)) {
				continue;
			}

			String value = entry.getValue();
			if (StringUtils.isNotEmpty(value)) {
				if (value.startsWith("REGEX_")) {
					String regex = value.replace("REGEX_", "");
					regex = regex.replaceAll("\\*+", ".*");
					regex = regex.replaceAll("[\\+\\{\\}\\(\\)\\:\\?]", "");
					basicDBObject.put(attr, java.util.regex.Pattern.compile(regex));
				} else {
					basicDBObject.put(attr, value);
				}
			}
		}

		// spécification sur le champs SHOW_RULE
		// certains document ont une politique particulière d'affichage
		// exmple des documents courtage qui ne doivent pas remonter sur starweb

		// si un paramètre spécifie le show_rule, alors il vient d'être inclu dans la requête ci-dessus
		// sinon par défaut on ne remonte pas le document correspondant
		if (!parameters.containsKey(GlobalVariable.ATTR_SHOW_RULE)) {
			basicDBObject.put(GlobalVariable.ATTR_SHOW_RULE, new BasicDBObject("$exists", false));
		}
		if (!parameters.containsKey(GlobalVariable.ATTR_DESTINATAIRE)) {
			basicDBObject.put(GlobalVariable.ATTR_DESTINATAIRE, new BasicDBObject("$exists", false));
		}
		logger.debug("QUERY : " + basicDBObject);
		return basicDBObject;
	}

	public static List<DBObject> getStatsQuery(Map<String, String> parameters) throws CimutMongoDBException {
		//initialisation des variables
		BasicDBObject match = null;

		final SimpleDateFormat df = new SimpleDateFormat("yyyyMMdd");

		//MATCH
		try {
			//filtre sur plage date (stats gestionnaire)
			if (parameters.containsKey(GlobalVariable.ATTR_DATE_CREATION_D)) {
				BasicDBObject dateRange = new BasicDBObject();
				dateRange.put("$gt", df.parse(parameters.get(GlobalVariable.ATTR_DATE_CREATION_D)));
				if (parameters.containsKey(GlobalVariable.ATTR_DATE_CREATION_F)) {
					dateRange.append("$lt", df.parse(parameters.get(GlobalVariable.ATTR_DATE_CREATION_F)));
				}
				match = new BasicDBObject("$match", new BasicDBObject(GlobalVariable.ATTR_DATE_CREATION, dateRange));
				//filtre sur le status traité
			} else {
				match = new BasicDBObject("$match", new BasicDBObject(GlobalVariable.ATTR_STATUS,
						new BasicDBObject("$ne", fr.cimut.ged.entrant.utils.GlobalVariable.STATUS_TRAITE)));
			}
		} catch (ParseException pException) {
			throw new CimutMongoDBException("Format DATE_CREATION incorrect");
		}
		if (parameters.containsKey(GlobalVariable.ATTR_STATS_FILTER) && parameters.containsKey(GlobalVariable.ATTR_STATS_FILTER_VAL)) {
			((BasicDBObject) match.get("$match")).append(parameters.get(GlobalVariable.ATTR_STATS_FILTER),
					parameters.get(GlobalVariable.ATTR_STATS_FILTER_VAL));
		}

		// Ajout du CMROC
		String cmroc = parameters.get(GlobalVariable.ATTR_ORGANISME);
		if (cmroc == null) {
			throw new CimutMongoDBException("Parametre obligatoire non present [" + GlobalVariable.ATTR_ORGANISME + "]");
		}
		((BasicDBObject) match.get("$match")).put(GlobalVariable.ATTR_ORGANISME, cmroc);

		//match.put(GlobalVariable.ATTR_SHOW_RULE, new BasicDBObject("$exists", false));

		//GROUP
		DBObject groupFields = new BasicDBObject("_id",
				new BasicDBObject("statsType", "$" + parameters.get(GlobalVariable.ATTR_STATS_TYPE)).append("status", "$STATUS"));
		groupFields.put(GlobalVariable.VAR_TYPE_COUNT, new BasicDBObject("$sum", 1));
		DBObject group1 = new BasicDBObject("$group", groupFields);

		DBObject groupFields2 = new BasicDBObject("_id", "$_id.statsType");
		groupFields2.put(GlobalVariable.VAR_ALL_STATUS,
				new BasicDBObject("$push", new BasicDBObject("status", "$_id.status").append("count", "$" + GlobalVariable.VAR_TYPE_COUNT)));
		DBObject group2 = new BasicDBObject("$group", groupFields2);

		return Arrays.asList(match, group1, group2);
	}

	/**
	 * Permet de formatter les resultats dans une string qui represente un tableau de json
	 * 
	 * @param cursor
	 * @return
	 */
	public static String convertDBCursorToString(DBCursor cursor) {
		if (cursor == null) {
			return "";
		}

		//initialisation des variables
		StringBuilder strBuff = new StringBuilder();

		//contruction du tableau
		strBuff.append("[");
		boolean first = true;
		while (cursor.hasNext()) {
			if (first) {
				first = false;
			} else {
				strBuff.append(",");
			}
			strBuff.append(cursor.next());
		}
		strBuff.append("]");

		//retour
		return strBuff.toString();
	}

	public static String convertAggregationOutputToString(AggregationOutput aggregationOutput) {
		if (aggregationOutput == null) {
			return "";
		}
		// initialisation des variables
		StringBuilder strBuff = new StringBuilder();

		// contruction du tableau
		strBuff.append("[");
		boolean first = true;
		for (DBObject result : aggregationOutput.results()) {
			if (first) {
				first = false;
			} else {
				strBuff.append(",");
			}
			strBuff.append(readableJson(result));
		}
		strBuff.append("]");

		// retour
		return strBuff.toString();
	}

	private static BasicDBObject readableJson(DBObject result) {
		BasicDBObject obj = new BasicDBObject();
		BasicDBList statuts = (BasicDBList) result.get(GlobalVariable.VAR_ALL_STATUS);
		BasicDBObject[] statutArr = statuts.toArray(new BasicDBObject[0]);

		obj.put("TYPE", result.get("_id"));
		for (BasicDBObject dbObj : statutArr) {
			try {
				obj.put(dbObj.get("status").toString(), dbObj.get("count"));
			} catch (Exception e) {
			}
		}

		return obj;
	}

}
