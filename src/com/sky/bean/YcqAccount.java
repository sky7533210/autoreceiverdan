package com.sky.bean;

public class YcqAccount extends Account{
	private String phone;
	private String password;
	private String userid;
	private String token;
	private String rsp;
	public YcqAccount(String phone, String password, String userid, String token, String rsp) {
		super();
		this.phone = phone;
		this.password = password;
		this.userid = userid;
		this.token = token;
		this.rsp = rsp;
	}
	public YcqAccount() {
		super();
		// TODO Auto-generated constructor stub
	}
	public String getPhone() {
		return phone;
	}
	public void setPhone(String phone) {
		this.phone = phone;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	public String getUserid() {
		return userid;
	}
	public void setUserid(String userid) {
		this.userid = userid;
	}
	public String getToken() {
		return token;
	}
	public void setToken(String token) {
		this.token = token;
	}
	public String getRsp() {
		return rsp;
	}
	public void setRsp(String rsp) {
		this.rsp = rsp;
	}
}
