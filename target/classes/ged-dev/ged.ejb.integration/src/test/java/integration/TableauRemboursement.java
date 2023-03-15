/**
 * @author gyclon
 */
package integration;

import static org.junit.Assert.fail;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.junit.Test;

import fr.cimut.ged.entrant.beans.db.Type;
import fr.cimut.ged.entrant.utils.GlobalVariable;

public class TableauRemboursement {

	private static String path = "C:/tmp/";
	private static String pathPdf = path + "TG_Pack/";
	private static String fileToCategories = "Liste_Pack.csv";
	private static String categoriesToIds = "CORRESP-PACK-RDO-20160524.csv";
	private static String environnement = "";

	private static SimpleDateFormat spf = new SimpleDateFormat("yyyyMMdd");

	/**
	 * Permet de bien formatter les categories et identifiants PACK !
	 * 
	 * @param todo
	 * @return
	 */
	private String stripMe(String todo) {
		while (todo != null && todo.startsWith("0")) {
			todo = todo.substring(1);
		}
		if ("".equals(todo)) {
			todo = "0";
		}
		return todo;
	}

	public String getDate() {
		Date today = new Date();
		return spf.format(today);
	}

	/**
	 * Genere les couple pdf et xml pour integration dans la GEDE via l'integration par répértoire
	 */
	@Test
	public void generateXml() {

		// parse le fichier csv "" et construit le mapping depuis

		BufferedReader br = null;

		File folderIntegration = new File(path + "integrate");

		if (folderIntegration.exists()) {
			String[] entries = folderIntegration.list();
			for (String s : entries) {
				File currentFile = new File(folderIntegration.getPath(), s);
				currentFile.delete();
			}
			folderIntegration.delete();
		}
		if (!folderIntegration.exists()) {
			if (!folderIntegration.mkdirs()) {
				System.out.println("impossible de crée le repertoire d'integration suivante : " + folderIntegration.getAbsolutePath());
				return;
			}
		}

		// l'objet de mapping 
		// la clé etant consituée de la concatenation des categories (non valorisé => "0")
		Map<String, String> map = new HashMap<String, String>();
		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader(path + fileToCategories));
			while ((sCurrentLine = br.readLine()) != null) {
				List<String> list = Arrays.asList(sCurrentLine.split(";"));
				// on evite les données sans interet
				if (list.get(0) != null && !list.get(0).matches("^\\d+$")) {
					System.out.println(list.get(0) + " " + sCurrentLine);
					continue;
				}

				String key = stripMe(list.get(0)) + "_" + stripMe(list.get(2)) + "_" + stripMe(list.get(3)) + "_" + stripMe(list.get(4));
				if (!map.containsKey(key)) {
					map.put(key, list.get(9));
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}

		// mappging pour les objets trouver plusieur fois
		Map<String, List<String>> doublon = new HashMap<String, List<String>>();

		// mapping pour les objets sains
		//Map<String,List<String>> cleaned = new HashMap<String,List<String>>();

		// mapping pour les pdfs non trouvés
		Set<String> pdfNotFound = new HashSet<String>();

		try {
			String sCurrentLine;
			br = new BufferedReader(new FileReader(path + categoriesToIds));
			while ((sCurrentLine = br.readLine()) != null) {
				List<String> list = Arrays.asList(sCurrentLine.split(";"));
				// la ligne ne ressemble en aucun cas à ce que je recherche
				if (list.size() > 4) {

					// je construit la cle depuis les categories
					String key = stripMe(list.get(0)) + "_" + stripMe(list.get(1)) + "_" + stripMe(list.get(2)) + "_" + stripMe(list.get(3));

					// je verifie si j'ai bien trouver la reference dans le premier fichier
					if (map.containsKey(key)) {

						// si il n'est pas en doublon !

						// cas des doublons, ceci ne sont pazs en fin de compte des doublons.
						// mais plusieur couple de code-produit iD pack peuvent pointer sur lesz meme categories.
						// afin de ne pas modifier en profondeur mon code GEDE => je duplique le fichier document en le renommant =>

						String filename = map.get(key);

						File file = new File(pathPdf + filename);
						if (!file.exists()) {
							// on efface du clean puisque le pdf est introuvable
							pdfNotFound.add(file.getAbsolutePath());
							continue;
						}

						// je contruit la string XML pour l'integration (plusieurs items -> 1 String => plus facile à gerer après)
						String output = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>" + "<data>" + "<ID_ORGANISME>" + list.get(5) + "</ID_ORGANISME>" // je variabilise le CMROC (9997, 9948, ...)
								+ "<TUTELLE>" + list.get(5) + "</TUTELLE>" // je variabilise le CMROC (9997, 9948, ...)
								+ "<" + GlobalVariable.ATTR_CODE_PRODUIT + ">" + list.get(6) + "</" + GlobalVariable.ATTR_CODE_PRODUIT + ">" + "<"
								+ GlobalVariable.ATTR_IDENTIFIANT_PACK + ">" + list.get(7) + "</" + GlobalVariable.ATTR_IDENTIFIANT_PACK + ">"
								+ "<DATE_CREATION>" + getDate() + "</DATE_CREATION>" + "<" + GlobalVariable.ATTR_SHOW_RULE
								+ ">ecart_umc_107_tmpSolution</" + GlobalVariable.ATTR_SHOW_RULE + ">" + "<TYPE_DOSSIER>" + Type.CODE_TABLEAU_GARANTIE
								+ "</TYPE_DOSSIER>" + "<CATEGORIE_1>" + stripMe(list.get(0)) + "</CATEGORIE_1>" + "<CATEGORIE_2>"
								+ stripMe(list.get(1)) + "</CATEGORIE_2>" + "<CATEGORIE_3>" + stripMe(list.get(2)) + "</CATEGORIE_3>"
								+ "<CATEGORIE_4>" + stripMe(list.get(3)) + "</CATEGORIE_4>" + "<ATTRIBUTS></ATTRIBUTS>" + "</data>";

						String fileNameXml = filename.replaceAll(".pdf$", ".xml");
						String fileNamePdf = filename;
						if (!doublon.containsKey(key)) {
							doublon.put(key, new ArrayList<String>());
						} else {
							fileNamePdf = filename.replaceAll(".pdf$", "_" + doublon.get(key).size() + ".pdf");
							fileNameXml = filename.replaceAll(".pdf$", "_" + doublon.get(key).size() + ".xml");
						}

						// on ecrit le fichier d'indexation
						PrintWriter writer = new PrintWriter(
								path + "integrate/" + ((environnement.isEmpty()) ? "" : environnement + "_") + fileNameXml, "UTF-8");
						writer.println(output);
						writer.close();

						// on copie les pdf dans le repertoire d'integration.
						File source = new File(pathPdf + filename);
						File dest = new File(path + "integrate/" + ((environnement.isEmpty()) ? "" : environnement + "_") + fileNamePdf);
						FileUtils.copyFile(source, dest);

						// output les couple xml/pdf sains
						System.out.println(key + " => " + fileNamePdf);

						// on ajoute pour verifier si y a doublons
						doublon.get(key).add("(CODE PROD : " + list.get(6) + ", ID_PACK " + list.get(7) + ")");
					}
				}
			}

			int counter = 0;

			// output les doublons
			System.out.println("##############################################");
			System.out.println("# DOCUMENTS INVALIDES                        #");
			System.out.println("##############################################");

			//			System.out.println("###########################");
			//			System.out.println("# DOCUMENTS EN DOUBLONS   #");
			//			System.out.println("###########################");
			//
			//			counter = 0;
			for (Entry<String, List<String>> entry : doublon.entrySet()) {
				//				// si dans doublon et referencer plus de 1 fois => c'est un vrai doublon
				//				if (entry.getValue().size() > 1){
				//					System.out.print("doublon => "+entry.getKey());
				//					for (String iterable_element : entry.getValue()) {
				//						System.out.print("\t "+iterable_element);
				//						counter++;
				//					}
				//					System.out.println(" ");
				//				}
				map.remove(entry.getKey());
			}
			//			System.out.println(" ");
			//			System.out.println("Nombre de document ne pouvant pas être integré car en doublon : "+counter);
			//			System.out.println(" ");
			//			System.out.println(" ");
			// output les pdf qui ne sont pas disponibles
			counter = 0;
			System.out.println("###########################");
			System.out.println("# FICHIER PDF INTROUVABLE #");
			System.out.println("###########################");

			for (String string : pdfNotFound) {
				// on imprime
				System.out.println("PDF Non trouvé  => " + string);
				counter++;
			}
			System.out.println(" ");
			System.out.println("Nombre de fichier PDF non trouvé : " + counter);
			System.out.println(" ");
			System.out.println(" ");
			// tous ceux present dans la map initial, c'est qu'il n'ont pas été trouver dans le deuxieme fichier
			// output les not found
			System.out.println("############################");
			System.out.println("# Références INTROUVABLES  #");
			System.out.println("############################");
			counter = 0;
			for (Entry<String, String> entry : map.entrySet()) {
				// on imprime
				System.out.println("Non trouvé  => " + entry.getKey() + " " + entry.getValue());
				counter++;
			}
			System.out.println(" ");
			System.out.println("Nombre de document non trouvé : " + counter);
			System.out.println(" ");
			System.out.println(" ");
		} catch (IOException e) {
			e.printStackTrace();
			fail();
		} finally {
			try {
				if (br != null) {
					br.close();
				}
			} catch (IOException ex) {
				ex.printStackTrace();
			}
		}
	}
}
