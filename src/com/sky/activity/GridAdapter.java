package com.sky.activity;

import com.sky.shuadan.R;

import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class GridAdapter extends BaseAdapter {

	private MainActivity context;

	private Resources resources;
	
	private Drawable drawableReceiving;
	
	private Drawable drawableReceived;
	private Integer[] images = {
			// 九宫格图片的设置
			R.drawable.ycqlogo, R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher,
			R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher, R.drawable.ic_launcher };

	private String[] platformName = { "赢创圈", "信客圈", "二师兄", "菩提园", "小白兔", "芳华", "百草园","木瓜"};
	private String[] state = { "空闲中", "空闲中", "空闲中", "空闲中", "空闲中", "空闲中", "空闲中","空闲中" };

	
	public GridAdapter(MainActivity context) {
		this.context = context;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return images.length;
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	public void changeState(int position,String message){
		this.state[position]=message;
	}
	@Override
	public View getView(int position, View view, ViewGroup viewgroup) {
		// TODO Auto-generated method stub
		ImgTextWrapper wrapper;
		if (view == null) {
			wrapper = new ImgTextWrapper();
			LayoutInflater inflater = LayoutInflater.from(context);
			view = inflater.inflate(R.layout.item, null);
			view.setTag(wrapper);
			//view.setPadding(15, 15, 15, 15); // 每格的间距
		} else {
			wrapper = (ImgTextWrapper) view.getTag();
		}
		wrapper.imageView = (ImageView) view.findViewById(R.id.imageView1);
		wrapper.imageView.setBackgroundResource(images[position]);
		wrapper.textView1 = (TextView) view.findViewById(R.id.textView1);
		wrapper.textView1.setText(platformName[position]);
		wrapper.textView2 = (TextView) view.findViewById(R.id.textView2);
		wrapper.textView2.setText(state[position]);
		if(state[position].equals("接单中")) {
			if(resources==null) {
				resources=context.getBaseContext().getResources(); 
				drawableReceiving=resources.getDrawable(R.drawable.shape_tv_receiving);
			}
			wrapper.textView2.setBackground(drawableReceiving);
		}else if(state[position].equals("接到单")) {
			if(drawableReceived==null) {
				if(resources==null)
					resources=context.getBaseContext().getResources(); 
				drawableReceived=resources.getDrawable(R.drawable.shape_tv_received);
			}
			wrapper.textView2.setBackground(drawableReceived);
		}
		return view;
	}

}

class ImgTextWrapper {
	ImageView imageView;
	TextView textView1;
	TextView textView2;
}
