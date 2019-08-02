package com.sky.bean;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.JavascriptInterface;

public class AndroidtoJs{

	private Handler handler;
    public AndroidtoJs(Handler handler) {
    	this.handler=handler;
	}
	@JavascriptInterface
    public void open(String msg) {
        Log.i("111111", msg);
        Message message= Message.obtain(handler, 1);
   	 	message.obj=msg;
   	 	message.sendToTarget();
    }
    @JavascriptInterface
    public void close(String msg) {
    	 Log.i("111111", msg);
    	 Message message= Message.obtain(handler, 1);
    	 message.obj=msg;
    	 message.sendToTarget();
    }
    @JavascriptInterface
    public void message(String msg) {
    	 Log.i("111111", msg);
    	 Message message= Message.obtain(handler, 1);
    	 message.obj=msg;
    	 message.sendToTarget();
    }
}
