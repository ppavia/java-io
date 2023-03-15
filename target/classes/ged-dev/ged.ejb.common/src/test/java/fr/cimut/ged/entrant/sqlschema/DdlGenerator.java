package fr.cimut.ged.entrant.sqlschema;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.StringUtils;
import org.hibernate.cfg.Configuration;
import org.hibernate.tool.hbm2ddl.SchemaExport;

/**
 * Classe permettant la creation des scripts sql de generation de BDD.
 * 
 *
 */
public final class DdlGenerator {

	/**
	 * Constructeur.
	 */
	private DdlGenerator() {
	}

	/**
	 * GÃ©nÃ¨re le script de la base de donnÃ©es Ã  partir des informations de mappings de classes persistantes
	 * 
	 * @param args
	 *            arguments attendus de l'application : <nom du package des classes persistantes> <nom du fichier SQL
	 *            gÃ©nÃ©rÃ©> <nom de la classe Dialect Hibernate>
	 * @throws Exception
	 */
	public static void main(final String[] args) throws Exception {

		String packageName = args[0];
		String subpackages = args[1];
		String output = args[2];
		String dialect = args[3];
		String version = args[5];

		// attention sequence peut etre null
		String[] sequences;
		if (StringUtils.isEmpty(args[4])) {
			// init avec un tableau vide
			sequences = new String[0];
		} else {
			sequences = args[4].split(",");
		}

		String[] subpackageNames = subpackages.split(",");

		// creation du rep s'il n'existe pas

		File file = new File(output);

		String outputPath = file.getParent();
		String outputFilename = file.getName();

		String outputFilenameCreate = outputPath + "/" + outputFilename.replace(".sql", "") + "-create.sql";
		String outputFilenameDrop = outputPath + "/" + outputFilename.replace(".sql", "") + "-drop.sql";

		String[] sqlExtensions = new String[1];
		sqlExtensions[0] = "sql";

		// fichier root
		File fileOutputPath = new File(outputPath);

		// drop puis creation des rep
		FileUtils.deleteDirectory(fileOutputPath);
		fileOutputPath.mkdirs();

		// get hibernate configuration
		Configuration configuration = new Configuration();
		List<Class<?>> classes = getClasses(packageName);
		for (Class<?> clazz : classes) {
			configuration.addAnnotatedClass(clazz);
		}
		for (int i = 0; i < subpackageNames.length; i++) {
			String subPackageName = packageName + "." + subpackageNames[i];
			List<Class<?>> subClasses = getClasses(subPackageName);
			for (Class<?> subClazz : subClasses) {
				configuration.addAnnotatedClass(subClazz);
			}
		}
		Properties props = new Properties();
		props.put("hibernate.dialect", dialect);
		configuration.addProperties(props);

		// creation du SQL create
		SchemaExport schemaExport = new SchemaExport(configuration);
		schemaExport.setDelimiter(";");
		schemaExport.setOutputFile(outputFilenameCreate);
		schemaExport.execute(true, false, false, true);
		// ajout des sequences
		FileWriter fw = new FileWriter(outputFilenameCreate, true);
		for (String sequence : sequences) {
			fw.append("    create sequence " + sequence + ";\n");
		}

		// ajout de la version applicative		
		System.out.println("ajout de la version applicative : " + version);
		fw.append("\n\n\t-- version applicative du pom : " + version);
		fw.close();

		// creation du SQL drop
		SchemaExport dropExport = new SchemaExport(configuration);
		dropExport.setDelimiter(";");
		dropExport.setOutputFile(outputFilenameDrop);
		dropExport.execute(true, false, true, false);
		// ajout des sequences
		fw = new FileWriter(outputFilenameDrop, true);
		for (String sequence : sequences) {
			fw.append("    drop sequence " + sequence + ";\n");
		}
		fw.close();

		System.out.println("parcours des fichiers d'init sql");

		// parcourt des repertoires de bootstrap
		File[] listOutputPath = fileOutputPath.listFiles();
		for (File fileOutput : listOutputPath) {
			if (fileOutput.isDirectory()) {

				System.out.println("repertoire " + fileOutput.getName());

				// dans le repertoire on liste les fichiers *.sql
				Iterator<File> it = FileUtils.iterateFiles(fileOutput, sqlExtensions, false);
				List<File> csvFiles = new ArrayList<File>(15);

				// on parcourt les fichiers sql insÃ©rÃ©s dans un tableau
				while (it.hasNext()) {
					File fileIt = it.next();
					csvFiles.add(fileIt);
					System.out.println("file depart : " + fileIt.getName());
				}

				// pour chaque repertoire on cree un fichier test-all.sql
				// qui incluera : drop + create + bootstraps

				System.out.println("create test-bootstraps-all ");

				fw = new FileWriter(outputPath + "/" + fileOutput.getName() + "/test-bootstraps-all.sql", false);
				System.out.println("write drop");
				// on ecrit le Drop
				FileReader in = new FileReader(outputFilenameDrop);
				int c;
				while ((c = in.read()) != -1) {
					fw.write(c);
				}
				in.close();
				System.out.println("write create");
				// on ecrit le Create
				in = new FileReader(outputFilenameCreate);
				while ((c = in.read()) != -1) {
					fw.write(c);
				}
				in.close();

				for (File fileCsv : csvFiles) {
					System.out.println("file : " + fileCsv.getName());
					in = new FileReader(fileCsv);
					while ((c = in.read()) != -1) {
						fw.write(c);
					}
					in.close();
				}

				fw.close();
			}
		}

		// dans le repertoir root on liste les fichiers *.sql
		Iterator<File> it = FileUtils.iterateFiles(fileOutputPath, sqlExtensions, true);
		while (it.hasNext()) {

			File fileIt = it.next();
			String fileString = FileUtils.readFileToString(fileIt);
			fileString = "\n\nset define off;\n\n" + fileString + "\n\ncommit;\n\n";

			fw = new FileWriter(fileIt, false);
			fw.write(fileString);
			fw.close();
		}

	}

	/**
	 * RecupÃ¨re la liste des classes d'un package
	 * 
	 * @param packageName
	 *            le nom du package
	 * @return la liste des classes
	 * @throws Exception
	 *             Exception
	 */
	private static List<Class<?>> getClasses(final String packageName) throws Exception {

		List<Class<?>> classes = new ArrayList<Class<?>>();

		List<File> directories = getPackageDirectories(packageName);
		for (File d : directories) {
			classes.addAll(getClasses(packageName, d));
		}

		return classes;
	}

	/**
	 * Charge la liste des classes d'un package Ã  partir d'un directory
	 * 
	 * @param packageName
	 *            nom du package
	 * @param directory
	 *            rÃ©pertoire du classpath
	 * @return liste des classes du package
	 * @throws Exception
	 *             Exception
	 */
	private static List<Class<?>> getClasses(final String packageName, final File directory) throws Exception {
		List<Class<?>> classes = new ArrayList<Class<?>>();
		if (directory.exists()) {
			String[] files = directory.list();
			for (int i = 0; i < files.length; i++) {
				if (files[i].endsWith(".class")) {
					// removes the .class extension
					classes.add(Class.forName(packageName + '.' + files[i].substring(0, files[i].length() - 6)));
				}
			}
		} else {
			throw new ClassNotFoundException(packageName + " is not a valid package");
		}

		return classes;
	}

	/**
	 * RÃ©cupÃ¨re la liste des directories du classpath pour un package donnÃ©
	 * 
	 * @param packageName
	 *            le nom du package
	 * @return liste des directories
	 * @throws ClassNotFoundException
	 *             Exception
	 */
	private static List<File> getPackageDirectories(final String packageName) throws ClassNotFoundException {
		List<File> directories = new ArrayList<File>();
		File directory = null;
		ClassLoader cld = DdlGenerator.class.getClassLoader(); // ClassLoader.getSystemClassLoader();
		if (cld == null) {
			throw new ClassNotFoundException("Can't get class loader.");
		}
		String path = packageName.replace('.', '/');
		Enumeration<URL> resources;
		try {
			resources = cld.getResources(path);
		} catch (IOException e) {
			resources = null;
		}
		while (resources != null && resources.hasMoreElements()) {
			URL resource = resources.nextElement();
			if (resource == null) {
				throw new ClassNotFoundException("No resource for " + path);
			}

			if ("jar".equals(resource.getProtocol())) {
				// We don't scan jar files
			} else {
				String file;
				try {
					file = URLDecoder.decode(resource.getFile(), "UTF-8");
				} catch (UnsupportedEncodingException e1) {
					file = resource.getFile();
				}

				directory = new File(file);
			}
			if (directory != null) {
				directories.add(directory);
			}
		}
		return directories;

	}

}
