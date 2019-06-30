package com.sky.service;

import com.alibaba.fastjson.JSON;
import com.sky.bean.Account;
import com.sky.main.XbtThread;
import com.sky.util.Jiami;

import android.content.SharedPreferences;
import android.os.Handler;

public class XbtServer extends Server {

	private XbtThread thread;
	private String key = "xbtAccount";
	private SharedPreferences sharedPreferences;
	public XbtServer(SharedPreferences sharedPreferences, Handler handler, int position) {
		super(handler, "小白兔", position);
		this.sharedPreferences = sharedPreferences;

		String jsonAccount = sharedPreferences.getString(key, Jiami.jiami2String("{\"phone\":\"12345678900\"}"));
		jsonAccount = Jiami.jiami2String(jsonAccount);
		super.setAccount(JSON.parseObject(jsonAccount, Account.class));
	}


	@Override
	public void start() {
		if (thread == null) {
			thread = new XbtThread(super.getAccount(), this);
			thread.start();
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString(key, new String(Jiami.jiami(JSON.toJSONString(super.getAccount()).getBytes())));
			editor.commit();
			
		} else {
			synchronized (thread) {
				thread.notify();
			}
		}
		super.setReceiving(true);
		super.updateState("接单中");
	}

	@Override
	public void stop() {
		thread.interrupt();
	}

}
