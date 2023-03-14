package fr.cimut.ged.entrant.appelmetier;

import java.text.SimpleDateFormat;
import java.util.Date;

import fr.cimut.ged.entrant.beans.appelmetier.Part;
import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.exceptions.CimutMetierException;
import fr.cimut.mos.MapDataMesl;

public class PartManager extends MetierManager {

	private static final SimpleDateFormat spdf = new SimpleDateFormat("yyyyMMdd");

	public PartManager() throws CimutConfException {
		super("IHM_PART");
	}

	/**
	 * Recherche un partenaire depuis son PART_ID|TYPPART_ID|PART_NIV et cmroc
	 * 
	 * @param idProf
	 * @param cmroc
	 * @param environnement
	 * @throws CimutMetierException
	 */
	public Part get(String idProf, String cmroc, String environnement) throws CimutMetierException {

		Part part = new Part();

		String[] splitted = idProf.split("\\|");
		MapDataMesl request = new MapDataMesl();
		request.addMesl("PART_ID", splitted[0]);
		if (splitted.length > 1) {
			request.addMesl("TYPPART_ID", splitted[1]);
		} else {
			request.addMesl("TYPPART_ID", "");
		}
		if (splitted.length > 2) {
			request.addMesl("PART_NIV", splitted[2]);
		} else {
			request.addMesl("PART_NIV", "");
		}
		request.addMesl("NIV", cmroc);
		request.addMesl("DT_SIT", spdf.format(new Date()));
		// Appel du service metier
		try {
			MapDataMesl mapMesl = this.call(request, "I", cmroc, environnement, true);
			if (mapMesl.getMesl("PART_CMROC") == null) {
				throw new CimutMetierException("Aucun partenaire trouvé pour : " + idProf);
			}
			try {
				if (mapMesl.getOcc("ADRP_OCC").get(1).get("ADRP_ADR_CP") != null && !mapMesl.getOcc("ADRP_OCC").get(1).get("ADRP_ADR_CP").isEmpty()) {
					part.setCodePostal(mapMesl.getOcc("ADRP_OCC").get(1).get("ADRP_ADR_CP"));
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

		} catch (CimutMetierException e) {
			throw new CimutMetierException(e);
		}
		return part;
	}

}
