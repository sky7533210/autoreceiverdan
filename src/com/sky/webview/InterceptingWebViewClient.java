package com.sky.webview;

import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.webkit.ValueCallback;
import android.webkit.WebResourceRequest;
import android.webkit.WebResourceResponse;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import com.sky.bean.Account;
import com.sky.service.Server;
import com.sky.util.IOUtils;

public class InterceptingWebViewClient extends WebViewClient {
	//public static final String TAG = "InterceptingWebViewClient";

	private WebView mWebView = null;
	private PostInterceptJavascriptInterface mJSSubmitIntercept = null;
	private String strJS;
	private Context context;
	
	private Account account;

	public InterceptingWebViewClient(Context context, WebView webView,Handler handler,Server server) {
		mWebView = webView;
		this.context=context;
		mJSSubmitIntercept = new PostInterceptJavascriptInterface(handler,server);
		mWebView.addJavascriptInterface(mJSSubmitIntercept, "interception");
	}
	public void setAccount(Account account) {
		this.account = account;
	}
	@Override
	public boolean shouldOverrideUrlLoading(WebView view, String url) {
		view.loadUrl(url);
		return true;
	}

	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	@Override
	public WebResourceResponse shouldInterceptRequest(WebView view, WebResourceRequest request) {
		if (request != null && request.getUrl() != null
				&& request.getUrl().toString().contains("http://mobile.xgkst.com/#/index")) {
			String scheme = request.getUrl().getScheme().trim();
			if (scheme.equalsIgnoreCase("http") || scheme.equalsIgnoreCase("https")) {
				try {
					URL url = new URL(request.getUrl().toString());
					URLConnection connection = url.openConnection();
					HttpURLConnection conn = (HttpURLConnection) connection;

					String charset = conn.getContentEncoding() != null ? conn.getContentEncoding()
							: Charset.defaultCharset().displayName();
					String mime = conn.getContentType();
					byte[] pageContents = IOUtils.readFully(connection.getInputStream());
					pageContents = mJSSubmitIntercept.enableIntercept(context, pageContents,account).getBytes(charset);
					InputStream isContents = new ByteArrayInputStream(pageContents);
					return new WebResourceResponse(mime, charset, isContents);
				} catch (Exception e) {
					e.printStackTrace();
					return null;
				}
			}
		}
		return null;

	}
	@TargetApi(Build.VERSION_CODES.LOLLIPOP)
	@Override
	public void onPageFinished(WebView view, String url) {
		if(strJS==null){
			try {
				strJS = new String(IOUtils.readFully(context.getAssets().open("www/writeform.js")), "UTF-8");
			} catch (UnsupportedEncodingException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
		}
		super.onPageFinished(view, url);
		if (Build.VERSION.SDK_INT >= 19) {
			view.evaluateJavascript(strJS, new ValueCallback<String>() {
				@Override
				public void onReceiveValue(String s) {

				}
			});
		} else {
			view.loadUrl(strJS);
		}
	}

	@Override
	public WebResourceResponse shouldInterceptRequest(final WebView view, final String urlstr) {

		if (urlstr.contains("http://aaa.xgkst.com/#/buyer/login")) {
			URL url;
			try {
				url = new URL(urlstr);
				URLConnection rulConnection = url.openConnection();
				HttpURLConnection conn = (HttpURLConnection) rulConnection;
				conn.setRequestProperty("Accept-Charset", "utf-8");
				conn.setRequestProperty("contentType", "utf-8");
				conn.setRequestMethod("GET");

				String charset = conn.getContentEncoding() != null ? conn.getContentEncoding()
						: Charset.defaultCharset().displayName();
				String mime = conn.getContentType();
				byte[] pageContents = IOUtils.readFully(conn.getInputStream());

				if (mime.equals("text/html")) {
					pageContents = mJSSubmitIntercept.enableIntercept(context, pageContents,account).getBytes(charset);
				}
				InputStream isContents = new ByteArrayInputStream(pageContents);
				return new WebResourceResponse(mime, charset, isContents);
			} catch (Exception e) {
				e.printStackTrace();
				return null;
			}
		} else {
			return null;
		}

	}
}