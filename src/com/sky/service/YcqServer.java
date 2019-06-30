package com.sky.service;

import java.util.Timer;
import java.util.TimerTask;

import com.alibaba.fastjson.JSON;
import com.sky.bean.Account;
import com.sky.shuadan.R;
import com.sky.util.Jiami;
import com.sky.webview.InterceptingWebViewClient;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.LinearLayout;

public class YcqServer extends Server {

	private Account account;
	
	private SharedPreferences sharedPreferences;
	
	private String key = "ycqAccount";
	
	private WebView mWebView;
	
	private Context context;
	
	private Handler handler;

	private InterceptingWebViewClient iwvc;

	private LinearLayout mWebContainer;
	
	private View contentVew;

	public YcqServer(Context context,SharedPreferences sharedPreferences,Handler handler, int position) {
		super(handler, "赢创圈", position);
		this.sharedPreferences = sharedPreferences;
		this.handler=handler;
		this.context=context;
		String jsonAccount = sharedPreferences.getString(key,Jiami.jiami2String("{\"phone\":\"12345678900\"}"));
		jsonAccount=Jiami.jiami2String(jsonAccount);
		account = JSON.parseObject(jsonAccount, Account.class);
		super.setAccount(account);
		Timer timer=new Timer();
		timer.schedule(new TimerTask() {
			@Override
			public void run() {
				Message msg=Message.obtain(YcqServer.this.handler, 5);
		    	msg.sendToTarget();
			}
		},20*60*1000 ,20*60*1000);
	}

	@Override
	public void start() {
		super.setReceiving(true);
		super.updateState("接单中");
		super.updateMessage("正在自动点击接单中");
		iwvc.setAccount(super.getAccount());
		if(mWebView==null) {
			showWebView(contentVew);
		}
		
		mWebView.loadUrl("http://mobile.xgkst.com/#/index");
		
		SharedPreferences.Editor editor = sharedPreferences.edit();
		editor.putString(key, Jiami.jiami2String(JSON.toJSONString(super.getAccount())));
		editor.commit();
	}

	@Override
	public void stop() {
		super.updateState("已停止");
		super.updateMessage("已停止接单");
		super.setReceiving(false);
		destroyWebView();
	}

	public void showWebView(View contentView) {
		this.contentVew=contentView;
		if(mWebView==null) {
			initWebView();
		}
		mWebContainer.setVisibility(View.VISIBLE);
	}
	
	private void initWebView() {
		
	    mWebView = new WebView(context.getApplicationContext());
	    iwvc = new InterceptingWebViewClient(context,mWebView,handler,this);
		mWebView.setWebViewClient(iwvc);
		mWebView.setWebChromeClient(new WebChromeClient());
		WebSettings settings = mWebView.getSettings();
		settings.setJavaScriptEnabled(true);
		settings.setDomStorageEnabled(true);
		 LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, 
				 ViewGroup.LayoutParams.MATCH_PARENT);
		
		mWebView.setLayoutParams(layoutParams);
		if(mWebContainer==null)
			mWebContainer = (LinearLayout) contentVew.findViewById(R.id.web_container);
	    mWebContainer.addView(mWebView);
	}
	private void destroyWebView() {
		if(mWebView!=null) {
			mWebView.loadDataWithBaseURL(null, "", "text/html", "utf-8", null);
	        ((LinearLayout) mWebView.getParent()).removeView(mWebView);
	        mWebView.destroy();
	        mWebView = null;
		}		
	}
	public void hideWebView() {
		mWebContainer.setVisibility(View.INVISIBLE);		
	}
}
