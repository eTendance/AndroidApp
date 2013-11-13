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
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

import com.example.etendance.MainActivity.storeInfo;

public class ClassViewActivity extends Activity implements OnClickListener{
	
	private class ClassListFiller extends AsyncTask<Void, Void, ArrayList<storeInfo>> {
		
		@Override
		protected void onPostExecute(ArrayList<storeInfo> result){
			if (result==null){
				terminate(-1);
			}
			classesInfo = result;
			displayClasses();
		}		
		@Override
		protected ArrayList<storeInfo> doInBackground(Void... v) {
			ArrayList<storeInfo> classes = new ArrayList<storeInfo>();
			
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
					if (text.equals("0")) {
						return null;
					}
					
					JSONArray classesArray = new JSONArray(text);
					
					for (int i = 0; i < classesArray.length(); i++) {
						JSONObject tmp = classesArray.getJSONObject(i);
						classes.add(new storeInfo(tmp.getString("classid"), tmp.getString("name")));
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
	
	
	Button logout;
	Button checkIn;
	ListView classList;
	
	ClassAdapter classAdapt;
	ArrayList<storeInfo> classesInfo;
	int classStatusFlag = 0;
	String selectedTitle;
	String selectedID;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_classview); 
		classList = (ListView) findViewById(R.id.class_list);
		// First parameter - Context
		// Second parameter - Layout for the row
		// Third parameter - ID of the TextView to which the data is written
		// Fourth - the Array of data
		
		SharedPreferences settings = getSharedPreferences("LoginInfo",0);
		String username = settings.getString("username","");
		setTitle(username + "'s Enrolled Classes");
		
		Intent i = this.getIntent();
		Bundle b = i.getExtras();
		classesInfo = b.getParcelableArrayList("classes");
		
		logout = (Button) findViewById(R.id.log_out);
		logout.setOnClickListener(this);
		
		checkIn = (Button) findViewById(R.id.check_in);
		checkIn.setOnClickListener(this);
		
		displayClasses();
	}

	public void viewAttendance(ArrayList<storeInfo> result) {
		// Method for transitioning to ViewAttendanceActivity
		/*Bundle b = new Bundle();
		
		b.putParcelableArrayList("attendance", result);
		b.putString("selected", selectedTitle);
		b.putString("selectedID", selectedID);
		
		Intent i = new Intent(this, ViewAttendanceActivity.class);
		i.putExtras(b);
		startActivityForResult(i,0);*/
	}
	
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		if (requestCode == 0 && resultCode == RESULT_OK){      
			this.terminate(data.getIntExtra("AppStatus", -1));
		}
	}

	
	public void terminate(int flag) {
		// TODO Auto-generated method stub
		if (flag==2){
			//TOAST error message
			Toast.makeText(getApplicationContext(),
				"No longer enrolled in selected class", Toast.LENGTH_SHORT)
				.show();
			Toast.makeText(getApplicationContext(),
					"Refreshing class list", Toast.LENGTH_SHORT)
					.show();
			
			//call displayClasses() again
		}
		else{
			if (flag!=3){
				clearLoginInfo();
			}
			if (flag==1){
				Toast.makeText(getApplicationContext(),
						  "Login Credentials No Longer Valid", Toast.LENGTH_LONG)
						  .show();
			}
			if (flag==0 && !getIntent().getBooleanExtra("AlreadyRunning", true)){
				Intent intent1 = new Intent(this, MainActivity.class);
	             intent1.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
	             intent1.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);  
	            startActivity(intent1);
			}
			Intent intent= getIntent(); 
			intent.putExtra("AppStatus", flag);
			setResult(Activity.RESULT_OK, intent);
			finish();
		}
	}
	
	
	@Override
	public void onBackPressed() {
	    terminate(3);
	}

	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}
	
	
	@Override
	public void onClick(View v){
		if (v == logout){
			terminate(0);
		}
		if (v == checkIn){
			//move to check in activity
			
			//debugging message
			Toast.makeText(getApplicationContext(),
					"Moving to check in screen", Toast.LENGTH_SHORT)
					.show();
		}
	}
	public void clearLoginInfo(){
		//device no longer holds saved login info
		SharedPreferences settings = getSharedPreferences("LoginInfo", 0);
		SharedPreferences.Editor editor = settings.edit();
		
		editor.remove("username");
		editor.remove("password");
		editor.commit();
	}
	
	
	public void displayClasses(){
		classAdapt = new ClassAdapter(getBaseContext(), R.layout.class_list_item, R.id.class_list_item_name, classesInfo);
		
		// Assign adapter to ListView
		classList.setAdapter(classAdapt); 
		
		//set click listeners on each class in the list
		classList.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,int position, long id) {
				TextView tmp = (TextView) view.findViewById(R.id.class_list_item_name);
				//call viewAttendance() method
			}
		});
	}

}