package com.sky.util;

import java.io.IOException;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.sky.bean.User;

import android.app.AlertDialog;
import android.content.Context;
import android.content.SharedPreferences;

public class Activate {

	private Context context;
	private SharedPreferences.Editor editor;
	private boolean flag = false;

	private String jsonData;

	public Activate(Context context, SharedPreferences.Editor editor) {
		this.context = context;
		this.editor = editor;
		getJsonData();
	}

	public void activate(String phone, String strYcq, String strXkq, String strEsx, String strPty, String strXbt,
			String strFh, String strBcy,String strMg) {
		
		if (jsonData == null || jsonData.equals("")) {
			new AlertDialog.Builder(context).setTitle("error").setMessage("激活失败").setPositiveButton("确定", null)
			.show();
			return;
		}
			
		JSONObject jsonObject = JSON.parseObject(jsonData);
		jsonData = jsonObject.getString(phone);
		if (jsonData != null && !jsonData.equals("")) {
			User user = JSON.parseObject(jsonData, User.class);
			if (strYcq != null && strYcq.equals(user.getYcqId())) {
				flag = true;
				String str = "{\"phone\":\"" + strYcq
						+ "\",\"userid\":\"1000002599484008\",\"token\":\"02_e851b9bb-29b7-47a5-b0fe-0e34a94270b8\",\"rsp\":\"\"}";
				editor.putString("ycqAccount", new String(Jiami.jiami(str.getBytes())));
			}
			if (strXkq != null && strXkq.equals(user.getXkqId())) {
				flag = true;
				String str = "{\"phone\":\"" + strXkq + "\"}";
				editor.putString("xkqAccount", new String(Jiami.jiami(str.getBytes())));
			}
			if (strEsx != null && strEsx.equals(user.getEsxId())) {
				flag = true;
				String str = "{\"phone\":\"" + strEsx + "\"}";
				editor.putString("esxAccount", new String(Jiami.jiami(str.getBytes())));
			}
			if (strPty != null && strPty.equals(user.getPtyId())) {
				flag = true;
				String str = "{\"phone\":\"" + strPty + "\"}";
				editor.putString("ptyAccount", new String(Jiami.jiami(str.getBytes())));
			}
			if (strXbt != null && strXbt.equals(user.getXbtId())) {
				flag = true;
				String str = "{\"phone\":\"" + strXbt + "\"}";
				editor.putString("xbtAccount", new String(Jiami.jiami(str.getBytes())));
			}
			if (strFh != null && strFh.equals(user.getFhId())) {
				flag = true;
				String str = "{\"phone\":\"" + strFh + "\"}";
				editor.putString("fhAccount", new String(Jiami.jiami(str.getBytes())));
			}
			if (strBcy != null && strBcy.equals(user.getBcyId())) {
				flag = true;
				String str = "{\"phone\":\"" + strBcy + "\"}";
				editor.putString("bcyAccount", str);
				editor.putString("bcyAccount", new String(Jiami.jiami(str.getBytes())));
			}
			if (strMg != null && strMg.equals(user.getMgId())) {
				flag = true;
				String str = "{\"phone\":\"" + strMg + "\"}";
				editor.putString("mgAccount", new String(Jiami.jiami(str.getBytes())));
			}
			if (flag) {
				editor.putBoolean("isActivate", true);
				editor.commit();
				new AlertDialog.Builder(context).setTitle("tip").setMessage("激活成功").setPositiveButton("确定", null)
						.show();
			}
		} else {
			new AlertDialog.Builder(context).setTitle("error").setMessage("手机号码填写错误，激活失败").setPositiveButton("确定", null)
					.show();
		}
	}

	private void getJsonData() {
		try {
			byte[] buff = IOUtils.readFully(context.getAssets().open("www/logo.gif"));
			jsonData = Jiami.jiami2String(buff);
		} catch (IOException e) {
			e.printStackTrace();
			new AlertDialog.Builder(context).setTitle("error").setMessage("激活文件读取失败").setPositiveButton("确定", null)
					.show();
		}
	}
}
