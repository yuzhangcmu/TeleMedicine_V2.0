/***
 * Package Name  :com.example.ovxexample
 * Version Name  :1.2.3
 * Date          :20140319 
 * Description   :Activity used to show cast the OVX Features.
  
 *****/

package edu.cmu.smartphone.telemedicine;

import java.sql.Date;
import java.util.ArrayList;
import java.util.UUID;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.res.Configuration;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings.Secure;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.openclove.ovx.OVXCallListener;
import com.openclove.ovx.OVXException;
import com.openclove.ovx.OVXView;
import com.parse.ParseUser;

import edu.cmu.smartphone.telemedicine.DBLayout.Dao_Sqlite;
import edu.cmu.smartphone.telemedicine.entities.ChatRecord;
import edu.cmu.smartphone.telemedicine.entities.Contact;
import edu.cmu.smartphone.telemedicine.ws.remote.Notification;

public class VideoActivity extends Activity {

	private static final String tag = "VideoActivity";
	private OVXView ovxView;
//	protected RelativeLayout media_control;
	private Dialog dialog;
	private EditText ovx_text;
	protected TextView chat_box;
	private String groupid;
	private TextView text_gid;
//	private OVXChat currentActivity;
//	private String gid;
	
	String currentUserId = Contact.getCurrentUserID();
	Dao_Sqlite dao = null;
	
	String currentUserName = null;
	String caller_username = null;
	String callee_username = null;
	String callee_fullname = null;
	String callee_email = null;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.videoview);
		
		currentUserId = Contact.getCurrentUserID();
		dao = new Dao_Sqlite(VideoActivity.this, currentUserId, null, 1);
		
		currentUserName = ParseUser.getCurrentUser().getUsername();
		caller_username = getIntent().getStringExtra("caller_username");
		callee_username = getIntent().getStringExtra("callee_username");
		callee_fullname = getIntent().getStringExtra("fullname");
		callee_email = getIntent().getStringExtra("email");
		
		Log.d(tag, "caller_username:" + caller_username);
		Log.d(tag, "callee_username:" + callee_username);
		
		// Comments provided for ovx sdk code
		Log.d("INDUS", "onCreate");

//		currentActivity = this;

		// Access the Shared Instance of the OVXView
		ovxView = OVXView.getOVXContext(this);

		try {
			// To get UID(User ID) for the device.
			String ovxuserId = Secure.getString(this.getContentResolver(), Secure.ANDROID_ID);

			/*We set the OVX User ID as the device id since it is unique. You can use any logic to set 
			*the userId as long as it distinguishes your device from the other devices involved in the conference.    
			*/
			ovxView.setOvxUserId(ovxuserId);
			ovxView.setOvxUserName(ParseUser.getCurrentUser().getUsername());

			/*Here we set the OVX Group Id as a unique identifier. Users who initiate call with 
			 * the same group id will end up in a video conference. You can use own logic to 
			 * share this group id, for example via links as invite to another user to join conference.    
			 */
//			ovxView.setOvxGroupId(UUID.randomUUID().toString().replaceAll("-", ""));
			
			if(currentUserName.equals(callee_username)) {
				ovxView.setOvxGroupId(caller_username + "-" + callee_username);
			} else {
				ovxView.setOvxGroupId(currentUserName + "-" + callee_username);
			}
			
			

			/* This refers to the Theme of video frames, background, etc. of
			* the video room.
			* Contact support@openclove.com for more details on the Themes
			* available.
			*/
			ovxView.setOvxMood("1");

			/* Here you can set whether to show the OVX menu when the user taps
			* the video view. OVX menu contains call control features,like audio mute,video mute etc;
			* it also allows you to minimize or maximize the video view.  
			*/
			ovxView.setShowOVXMenuOnTap(true);
			
			/*Remote gesture api is true by default, setting it to false 
			 * will disable the pinch/zoom and drag event of the video view 
			 * and also prevents you from maximizing the video on double tap, 
			 * hence setting it to false will lock the video view in a fixed 
			 * position on the screen.  
			 */
			ovxView.enableRemoteGesture(true);
			
			/* You can set the x and y position of the video view which is relative to 
			*  the top,left position of the screen.
			*/ 
			ovxView.setRemoteViewX(100);
			ovxView.setRemoteViewY(100);
			
//			ovxView.setRemoteViewWidth(500);
//			ovxView.setRemoteViewWidth(300);

			/*We use a TextView for displaying the current GroupId set through the sdk.
			 */
			text_gid = (TextView) findViewById(R.id.app);
			text_gid.setText("Current Group ID : " + ovxView.getOvxGroupId());

			// Bind the the call event to the click of start_call button  
			Button ovx_call_button = (Button) findViewById(R.id.start_call);
			ovx_call_button.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (!ovxView.isCallOn()) //Checks if the call is on 
						try {
							ovxView.call(); // Initiates call and starts a session with the specified OVX group id and other parameters set earlier.
							// Send push notification to the other user to receive the call
							Log.d(tag, "caller_username:" + caller_username);
							Log.d(tag, "callee_username:" + callee_username);
							if(currentUserName!=null && !currentUserName.equals(callee_username)) {
								Notification notify = new Notification(VideoActivity.this);
								notify.sendInComingCallPush(currentUserName, callee_username, "msg1");
							}
						} catch (OVXException e) {
							e.printStackTrace();
						}
					else { // If call is already started
						CharSequence[] ch = { "Call is already on" };
						showDialog("Warning", ch);
					}
				}
			});

			// Bind the end call event to the click of end_call button
			Button ovx_end_call = (Button) findViewById(R.id.end_call);
			ovx_end_call.setOnClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					if (ovxView.isCallOn()) { // 
						ovxView.exitCall(); // ends the existing call and removes the user from the live conference.
					}
				}
			});


			// When performing action on EditText..
			ovx_text = (EditText) findViewById(R.id.chat_text);
			ovx_text.setTextColor(Color.BLACK);
			if(!ovxView.isCallOn()) {
				ovx_text.setEnabled(false);
				ovx_text.setHint("Please start call before text!");
			} else {
				ovx_text.setEnabled(true);
				ovx_text.setHint("Enter Message:");
			}
			
			ovx_text.setOnEditorActionListener(new OnEditorActionListener() {
				@Override
				public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
					if (!ovx_text.getText().toString().trim().equals("") && ovxView.isCallOn()) {
						/*
						 * We can use this to send data to other parties in the conference. 
						 * The first parameter is the message type which in this case is a link, 
						 * the second parameter contains the data.
						 * It is up to the developer to 
						 * display the data any way he wants.   
						 */
						String message = ovx_text.getText().toString();
						ovxView.sendData("chat", message);
						chat_box.append("\n" + ovxView.getOvxUserName() + " : " + message);
						ovx_text.setText("");
						
						
						focusOnText();
					}
					return true;
				}
			});

			
			/* This method contains implementation of 
			 * OVX call listeners. Look into the method
			 * definition for further description
			 * 
			 */
			callListener();

			
			/*Once call is started the scroll event will not take place when user tries to scroll through the 
			*text box since it has its own custom scroll. Users will have to scroll through the sides(regions other than the chat text box)
			*/
			ScrollView scroll_layout = (ScrollView) findViewById(R.id.scroll_layout);

			scroll_layout.setOnTouchListener(new View.OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					// Log.d("INDUS", "PARENT TOUCH");
					findViewById(R.id.chat_text_box).getParent().requestDisallowInterceptTouchEvent(false);
					return false;
				}
			});

			
			chat_box.setOnTouchListener(new View.OnTouchListener() {
				public boolean onTouch(View v, MotionEvent event) {
					// Log.v("INDUS", "CHILD TOUCH");
					// Disallow the touch request for parent scroll on touch of child view
					v.getParent().requestDisallowInterceptTouchEvent(true);
					return false;
				}
			});
		} catch (OVXException e) {
			e.printStackTrace();
		}

	}


	@Override
	public void onConfigurationChanged(Configuration newConfig) {
		// don't reload the current page when the orientation is changed
		Log.d("INDUS", "onConfigurationChanged() Called");
		super.onConfigurationChanged(newConfig);
		try {
			ovxView.setRemoteViewX(50);
			ovxView.setRemoteViewY(50);
		} catch (OVXException e) {
			e.printStackTrace();
		}
		
		/*
		 * Should be called to update the dimensions and position
		 * of the video view that had been changed after the call was started or 
		 * to resume the video stream if it had been paused while launching another activity.  
		 */
		ovxView.updateVideoOrientation();
	}

	@Override
	public void onDestroy() {
		super.onDestroy();
		
		//exit the call before destroying the activity
		ovxView.exitCall();
		
		//and free the resources used by OVX context
		ovxView.unregister();		

		// Don't kill process when onDestroy
		android.os.Process.killProcess(android.os.Process.myPid());
	}
				
	// generic dialog used to display messages
	public void showDialog(String title, CharSequence[] items) {

		final CharSequence[] fitems = items;

		AlertDialog.Builder lmenu = new AlertDialog.Builder(this);

		final AlertDialog ad = lmenu.create();
		lmenu.setTitle(title);
		lmenu.setMessage(fitems[0]);
		lmenu.setPositiveButton("OK", new DialogInterface.OnClickListener() {
			public void onClick(DialogInterface dialog, int id) {
				ad.dismiss();
			}
		});
		lmenu.show();
	}

	 /* 
	 * We can listen to call related events like call started , call ended , call failed and perform appropriate actions 
	 * in these callback functions, we can also receive messages sent from the other parties in the conference using the call listener. 
	 */
	public void callListener() {
		chat_box = (TextView) findViewById(R.id.chat_text_box);

		/** call back listener to listen to call events  */
		ovxView.setCallListener(new OVXCallListener() {
			//invoked when the call has been disconnected by the user.
			@Override
			public void callEnded() {
				Log.d("INDUS", "Call Ended ");
				chat_box.setText("");
			}

			// invoked when the call fails due to some reasons.
			@Override
			public void callFailed() {
				chat_box.clearComposingText();
			}

			// invoked when the call has been established.
			@Override
			public void callStarted() {
				Log.d("INDUS", "Call Started");
				ovx_text.setEnabled(true);
				ovx_text.setHint("Enter Message:");
			}

			/* Invoked when messages are sent from other parties in the conference and 
			*the server as notifications.
			*/
			@Override
			public void ovxReceivedData(String arg0) {
				Uri uri = Uri.parse("http://dummyserver.com?" + arg0);
				String type = uri.getQueryParameter("type");
				String data = uri.getQueryParameter("data");
				String sender = uri.getQueryParameter("sender");
				
				if(sender!=null) {
					if(sender.equals("OpenClove")) {
						sender = "System";
					} 
					
					chat_box.setMovementMethod(new ScrollingMovementMethod());
					chat_box.append("\n" + sender + " : " + data);
					chat_box.setTextColor(Color.BLACK);
					
				}
				Log.d("INDUS", "Received message from ac_server:" + arg0);
				
				focusOnText();
			}

			//invoked when the call has been terminated due to n/w issues.
			@Override
			public void callTerminated(String arg0) {
				chat_box.clearComposingText();
			}
			
			@Override 
			public void onNotificationEvent(String eventType,String data) {
				if(eventType.equals("broadcastUrl"))
				{
					Log.d("INDUS","notification data:"+data);
				}
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		Log.d("INDUS", "OnResume:" + this);
		/*
		 * Should be called to update the dimensions and position
		 * of the video view that had been changed after the call was started or 
		 * to resume the video stream if it had been paused while launching another activity.  
		 */
		if (ovxView.isCallOn())
			ovxView.updateVideoOrientation();
	}

	@Override
	public void onPause() {
		super.onPause();
		Log.d("INDUS", "On Pause");

	}

	private void focusOnText() {
		// append the new string
			
		// find the amount we need to scroll. This works by
		// asking the TextView's internal layout for the position
		// of the final line and then subtracting the TextView's height
		final int scrollAmount = chat_box.getLayout().getLineTop(
				chat_box.getLineCount())
				- chat_box.getHeight();
		// if there is no need to scroll, scrollAmount will be <=0
		if (scrollAmount > 0)
			chat_box.scrollTo(0, scrollAmount);
		else
			chat_box.scrollTo(0, 0);
	}
	
	
	public static void alert(Context context, String message) {
        new AlertDialog.Builder(context)
        .setIcon(R.drawable.ic_launcher)
        .// the icon
        setTitle("Friend add request")
        .// title
        setMessage(message)
        .// info
        setPositiveButton("Yes", new DialogInterface.OnClickListener() {// ok
                    @Override
                    public void onClick(DialogInterface arg0, int arg1) {
                        
                        
                    }
                })
        .setNegativeButton("No", new DialogInterface.OnClickListener() {// cancel
                    @Override
                    public void onClick(DialogInterface arg1, int witch) {
                        
                    }
                }).show();
    }

}
