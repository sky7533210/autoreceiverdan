package com.sky.service;

import com.alibaba.fastjson.JSON;
import com.sky.bean.Account;
import com.sky.main.BcyThread;
import com.sky.util.Jiami;

import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

public class BcyServer extends Server {

	private SharedPreferences sharedPreferences;
	private String key="bcyAccount";
	private BcyThread thread;
	public BcyServer(SharedPreferences sharedPreferences, Handler handler, int position) {
		super(handler, "°Ù²ÝÔ°", position);
		
		this.sharedPreferences = sharedPreferences;

		String jsonAccount = sharedPreferences.getString(key, Jiami.jiami2String("{\"phone\":\"12345678900\"}"));
		jsonAccount = Jiami.jiami2String(jsonAccount);
//		Log.i("È¡³ö", jsonAccount);
		super.setAccount(JSON.parseObject(jsonAccount, Account.class));
	}

	@Override
	public void start() {
		if (thread == null) {
			thread = new BcyThread(super.getAccount(), this);
			thread.start();
			SharedPreferences.Editor editor = sharedPreferences.edit();
			editor.putString(key, new String(Jiami.jiami(JSON.toJSONString(super.getAccount()).getBytes())));
			editor.commit();
		} else {
			synchronized (thread) {
				thread.notify();
			}
		}
	}

	@Override
	public void stop() {
		thread.interrupt();
	}

}
