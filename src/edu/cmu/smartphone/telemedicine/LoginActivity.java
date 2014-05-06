package edu.cmu.smartphone.telemedicine;

import java.util.Set;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.PushService;

import edu.cmu.smartphone.telemedicine.DBLayout.Dao_Sqlite;
import edu.cmu.smartphone.telemedicine.DBLayout.DataLoadCallback;
import edu.cmu.smartphone.telemedicine.entities.Contact;
import edu.cmu.smartphone.telemedicine.test.DatabaseTest;

public class LoginActivity extends Activity implements DataLoadCallback{

	EditText loginUsernameEditText;
	EditText loginPasswordEditText;
	Button loginButton;
	
	TextView registerTextView;	
	
	public static void login(Context context, String username) {
	    // remove from other channels
	    Set<String> setOfAllSubscriptions = PushService.getSubscriptions(context);
	    for (String s: setOfAllSubscriptions) {
	        if (!s.equals(username)) {
	            PushService.unsubscribe(context, s);
	        }
	    }
	    
	    // When users indicate they are Giants fans, we subscribe them to that channel.
	    PushService.subscribe(context, username, ContactActivity.class);
	    
        Contact.setCurrentUserID(username);
        
        // create the database.
        //Dao_Sqlite dao = new Dao_Sqlite(context, username, null, 1);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// fix bug: should set to loginView, not register view.
		setContentView(R.layout.loginview);

		loginUsernameEditText = (EditText) findViewById(R.id.loginUsername);
		loginPasswordEditText = (EditText) findViewById(R.id.loginPassword);

		loginButton = (Button) findViewById(R.id.login_loginBtn);
		
		registerTextView = (TextView)findViewById(R.id.login_link_to_register);
		registerTextView.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                startActivity(intent);
                finish();
            }
        });

		loginButton.setOnClickListener(new OnClickListener() {
		    String username = null;
		    String password = null;
		    
			@Override
			public void onClick(View v) {
			    username = loginUsernameEditText.getEditableText().toString();
	            password = loginPasswordEditText.getEditableText().toString();
	            
	            // hide the keyboard.
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(loginUsernameEditText.getWindowToken(), 0);
			    
				ParseUser.logInInBackground(username, password,
						new LogInCallback() {
							public void done(ParseUser user, ParseException e) {
								if (user != null) {
									
									// login and prepare data.
									login(LoginActivity.this, username);
									
									// this is just fixed for testing. display the contact list of yuzhang
							        Dao_Sqlite dao = new Dao_Sqlite(LoginActivity.this, username, null, 1);
							        
							        // logint from here.
							        // load the contact list of the specific user to the database.
							        dao.loadDataFromCloud(username, LoginActivity.this);
							        
							        // added by yu zhang. For testing.
							        //DatabaseTest.databaseChatRecordTest(LoginActivity.this);
							        
							        // modified by yuzhang. wait until the new activity to be load.
							        // this is moved to the callback function to keep a screen to the user.
								} else {
									// Signup failed. Look at the ParseException
									// to see what happened.
								    Toast toast = Toast.makeText(getApplicationContext(),
                                            e.getMessage(), Toast.LENGTH_LONG);
								    
								    // change the position to show the message.
								    toast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
								    toast.show();
								}
							}
						});
			}
		});

	}

    @Override
    public void dataloadCallback() {
        // Hooray! The user is logged in.
        // modified by yu zhang, display the tab view.
        Intent intent = new Intent(LoginActivity.this, TabWidget.class);
        startActivity(intent);
        
        finish();
    }

}