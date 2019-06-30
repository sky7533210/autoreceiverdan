package com.sky.bean;

public class User {
	private String ycqId;
	private String xkqId;
	private String esxId;
	private String ptyId;
	private String xbtId;
	private String fhId;
	private String bcyId;
	private String mgId;
	public User() {
		super();
	}

	public User(String ycqId, String xkqId, String esxId, String ptyId, String xbtId, String fhId, String bcyId,String mgId) {
		super();
		this.ycqId = ycqId;
		this.xkqId = xkqId;
		this.esxId = esxId;
		this.ptyId = ptyId;
		this.xbtId = xbtId;
		this.fhId = fhId;
		this.bcyId = bcyId;
		this.mgId=mgId;
	}

	public String getYcqId() {
		return ycqId;
	}

	public void setYcqId(String ycqId) {
		this.ycqId = ycqId;
	}

	public String getXkqId() {
		return xkqId;
	}

	public void setXkqId(String xkqId) {
		this.xkqId = xkqId;
	}

	public String getEsxId() {
		return esxId;
	}

	public void setEsxId(String esxId) {
		this.esxId = esxId;
	}

	public String getPtyId() {
		return ptyId;
	}

	public void setPtyId(String ptyId) {
		this.ptyId = ptyId;
	}

	public String getXbtId() {
		return xbtId;
	}

	public void setXbtId(String xbtId) {
		this.xbtId = xbtId;
	}

	public String getFhId() {
		return fhId;
	}

	public void setFhId(String fhId) {
		this.fhId = fhId;
	}

	public String getBcyId() {
		return bcyId;
	}

	public void setBcyId(String bcyId) {
		this.bcyId = bcyId;
	}
	public String getMgId() {
		return mgId;
	}
	public void setMgId(String mgId) {
		this.mgId = mgId;
	}
}
