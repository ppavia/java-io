package fr.cimut.ged.entrant.dao;

import fr.cimut.ged.entrant.exceptions.BadTenantRuntimeGedeException;

import static org.apache.commons.lang3.StringUtils.isBlank;

/**
 * Informations propres à Assia considérées valides pour la requête courante
 *
 * <p>
 *     Inspiré par la section "Claims" d'un Token JWT.
 *     En pratique : Utilisé pour pointer vers un schéma côté StarwebDAO
 * </p>
 */
public class AssiaClaims {

	public static final String OC_0001 = "0001";
	public static final String OC_0000 = "0000";

	/**
	 * Le tenant utilisé lorsque on a uniquement besoin de lire le schéma OC0001 et pas les schémas OC
	 *
	 * Pour le moment on réutilise OC0001 faute de mieux
	 */
	public static final String REFERENTIAL_ACCESS_ONLY_TENANT = "0001";

	public static final String MSG_ERR_NO_CMROC = "cmroc doit être présent";
	public static final String ERR_MSG_CMROC_MUST_BE_A_TENANT_ONE = "Il faut un cmroc organsime pour accéder à cette fonctionnalité";

	private final String cmroc;
	private final String env;

	/**
	 * Cibler un endpoint qui supporte à la fois une requête sans accès OC, et avec accès OC
	 *
	 * <p>
	 *     Typiquement: On peut obtenir des types avec le paramétrage de l'OC et
	 *     sinon avec le paramétrage par défaut
	 * </p>
	 * @param cmroc
	 * @param env
	 * @return
	 */
	public static AssiaClaims forTenantOrReferential(String cmroc, String env) {
		String effectiveCmroc;
		if (isBlank(cmroc) || cmroc.equals(OC_0001) || cmroc.equals(OC_0000)) {
			// A l'avenir il faudrait un user Oracle uniquement capable de lire OC0001 et rien d'autre
			// En attendant on pointe vers OC0001
			effectiveCmroc = REFERENTIAL_ACCESS_ONLY_TENANT;
		} else {
			effectiveCmroc = cmroc;
		}

		return new AssiaClaims(effectiveCmroc, env);
	}

	/**
	 * Cibler un endpoint qui supporte uniqument des requête avec accès OC
	 *
	 * @param cmroc
	 * @param env
	 * @return
	 */
	public static AssiaClaims forTenantOnly(String cmroc, String env) {
		if (isBlank(cmroc)) {
			throw new BadTenantRuntimeGedeException(MSG_ERR_NO_CMROC);
		}

		if (cmroc.equals(OC_0001) || cmroc.equals(OC_0000)) {
			throw new BadTenantRuntimeGedeException(ERR_MSG_CMROC_MUST_BE_A_TENANT_ONE);
		}

		return new AssiaClaims(cmroc, env);
	}

	/**
	 * Cibler un endpoint qui supporte uniqument des requête vers le référentiel (sans accès OC)
	 *
	 * @param env
	 * @return
	 */
	public static AssiaClaims forReferentialOnly(String env) {
		return new AssiaClaims(REFERENTIAL_ACCESS_ONLY_TENANT, env);
	}

	private AssiaClaims(String cmroc, String env) {
		this.cmroc = cmroc;
		this.env = env;
	}

	public boolean isReferentialOnly() {
		// Certaines requêtes relatives au référentiel de données ne nécessitent pas de connaître le Tenant
		return null == cmroc || cmroc.equals(REFERENTIAL_ACCESS_ONLY_TENANT);
	}

	public String getCmroc() {
		return cmroc;
	}

	public String getEnv() {
		return env;
	}
}
