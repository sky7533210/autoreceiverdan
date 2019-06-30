package com.sky.main;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jsoup.Connection;
import org.jsoup.Connection.Method;
import org.jsoup.Connection.Response;
import org.jsoup.Jsoup;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.bean.Account;
import com.sky.bean.Data;
import com.sky.bean.KeyWord;
import com.sky.bean.Task;
import com.sky.service.Server;

public class BcyThread extends Thread {

	Map<String, String> cookie = new HashMap<String, String>();
	private Account account;
	private Server server;
	private String taobaoId;
	public BcyThread(Account account, Server server) {
		this.account=account;
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
					e.printStackTrace();
				}
			}
		}
		
	}
	private void simulate() {
		Map<String, String> parameter = new HashMap<String, String>();
		
		parameter.put("c", "ShouYe");
		parameter.put("a", "getApageTask");
		parameter.put("name", account.getPhone());
		parameter.put("pass", account.getPassword());
		int page=0;
		server.updateMessage("�����Զ��ӵ���");
		server.setReceiving(true);
		server.updateState("�ӵ���");
		while (true) {
			int status = 0;
			while (status == 0) {
				++page;
				parameter.put("page", ""+page);
				server.updateMessage("�������"+page+"ҳ�Ƿ��пɽӵĵ�");
				try {
					Thread.sleep(3000);
				} catch (InterruptedException e1) {
					server.updateState("��ֹͣ");
					server.updateMessage("��ֹͣ�Զ��ӵ�");
					server.setReceiving(false);
					return;
				}
				Connection con = Jsoup.connect("http://fz.taodouyu.com/taomessage/index.php");
				con.ignoreContentType(true);
				con.method(Method.GET);
				con.data(parameter);
				con.cookies(cookie);
				try {
					Response rs = con.execute();
					cookie.putAll(rs.cookies());
					Data data = JSON.parseObject(rs.body(), Data.class);
					status=data.getStatus();
					if(status==0) {
						List<Task> tasks=data.getData();
						for(Task task:tasks) {
							if(task.getCando()==1) {
								KeyWord keyWord=clickDan(task.getTrueid());
								int state=receiveDan(task.getTrueid(), keyWord);
								if(state==0) {
									server.updateState("�ӵ���");
									server.updateMessage("��ϲ��ɹ��ӵ���");
									server.receiveDan();
									synchronized (this) {
										try {
											this.wait();
										} catch (InterruptedException e) {
											e.printStackTrace();
											server.setReceiving(false);
											return;
										}	
									}																
								}else if(state==9) {
									
									server.setReceiving(false);
									server.updateState("��ֹͣ");
									server.updateMessage("24Сʱ֮���ܽ�����");
									synchronized (this) {
										try {
											this.wait();
										} catch (InterruptedException e) {
											e.printStackTrace();
											return;
										}	
									}
								}
							}
						}
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
			page=0;
		}
		
	}
	
	/**
	 * �����
	 * @param taskid
	 * @throws IOException 
	 */
	public KeyWord clickDan(int taskid) throws IOException {
		
		Map<String, String> parameter = new HashMap<String, String>();
		
		parameter.put("c", "ShouYe");
		parameter.put("a", "userandorder");
		parameter.put("name", account.getPhone());
		parameter.put("pass", account.getPassword());
		parameter.put("taskid", taskid+"");
		parameter.put("v", "62");
		
		Connection con = Jsoup.connect("http://fz.taodouyu.com/taomessage/index.php");
		con.ignoreContentType(true);
		con.method(Method.GET);
		con.data(parameter);
		con.cookies(cookie);
		
		Response rs=con.execute();
		cookie.putAll(rs.cookies());
		String json=rs.body();
		JSONObject jsonObject=JSON.parseObject(json);
		//System.out.println(jsonObject.toString());
		taobaoId=jsonObject.getString("buyer1");
		json=jsonObject.getString("keywordInfo");
		//System.out.println(json);
		return JSON.parseObject(json,KeyWord.class);
	}
	//����ӵ���ť
	public int receiveDan(int taskid,KeyWord keyWord) throws IOException {
		Map<String, String> parameter = new HashMap<String, String>();
		parameter.put("name", account.getPhone());
		parameter.put("pass", account.getPassword());
		parameter.put("taskid",taskid+"");
		parameter.put("taobaoaccount", taobaoId);
		try {
			parameter.put("keyword", keyWord.getKeyword());
			parameter.put("keywordid", keyWord.getKeywordid());
		}catch (NullPointerException e) {
			return 100;
		}
		
		Connection con = Jsoup.connect("http://fz.taodouyu.com/index.php/TaobaoTask/regtask");
		con.ignoreContentType(true);
		con.method(Method.GET);
		con.data(parameter);
		con.cookies(cookie);
		Response rs= con.execute();
		
		String json=rs.body();
		JSONObject jsonObject= JSON.parseObject(json);
		int status=jsonObject.getIntValue("status");
		return status;
	}
}
