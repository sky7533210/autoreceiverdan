package com.simple.spiderman;

import com.sky.shuadan.R;

import android.app.Activity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.widget.TextView;

public class CrashActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_crash);		
		CrashModel model = getIntent().getParcelableExtra("crash");
		TextView tv=(TextView) findViewById(R.id.text);
		tv.setMovementMethod(ScrollingMovementMethod.getInstance()); 
		tv.setText(model.toString());
		Log.i("crash", model.toString());
	}
	@Override
	protected void onDestroy() {
		android.os.Process.killProcess(android.os.Process.myPid());
		super.onDestroy();
	}
}
