package fr.cimut.ged.entrant.dto;

/**
 * Requête de modification du paramètrage d'un type pour un OC arbitraire
 */
public class TypeParamsModificationDto {

	private Boolean integrableIdoc;
	private Boolean visibleExtranet;
	private Integer dureeVisibleExtranet;

	public TypeParamsModificationDto() {
	}

	public Boolean getIntegrableIdoc() {
		return integrableIdoc;
	}

	public void setIntegrableIdoc(Boolean integrableIdoc) {
		this.integrableIdoc = integrableIdoc;
	}

	public Boolean getVisibleExtranet() {
		return visibleExtranet;
	}

	public void setVisibleExtranet(Boolean visibleExtranet) {
		this.visibleExtranet = visibleExtranet;
	}

	public Integer getDureeVisibleExtranet() {
		return dureeVisibleExtranet;
	}

	public void setDureeVisibleExtranet(Integer dureeVisibleExtranet) {
		this.dureeVisibleExtranet = dureeVisibleExtranet;
	}
}
