package fr.cimut.ged.entrant.beans.mongo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class RuleCriteres implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@JsonProperty("id")
	private String id;

	@JsonProperty("LISTE")
	private List<String> parameters = new ArrayList<String>();

	public RuleCriteres() {

	}

	public RuleCriteres(String id, List<String> parameters) {
		this.parameters = parameters;
		this.id = id;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getParameters() {
		return parameters;
	}

	public void addParameter(String parameter) {
		this.parameters.add(parameter);
	}

	public void setParameters(List<String> parameters) {
		this.parameters = parameters;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		RuleCriteres other = (RuleCriteres) obj;
		if (!id.equals(other.id)) {
			return false;
		}
		if (parameters == null && other.parameters == null) {
			return true;
		}
		if (parameters.isEmpty() && other.parameters.isEmpty()) {
			return true;
		}
		if (parameters.size() != other.parameters.size()) {
			return false;
		}
		parameters = new ArrayList<String>(parameters);
		other.parameters = new ArrayList<String>(other.parameters);
		Collections.sort(parameters);
		Collections.sort(other.parameters);
		return parameters.equals(other.parameters);
	}

	@Override
	public int hashCode() {
		return Arrays.hashCode(parameters.toArray());
	}

}