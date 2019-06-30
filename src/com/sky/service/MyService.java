package com.sky.service;

import com.sky.activity.MainActivity;
import com.sky.shuadan.R;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;

public class MyService extends Service {

	// private static final String TAG = "ForegroundService";
	private static final int NOTIFICATIONID = 110;


	//private LocalBinder mBinder = new LocalBinder();
	private Notification notification;
	private Notification.Builder builder;

	/*public class LocalBinder extends Binder {

		public MyService getService() {
			return MyService.this;
		}
	}*/

	@Override
	public IBinder onBind(Intent intent) {
		// Log.i(TAG, "onBind: ");
		return null;
	}

	@Override
	public void onCreate() {
		super.onCreate();
		// Log.i(TAG, "onCreate:");
		builder = new Notification.Builder(this);
		Intent intent = new Intent(this, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
		PendingIntent pi = PendingIntent.getActivity(this, 0, intent, 
				PendingIntent.FLAG_UPDATE_CURRENT);
		notification = builder.setContentText("正在后台持续运行中")
				.setContentTitle("请不要关闭此通知")
				.setSmallIcon(R.drawable.ic_launcher)
				.setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
				.setContentIntent(pi)
				.build();
		startForeground(NOTIFICATIONID, notification);
	}

	@Override
	public int onStartCommand(Intent intent, int flags, int startId) {
		// Log.i(TAG, "onStartCommand:");
		return super.onStartCommand(intent, flags, startId);
	}

	@Override
	public void onDestroy() {
		// Log.i(TAG, "onDestroy:");
		stopForeground(true);
		super.onDestroy();
	}

	@Override
	public void onRebind(Intent intent) {
		// Log.i(TAG, "onRebind: ");
		super.onRebind(intent);
	}

	@Override
	public boolean onUnbind(Intent intent) {
		// Log.i(TAG, "onUnbind: ");
		stopForeground(true);
		return super.onUnbind(intent);
	}
}