package com.example.etendance;

import java.io.IOException;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;

public class AttendanceViewActivity extends Activity implements OnClickListener{

	private class AbsenceListFiller extends AsyncTask<String, Void, ArrayList<String>> {
		
		@Override
		protected void onPostExecute(ArrayList<String> result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			absenceInfo = result;
			displayAbsences();
		}
		
		@Override
		protected ArrayList<String> doInBackground(String... cid) {
			// TODO Auto-generated method stub
			ArrayList<String> absences = new ArrayList<String>();
			
			SharedPreferences settings = getSharedPreferences("LoginInfo",0);
			String username = settings.getString("username","");
			String password = settings.getString("password","");
			
			String selected_class = cid[0];
			Integer selected_class_num = Integer.parseInt(selected_class);
			
			HttpClient client = new DefaultHttpClient();
			String getAbsences = "http://etendance.kleq.net/API.php?activity=get_absences_for_student&user=" + username+ "&pass=" + password + "&classid=" + selected_class_num;
			HttpPost httppost = new HttpPost(getAbsences); 
			try {
				HttpResponse response = client.execute(httppost);
				HttpEntity entity = response.getEntity();
				
				if (entity != null) {
					String text = EntityUtils.toString(entity);
					if(text.equals("")) {
						return null;
					} else {
					
						JSONArray absencesArray = new JSONArray(text);
						
						for (int i = 0; i < absencesArray.length(); i++) {
							JSONObject tmp = absencesArray.getJSONObject(i);
							absences.add(tmp.getString("forclassday"));
						}
					}
					
					return absences;
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}
	
	Button logout;
	Button back;
	
	TextView classname;
	ListView absenceList;
	ArrayList<String> absenceInfo;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_attendanceview);
		
		logout = (Button) findViewById(R.id.log_out);
		logout.setOnClickListener(this);
		
		back = (Button) findViewById(R.id.back);
		back.setOnClickListener(this);
		
		classname = (TextView) findViewById(R.id.class_attendance);
		absenceList = (ListView) findViewById(R.id.absence_list);
		
		Intent i = this.getIntent();
		Bundle b = i.getExtras();
		
		String name = "Absences for\n" + b.getString("classname");
		
		classname.setText(name);
		classname.setTag(b.getString("classid"));
		
		new AbsenceListFiller().execute(b.getString("classid"));
	}
	
	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		if(v == logout) {
			clearLoginInfo();
		}
		else {
			Intent i = new Intent(this, MainActivity.class);
			startActivity(i);
		}
	}
	
	
	public void clearLoginInfo(){
		//device no longer holds saved login info
		SharedPreferences settings = getSharedPreferences("LoginInfo", 0);
		SharedPreferences.Editor editor = settings.edit();
		
		editor.remove("username");
		editor.remove("password");
		editor.commit();
		
		Intent returnToMainIntent = new Intent(this, MainActivity.class);
        returnToMainIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        returnToMainIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
        startActivity(returnToMainIntent);
	}
	


	public void displayAbsences(){
		AbsenceAdapter absenceAdapt = new AbsenceAdapter(getBaseContext(), android.R.layout.simple_list_item_1, android.R.id.text1, absenceInfo);
		
		// Assign adapter to ListView
		absenceList.setAdapter(absenceAdapt);
	}

}
