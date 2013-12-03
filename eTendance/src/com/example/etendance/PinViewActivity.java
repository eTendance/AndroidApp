package com.example.etendance;

import java.io.IOException;

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
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

public class PinViewActivity extends Activity implements OnClickListener{
	
	
	private class RetrievePin extends AsyncTask<String, Void, String> {
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			classPin.setText(result);
			classPin.setTag("pin");
		}
		
		@Override
		protected String doInBackground(String... cid) {
			// TODO Auto-generated method stub
			String newPin = "";
			
			SharedPreferences settings = getSharedPreferences("LoginInfo",0);
			String username = settings.getString("username","");
			String password = settings.getString("password","");
			
			String selected_class = cid[0];
			Integer selected_class_num = Integer.parseInt(selected_class);
			
			HttpClient client = new DefaultHttpClient();
			String getPin = "http://etendance.kleq.net/API.php?activity=get_checkin_PIN&user=" + username+ "&pass=" + password + "&classid=" + selected_class_num;
			HttpPost httppost = new HttpPost(getPin); 
			
			try {
				HttpResponse response = client.execute(httppost);
				HttpEntity entity = response.getEntity();
				
				if (entity != null) {
					String text = EntityUtils.toString(entity);
					if (text.equals("-1")) {
						newPin = "Either no pin has been generated or it is expired.";
					} else {
					
						JSONArray pinListJSON = new JSONArray(text);
						JSONObject newPinJSON = pinListJSON.getJSONObject(0);
						newPin = newPinJSON.getString("code");
					}
					
					return newPin;
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
	TextView className;
	TextView classPin;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_pinview);
		
		logout = (Button) findViewById(R.id.log_out);
		logout.setOnClickListener(this);
		
		back = (Button) findViewById(R.id.back);
		back.setOnClickListener(this);
		
		className = (TextView) findViewById(R.id.selected_class_name);
		classPin = (TextView) findViewById(R.id.selected_class_pin);
		
		Intent i = this.getIntent();
		Bundle b = i.getExtras();
		
		className.setText(b.getString("classname"));
		className.setTag(b.getString("classid"));
		
		String tmpArr[] = new String[1];
		tmpArr[0] = b.getString("classid");
		new RetrievePin().execute(tmpArr);
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

}
