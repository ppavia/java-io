package fr.cimut.ged.entrant.dto;

import fr.cimut.ged.entrant.beans.TypeContexte;

/**
 * Filtres à appliquer côté requête document DAO
 */
public class DocumentsFilters {
	private final Boolean visibleExtranet;
	private final String typeCode;

	// Lorsque les critères de recherches ne sont pas pris en compte par une API Document
	public static DocumentsFilters EMPTY_CLIENT_FILTERS = new DocumentsFilters();

	private DocumentsFilters() {
		visibleExtranet = null;
		typeCode = null;
	}

	public DocumentsFilters(TypeContexte typeContexte, String typeCode) {
		// visibleExtranet null indique de ne pas tenir compte de la visibilité extranet au niveau type (pour le moment)
		this.visibleExtranet = TypeContexte.EXTRANET.equals(typeContexte) ? true : null;
		this.typeCode = typeCode;
	}

	public Boolean getVisibleExtranet() {
		return visibleExtranet;
	}

	public String getTypeCode() {
		return typeCode;
	}
}
