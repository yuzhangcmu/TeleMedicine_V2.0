package edu.cmu.smartphone.telemedicine;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;

public class ChatActivity extends Activity {

	EditText chatInputEditText;
	Button chatSendButton;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.chatview);
		
		chatInputEditText = (EditText)findViewById(R.id.chatInputEditText);
		chatSendButton = (Button)findViewById(R.id.chatSendButton);
		
		chatSendButton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				
			}
		});
		
		
		
	}

}
