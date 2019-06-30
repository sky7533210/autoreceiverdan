package com.sky.service;

import com.alibaba.fastjson.JSON;
import com.sky.bean.Account;
import com.sky.main.MgThread;
import com.sky.util.Jiami;

import android.content.SharedPreferences;
import android.os.Handler;

public class MgServer extends Server {

	private MgThread thread;
	private SharedPreferences sharedPreferences;
	private String key="mgAccount";
	public MgServer(SharedPreferences sharedPreferences,Handler handler, int position) {
		super(handler, "Ä¾¹Ï", position);
		
		this.sharedPreferences = sharedPreferences;
		String jsonAccount = sharedPreferences.getString(key, Jiami.jiami2String("{\"phone\":\"12345678900\"}"));
		jsonAccount = Jiami.jiami2String(jsonAccount);
		super.setAccount(JSON.parseObject(jsonAccount, Account.class));
	}

	@Override
	public void start() {
		if(thread==null) {
			thread=new MgThread(this);
			thread.start();
		} else {
			synchronized (thread) {
				thread.notify();
			}
		}
		super.setReceiving(true);
		super.updateState("½Óµ¥ÖÐ");
		
	}

	@Override
	public void stop() {
		thread.interrupt();
	}

}
