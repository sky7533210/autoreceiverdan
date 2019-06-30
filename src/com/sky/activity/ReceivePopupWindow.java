package com.sky.activity;

import com.sky.bean.Account;
import com.sky.service.Server;
import com.sky.service.YcqServer;
import com.sky.shuadan.R;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.PopupWindow.OnDismissListener;

public class ReceivePopupWindow implements OnDismissListener ,OnClickListener{
	private PopupWindow popupWindow;
	private View parent;
	private EditText ETPhone;
	private EditText ETPwd;
	private TextView tvState;
	private EditText etMessage;
	private Server server;
	private Button btnstart;
	private MainActivity context;
	private Account account;
	private View contentView;
	public ReceivePopupWindow(MainActivity context,View parent) {
		this.parent=parent;
		this.context=context;
		contentView = LayoutInflater.from(context).inflate(R.layout.receive_view, null);
		popupWindow = new PopupWindow(contentView, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT, true);
		popupWindow.setTouchable(true);
		popupWindow.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
		popupWindow.setOnDismissListener(this);
		ETPhone=(EditText)contentView.findViewById(R.id.userid);
		ETPhone.setEnabled(false);
		ETPwd=(EditText)contentView.findViewById(R.id.password);
		tvState=(TextView)contentView.findViewById(R.id.state);
		etMessage=(EditText)contentView.findViewById(R.id.message);
		etMessage.setEnabled(false);
		btnstart=(Button)contentView.findViewById(R.id.btnStart);
		btnstart.setOnClickListener(this);
	}
	public void show(Server server){
		this.server=server;
		account=server.getAccount();
		server.setShowing(true);
		context.changeTitile(server.getPlatformName());
		ETPhone.setText(account.getPhone());
		ETPwd.setText(account.getPassword());
		etMessage.setText(server.getMessage());
		
		updateState();
		
		if(server.getPosition()==0) {
			((YcqServer)server).showWebView(contentView);
		}
		
		popupWindow.showAtLocation(parent, 1, 0, 0);
	}
	public void updateMessage() {
		etMessage.setText(server.getMessage());
	}
	public void updateState() {
		btnstart.setText("开始接单");
		if(server.isReceiving()) {
			btnstart.setText("停止接单");
		}
		tvState.setText(server.getState());
	}
	@Override
	public void onDismiss() {
		server.setShowing(false);
		context.changeTitile("接单助手");
		account.setPassword(ETPwd.getText().toString());
		
		if(server.getPosition()==0) {
			((YcqServer)server).hideWebView();
		}
		
	}
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btnStart:
			//判断是否正在运行
			 if(server.isReceiving()) {
				 server.stop();
				 btnstart.setText("开始接单");			
				 context.stopMusic();
			 }else {
				 account.setPassword(ETPwd.getText().toString());
				 server.start();
				 btnstart.setText("停止接单");
				 etMessage.setText("正在开始接单");
			 }
			break;
		default:
			break;
		}
	}
}
