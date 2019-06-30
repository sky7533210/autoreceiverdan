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
		server.updateState("接单中");
		server.updateMessage("获取ip、端口、id中");		
		
		Connection con = Jsoup.connect("http://aaa.698mn.com/passport/login.html");
		con.method(Method.GET);
		con.header(Config.USER_AGENT, Config.USER_AGENT_VALUE2);
		Response rs=null;
		try {
			rs = con.execute();
		} catch (IOException e) {
			e.printStackTrace();
			server.updateMessage("网络故障,请重新开始");
			server.updateState("已停止");
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
			server.updateMessage("网络故障,请重新开始");
			server.updateState("已停止");
			return;
		}
		
		
		String rsString = rs.body();
		
		//Log.i("登入标志", rsString);
		
		try {
			JSONObject jsonObject=JSON.parseObject(rsString);
			Integer code= jsonObject.getInteger("err_code");
			if(code!=0) {
				server.updateMessage(jsonObject.getString("msg"));
				server.updateState("已停止");
				return;
			}
		} catch (Exception e) {
			e.printStackTrace();
			server.updateMessage("程序出错位置1");
			server.updateState("已停止");
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
			server.updateMessage("网络故障,请重新开始");
			server.updateState("已停止");
			return;
		}
						
		String body=rs.body();	
		//Log.i("2222", body);
		
		
		String ipPortRegEx ="((2[0-4]\\d|25[0-5]|[01]?\\d\\d?)\\.){3}(2[0-4]\\d|25[0-5]|[01]?\\d\\d?):\\d+";
		String useridRegEx="UserID=\\d+";
		String guidRegEx="guid =\"\\w+\"";
		String ipPort="";
		String uid="";
		String guid="";
		
		Pattern p = Pattern.compile(ipPortRegEx);
		Matcher m = p.matcher(body);
		if (m.find()) {
			ipPort = m.group() ;
			
		}
		p = Pattern.compile(useridRegEx);
		m = p.matcher(body);
		if (m.find()) {
			 uid = m.group() ;
		}
		p = Pattern.compile(guidRegEx);
		m = p.matcher(body);
		if (m.find()) {
			 guid = m.group();
		}
		guid=guid.replace("\"", "");
		guid=guid.replace(" ", "");
		guid=guid.replace("guid", "token");
		Log.i("拿到地址", ipPort+"|   |"+uid+"|   |"+guid);
		url="ws://"+ipPort+"/Task?"+uid+"&TaskPrice=&DownTaskPoint=0&TaskCategory=0&"+guid;	
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
			server.updateState("已停止");
			server.setReceiving(false);
			server.updateMessage(e.getMessage());
			return;
		}
	}
	
}
