package com.sky.main;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.bean.Account;
import com.sky.bean.Config;
import com.sky.service.Server;

import android.util.Log;

public class XkqThread extends Thread {
	private Server server;
	private Account account;
	private MyWebSocketClient myWebSocketClient;
	private String url;
	private boolean flag=true;
	public XkqThread(Account account, Server server) {
		this.server=server;
		this.account=account;
	}
	@Override
	public void run() {
		simulate();
		while(flag) {
			synchronized (this) {
				try {
					this.wait();
					Thread.sleep(60000);
					connectWebSocket();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public void simulate() {
		server.setReceiving(true);
		server.updateState("�ӵ���");
		server.updateMessage("��ȡip���˿ڡ�id��");		
		
		Connection con = Jsoup.connect("http://aaa.698mn.com/passport/login.html");
		con.method(Method.GET);
		con.header(Config.USER_AGENT, Config.USER_AGENT_VALUE2);
		Response rs=null;
		try {
			rs = con.execute();
		} catch (IOException e) {
			e.printStackTrace();
			server.updateMessage("�������,�����¿�ʼ");
			server.updateState("��ֹͣ");
			return;
		}
		
		
		Map<String, String> cookie=rs.cookies();
		
		Map<String, String> user = new HashMap<String, String>();
		user.put("usernameone", account.getPhone());
		user.put("pwdone", account.getPassword());
		user.put("code", "");
		user.put("cid", "passport");
		con = Jsoup.connect("http://aaa.698mn.com/passport/login.html");
		con.method(Method.POST);
		con.header(Config.USER_AGENT, Config.USER_AGENT_VALUE2);
		con.data(user);
		con.cookies(cookie);
		try {
			rs = con.execute();
		} catch (IOException e) {
			e.printStackTrace();
			server.updateMessage("�������,�����¿�ʼ");
			server.updateState("��ֹͣ");
			return;
		}
		
		
		String rsString = rs.body();
		
		//Log.i("�����־", rsString);
		
		try {
			JSONObject jsonObject=JSON.parseObject(rsString);
			Integer code= jsonObject.getInteger("err_code");
			if(code!=0) {
				server.updateMessage(jsonObject.getString("msg"));
				server.updateState("��ֹͣ");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			server.updateMessage("�������λ��1");
			server.updateState("��ֹͣ");
		}
		
		cookie.putAll(rs.cookies());
		
		
		
		con = Jsoup.connect("http://aaa.698mn.com/passport/login.html");
		con.cookies(cookie);
		
		con.header("Referer", "http://aaa.698mn.com/passport/login.html");
		con.method(Method.GET);
		con.header(Config.USER_AGENT, Config.USER_AGENT_VALUE2);
		try {
			rs = con.execute();
		} catch (IOException e) {
			e.printStackTrace();
			server.updateMessage("�������,�����¿�ʼ");
			server.updateState("��ֹͣ");
			return;
		}
						
		String body=rs.body();	
		//Log.i("2222", body);
		
		String guidRegEx="guid =\"\\w+\"";
		String guid="";

		Pattern p = Pattern.compile(guidRegEx);
		Matcher m = p.matcher(body);
		if (m.find()) {
			 guid = m.group();
		}
		guid=guid.replace("\"", "");
		guid=guid.replace(" ", "");
		guid=guid.replace("guid", "token");
		//Log.i("�õ���ַ", ipPort+"|   |"+uid+"|   |"+guid);
		//url="ws://"+ipPort+"/Task?"+uid+"&TaskPrice=&DownTaskPoint=0&TaskCategory=0&"+guid;
		Log.i("token", guid);
		
		con = Jsoup.connect("http://aaa.698mn.com/site/acceptTask01.html");
		con.cookies(cookie);
		con.method(Method.POST);
		con.header(Config.USER_AGENT, Config.USER_AGENT_VALUE2);
		
		con.data("outtime", "1562507338").data("PlatformTypes", "0")
		.data("TaskType", "%E6%97%A0%E7%BA%BF%E7%AB%AF")
		.data("TaskTypelen","2").data("TaskPriceEnd", "")
		.data("FineTaskClassType","%E9%94%80%E9%87%8F%E4%BB%BB%E5%8A%A1")
		.data("terminal", "1");
		
		
		try {
			rs = con.execute();
		} catch (IOException e) {
			e.printStackTrace();
			server.updateMessage("�������,�����¿�ʼ");
			server.updateState("��ֹͣ");
			return;
		}
		
		body=rs.body();
		
		JSONObject jsonObject= JSON.parseObject(body);
		String host= jsonObject.getString("host");
		String uid=jsonObject.getString("uid");
		this.url="ws://"+host+"/Task?UserID="+uid+"&TaskPrice=&DownTaskPoint=0&TaskCategory=0&"+guid;
		Log.i("url",url);
		//ws://211.159.185.14:9877/Task?UserID=19297&TaskPrice=&DownTaskPoint=0&TaskCategory=0&token=faa824ca91989ce86235bb53b68ea46d

		connectWebSocket();
	}
	public void close(){
		flag=false;
		server.setReceiving(false);
		if(myWebSocketClient!=null&& myWebSocketClient.isFlag()){
			myWebSocketClient.close();
		}
	}
	private void connectWebSocket() {
		try {
			myWebSocketClient= new MyWebSocketClient(url,server);						
			myWebSocketClient.connect();
		} catch (Exception e) {
			e.printStackTrace();
			server.updateState("��ֹͣ");
			server.setReceiving(false);
			server.updateMessage(e.getMessage());
			return;
		}
	}
	
}
