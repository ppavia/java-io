package fr.cimut.ged.entrant.dto;

/**
 * Paramètres OC associés à un type de document
 */
public class TypeParamsDto {
	private String id;

	private Boolean visibleExtranet;

	private Integer dureeVisibleExtranet;

	private String userMaj;

	private String dateMaj;

	private Boolean integrableIdoc;

	public TypeParamsDto() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
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

	public String getUserMaj() {
		return userMaj;
	}

	public void setUserMaj(String userMaj) {
		this.userMaj = userMaj;
	}

	public String getDateMaj() {
		return dateMaj;
	}

	public void setDateMaj(String dateMaj) {
		this.dateMaj = dateMaj;
	}

	public Boolean getIntegrableIdoc() {
		return integrableIdoc;
	}

	public void setIntegrableIdoc(Boolean integrableIdoc) {
		this.integrableIdoc = integrableIdoc;
	}
}
