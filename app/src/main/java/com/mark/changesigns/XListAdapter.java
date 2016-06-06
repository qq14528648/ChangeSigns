package com.mark.changesigns;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class XListAdapter extends BaseAdapter {
	private List<String> accounts = new ArrayList<String>();

	  
	private LayoutInflater mLayoutInflater;

	public XListAdapter(Context context) {
		super();

		mLayoutInflater = (LayoutInflater) context
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	}

	public List<String> getItems( ) {
		// TODO Auto-generated method stub
		return accounts ;
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return accounts.size();
	}

	public void removeAll() {
		// TODO Auto-generated method stub
		accounts.clear();
		notifyDataSetChanged();
	}



	public void add(String name) {
		// TODO Auto-generated method stub
		accounts.add(name);
		notifyDataSetChanged();

	}

	@Override
	public String getItem(int position) {
		// TODO Auto-generated method stub
		return accounts.get(position);
	}

	public boolean contain(String a) {
		return accounts.contains(a);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder holder;
		if (convertView == null) {
			holder = new ViewHolder();
			convertView = mLayoutInflater.inflate(R.layout.list_item, parent,
					false);
			holder.tv = (TextView) convertView.findViewById(R.id.name);
			holder.num = (TextView) convertView.findViewById(R.id.num);
			convertView.setTag(holder);
		} else {
			holder = (ViewHolder) convertView.getTag();
		}

		holder.tv.setText(accounts.get(position));
		holder.num.setText((1+position)+".");
		return convertView;
	}

	public void remove(String account) {
		// TODO Auto-generated method stub
		accounts.remove(account);
		notifyDataSetChanged();
	}
	public void remove(int p) {
		// TODO Auto-generated method stub
		accounts.remove(p);
		notifyDataSetChanged();
	}
	private static class ViewHolder {
		TextView tv;
		TextView num;
	}

}
