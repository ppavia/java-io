package fr.cimut.ged.entrant.beans;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import fr.cimut.ged.entrant.beans.db.User;
import fr.cimut.ged.entrant.beans.mongo.Rule;

/**
 * déclaration dun bean agrgeant toutes les données d'entrée necessaires à CDDE ou CRDE pour les appels REST
 * 
 * @author jlebourgocq
 *
 */
public class CddeCrdeBeanAggregation implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 242753153663334369L;

	public Map<String, String> parametersCdde;

	public User user;

	public String id;

	public String cmroc;

	public List<String> eddocIds;

	public Rule rule;

	Map<String, List<String>> parametersCrde;

	public Map<String, String> getParametersCdde() {
		return parametersCdde;
	}

	public void setParametersCdde(Map<String, String> parametersCdde) {
		this.parametersCdde = parametersCdde;
	}

	public Map<String, List<String>> getParametersCrde() {
		return parametersCrde;
	}

	public void setParametersCrde(Map<String, List<String>> parametersCrde) {
		this.parametersCrde = parametersCrde;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public List<String> getEddocIds() {
		return eddocIds;
	}

	public void setEddocIds(List<String> eddocIds) {
		this.eddocIds = eddocIds;
	}

	public String getCmroc() {
		return cmroc;
	}

	public void setCmroc(String cmroc) {
		this.cmroc = cmroc;
	}

	public Rule getRule() {
		return rule;
	}

	public void setRule(Rule rule) {
		this.rule = rule;
	}

	@Override
	public String toString() {
		return "CddeBeanAggregation [parametersCrde=" + (parametersCrde == null ? "null" : parametersCrde.size()) + "parametersCdde="
				+ (parametersCdde == null ? "null" : parametersCdde.size()) + ", user=" + (user == null ? "null" : user.getLogin()) + ", id=" + id
				+ ", cmroc=" + cmroc + ", eddocIds=" + eddocIds + "]";
	}

}
