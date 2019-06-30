package com.sky.main;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.sky.bean.Account;
import com.sky.bean.Config;
import com.sky.service.Server;

public class XbtThread extends Thread {
	private Server server;
	private Account account;

	public XbtThread(Account account, Server server) {
		this.server = server;
		this.account = account;
	}

	@Override
	public void run() {
		while (true) {
			receiver();
			try {
				this.wait();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private void receiver() {
		Map<String, String> user = new HashMap<String, String>();
		user.put("mobile", account.getPhone());
		user.put("password", account.getPassword());
		user.put("mobileImei", "860988038027814%2C860988038027822");
		user.put("key", "");
		user.put("clientId", "10f1cc9d595176b29f8f407153457e95");
		user.put("mobileOs", "1");
		user.put("timeStamp", new Date().getTime()/1000+"");					   
		user.put("sign", "6c0c92ad247d6ebf8214b61e49318553");

		try {
			Connection con = Jsoup.connect("http://www.xiaobay.com/api/index/doLogin");
			Response response = con.header(Config.USER_AGENT, Config.USER_AGENT_VALUE2)
					.header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
					.header("X-Requested-With", "XMLHttpRequest").ignoreContentType(true).method(Method.POST).data(user)
					.execute();
			String string=response.body();
			String key = anylizeJson(anylizeJson(response.body(), "data"), "token");
			server.updateMessage("登入成功");
			Connection con1 = Jsoup.connect("http://www.xiaobay.com/api/order/getBuyerAccountBindingList");
			Response response2 = con1.header(Config.USER_AGENT, Config.USER_AGENT_VALUE2)
					.header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
					.header("X-Requested-With", "XMLHttpRequest").ignoreContentType(true).method(Method.POST)
					.data("key", key).execute();	
			server.updateMessage("账户信息" + response2.body());
			String accountId = anylizeJson2(anylizeJson(anylizeJson(response2.body(), "data"), "list"), "accountId");
			server.updateMessage("accountid=" + accountId);

			Map<String, String> account = new HashMap<String, String>();
			account.put("key", key);
			account.put("accountId", accountId);
			while (true) {
				Connection con2 = Jsoup.connect("http://www.xiaobay.com/api/order/doStartReceiptTask");
				Response response3 = con2.header(Config.USER_AGENT, Config.USER_AGENT_VALUE2)
						.header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
						.header("X-Requested-With", "XMLHttpRequest").ignoreContentType(true).method(Method.POST)
						.data(account).execute();
				server.updateMessage("开始排队");
				if (!anylizeJson(response3.body(), "data").equals("[]")) {
					server.receiveDan();
					server.updateMessage("接单成功" + anylizeJson(response3.body(), "data"));
					this.wait();
				}
				Thread.sleep(3000);
				Connection con3 = Jsoup.connect("http://www.xiaobay.com/api/order/doStopReceiptTask");
				Response response4 = con3.header(Config.USER_AGENT, Config.USER_AGENT_VALUE2)
						.header("Content-Type", "application/x-www-form-urlencoded; charset=UTF-8")
						.header("X-Requested-With", "XMLHttpRequest").ignoreContentType(true).method(Method.POST)
						.data(account).execute();
				server.updateMessage("停止排队");
				if (!anylizeJson(response4.body(), "data").equals("[]")) {
					server.receiveDan();
					server.updateMessage("接单成功" + anylizeJson(response3.body(), "data"));
					synchronized (this) {
						this.wait();
					}
					
				}
				Thread.sleep(7000);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}catch (InterruptedException e) {
			server.setReceiving(false);
			server.updateState("已停止");
			server.updateMessage("已停止接单");
			return;
		}
	}

	public static String anylizeJson(String data, String key) {
		JSONObject jsonObject = JSON.parseObject(data);
		return jsonObject.getString(key);
	}

	public static String anylizeJson2(String data, String key) {
		JSONArray jsonArray = JSON.parseArray(data);
		return jsonArray.getJSONObject(0).getString(key);
	}
}
