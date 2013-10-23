package com.example.etendance;

import android.os.Bundle;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        //initialize views
        final EditText user = (EditText)findViewById(R.id.username);
        final EditText pass = (EditText)findViewById(R.id.password);
        Button sign_in = (Button)findViewById(R.id.sign_in);
        
        //set onClickListener for button
        sign_in.setOnClickListener(new OnClickListener() {
        	
        	@Override
        	public void onClick(View v) {
        		String username = user.getText().toString();
        		String password = pass.getText().toString();
        		showLoginInfo(username, password);
        		
        		user.setText("");
        		pass.setText("");
        	}
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }
    
    
    public void showLoginInfo (String username, String password) {
    	Context context = getApplicationContext();
    	int duration = Toast.LENGTH_LONG;
    	
    	CharSequence text = "User: " + username + " Password: " + password;
    	
    	
    	Toast loginToast = Toast.makeText(context, text, duration);
    	loginToast.show();
    }
    
}
