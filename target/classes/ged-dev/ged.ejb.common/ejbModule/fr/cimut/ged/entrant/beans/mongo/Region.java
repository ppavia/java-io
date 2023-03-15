package fr.cimut.ged.entrant.beans.mongo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Region {

	private static Map<String, List<String>> list = new HashMap<String, List<String>>();
	private static Map<String, String> invert = new HashMap<String, String>();

	private Region() {

	}

	public static List<String> getRegions() {
		return new ArrayList<String>(getList().keySet());
	}

	public static String getRegion(String codePostal) {
		if (!codePostal.matches("^\\d{4,5}$")) {
			return "";
		} else if (codePostal.length() == 4) {
			codePostal = "0" + codePostal.substring(0, 1);
		} else if (codePostal.startsWith("97")) {
			codePostal = codePostal.substring(0, 3);
		} else {
			codePostal = codePostal.substring(0, 2);
		}
		if (invereMapping().containsKey(codePostal)) {
			return invert.get(codePostal);
		} else {
			return "";
		}
	}

	private static Map<String, List<String>> getList() {
		if (list.isEmpty()) {
			list.put("Auvergne-Rhone-Alpes", Arrays.asList("01", "03", "07", "15", "26", "38", "42", "43", "63", "69", "73", "74"));
			list.put("Nord-Pas-de-Calais-Picardie", Arrays.asList("02", "59", "60", "62", "80"));
			list.put("Provence-Alpes-Cote d'Azur", Arrays.asList("04", "05", "06", "13", "83", "84"));
			list.put("Alsace-Champagne-Ardenne-Lorraine", Arrays.asList("08", "10", "51", "52", "54", "55", "57", "67", "68", "88"));
			list.put("Languedoc-Roussillon-Midi-Pyrenees",
					Arrays.asList("09", "11", "12", "30", "31", "32", "34", "46", "48", "65", "66", "81", "82"));
			list.put("Bretagne", Arrays.asList("22", "29", "35", "56"));
			list.put("Normandie", Arrays.asList("14", "27", "50", "61", "76"));
			list.put("Aquitaine-Limousin-Poitou-Charentes", Arrays.asList("16", "17", "19", "23", "24", "33", "40", "47", "64", "79", "86", "87"));
			list.put("Centre-Val de Loire", Arrays.asList("18", "28", "36", "37", "41", "45"));
			list.put("Bourgogne-Franche-Comte", Arrays.asList("21", "25", "39", "58", "70", "71", "89", "90"));
			list.put("Corse", Arrays.asList("20"));
			list.put("Pays de la Loire", Arrays.asList("44", "49", "53", "72", "85"));
			list.put("Ile-de-France", Arrays.asList("75", "77", "78", "91", "92", "93", "94", "95"));

		}
		return list;
	}

	private static Map<String, String> invereMapping() {
		if (invert.isEmpty()) {
			Map<String, List<String>> list = getList();
			for (Map.Entry<String, List<String>> entry : list.entrySet()) {
				String region = entry.getKey();
				List<String> codes = entry.getValue();
				for (String code : codes) {
					invert.put(code, region);
				}
			}
		}
		return invert;
	}

}
