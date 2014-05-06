package edu.cmu.smartphone.telemedicine;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

import edu.cmu.smartphone.telemedicine.test.DatabaseTest;

// Loading Splash View
public class MainActivity extends Activity {
	Button welcomeButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splashview);
		
		welcomeButton = (Button)findViewById(R.id.welcomeButton);
		
		welcomeButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// no use .
				//ParseUser currentUser = ParseUser.getCurrentUser();
				
                Intent intent = new Intent(MainActivity.this,
                        LoginActivity.class);
                startActivity(intent);
				
				// deleted by yu zhang. this will make Alert window not pop up.
                //finish();
			}
		});
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		android.os.Process.killProcess(android.os.Process.myPid());
	}

}
