package fr.cimut.ged.entrant.dto;

public class TypeFilters {

	private Boolean idoc;

	private Boolean visibleExtranet;

	private String codeType;

	private String libelleType;

	private String category;

	public TypeFilters() {
	}

	public Boolean getIdoc() {
		return idoc;
	}

	public void setIdoc(Boolean idoc) {
		this.idoc = idoc;
	}

	public Boolean getVisibleExtranet() {
		return visibleExtranet;
	}

	public void setVisibleExtranet(Boolean visibleExtranet) {
		this.visibleExtranet = visibleExtranet;
	}

	public String getCodeType() {
		return codeType;
	}

	public void setCodeType(String codeType) {
		this.codeType = codeType;
	}

	public String getLibelleType() {
		return libelleType;
	}

	public void setLibelleType(String libelletype) {
		this.libelleType = libelletype;
	}

	public String getCategory() {
		return category;
	}

	public void setCategory(String category) {
		this.category = category;
	}

	@Override public String toString() {
		return "TypeFilters{" + "idoc=" + idoc + ", visibleExtranet=" + visibleExtranet + ", codeType='" + codeType + '\'' + ", libelleType='"
				+ libelleType + '\'' + ", category='" + category + '\'' + '}';
	}
}
