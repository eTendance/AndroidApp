package com.example.etendance;

import java.util.ArrayList;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.etendance.MainActivity.storeInfo;

public class ClassAdapter extends ArrayAdapter<storeInfo>{
	ArrayList<storeInfo> names;
	public ClassAdapter(Context context, int list_item, int textEdit, ArrayList<storeInfo> variables) {
		super(context, list_item, textEdit, variables);
	    names = variables;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View listItem = super.getView(position, convertView, parent);
		TextView t = (TextView) listItem.findViewById(R.id.class_list_item_name);
		t.setTag(names.get(position).id);
		t.setText(names.get(position).name);
	    return listItem;
		}
}
