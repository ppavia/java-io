package fr.cimut.ged.entrant.mongo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.bson.types.ObjectId;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.mongodb.WriteResult;
import com.mongodb.util.JSON;

import fr.cimut.ged.entrant.beans.mongo.Rule;
import fr.cimut.ged.entrant.beans.mongo.RuleCriteres;
import fr.cimut.ged.entrant.beans.mongo.RuleHistorique;
import fr.cimut.ged.entrant.exceptions.CimutMongoDBException;

public class RuleManager extends Manager<Rule> {

	private static final String collection = "Rule";

	/** Conserve l'environnement car il est nécessaire pour mettre à jour les RuleHistorique */
	private String environnement;

	public RuleManager(String environnement, String cmroc, InteractionMongo inter) throws CimutMongoDBException {
		super(environnement, collection, cmroc, inter, Rule.class);
		this.environnement = environnement;
	}

	/**
	 * Get the DBObject from his id
	 * 
	 * @param id
	 * @return
	 * @throws CimutMongoDBException
	 */
	@Override
	protected DBObject getDBobj(String id) throws CimutMongoDBException {
		if (id.isEmpty()) {
			throw new CimutMongoDBException("Aucun identifiant valid");
		}

		BasicDBObject query = new BasicDBObject("_id", new ObjectId(id));
		return this.getCollection().findOne(query);
	}

	/**
	 * Ajoute une nouvelle regle
	 */
	@Override
	public Rule insert(Rule rule) throws CimutMongoDBException {
		Manager<RuleHistorique> historyManager = MongoManagerFactory.getRuleHistoryManager(environnement, getCmroc(), this.getConnection());

		Date now = new Date();
		rule.setDateModif(now);
		rule = super.insert(rule);

		// gestion de l'historique
		RuleHistorique historique = new RuleHistorique();
		historique.setId(rule.getId());
		historique.setCmroc(getCmroc());
		historique.setUser(rule.getUser());
		historique.setObject("Création");
		historique.setDateModif(now);
		historyManager.insert(historique);

		return rule;
	}

	/**
	 * Met a jour la regle en base + historique
	 */
	@Override
	public void update(Rule rule) throws CimutMongoDBException {
		Manager<RuleHistorique> historyManager = MongoManagerFactory.getRuleHistoryManager(environnement, getCmroc(), this.getConnection());

		// gestion de l'historique
		StringBuilder outputMsg = new StringBuilder();
		outputMsg.append("Modification");

		if (rule.getId().isEmpty()) {
			throw new CimutMongoDBException("Aucun identifiant valid");
		}

		Rule previousRule = get(rule.getId());

		if (previousRule == null) {
			throw new CimutMongoDBException("l'Occurence n'existe pas");
		}

		if (!criteresEquals(previousRule.getCriteres(), rule.getCriteres())) {
			outputMsg.append(" des criteres de selection, ");
		}
		if (!previousRule.getService().equals(rule.getService())) {
			outputMsg.append(" du parametrage DA : (");

			if (previousRule.getService().getId() == null) {
				outputMsg.append("Suppression de la DA, ");
			} else {

				if (!previousRule.getService().getId().equals(rule.getService().getId())) {
					outputMsg.append("service id:" + previousRule.getService().getId() + " => " + rule.getService().getId() + ", ");
				}
				if (!previousRule.getService().getName().equals(rule.getService().getName())) {
					outputMsg.append("service nom:" + previousRule.getService().getName() + " => " + rule.getService().getName() + ", ");
				}
				if (!previousRule.getService().getSupport().equals(rule.getService().getSupport())) {
					outputMsg.append("support:" + previousRule.getService().getSupport() + " => " + rule.getService().getSupport() + ", ");
				}
				if (!previousRule.getService().getSujet().equals(rule.getService().getSujet())) {
					outputMsg.append("sujet:" + previousRule.getService().getSujet() + " => " + rule.getService().getSujet() + ", ");
				}
				if (!previousRule.getService().getType().equals(rule.getService().getType())) {
					outputMsg.append("type:" + previousRule.getService().getType() + " => " + rule.getService().getType() + ", ");
				}
				if (!StringUtils.equals(previousRule.getService().getCategorie(), rule.getService().getCategorie())) {
					Map<String, String> categories = new HashMap<String, String>();
					categories.put("0", "information");
					categories.put("2", "acte de gestion");

					String pCategorie = previousRule.getService().getCategorie() != null ? categories.get(previousRule.getService().getCategorie())
							: "";
					String nCategorie = categories.get(rule.getService().getCategorie());

					outputMsg.append("categorie:" + pCategorie + " => " + nCategorie + ", ");
				}

				outputMsg.deleteCharAt(outputMsg.length() - 2);
				outputMsg.append("), ");
			}
		}

		if (!previousRule.getName().equals(rule.getName())) {
			outputMsg.append(" du nom :" + previousRule.getName() + " => " + rule.getName() + ", ");
		}

		if (!previousRule.getPriority().equals(rule.getPriority())) {
			outputMsg.append(" de la priorité : " + previousRule.getPriority() + " => " + rule.getPriority() + ", ");
		}
		if (previousRule.isActif() != rule.isActif()) {
			outputMsg.append(((rule.isActif()) ? " Activation" : " Désactivation") + " de la règle, ");
		}

		if (!mailsEqual(previousRule.getMails(), rule.getMails())) {
			outputMsg.append(" des mails : " + previousRule.getMails().toString() + " => " + rule.getMails().toString() + ", ");
		}

		// do not update if nothing has changed !
		if (outputMsg.toString().equals("Modification")) {
			return;
		}

		outputMsg.deleteCharAt(outputMsg.length() - 2);

		rule.setUser(rule.getUser());
		rule.setDateModif(new Date());

		try {
			Boolean upsert = false;

			BasicDBObject docCriteres = new BasicDBObject("_id", new ObjectId(rule.getId()));
			ObjectMapper mapper = getObjectMapper();
			String docMaj = mapper.writeValueAsString(rule);
			DBObject mongoDBObject = (DBObject) JSON.parse(docMaj);
			mongoDBObject.removeField("_id");
			WriteResult writeresult = this.getCollection().update(docCriteres, mongoDBObject, false, false);
			Boolean resultupdatedExisting = (Boolean) writeresult.getField("updatedExisting");
			if (upsert) {
				//Update du fichier document  ok 	
			} else {
				if (!resultupdatedExisting) {
					throw new CimutMongoDBException("Update du document ( id = " + rule.getId()
							+ " ) en erreur. Le document n existe pas en base. Aucune operation d effectue. ");
				}
			}
		} catch (Exception e) {
			throw new CimutMongoDBException(e.getMessage(), e);
		}

		RuleHistorique historique = new RuleHistorique();
		historique.setId(rule.getId());
		historique.setCmroc(getCmroc());
		historique.setUser(rule.getUser());
		historique.setDateModif(new Date());
		historique.setObject(outputMsg.toString());
		historyManager.insert(historique);
	}

	private boolean mailsEqual(List<String> a, List<String> b) {
		if (a == null && b == null) {
			return true;
		}
		if (a.isEmpty() && b.isEmpty()) {
			return true;
		}
		if (a.size() != b.size()) {
			return false;
		}
		a = new ArrayList<String>(a);
		b = new ArrayList<String>(b);
		Collections.sort(a);
		Collections.sort(b);
		return a.equals(b);
	}

	/**
	 * 
	 * @param a
	 * @param b
	 * @return
	 */
	private boolean criteresEquals(List<RuleCriteres> a, List<RuleCriteres> b) {
		if (a == null && b == null) {
			return true;
		}
		if (a.isEmpty() && b.isEmpty()) {
			return true;
		}
		if (a.size() != b.size()) {
			return false;
		}
		for (RuleCriteres bs : b) {
			boolean found = false;
			for (RuleCriteres as : a) {
				if (bs.equals(as)) {
					found = true;
					break;
				}
			}
			if (!found) {
				return false;
			}
		}
		return true;
	}

	@Override
	public void remove(String id) throws CimutMongoDBException {
		Manager<RuleHistorique> historyManager = MongoManagerFactory.getRuleHistoryManager(environnement, getCmroc(), this.getConnection());
		historyManager.remove(id);
		super.remove(id);
	}

}
