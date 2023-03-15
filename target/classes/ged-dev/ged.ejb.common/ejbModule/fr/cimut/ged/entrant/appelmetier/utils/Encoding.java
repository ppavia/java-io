package fr.cimut.ged.entrant.appelmetier.utils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.Normalizer;

public class Encoding {

	private Encoding() {

	}

	/**
	 * Permet de convertir les chaines iso-8859-1 en utf-8 pour le format json
	 * 
	 * @param string
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String encodeISOtoUTF8(String string) throws UnsupportedEncodingException {
		return new String(new String(string.getBytes("iso-8859-1"), "iso-8859-1").getBytes("UTF-8"), "utf-8");
	}

	/**
	 * Permet de supprimer les accents et caracteres speciaux fournis en entree tout en decodant les parametres
	 * 
	 * @param string
	 * @return
	 * @throws UnsupportedEncodingException
	 */
	public static String supprAccentToUpperCase(String string) throws UnsupportedEncodingException {
		return Normalizer.normalize(URLDecoder.decode(string, "utf-8").toUpperCase().trim(), Normalizer.Form.NFD).replaceAll("-", " ")
				.replaceAll("[\\p{InCombiningDiacriticalMarks}\\p{Punct}\\p{Sc}����&&[^'./]]", "");
	}

}
