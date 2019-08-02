package com.sky.activity;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class MyUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler{

	
	private Handler handler;
	public MyUncaughtExceptionHandler(Handler handler) {
		this.handler=handler;
	}	
	@Override
	public void uncaughtException(final Thread thread, final Throwable ex) {
		new Thread(new Runnable() {			
			@Override
			public void run() {
				StringBuilder sb=new StringBuilder();
				sb.append("Threadname:"+thread.getName());
				sb.append("\n");
				sb.append("message:"+ex.getMessage());
				sb.append("\n");
				ByteArrayOutputStream bos=new ByteArrayOutputStream();
				ex.printStackTrace(new PrintStream(bos));
				sb.append("stacktrace:"+new String(bos.toByteArray()));
				Message msg=handler.obtainMessage(8, sb);
				msg.sendToTarget();
			}
		}).start();
		
	}

}
