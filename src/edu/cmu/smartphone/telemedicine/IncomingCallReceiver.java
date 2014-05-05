package edu.cmu.smartphone.telemedicine;

import java.util.Iterator;

import org.json.JSONException;
import org.json.JSONObject;

import edu.cmu.smartphone.telemedicine.DBLayout.Dao_Sqlite;
import edu.cmu.smartphone.telemedicine.DBLayout.Dao_parse;
import edu.cmu.smartphone.telemedicine.adapt.BuildContact;
import edu.cmu.smartphone.telemedicine.entities.Contact;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.app.AlertDialog;  
import android.content.DialogInterface;  


public class IncomingCallReceiver extends BroadcastReceiver {
    private static final String tag = "InComingCallReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String action = intent.getAction();
            String channel = intent.getExtras().getString("com.parse.Channel");
            JSONObject json = new JSONObject(intent.getExtras().getString("com.parse.Data"));

            Log.d(tag, "got action " + action + " on channel " + channel + " with:");
            
            // jump to the contact activity to add contact.
            String caller_username = json.getString("caller_username");		// a
            String callee_username = json.getString("callee_username");		// b
            String message = json.getString("message");
            
            jumpToVideoActivity(context, caller_username, callee_username, message);
            
            Iterator itr = json.keys();
            while (itr.hasNext()) {
                String key = (String) itr.next();
                Log.d(tag, "..." + key + " => " + json.getString(key));
            }
        } catch (JSONException e) {
            Log.d(tag, "JSONException: " + e.getMessage());
        }
    }
    

    // we can not show alert here. need to move to "history"
    // now we just pop up a alert window to do that.
    private void jumpToVideoActivity(Context context, String caller_username, String callee_username, String message) {
        // create a new activity to deal with the notification.
        Intent intent = new Intent(
                context,
                VideoActivity.class); 
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        intent.putExtra("caller_username", caller_username);
        intent.putExtra("callee_username", callee_username);
        intent.putExtra("message", message);
        
        Log.d(tag, "caller_username:" + caller_username);
		Log.d(tag, "callee_username:" + callee_username);
        
        context.startActivity(intent);
    }

}