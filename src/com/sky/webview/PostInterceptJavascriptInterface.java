package com.sky.webview;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.webkit.JavascriptInterface;

import org.jsoup.Jsoup;

import com.sky.bean.Account;
import com.sky.service.Server;
import com.sky.util.IOUtils;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
 
public class PostInterceptJavascriptInterface {
    public static final String TAG = "PostInterceptJavascriptInterface";
 
    private static String mInterceptHeader = null;
	
    private Handler handler;
    
    private Server server;

	public PostInterceptJavascriptInterface(Handler handler, Server server) {	
		this.handler=handler;
		this.server=server;
	}

	public String enableIntercept(Context context, byte[] data,Account account) throws IOException {   	
        if (mInterceptHeader == null) {
            mInterceptHeader = new String(IOUtils.readFully(context.getAssets().open("www/interceptheader.html")), "UTF-8");
            mInterceptHeader=String.format(mInterceptHeader, account.getPhone(),account.getPassword());
        }
        //System.out.println(mInterceptHeader);
        org.jsoup.nodes.Document doc = Jsoup.parse(new String(data));
        doc.outputSettings().prettyPrint(true);
        org.jsoup.select.Elements el = doc.getElementsByTag("head");
        
        if (el.size() > 0) {
            el.get(0).prepend(mInterceptHeader);
        }
        String pageContents = doc.toString();
        return pageContents;
    }
    
    /*@JavascriptInterface
    public void customAjax(String method,String body) {
        Log.i(TAG, "Ajax data: " + method + " " + body);
    }*/
   /* @JavascriptInterface
    public void customSubmit(String json, String method, String enctype) {
        //Log.i(TAG, "json="+json+"method="+method+"enctype="+enctype);
    }*/
    @JavascriptInterface
    public void customReceived() {
       // Log.i(TAG, "success");
    	server.updateMessage("恭喜你成功接到单");
    	server.updateState("接到单");
    	server.receiveDan();
    	Message msg=Message.obtain(handler, 3);
    	msg.sendToTarget();
    }
}