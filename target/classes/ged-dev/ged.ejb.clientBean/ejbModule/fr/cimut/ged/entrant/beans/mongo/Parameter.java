package fr.cimut.ged.entrant.beans.mongo;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Parameter implements Serializable, GenericMgDbBean {

	private static final long serialVersionUID = 1L;

	private String id;

	private List<String> list = new ArrayList<String>();

	private String cmroc;

	@Override
	public String getId() {
		return id;
	}

	@Override
	public void setId(String id) {
		this.id = id;
	}

	public List<String> getList() {
		return list;
	}

	public void setList(List<String> arrayList) {
		this.list = arrayList;
	}

	public String getCmroc() {
		return cmroc;
	}

	public void setCmroc(String cmroc) {
		this.cmroc = cmroc;
	}

}
