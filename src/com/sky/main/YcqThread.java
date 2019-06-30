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
			server.updateState("�ӵ���");
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
					//System.out.println("matchMask��" + rsString);
					jsonObject = JSON.parseObject(rsString);
					// ���û�д�������ֱ������ִ��������߼�
					if (jsonObject.getBoolean("errcode")) {
						break;
					}
					// �ж���ʲô����
					if (jsonObject.getString("errmsg").contains("��¼��ʱ")) {
						server.updateMessage("��¼��ʱ,�Զ������С�����������ڴ˴��������������");
						//server.sendLoginHandler();
						// ����ɹ����µ���Ϣ���·���usreMap��
						userMap.put("token", account.getToken());
						userMap.put("userid", account.getUserid());
						userMap.put("recaptchaRsp", account.getRsp());

						server.updateMessage("����ɹ�");
					} else if (jsonObject.getString("errmsg").contains("����Ƶ��")) {
						server.updateMessage("����Ƶ�������ڵ�30�������Զ��ӵ�");
						Thread.sleep(32000);
					} else if(jsonObject.getString("errmsg").contains("ƽ̨ά����")) {
						server.setReceiving(false);
						server.updateState("��ֹͣ");
						server.updateMessage(rsString);
						return;
					}else if(jsonObject.getString("errmsg").contains("Token��Ӧ��")){
						server.setReceiving(false);
						server.updateState("��ֹͣ");
						server.updateMessage("�����˺ų��ִ���");
					}else if(jsonObject.getString("errmsg").contains("�����Ŷӽӵ���")) {
						server.updateMessage("����Ƶ�������ڵ�30�������Զ��ӵ�");
						Thread.sleep(20000);
					}else{
						server.setReceiving(false);
						server.updateState("��ֹͣ");
						server.updateMessage("δ֪�����뽫�������Ϣ�������߿���" + rsString);
						return;
					}

				}
				jsonObject = JSON.parseObject(jsonObject.getString("resultObj"));
				server.updateMessage("�ȴ���������ǰ�滹��" + jsonObject.get("queueOrder") + "λС������Ŷ�");
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
					// �жϴ�����
					if (jsonObject.getBooleanValue("errcode")) {
						// û�д���
						jsonObject = JSON.parseObject(jsonObject.getString("resultObj"));
						applystatus = jsonObject.getInteger("applystatus");

						if (applystatus == 0) {
							server.updateMessage("�ȴ���������ǰ�滹��" + jsonObject.get("queueOrder") + "λС������Ŷ�");
						} else if (applystatus == 2) {
							server.updateMessage(jsonObject.getString("applymsg"));
							break;
						} else {
							server.updateMessage(jsonObject.getString("applymsg"));
							server.receiveDan();
							server.updateState("�ӵ���");
							try {
								this.wait();
							} catch (InterruptedException e) {
								server.setReceiving(false);
								return;
							}

						}

					} else {
						// �д���ֹͣ�ӵ����������
						server.setReceiving(false);
						server.updateMessage(jsonObject.getString("errmsg"));
						server.updateState("��ֹͣ");
						return;
					}

				} while (applystatus == 0);
			} catch (NoRouteToHostException | UnknownHostException e) {
				e.printStackTrace();
				server.updateMessage("�����жϣ����ڳ������½ӵ�");
			} catch (SocketTimeoutException e) {
				e.printStackTrace();
				server.updateMessage("���ӳ�ʱ�����ڳ������½ӵ�");
			} catch (IOException e) {
				e.printStackTrace();
				server.updateMessage("IO�쳣�����ڳ������½ӵ�");
			} catch (InterruptedException e) {
				server.setReceiving(false);
				server.updateMessage("��ֹͣ�Զ��ӵ�");
				server.updateState("��ֹͣ");
				return;
			}
			try {
				Thread.sleep(5000);
			} catch (InterruptedException e) {
				server.setReceiving(false);
				server.updateState("��ֹͣ");
				server.updateMessage("��ֹͣ�Զ��ӵ�");
				return;
			}
		}

	}
}
