package fr.cimut.ged.entrant.appelmetier;

import java.util.Map;
import java.util.Map.Entry;

import org.apache.commons.lang.StringUtils;

import fr.cimut.ged.entrant.beans.appelmetier.Alph;
import fr.cimut.ged.entrant.exceptions.CimutConfException;
import fr.cimut.ged.entrant.exceptions.CimutMetierException;
import fr.cimut.mos.MapDataMesl;

public class AlphManager extends MetierManager {

	public AlphManager() throws CimutConfException {
		super("IHM_ALPH");
	}

	public Alph get(String insee, String cmroc, String environnement) throws CimutMetierException {
		MapDataMesl request = new MapDataMesl();

		Alph alph = new Alph();
		String cmsi = "I";

		if (insee.matches("^\\d{13}$")) {
			request.addMesl("ALPHA_INSEE", insee);
		} else {
			request.addMesl("ALPHA_S_ASSURE", insee);
			cmsi = "L";
			request.addMesl("ALPHA_MODE", "court");
			request.addMesl("INCOMPLET", "0");
		}

		try {
			MapDataMesl mapMesl = this.call(request, cmsi, cmroc, environnement, true);
			String ztLibError = mapMesl.getZt("ZT_LIBERR");
			String ztTypeError = mapMesl.getZt("ZT_TYPERR");

			if (insee.matches("^\\d{13}$")) {
				alph.setsAssu(mapMesl.getMesl("ALPHA_S_ASSURE"));
				alph.setInsee(insee);
				if (mapMesl.getMesl("ALPHA_CDPOST") != null && !mapMesl.getMesl("ALPHA_CDPOST").isEmpty()) {
					alph.setCodePostal(mapMesl.getMesl("ALPHA_CDPOST"));
				}
			} else {

				// Bon, faut pas prendre n importe quoi ici, faut prendre le benef de rang 1
				for (Entry<Integer, Map<String, String>> entry : mapMesl.getOcc("ALPHA_OCC").entrySet()) {
					if ("01".equals(entry.getValue().get("LALPHA_CDBEN"))) {
						alph.setInsee(mapMesl.getOcc("ALPHA_OCC").get(1).get("LALPHA_INSEE"));
						if (StringUtils.isNotBlank(entry.getValue().get("LALPHA_CDPOST"))) {
							alph.setCodePostal(entry.getValue().get("LALPHA_CDPOST"));
						}
					}
				}
				alph.setsAssu(insee);
			}

			if (ztTypeError != null && ztTypeError.equals("SI")) {
				throw new CimutMetierException(ztLibError);
			} else if (insee.matches("^\\d{13}$") && (mapMesl.getMesl("ALPHA_S_ASSURE") == null || mapMesl.getMesl("ALPHA_S_ASSURE").isEmpty())) {
				throw new CimutMetierException("Aucun assuré trouvé pour l'insee suivant : " + insee);
			} else if (insee.matches("^[0-9ab]{1,12}$") && mapMesl.getOcc("ALPHA_OCC") == null) {
				throw new CimutMetierException("Aucun assuré trouvé pour le numero de dossier suivant : " + insee);
			}

			return alph;

		} catch (Exception e) {
			throw new CimutMetierException(e);
		}
	}

}
