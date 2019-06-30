package com.sky.bean;

public class Account {

	private String phone;
	private String password;
	
	public Account() {
		super();
	}
	public Account(String phone, String password) {
		super();
		this.phone = phone;
		this.password = password;
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
	
}
