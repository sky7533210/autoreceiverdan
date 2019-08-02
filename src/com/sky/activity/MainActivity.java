package com.sky.activity;


import com.sky.service.BcyServer;
import com.sky.service.EsxServer;
import com.sky.service.MgServer;
import com.sky.service.MyService;
import com.sky.service.Server;
import com.sky.service.XbtServer;
import com.sky.service.XkqServer;
import com.sky.service.YcqServer;
import com.sky.shuadan.R;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.media.MediaPlayer;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Vibrator;
import android.text.method.ScrollingMovementMethod;
import android.util.SparseArray;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

@SuppressLint("SetJavaScriptEnabled")
public class MainActivity extends Activity implements OnItemClickListener{
	private GridAdapter gridAdapter;
	private Context context;
	private GridView gridView;
	private ReceivePopupWindow popupWindow;
	private SparseArray<Server> servers;
	private TextView tvTitle;
	private SharedPreferences sharedPreferences;
	private TextView tvErrorMsg;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.main);
		this.context = this;
		
		Thread.setDefaultUncaughtExceptionHandler(new MyUncaughtExceptionHandler(handler));
		
		this.servers=new  SparseArray<Server>();
		tvErrorMsg=(TextView)findViewById(R.id.errormsg);
		tvErrorMsg.setMovementMethod(ScrollingMovementMethod.getInstance()); 
		tvTitle=(TextView)findViewById(R.id.textView_title);
		gridView = (GridView) findViewById(R.id.gridView1);
		gridAdapter = new GridAdapter(this);
		gridView.setAdapter(gridAdapter);
		gridView.setOnItemClickListener(this);
		Intent intent = new Intent(this, MyService.class);
		bindService(intent, mConnection, Context.BIND_AUTO_CREATE);
		
		sharedPreferences=getSharedPreferences("account", Context.MODE_PRIVATE);
		
		handler.sendEmptyMessageDelayed(7, 500);
		
	}
	private ServiceConnection mConnection = new ServiceConnection() {
		@Override
		public void onServiceConnected(ComponentName className, IBinder service) {
		
		}

		@Override
		public void onServiceDisconnected(ComponentName arg0) {

		}
	};
	@Override
	protected void onDestroy() {
		unbindService();
		android.os.Process.killProcess(android.os.Process.myPid());
		super.onDestroy();
	}
	public void unbindService(){
		unbindService(mConnection);
	}
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {			
			Intent home = new Intent(Intent.ACTION_MAIN);
	        home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	        home.addCategory(Intent.CATEGORY_HOME);
	        startActivity(home);
	        return true;
		}
		return super.onKeyDown(keyCode, event);
	}
	private Handler handler = new Handler(new Handler.Callback() {
		@Override
		public boolean handleMessage(Message msg) {
			switch (msg.what) {
			//更新状态
			case 1:
				popupWindow.updateState();
				gridAdapter.changeState(msg.arg2, msg.obj.toString());
				gridAdapter.notifyDataSetChanged();
				break;
				//更新消息
			case 2:
				popupWindow.updateMessage();
				break;
				//接到单
			case 3:
				playMusic();
				break;
				//刷新赢创圈
			case 5:
				ycqServer.stop();
				ycqServer.start();
				if(!ycqServer.isShowing())
					ycqServer.hideWebView();
				break;
			case 6:				
				break;
			case 7:
				boolean isActivate= sharedPreferences.getBoolean("isActivate", false);
				if(!isActivate) {			
					new ActivatePopupWindow(context, gridView, sharedPreferences.edit()).show();
				}
				break;
			case 8://更新崩溃消息
				tvErrorMsg.append(msg.obj.toString());
				break;
			default:
				break;
			}
			return false;
		}
	});	
	public void changeTitile(String strTitle) {
		tvTitle.setText(strTitle);
	}
	private MediaPlayer mMediaPlayer;
	private Vibrator vibrator;
	private YcqServer ycqServer;
	
	//播放音乐和震动
	private void playMusic() {
		
		Uri uri = RingtoneManager.getActualDefaultRingtoneUri(context, RingtoneManager.TYPE_RINGTONE);
		mMediaPlayer = MediaPlayer.create(context, uri);
		mMediaPlayer.setLooping(true);
		mMediaPlayer.start();
		vibrator = (Vibrator) context.getSystemService(Context.VIBRATOR_SERVICE);
		vibrator.vibrate(new long[] { 200, 1200 }, 0);
	}
	//停止播放音乐和震动
	public void stopMusic() {
		if (mMediaPlayer != null && mMediaPlayer.isPlaying()) {
			mMediaPlayer.stop();
			vibrator.cancel();
			mMediaPlayer.release();
			mMediaPlayer=null;
			vibrator=null;
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View arg1, int position, long id) {		
		if (popupWindow == null) {
			popupWindow = new ReceivePopupWindow((MainActivity)context, gridView);
		}
		if(servers.get(position)==null){
			switch (position) {
			case 0:
				ycqServer= new YcqServer(context,sharedPreferences,handler,position);
				servers.put(position, ycqServer);
				break;
			case 1:
				servers.put(position, new XkqServer(sharedPreferences,handler,position));
				break;
			case 2:
				servers.put(position,new EsxServer(sharedPreferences, handler, position));
				break;
			case 3:
				//servers.put(position, null);
				break;
			case 4:
				servers.put(position, new XbtServer(sharedPreferences, handler, position));
				break;
			case 5:
				//servers.put(position,null);
				break;
			case 6:
				servers.put(position,new BcyServer(sharedPreferences, handler, position));
				break;
			case 7:
				servers.put(position, new MgServer(sharedPreferences,handler, position));
			}
		}
		popupWindow.show(servers.get(position));
	}
}
