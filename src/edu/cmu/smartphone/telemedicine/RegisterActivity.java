package edu.cmu.smartphone.telemedicine;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseUser;
import com.parse.SignUpCallback;

public class RegisterActivity extends Activity {

	EditText usernameEditText;
	EditText fullnameEditText;
	EditText emailEditText;
	EditText passwordEditText;
	Button registerButton;
	TextView loginTextView;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.registerview);
		
		usernameEditText = (EditText)findViewById(R.id.reg_username);
		fullnameEditText = (EditText)findViewById(R.id.reg_fullname);
		emailEditText = (EditText)findViewById(R.id.reg_email);
		passwordEditText = (EditText)findViewById(R.id.reg_password);
		
		registerButton = (Button)findViewById(R.id.reg_btnRegister);
		loginTextView = (TextView)findViewById(R.id.reg_link_to_login);
		
		registerButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				final String username = usernameEditText.getText().toString();
				String fullname = fullnameEditText.getText().toString();
				String email = emailEditText.getText().toString();
				String password = passwordEditText.getText().toString();
				
				ParseUser user = new ParseUser();
				user.setUsername(username);
				user.setPassword(password);
				user.setEmail(email);
				
				// other fields can be set just like with ParseObject
				user.put("fullname", fullname);
				 
				user.signUpInBackground(new SignUpCallback() {
				  public void done(ParseException e) {
				    if (e == null) {
				        // Hooray! Let them use the application now.
				    	Intent intent = new Intent(RegisterActivity.this,
				    	        ContactActivity.class);
						startActivity(intent);
						
						// This is to create a new table and save himself/himself.
						ParseObject newFriend = new ParseObject(username);
						newFriend.put("friend_username", username);		// A user is his own friend in the beginning
						newFriend.saveInBackground();
						
						// log into and save data.
						LoginActivity.login(RegisterActivity.this, username);
				    } else {
				        
				        // Sign up didn't succeed. Look at the ParseException
				        // to figure out what went wrong
				    	Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
				    }
				  }
				});
				
				
			}
		});

		loginTextView.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				Intent intent = new Intent(RegisterActivity.this,
		    			LoginActivity.class);
				startActivity(intent);
				finish();
			}
		});
		
		
	}

}