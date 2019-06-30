package com.sky.main;

import java.io.IOException;
import java.net.NoRouteToHostException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.bean.Config;
import com.sky.bean.YcqAccount;
import com.sky.service.YcqServer;

public class YcqThread extends Thread {
	private YcqServer server;
	private YcqAccount account;

	public YcqThread(YcqAccount user, YcqServer server) {
		this.account = user;
		this.server = server;
	}

	@Override
	public void run() {
		while (true) {
			server.setReceiving(true);
			server.updateState("接单中");
			simulate();
			synchronized (this) {
				try {
					this.wait();
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}

	private void simulate() {
		while (true) {
			try {
				Map<String, String> userMap = new HashMap<String, String>();
				userMap.put("type", "1");
				userMap.put("masktype", "2");
				userMap.put("minmoney", "0");
				userMap.put("maxmoney", "");
				userMap.put("datetime", "1");

				userMap.put("token", account.getToken());
				userMap.put("userid", account.getUserid());
				userMap.put("recaptchaRsp", account.getRsp());

				JSONObject jsonObject = null;
				Connection con = null;
				Response rs = null;
				String rsString = "";
				while (true) {
					con = Jsoup.connect("http://api.xgkst.com/api/brushuser/matchMask");
					con.method(Method.POST);
					con.header(Config.USER_AGENT, Config.USER_AGENT_VALUE2);
					con.data(userMap);
					rs = con.execute();
					rsString = rs.body();
					//System.out.println("matchMask：" + rsString);
					jsonObject = JSON.parseObject(rsString);
					// 如果没有错误代码就直接跳出执行下面的逻辑
					if (jsonObject.getBoolean("errcode")) {
						break;
					}
					// 判断是什么错误
					if (jsonObject.getString("errmsg").contains("登录超时")) {
						server.updateMessage("登录超时,自动登入中。。。如果卡在此处可能是密码错误");
						//server.sendLoginHandler();
						// 登入成功把新的信息重新放入usreMap中
						userMap.put("token", account.getToken());
						userMap.put("userid", account.getUserid());
						userMap.put("recaptchaRsp", account.getRsp());

						server.updateMessage("登入成功");
					} else if (jsonObject.getString("errmsg").contains("请求频繁")) {
						server.updateMessage("请求频繁，正在等30秒后继续自动接单");
						Thread.sleep(32000);
					} else if(jsonObject.getString("errmsg").contains("平台维护中")) {
						server.setReceiving(false);
						server.updateState("已停止");
						server.updateMessage(rsString);
						return;
					}else if(jsonObject.getString("errmsg").contains("Token对应的")){
						server.setReceiving(false);
						server.updateState("已停止");
						server.updateMessage("您的账号出现错误");
					}else if(jsonObject.getString("errmsg").contains("正在排队接单中")) {
						server.updateMessage("请求频繁，正在等30秒后继续自动接单");
						Thread.sleep(20000);
					}else{
						server.setReceiving(false);
						server.updateState("已停止");
						server.updateMessage("未知错误，请将后面的信息给开发者看：" + rsString);
						return;
					}

				}
				jsonObject = JSON.parseObject(jsonObject.getString("resultObj"));
				server.updateMessage("等待分配任务：前面还有" + jsonObject.get("queueOrder") + "位小伙伴在排队");
				Map<String, String> requestData = new HashMap<String, String>();
				requestData.put("token", account.getToken());
				requestData.put("userid", account.getUserid());
				requestData.put("applyid", jsonObject.getString("applyid"));
				int applystatus = 0;
				do {
					Thread.sleep(6000);
					con = Jsoup.connect("http://api.xgkst.com/api/brushuser/queyrMatchResult");
					con.header(Config.USER_AGENT, Config.USER_AGENT_VALUE2);
					con.method(Method.POST);
					con.data(requestData);
					rs = con.execute();
					rsString = rs.body();

					//System.out.println(rsString);
					jsonObject = JSON.parseObject(rsString);
					// 判断错误码
					if (jsonObject.getBooleanValue("errcode")) {
						// 没有错误
						jsonObject = JSON.parseObject(jsonObject.getString("resultObj"));
						applystatus = jsonObject.getInteger("applystatus");

						if (applystatus == 0) {
							server.updateMessage("等待分配任务：前面还有" + jsonObject.get("queueOrder") + "位小伙伴在排队");
						} else if (applystatus == 2) {
							server.updateMessage(jsonObject.getString("applymsg"));
							break;
						} else {
							server.updateMessage(jsonObject.getString("applymsg"));
							server.receiveDan();
							server.updateState("接到单");
							try {
								this.wait();
							} catch (InterruptedException e) {
								server.setReceiving(false);
								return;
							}

						}

					} else {
						// 有错误停止接单，输出错误
						server.setReceiving(false);
						server.updateMessage(jsonObject.getString("errmsg"));
						server.updateState("已停止");
						return;
					}

				} while (applystatus == 0);
			} catch (NoRouteToHostException | UnknownHostException e) {
				e.printStackTrace();
				server.updateMessage("网络中断，正在尝试重新接单");
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
				server.updateMessage("连接超时，正在尝试重新接单");
			} catch (IOException e) {
				e.printStackTrace();
				server.updateMessage("IO异常，正在尝试重新接单");
			} catch (InterruptedException e) {
				server.setReceiving(false);
				server.updateMessage("已停止自动接单");
				server.updateState("已停止");
				return;
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				server.setReceiving(false);
				server.updateState("已停止");
				server.updateMessage("已停止自动接单");
				return;
			}
		}

	}
}
