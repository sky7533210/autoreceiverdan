package com.sky.main;

import java.net.URI;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_10;
import org.java_websocket.drafts.Draft_17;
import org.java_websocket.handshake.ServerHandshake;
import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.service.Server;
import com.sky.service.XkqServer;

public class MyWebSocketClient extends WebSocketClient {
	private Server server;
	private boolean flag;
	public MyWebSocketClient(String serverURI,Server server) throws Exception {	
		super(new URI(serverURI),new Draft_17());
		this.server=server;
	}

	public boolean isFlag() {
		return flag;
	}

	@Override
	public void onClose(int arg0, String arg1, boolean arg2) {
		//System.out.println("arg0:"+arg0+"arg1:"+arg1+"arg2:"+arg2);
		if(flag&&arg0==1006) {
			server.updateMessage("网络中断，60秒后尝试重新接单");
			((XkqServer)server).restart();
			return;
		}		
		if(flag) {
			server.updateMessage("已停止自动接单");
			server.updateState("已停止");
			server.setReceiving(false);
			return;
		}
		
	}

	@Override
	public void onError(Exception arg0) {
		server.updateMessage(arg0.getMessage());
		server.updateState("已停止");
		server.setReceiving(false);
	}

	@Override
	public void onMessage(String arg0) {
		//System.out.println("onmessage:"+arg0);
		JSONObject jsonObject=JSON.parseObject(arg0);
		String message=jsonObject.getString("Description");
		server.updateMessage(message);
	
		int rType=jsonObject.getIntValue("RType");
		if(rType==100||rType==101){
			flag=false;
			server.updateState("接到单");
			server.receiveDan();
		}
	}

	@Override
	public void onOpen(ServerHandshake arg0) {
		//this.send("hearbear_p31974");
		flag=true;
		server.updateMessage("正在排队中。。。");
	}
}
