package edu.cmu.smartphone.telemedicine;

import android.app.Activity;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.RequestPasswordResetCallback;

public class SettingActivity extends Activity {

	EditText newpassword_editText = null;
	Button newpassword_button = null;
	Button logout_button = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.settingview);

		newpassword_editText = (EditText) findViewById(R.id.newpassword_editText);
		logout_button = (Button) findViewById(R.id.logout_button);
		newpassword_button = (Button) findViewById(R.id.newpassword_button);
		logout_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				ParseUser.logOut();
				android.os.Process.killProcess(android.os.Process.myPid());
			}
		});

		newpassword_button.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {
				String email = newpassword_editText.getText().toString();
				ParseUser.requestPasswordResetInBackground(
						email,
						new RequestPasswordResetCallback() {
							public void done(ParseException e) {
								if (e == null) {
									// An email was successfully sent with reset
									// instructions.
									Toast toast = Toast.makeText(getApplicationContext(), "Check email to change password!", Toast.LENGTH_LONG);
								    // change the position to show the message.
								    toast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
								    toast.show();
								} else {
									// Something went wrong. Look at the
									// ParseException to see what's up.
									Toast toast = Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG);
								    // change the position to show the message.
								    toast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
								    toast.show();
								}
							}
						});
			}
		});
	}

}
