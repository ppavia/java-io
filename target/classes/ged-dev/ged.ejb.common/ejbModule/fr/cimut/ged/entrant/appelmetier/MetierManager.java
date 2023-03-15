package fr.cimut.ged.entrant.appelmetier;

import java.util.Collection;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.axis2.AxisFault;
import org.apache.log4j.Logger;

import fr.cimut.ged.entrant.appelmetier.pool.ApplicationFactory;
import fr.cimut.ged.entrant.appelmetier.pool.ClientMosFactory;
import fr.cimut.ged.entrant.appelmetier.pool.TechFieldFactory;
import fr.cimut.ged.entrant.appelmetier.pool.UserPoolFactory;
import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.exceptions.CimutMetierException;
import fr.cimut.habilitation.core.ApplicationCimut;
import fr.cimut.habilitation.core.Personne;
import fr.cimut.mos.ClientMos;
import fr.cimut.mos.MapDataMesl;
import fr.cimut.mos.TechnicalFieldAdaptator;
import fr.cimut.mos.util.MapDataMeslTransformer;
import fr.cimut.util.GlobalProperties;

public abstract class MetierManager {

	protected String codeAppli;
	private String login;
	private String password;

	public MetierManager(String codeMetier) throws CimutConfException {
		initProperties(codeMetier);
	}

	public static void closeAll() {
		Collection<ClientMos> list = ClientMosFactory.getClients();
		for (ClientMos clientMos : list) {
			try {
				clientMos.close();
			} catch (Exception e) {
				Logger.getLogger(MetierManager.class).error("commentaire manquant", e);
			}
		}
		TechFieldFactory.clean();
	}

	public void close(String cmroc, String environnement) throws CimutMetierException {

		ClientMos clientMos = ClientMosFactory.getClient(cmroc, codeAppli, environnement);
		try {
			if (clientMos != null) {
				Logger.getLogger(EddmManager.class).info("closing " + codeAppli + "/" + cmroc);
				clientMos.close();
			}
		} catch (Exception e) {
			throw new CimutMetierException(e);
		} finally {
			if (clientMos != null) {
				clientMos.setProfilCallService(null);
			}
			TechFieldFactory.clean(codeAppli);
		}

	}

	/**
	 * Methode generic permettant de faire des appels au metier
	 * 
	 * @param mapData
	 * @param cmsid
	 * @param cmroc
	 * @param environnement
	 * @param close
	 * @return
	 * @throws CimutMetierException
	 */
	public MapDataMesl call(MapDataMesl mapData, String cmsid, String cmroc, String environnement, boolean close) throws CimutMetierException {
		//----On recupere notre User (Singleton)
		//user = UserFactory.getInstance(request.getEntete().getUitsemUser(), request.getEntete().getUitsemPassword());
		Personne user = UserPoolFactory.getInstance(getLogin() + cmroc, getPassword());
		//----On recupere l'application (Singleton)
		//    APPEL AU SERVICE    //
		ApplicationCimut application = ApplicationFactory.getInstance(user, this.getCodeAppli());
		//----On recupere notre instance client MOS
		ClientMos client;
		try {
			client = ClientMosFactory.getInstance(user, this.getCodeAppli(), environnement);
		} catch (AxisFault e) {
			throw new CimutMetierException(e);
		}

		//----Ouverture
		client.open(application);

		//----Preparation de la requete
		//----On recup notre instance tecnicalField
		//TechnicalFieldAdaptator technicalField = TechFieldFactory.getInstance(this.getCodeAppli());
		TechnicalFieldAdaptator technicalField = new TechnicalFieldAdaptator();
		technicalField.setCmsi(cmsid);
		technicalField.setServiceName(application.getService().getCode());

		//----Interrogation
		String retourService;
		try {
			// Récupération du timeout (valeur par défaut 180 secondes)
			long timeout = 180000L;
			String timeoutStr = GlobalProperties.getGlobalProperty("fr.cimut.mos.stub.timeout." + environnement);
			if (timeoutStr == null || timeoutStr.trim().isEmpty()) {
				timeoutStr = GlobalProperties.getGlobalProperty("fr.cimut.mos.stub.timeout");
			}
			if (timeoutStr != null && !timeoutStr.trim().isEmpty()) {
				try {
					timeout = Long.parseLong(timeoutStr);
				} catch (NumberFormatException e) {
				}
			}

			retourService = (String) client.callService(technicalField, ClientMos.RETURN_TYPE_STRING, mapData, timeout);
			if (close) {
				this.close(cmroc, environnement);
			}
		} catch (Exception e) {
			try {
				this.close(cmroc, environnement);
			} catch (Exception e1) {
			}
			throw new CimutMetierException(e.getMessage(), e);
		}

		MapDataMesl map;
		try {
			/*
			Parfois, la magie de la technique et de la théorie de l'oignon aka. 36 couches d'appels et de technos crée des problèmes.
			Dans le cadre de l'intégration en masse de Documents pour les SUDE depuis Crescendo (en passant par le file system),
			certaines SUDE récupérées depuis le métier retourne des notes encodée en XML invalide ('&a ', '&l ', ...). Sans le ';' le parser se vautre lamentablement.
			On corrige donc ici le flux xml si nécessaire pour éviter des erreurs techniques bien bêtes mais bien embétantes.
			*/
			retourService = fixRetourServiceXML(retourService);
			map = MapDataMeslTransformer.getMapDataMesl(retourService);
		} catch (Exception e) {
			throw new CimutMetierException(e);
		}

		String ztLibError = map.getZt("ZT_LIBERR");
		String ztTypeError = map.getZt("ZT_TYPERR");

		// on verifie le TypeError et le ZtLibError
		if (ztLibError != null && !ztLibError.isEmpty() && !ztTypeError.equalsIgnoreCase("SI")) {
			ztLibError = geterrorMsg(retourService);
			throw new CimutMetierException(ztLibError);
		}
		return map;
	}

	private String fixRetourServiceXML(String retourService) {
		String result = retourService;
		Matcher matcher = Pattern.compile("&a").matcher(result);
		while (matcher.find()) {
			// Si on trouver 'amp;', rien à faire, on agit sinon
			if (!result.substring(matcher.start(), matcher.start() + 5).equals("&amp;")) {
				// On a un m ou un p en plus ?
				if (result.charAt(matcher.start() + 2) == 'm') {
					if (result.charAt(matcher.start() + 4) == 'p') {
						result = result.substring(0, matcher.start() + 4) + ";" + result.substring(matcher.start() + 4);
					} else {
						result = result.substring(0, matcher.start() + 3) + "p;" + result.substring(matcher.start() + 3);
					}
				} else {
					result = result.substring(0, matcher.start() + 2) + "mp;" + result.substring(matcher.start() + 2);
				}
				// On modifie à la volée la chaîne de résultats, on met à jour le matcher
				// TODO Optimiser cette search and replace ?
				matcher.reset(result);
			}
		}
		// Second matcher sur les '&' isolés entre des chiffres ou '<', présent en fin de texte parfois
		matcher = Pattern.compile("&[0-9<l]").matcher(result);
		while (matcher.find()) {
			// le '&' devient '&amp;'
			result = result.substring(0, matcher.start() + 1) + "amp;" + result.substring(matcher.start() + 1);
			// On modifie à la volée la chaîne de résultats, on met à jour le matcher
			// TODO Optimiser cette search and replace ? #2
			matcher.reset(result);
		}
		// Troisième matcher sur les '&oursement' à supprimer
		matcher = Pattern.compile("&oursement").matcher(result);
		while (matcher.find()) {
			// le '&' est supprimé
			result = result.substring(0, matcher.start()) + result.substring(matcher.start() + 1);
			// On modifie à la volée la chaîne de résultats, on met à jour le matcher
			// TODO Optimiser cette search and replace ? #2
			matcher.reset(result);
		}
		return result;
	}

	protected String geterrorMsg(String retourService) {

		String ztLibError = "";

		// truc degeulasse parce que le map.getZt("ZT_LIBERR") me renvoie un truc tronqué ...
		Pattern patt = Pattern.compile(".*ZT_LIBERR>(.*)<\\/ZT_LIBERR.*");
		Matcher m = patt.matcher(retourService);
		if (m.matches()) {
			ztLibError = m.group(1);
		}

		return ztLibError;
	}

	protected String getCodeAppli() throws CimutMetierException {
		if (this.codeAppli == null) {
			throw new CimutMetierException("Le code application ne peux etre null");
		}
		return codeAppli;
	}

	protected String getLogin() {
		return this.login;
	}

	protected void setLogin(String login) {
		this.login = login;
	}

	protected String getPassword() {
		return this.password;
	}

	protected void setPassword(String password) {
		this.password = password;
	}

	/**
	 * <p>
	 * Recuperation des informations de connexion au Mos dans le fichier properties
	 * </p>
	 * 
	 * @throws CimutMetierException
	 */
	private void initProperties(String codeMetier) throws CimutConfException {
		this.codeAppli = codeMetier;
		try {
			this.setLogin(GlobalProperties.getGlobalProperty("fr.cimut.ged.entrant.mos.login").trim());
		} catch (NullPointerException exception) {
			throw new CimutConfException("La propriete fr.cimut.ged.entrant.mos.login n'est pas presente dans le fichier properties");
		}
		try {
			this.setPassword(GlobalProperties.getGlobalProperty("fr.cimut.ged.entrant.mos.password").trim());
		} catch (NullPointerException exception) {
			throw new CimutConfException("La propriete fr.cimut.ged.entrant.mos.password n'est pas presente dans le fichier properties");
		}
	}

}
