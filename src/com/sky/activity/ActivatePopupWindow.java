package com.sky.activity;

import com.sky.shuadan.R;
import com.sky.util.Activate;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.EditText;
import android.widget.PopupWindow;

public class ActivatePopupWindow  implements OnClickListener{
	private PopupWindow popupWindow;
	private View parent;
	private EditText ETPhone;
	private EditText ETYcqId;
	private EditText ETXkqId;
	private EditText ETEsxId;
	private EditText ETPtyId;
	private EditText ETXbtId;
	private EditText ETFhId;
	private EditText ETBcyqId;
	private EditText ETMgId;
	private Button btnActivate;
	private Context context;
	private SharedPreferences.Editor editor;
	private Activate activate;
	
	public ActivatePopupWindow(Context context,View parent,SharedPreferences.Editor editor) {
		this.parent=parent;
		this.context=context;
		this.editor=editor;
		View contentView = LayoutInflater.from(context).inflate(R.layout.activate_view, null);
		popupWindow = new PopupWindow(contentView, LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT, true);	
		ETPhone=(EditText)contentView.findViewById(R.id.phone);	
		ETYcqId=(EditText)contentView.findViewById(R.id.ycqid);
		ETXkqId=(EditText)contentView.findViewById(R.id.xkqid);
		ETEsxId=(EditText)contentView.findViewById(R.id.esxid);
		ETPtyId=(EditText)contentView.findViewById(R.id.ptyid);
		ETXbtId=(EditText)contentView.findViewById(R.id.xbtid);
		ETFhId=(EditText)contentView.findViewById(R.id.fhid);
		ETBcyqId=(EditText)contentView.findViewById(R.id.bcyid);
		ETMgId= (EditText)contentView.findViewById(R.id.mgid);
		
		btnActivate=(Button)contentView.findViewById(R.id.btnok);
		btnActivate.setOnClickListener(this);
	}
	public void show(){		
		popupWindow.showAtLocation(parent, 1, 0, 0);
		
	}
	@Override
	public void onClick(View arg0) {
		switch (arg0.getId()) {
		case R.id.btnok:
			if(activate==null)
				activate=new Activate(context, editor);
			activate.activate(ETPhone.getText().toString().trim(), ETYcqId.getText().toString().trim(), ETXkqId.getText().toString().trim(),
					ETEsxId.getText().toString().trim(), ETPtyId.getText().toString().trim(), ETXbtId.getText().toString().trim(),
					ETFhId.getText().toString().trim(),ETBcyqId.getText().toString().trim(),ETMgId.getText().toString());			
			break;
		}
	}
}
