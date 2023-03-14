package fr.cimut.ged.entrant.beans.p360;

import org.codehaus.jackson.annotate.JsonIgnore;

/**
 * Classe abstraite utilisée comme classe mere des DTO utilisés par les web services de Search 360
 * 
 * @author pgarel
 */
public abstract class Search360DtoAbstract implements Comparable<Search360DtoAbstract> {

	/** Methode utilisée pour comparer 2 objets Search360DtoAbstract */
	@JsonIgnore
	public abstract String getCompareString();

	@Override
	public int compareTo(Search360DtoAbstract o) {
		if (o == null) {
			return -1;
		}
		if (getCompareString() == null) {
			if (o.getCompareString() == null) {
				return 0;
			} else {
				return 1;
			}
		}
		return getCompareString().compareTo(o.getCompareString());
	}
}
