package com.sky.service;



import com.alibaba.fastjson.JSON;
import com.sky.bean.Account;
import com.sky.main.EsxThread;
import com.sky.util.Jiami;

import android.content.SharedPreferences;
import android.os.Handler;

public class EsxServer extends Server {

	private EsxThread thread;
	private String key = "esxAccount";
	private SharedPreferences sharedPreferences;
	
	public EsxServer(SharedPreferences sharedPreferences, Handler handler, int position) {
		super(handler, "ЖўЪІаж", position);
		this.sharedPreferences = sharedPreferences;

		String	jsonAccount = sharedPreferences.getString(key,Jiami.jiami2String("{\"phone\":\"12345678900\"}"));
		jsonAccount = Jiami.jiami2String(jsonAccount);
	
		super.setAccount(JSON.parseObject(jsonAccount, Account.class));
		
		
	}
	
	@Override
	public void start() {
		thread = new EsxThread(super.getAccount().getPhone(), super.getAccount().getPassword(), this);
		thread.start();
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(key, Jiami.jiami2String(JSON.toJSONString(super.getAccount())));
		editor.commit();
	}

	@Override
	public void stop() {
		if (thread != null)
			thread.close();
	}
}
