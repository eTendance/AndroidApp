package com.example.etendance;

import java.util.ArrayList;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;


public class ClassAdapter extends ArrayAdapter<StoreInfo>{
	ArrayList<StoreInfo> names;
	
	public ClassAdapter(Context context, int list_item, int textEdit, ArrayList<StoreInfo> variables) {
		super(context, list_item, textEdit, variables);
	    names = variables;
	}
	@Override
	public View getView(final int position, View convertView, ViewGroup parent) {
		View listItem = super.getView(position, convertView, parent);
		
		final TextView t = (TextView) listItem.findViewById(R.id.class_list_item_name);
		t.setTag(names.get(position).id);
		t.setText(names.get(position).name);
		
		t.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("classid", names.get(position).id);
				bundle.putString("classname", names.get(position).name);				
				
				Intent intent = new Intent(getContext(), AttendanceViewActivity.class);
				intent.putExtras(bundle);
				v.getContext().startActivity(intent);
			}
		});
		
		final ImageButton b = (ImageButton) listItem.findViewById(R.id.checkmark_button);
		b.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Bundle bundle = new Bundle();
				bundle.putString("classname", names.get(position).name);
				bundle.putString("classid", names.get(position).id);
				
				Intent intent = new Intent(getContext(), PinViewActivity.class);
				intent.putExtras(bundle);
				v.getContext().startActivity(intent);
				
			}
		});
		
	    return listItem;
	}
}
