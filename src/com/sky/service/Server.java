package com.sky.service;

import com.sky.bean.Account;

import android.os.Handler;
import android.os.Message;

public abstract class Server {
	private Handler handler;
	private String platformName;
	private String state="ÒÑÍ£Ö¹";
	private String message;
	private Account account;
	private int position;
	private boolean isReceiving=false;
	private boolean isShowing;
	public Server(Handler handler,String platformName,int position){
		this.handler=handler;
		this.platformName=platformName;
		this.position=position;
	}
	public abstract void start();
	public abstract void stop();
	public void updateMessage(String message) {
		this.message=message;
		if(isShowing()) {
		Message msg=Message.obtain(handler,2);
		msg.sendToTarget();
		}
	}
	public void updateState(String state) {
		this.state=state;
		Message msg=Message.obtain(handler,1);
		msg.obj=state;
		msg.arg2=position;
		msg.sendToTarget();
	}
	public void receiveDan() {
		Message msg=Message.obtain(handler,3);
		msg.sendToTarget();
	}
	public String getPlatformName() {
		return platformName;
	}
	public void setPlatformName(String platformName) {
		this.platformName = platformName;
	}
	public String getState() {
		return state;
	}
	public String getMessage() {
		return message;
	}
	public boolean isReceiving() {
		return isReceiving;
	}
	public void setReceiving(boolean isReceiving) {
		this.isReceiving = isReceiving;
	}
	public boolean isShowing() {
		return isShowing;
	}
	public void setShowing(boolean isShowing) {
		this.isShowing = isShowing;
	}
	
	public int getPosition() {
		return position;
	}
	public Account getAccount() {
		return account;
	}
	public void setAccount(Account account) {
		this.account=account;
	}
}
