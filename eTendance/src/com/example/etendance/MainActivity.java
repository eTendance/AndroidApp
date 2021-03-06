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

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity implements OnClickListener{
	
	//used for testing and debugging purposes
	static String LOGNAME = "main_activity";
	
	
    private class ClassListFiller extends AsyncTask<Void, Void, ArrayList<StoreInfo>> {
		
		@Override
		protected void onPostExecute(ArrayList<StoreInfo> result){
			if (result==null){
				terminate(-1);
			}
			classesInfo = result;
			loadViewClasses();
		}		
		@Override
		protected ArrayList<StoreInfo> doInBackground(Void... v) {
			ArrayList<StoreInfo> classes = new ArrayList<StoreInfo>();
			
			SharedPreferences settings = getSharedPreferences("LoginInfo",0);
			String username = settings.getString("username","");
			String password = settings.getString("password","");
			
			HttpClient client = new DefaultHttpClient();
			String getClasses = "http://etendance.kleq.net/API.php?activity=view_classes&user=" + username+ "&pass=" + password;
			HttpPost httppost = new HttpPost(getClasses); 
			try {
				HttpResponse response = client.execute(httppost);
				HttpEntity entity = response.getEntity();
				if (entity != null) {
					String text = EntityUtils.toString(entity);
					if (text.equals("0")){
						return null;
					}
					JSONArray classesArray = new JSONArray(text);
					for (int i=0; i<classesArray.length(); i++){
						JSONObject tmp = classesArray.getJSONObject(i);
						classes.add(new StoreInfo(tmp.getString("classid"), tmp.getString("name")));
					}
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
			return classes;
		}
	}



	private class SignInThread extends AsyncTask<String, Void, Boolean> {

		@Override
		protected void onPostExecute(Boolean result){
			loadClasses(result);
		}
		
		
		@Override
		protected Boolean doInBackground(String... logins) {
			Boolean successfulLogin = false;
			SharedPreferences settings = getSharedPreferences("LoginInfo", 0);
		    SharedPreferences.Editor editor = settings.edit();
		    String user = logins[0];
			String pass = logins[1];
		   
			// TODO Auto-generated method stub
			HttpClient client = new DefaultHttpClient();
			String checkLogin = "http://etendance.kleq.net/API.php?activity=login&user=" + user + "&pass=" + pass;
			HttpPost httppost = new HttpPost(checkLogin); 
			try {
				HttpResponse response = client.execute(httppost);
				HttpEntity entity = response.getEntity();
				if (null != entity) {
					String text = EntityUtils.toString(entity);
					if (Integer.parseInt(text)==1){
					successfulLogin=true;
					editor.putString("username", user);
					editor.putString("password", pass);
				    // Commit the edits!
					editor.commit();
					}
				}
			} catch (ClientProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			return successfulLogin;
		}

	}
	
	
	
	Button sign_in;
	EditText userField, passField;
	ArrayList<StoreInfo> classesInfo;
	Boolean running;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
		SharedPreferences settings = getSharedPreferences("LoginInfo", 0);
		String username = settings.getString("username", "");
	    String password = settings.getString("password", "");
	    
	    if (!(username.length()==0 || password.length()==0)){
	    	String[] information = {username, password};
	    	running=false;
	    	new SignInThread().execute(information);
	    }
	    else{
	    	running=true;
	    }
	    
	    super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		sign_in = (Button) findViewById(R.id.sign_in);
		userField = (EditText) findViewById(R.id.username);
		passField = (EditText) findViewById(R.id.password);
		
		sign_in.setOnClickListener(this);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    
	public void terminate(int flag) {
		// TODO Auto-generated method stub
		if (flag==3){
			finish();
		}
	}
	
	
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0 && resultCode == RESULT_OK){
			this.setVisible(true);
			this.terminate(data.getIntExtra("AppStatus", 0));
		}
	}
    
    
    @Override
	public void onClick(View v) {
		if(v == sign_in) {
			String tempUser = userField.getText().toString();
			String tempPass = passField.getText().toString();
			
			if((tempUser.length() < 8)||(tempUser.length() > 16)) {
				Toast.makeText(getApplicationContext(),
						"Invalid username length", Toast.LENGTH_SHORT)
						.show();
			}//invalid username length
			else if((tempPass.length() < 8)||(tempPass.length() > 16)) {
				Toast.makeText(getApplicationContext(),
						"Invalid password length", Toast.LENGTH_SHORT)
						.show();
			}//invalid password length
			else {
				String[] information = {tempUser, tempPass};
				if (information[0].length()==0 || information[0]==null){
					return;
				}
				if(information[1].length()==0 || information[1]==null){
					return;
				}
				else if (!(information[0].length()==0 || information[0]==null)){
					new SignInThread().execute(information);
					return;
				}
			}//no errors in input
		}//view v = sign in
		return;
		
	}
    
    
    public void loadClasses(Boolean check){
		if (check){
			new ClassListFiller().execute();
		}
		else{
			Toast.makeText(getApplicationContext(),
					"Invalid Login", Toast.LENGTH_SHORT)
					.show();
		}
	}
    
    
	public void loadViewClasses(){
		Bundle b = new Bundle();
		b.putParcelableArrayList("classes", classesInfo);
		Intent i = new Intent(getApplicationContext(), ClassViewActivity.class);
		i.putExtras(b);
		i.putExtra("AlreadyRunning", running);
		startActivityForResult(i,0);
	}
	
	
}
