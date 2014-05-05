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


public class MyCustomReceiver extends BroadcastReceiver {
    private static final String TAG = "MyCustomReceiver";

    @Override
    public void onReceive(Context context, Intent intent) {
        try {
            String action = intent.getAction();
            String channel = intent.getExtras().getString("com.parse.Channel");
            JSONObject json = new JSONObject(intent.getExtras().getString(
                    "com.parse.Data"));

            Log.d(TAG, "got action " + action + " on channel " + channel
                    + " with:");
            
            // jump to the contact activity to add contact.
            String fromUsername = json.getString("username");
            
            if (json.getString("messType").equals("0")) {
                jumpToContactActivity(context, fromUsername, json.getString("alert"));
            } else {
                // save contact to local database.
                BuildContact buildContact = new BuildContact();
                buildContact.addContactToLocal(context, fromUsername);
            }
            
            saveRequestNotification(fromUsername);
            
            Iterator itr = json.keys();
            while (itr.hasNext()) {
                String key = (String) itr.next();
                Log.d(TAG, "..." + key + " => " + json.getString(key));
            }
        } catch (JSONException e) {
            Log.d(TAG, "JSONException: " + e.getMessage());
        }
    }
    
    // save the notification message.
    private void saveRequestNotification(String fromUsername) {
        // save the notification to the local database.
    }
    
    

    // we can not show alert here. need to move to "history"
    // now we just pop up a alert window to do that.
    private void jumpToContactActivity(Context context, String userID, String message) {
        // create a new activity to deal with the notification.
        Intent intent = new Intent(
                context,
                ContactActivity.class); 
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        
        intent.putExtra("username", userID);
        intent.putExtra("messType", "addContactRequest");
        intent.putExtra("message", message);
        
        context.startActivity(intent);
    }

}