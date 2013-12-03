package com.example.etendance;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class AbsenceAdapter extends ArrayAdapter<String> {
	ArrayList<String> dates;

	public AbsenceAdapter(Context context, int resource, int textViewResourceId, ArrayList<String> objects) {
		super(context, resource, textViewResourceId, objects);
		// TODO Auto-generated constructor stub
		
		dates = objects;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		View listItem = super.getView(position, convertView, parent);
		
		TextView t = (TextView) listItem.findViewById(android.R.id.text1);
		t.setText(dates.get(position));
		
		return listItem;
	}

}
