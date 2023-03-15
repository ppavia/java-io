package fr.cimut.ged.entrant.mongo;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.log4j.Logger;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.mongodb.BasicDBObject;

import fr.cimut.ged.entrant.beans.mongo.Departement;
import fr.cimut.ged.entrant.beans.mongo.Parameter;
import fr.cimut.ged.entrant.beans.mongo.ParameterDefault;
import fr.cimut.ged.entrant.beans.mongo.Region;
import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.exceptions.CimutMongoDBException;

public class ParameterManager extends Manager<Parameter> {

	private static final String collection = "parametres";

	public ParameterManager(String environnement, String cmroc, InteractionMongo inter) throws CimutMongoDBException {
		super(environnement, collection, cmroc, inter, Parameter.class);
	}

	/** Fournit un {@link ObjectMapper} instancié et paramétré */
	@Override
	protected ObjectMapper getObjectMapper() {
		ObjectMapper objectMapper = new ObjectMapper();
		// Enregistre les sérialisateur et désérialisateur pour les Paramètres
		// Doit être fait programmatiquement car le CMROC doit être fourni au désérialisateur
		SimpleModule testModule = new SimpleModule("ParameterModule")
				.addDeserializer(Parameter.class, new Parameter.ParameterDeserializer(getCmroc()))
				.addSerializer(Parameter.class, new Parameter.ParameterSerializer());
		objectMapper.registerModule(testModule);
		return objectMapper;
	}

	@Override
	public List<Parameter> list(BasicDBObject query, int pagesize, int page) throws CimutMongoDBException {

		if (query == null) {
			query = new BasicDBObject();
		}

		Logger.getLogger(ParameterManager.class).info("list(" + query.toString() + "," + pagesize + "," + page + ")");

		String id = query.getString("_id");

		List<Parameter> output = new ArrayList<Parameter>();

		Parameter parameter = new Parameter();
		parameter.setId(id);
		if (id.equals("REGION")) {
			parameter.setList(Region.getRegions());
		} else if (id.equals("DEPARTEMENT")) {
			parameter.setList(Departement.getDepartements());
		} else {
			parameter = super.get(id);
			// peux etre null ...
			if (parameter == null) {
				parameter = new Parameter();
				parameter.setId(id);
			}
		}

		if (query.containsField("q") && !query.getString("q").isEmpty()) {

			String searchPattern = query.getString("q").toUpperCase();
			// loop over array to filter the unwanted
			Iterator<String> iterator = parameter.getList().iterator();
			while (iterator.hasNext()) {
				String element = iterator.next();
				if (element.toUpperCase().indexOf(searchPattern) < 0) {
					iterator.remove();
				}
			}
		}

		// get the whole list
		List<String> list = parameter.getList();
		try {
			// add the defaults values
			List<String> listDefault = ParameterDefault.getDefault(getCmroc(), id);
			if (!listDefault.isEmpty()) {
				list.removeAll(listDefault);
				list.addAll(listDefault);
			}
		} catch (CimutConfException e) {
			Logger.getLogger(ParameterManager.class).error(e.getMessage(), e);
		}

		// sorting
		java.util.Collections.sort(list);

		// handle the pagination
		int sizeOfArray = list.size();
		int minIndex = pagesize * (page - 1);
		int maxIndex = pagesize * page + 1;

		if (minIndex > sizeOfArray) {
			return output;
		}
		if (maxIndex > sizeOfArray) {
			maxIndex = sizeOfArray;
		}

		// et oui ... c'est une reference. faut copier ça dans un new ArrayList
		List<String> listLoop = list.subList(minIndex, maxIndex);
		List<String> listFinal = new ArrayList<String>();
		for (String string : listLoop) {

			if (!string.trim().isEmpty()) {
				listFinal.add(string);
			}
		}
		parameter.setList(listFinal);
		output.add(parameter);
		return output;
	}
}
