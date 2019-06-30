package com.sky.main;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.service.Server;

public class MgThread extends Thread {

	private Server server;
	public MgThread(Server server) {
		this.server=server;
	}
	@Override
	public void run() {
		while(true) {
			simulate();
			synchronized (this) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					server.setReceiving(false);
					e.printStackTrace();
				}
			}
		}				
	}
	public void simulate() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		Map<String, String> parameter = new HashMap<String, String>();
		parameter.put("userType", "02");
		parameter.put("userName", server.getAccount().getPhone());
		int i=0;
		while (true) {
			++i;
			server.updateMessage("正在自动接单"+i+"次");
			Date date = new Date();
			parameter.put("reqTime", simpleDateFormat.format(date));

			Connection con = Jsoup
					.connect("http://60.205.186.218:8888/api/shop/myBuy?userType=02&userName=18679447632&reqTime="
							+ simpleDateFormat.format(date));
			con.method(Method.OPTIONS);
			// con.data(parameter);
			try {
				con.execute();
			} catch (IOException e) {
				e.printStackTrace();
				server.setReceiving(false);
				server.updateState("已停止");
				server.updateMessage("IO异常");
				return;
			}

			con = Jsoup.connect("http://60.205.186.218:8888/api/shop/myBuy?userType=02&userName=18679447632&reqTime="
					+ simpleDateFormat.format(date));
			con.method(Method.POST);
			con.ignoreContentType(true);
			con.header("Content-Type", "application/json;charset=UTF-8");
			con.data(parameter);
			try {
				Response rs = con.execute();
				JSONObject jsonObject=JSON.parseObject(rs.body());
				int returnCode=jsonObject.getIntValue("returnCode");
				if(returnCode==1) {
					server.updateState("接到单");
					server.receiveDan();
					server.updateMessage("恭喜你已经接到新任务");
					synchronized (this) {
						try {
							this.wait();
						} catch (InterruptedException e) {
							e.printStackTrace();
							server.updateMessage("已停止播放通知");
							server.updateState("已停止");
							server.setReceiving(false);
							return;
						}
					}
				}
			} catch (IOException e) {
				e.printStackTrace();
				server.setReceiving(false);
				server.updateState("已停止");
				server.updateMessage("IO异常");
				return;
			}
			try {
				Thread.sleep(40*1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
				server.updateMessage("已停止自动接单");
				server.setReceiving(false);
				server.updateState("已停止");
				return;
			}
		}
	}
}
