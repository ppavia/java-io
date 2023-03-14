package fr.cimut.ged.entrant.appelmetier;

import fr.cimut.ged.entrant.beans.appelmetier.Stru;
import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.exceptions.CimutMetierException;
import fr.cimut.mos.MapDataMesl;

public class StruManager extends MetierManager {

	public StruManager() throws CimutConfException {
		super("IHM_STRU");
	}

	/**
	 * trouve une entreprise en fournissant l'id et le cmroc id : STRU_NO_INF|STRU_CLASS|STRU_SECT_ALPH (SECTION) ou
	 * STRU_NO_INF|STRU_CLASS (ETAB) ou STRU_SIREN_ALPH|STRU_NIC_ALPH (ETAB)
	 * 
	 * @param idEntreprise
	 * @param cmroc
	 * @param environnement
	 * @return
	 * @throws CimutMetierException
	 */
	public Stru get(String idEntreprise, String cmroc, String environnement) throws CimutMetierException {
		Stru stru = new Stru();
		if (idEntreprise == null) {
			throw new CimutMetierException("identifiant entreprise pas au bon format (null): " + idEntreprise);
		}
		MapDataMesl request = new MapDataMesl();

		if (idEntreprise.matches("^\\d+$")) {
			request.addMesl("STRU_NO_INF", idEntreprise);
			// j'ai pas la classe ... en plus pas moyen de faire 
			// une recherche sur le N° interne d'etablissement
			request.addMesl("TYPSTRU_ID", "ETAB");
		} else if (idEntreprise.matches("^\\d{9}\\|\\d{5}$")) {
			String[] partStruId = idEntreprise.split("\\|");
			request.addMesl("STRU_SIREN_ALPH", partStruId[0]);
			request.addMesl("STRU_NIC_ALPH", partStruId[1]);
			request.addMesl("TYPSTRU_ID", "ETAB");
		} else if (idEntreprise.matches("^\\d+\\|[A-Za-z\\d\\s]+$")) {
			String[] partStruId = idEntreprise.split("\\|");
			request.addMesl("STRU_NO_INF", partStruId[0]);
			if (!partStruId[1].trim().isEmpty()) {
				request.addMesl("STRU_CLASS", partStruId[1]);
			}
			request.addMesl("TYPSTRU_ID", "ETAB");
		} else if (idEntreprise.matches("^\\d+\\|([A-Za-z\\d\\s]+)?\\|\\w+$")) {
			String[] partStruId = idEntreprise.split("\\|");
			request.addMesl("STRU_NO_INF", partStruId[0]);
			if (!partStruId[1].trim().isEmpty()) {
				request.addMesl("STRU_CLASS", partStruId[1]);
			}
			// ARGGGGGGGGGGGGG !!!!!!! mais bordel de m ....
			if (!partStruId[2].trim().isEmpty()) {
				if (partStruId[2].trim().length() < 2) {
					partStruId[2] = "0" + partStruId[2];
				}
			}

			request.addMesl("STRU_SECT_ALPH", partStruId[2]);
			request.addMesl("TYPSTRU_ID", "SECT");
		} else {
			throw new CimutMetierException("identifiant entreprise pas au bon format : " + idEntreprise);
		}

		try {

			MapDataMesl mapMesl = this.call(request, "I", cmroc, environnement, true);

			String ztLibError = mapMesl.getZt("ZT_LIBERR");
			String ztTypeError = mapMesl.getZt("ZT_TYPERR");
			if (ztTypeError != null && ztTypeError.equals("SI")) {
				throw new CimutMetierException(ztLibError);
			}

			stru.setId(mapMesl.getMesl("STRU_ID_ALPH"));
			stru.setClasse(mapMesl.getMesl("STRU_CLASS"));
			stru.setNumInterne(mapMesl.getMesl("STRU_NO_ALPH"));
			stru.setSiren(mapMesl.getMesl("STRU_SIREN_ALPH"));
			stru.setType(mapMesl.getMesl("TYPSTRU_ID"));
		} catch (CimutMetierException e) {
			throw new CimutMetierException(e);
		}
		return stru;
	}

	/**
	 * 
	 * @param siren
	 * @param nic
	 * @param typeStruId
	 * @param cmroc
	 * @param environnement
	 * @return
	 * @throws CimutMetierException
	 */
	public Stru get(String siren, String nic, String typeStruId, String cmroc, String environnement) throws CimutMetierException {
		Stru stru = new Stru();
		MapDataMesl request = new MapDataMesl();

		request.addMesl("STRU_SIREN_ALPH", siren);
		request.addMesl("STRU_NIC_ALPH", nic);
		request.addMesl("TYPSTRU_ID", typeStruId);

		try {

			MapDataMesl mapMesl = this.call(request, "I", cmroc, environnement, true);
			String ztLibError = mapMesl.getZt("ZT_LIBERR");
			String ztTypeError = mapMesl.getZt("ZT_TYPERR");
			if (ztTypeError != null && ztTypeError.equals("SI")) {
				throw new CimutMetierException(ztLibError);
			}

			stru.setId(mapMesl.getMesl("STRU_ID_ALPH"));
			stru.setClasse(mapMesl.getMesl("STRU_CLASS"));
			stru.setNumInterne(mapMesl.getMesl("STRU_NO_ALPH"));
			stru.setSiren(mapMesl.getMesl("STRU_SIREN_ALPH"));
			stru.setType(mapMesl.getMesl("TYPSTRU_ID"));

		} catch (CimutMetierException e) {
			throw new CimutMetierException(e);
		}
		return stru;
	}

}
