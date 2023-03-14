package fr.cimut.ged.entrant.beans.mongo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Departement {

	private Departement() {

	}

	private static Map<String, String> list = new HashMap<String, String>();

	public static List<String> getDepartements() {
		return new ArrayList<String>(getList().values());
	}

	public static String getDepartement(String codePostal) {

		if (!codePostal.matches("^\\d{4,5}$")) {
			return "";
		} else if (codePostal.length() == 4) {
			codePostal = "0" + codePostal.substring(0, 1);
		} else if (codePostal.startsWith("97")) {
			codePostal = codePostal.substring(0, 3);
		} else {
			codePostal = codePostal.substring(0, 2);
		}

		if (getList().containsKey(codePostal)) {
			return list.get(codePostal);
		} else {
			return "";
		}
	}

	private static Map<String, String> getList() {
		if (list.isEmpty()) {
			list.put("01", "Ain");
			list.put("02", "Aisne");
			list.put("03", "Allier");
			list.put("05", "Hautes-Alpes");
			list.put("04", "Alpes-de-Haute-Provence");
			list.put("06", "Alpes-Maritimes");
			list.put("07", "Ardèche");
			list.put("08", "Ardennes");
			list.put("09", "Ariège");
			list.put("10", "Aube");
			list.put("11", "Aude");
			list.put("12", "Aveyron");
			list.put("13", "Bouches-du-Rhône");
			list.put("14", "Calvados");
			list.put("15", "Cantal");
			list.put("16", "Charente");
			list.put("17", "Charente-Maritime");
			list.put("18", "Cher");
			list.put("19", "Corrèze");
			list.put("20", "Corse-du-sud");
			list.put("20", "Haute-corse");
			list.put("21", "Côte-d'or");
			list.put("22", "Côtes-d'armor");
			list.put("23", "Creuse");
			list.put("24", "Dordogne");
			list.put("25", "Doubs");
			list.put("26", "Drôme");
			list.put("27", "Eure");
			list.put("28", "Eure-et-Loir");
			list.put("29", "Finistère");
			list.put("30", "Gard");
			list.put("31", "Haute-Garonne");
			list.put("32", "Gers");
			list.put("33", "Gironde");
			list.put("34", "Hérault");
			list.put("35", "Ille-et-Vilaine");
			list.put("36", "Indre");
			list.put("37", "Indre-et-Loire");
			list.put("38", "Isère");
			list.put("39", "Jura");
			list.put("40", "Landes");
			list.put("41", "Loir-et-Cher");
			list.put("42", "Loire");
			list.put("43", "Haute-Loire");
			list.put("44", "Loire-Atlantique");
			list.put("45", "Loiret");
			list.put("46", "Lot");
			list.put("47", "Lot-et-Garonne");
			list.put("48", "Lozère");
			list.put("49", "Maine-et-Loire");
			list.put("50", "Manche");
			list.put("51", "Marne");
			list.put("52", "Haute-Marne");
			list.put("53", "Mayenne");
			list.put("54", "Meurthe-et-Moselle");
			list.put("55", "Meuse");
			list.put("56", "Morbihan");
			list.put("57", "Moselle");
			list.put("58", "Nièvre");
			list.put("59", "Nord");
			list.put("60", "Oise");
			list.put("61", "Orne");
			list.put("62", "Pas-de-Calais");
			list.put("63", "Puy-de-Dôme");
			list.put("64", "Pyrénées-Atlantiques");
			list.put("65", "Hautes-Pyrénées");
			list.put("66", "Pyrénées-Orientales");
			list.put("67", "Bas-Rhin");
			list.put("68", "Haut-Rhin");
			list.put("69", "Rhône");
			list.put("70", "Haute-Saône");
			list.put("71", "Saône-et-Loire");
			list.put("72", "Sarthe");
			list.put("73", "Savoie");
			list.put("74", "Haute-Savoie");
			list.put("75", "Paris");
			list.put("76", "Seine-Maritime");
			list.put("77", "Seine-et-Marne");
			list.put("78", "Yvelines");
			list.put("79", "Deux-Sèvres");
			list.put("80", "Somme");
			list.put("81", "Tarn");
			list.put("82", "Tarn-et-Garonne");
			list.put("83", "Var");
			list.put("84", "Vaucluse");
			list.put("85", "Vendée");
			list.put("86", "Vienne");
			list.put("87", "Haute-Vienne");
			list.put("88", "Vosges");
			list.put("89", "Yonne");
			list.put("90", "Territoire de Belfort");
			list.put("91", "Essonne");
			list.put("92", "Hauts-de-Seine");
			list.put("93", "Seine-Saint-Denis");
			list.put("94", "Val-de-Marne");
			list.put("95", "Val-d'oise");
			list.put("971", "Guadeloupe");
			list.put("972", "Martinique");
			list.put("973", "Guyane");
			list.put("974", "Réunion");
			list.put("976", "Mayotte");
		}
		return list;
	}

}
