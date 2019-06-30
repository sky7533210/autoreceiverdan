package com.sky.service;

import com.alibaba.fastjson.JSON;
import com.sky.bean.Account;
import com.sky.main.XkqThread;
import com.sky.util.Jiami;

import android.content.SharedPreferences;
import android.os.Handler;

public class XkqServer extends Server {

	private XkqThread thread;
	private String key = "xkqAccount";
	private SharedPreferences sharedPreferences;

	public XkqServer(SharedPreferences sharedPreferences, Handler handler, int position) {
		super(handler, "ÐÅ¿ÍÈ¦", position);
		this.sharedPreferences = sharedPreferences;

		String jsonAccount = sharedPreferences.getString(key, Jiami.jiami2String("{\"phone\":\"12345678900\"}"));
		jsonAccount = Jiami.jiami2String(jsonAccount);
		super.setAccount(JSON.parseObject(jsonAccount, Account.class));
	}

	@Override
	public void start() {
		thread = new XkqThread(super.getAccount(), this);
		thread.start();
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(key, Jiami.jiami2String(JSON.toJSONString(super.getAccount())));
		editor.commit();
	}

	@Override
	public void stop() {
		thread.close();
		thread.interrupt();
	}

	public void restart() {
		synchronized (thread) {
			thread.notify();
		}
	}
}
