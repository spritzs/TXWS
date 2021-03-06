package cn.txws.board.connect;

import android.content.Context;
import android.os.Build;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

import cn.robotpen.model.entity.DeviceEntity;
import cn.txws.board.R;

public class PenAdapter extends BaseAdapter {
	private LayoutInflater inflater;
	private ArrayList<DeviceEntity> mPenDevices;

	HashMap<String, DeviceEntity> dataCache = new HashMap<>();
	HashMap<String, CheckRunnable> checkMap = new HashMap<>();
	Handler checkHandler;

	public PenAdapter(Context context) {
		inflater = LayoutInflater.from(context);
		mPenDevices = new ArrayList<DeviceEntity>();
		checkHandler = new Handler();
	}

	@Override
	public int getCount() {
		return mPenDevices.size();
	}

	@Override
	public DeviceEntity getItem(int i) {
		return mPenDevices.get(i);
	}

	@Override
	public long getItemId(int i) {
		return i;
	}
	
	public void addItem(DeviceEntity item){
		for (int i=0;i<mPenDevices.size();i++) {
			if (mPenDevices.get(i).getAddress().equals(item.getAddress()))
				return;
		}
		mPenDevices.add(item);
		notifyDataSetChanged();
	}
	
	/**
	 * 清除集合内容
	 */
	public void clearItems(){
		mPenDevices.clear();
	}
	
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		PageItem pageItem;
		if(convertView != null){
			pageItem = (PageItem)convertView.getTag();
		}else{
			convertView = inflater.inflate(R.layout.pen_adapter_item,null);
			pageItem = new PageItem(convertView);
			convertView.setTag(pageItem);
		}

		DeviceEntity deviceObject = getItem(position);
		if("".equals(deviceObject.getName())||deviceObject.getName()==null){
			pageItem.deviceName.setText(R.string.no_name_device);
		}else{
			pageItem.deviceName.setText(deviceObject.getName());
		}
		pageItem.deviceAddress.setText(deviceObject.getAddress());
		
		
		return convertView;
	}
	
	private class PageItem{
		public TextView deviceName;
		public TextView deviceAddress;
		public PageItem(View view){
			deviceName = (TextView) view.findViewById(R.id.deviceName);
			deviceAddress = (TextView) view.findViewById(R.id.deviceAddress);
		}
	}

		class CheckRunnable implements Runnable {
			String macAddr;

			public CheckRunnable(String macAddr) {
				this.macAddr = macAddr;
			}

			@Override
			public void run() {
				if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
					checkMap.remove(macAddr);
					DeviceEntity d = dataCache.get(macAddr);
					int index = mPenDevices.indexOf(d);
					mPenDevices.remove(index);
					dataCache.remove(macAddr);
					notifyDataSetChanged();
				}
			}
		}

	/**
	 * 释放资源
	 */
	public void release() {
		checkHandler.removeCallbacksAndMessages(null);
		checkMap.clear();
	}

}
