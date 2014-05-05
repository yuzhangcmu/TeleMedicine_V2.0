package edu.cmu.smartphone.telemedicine.ws.remote;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.parse.ParsePush;

import edu.cmu.smartphone.telemedicine.R;
import edu.cmu.smartphone.telemedicine.R.drawable;
import edu.cmu.smartphone.telemedicine.entities.Contact;

public class Notification {
    private Context contest;
    
    public Notification (Context contest) {
        this.contest = contest;
    }
    
    public void sendNotification(String userID, String mess, int intMessType) {
        // send a notification to add the contact.
        JSONObject obj=new JSONObject();
        try {
            obj.put("action","edu.cmu.smartphone.telemedicine.UPDATE_STATUS");
            
            // tell friend your user id.
            obj.put("username",Contact.getCurrentUserID());
            obj.put("alert", mess);
            obj.put("messType", intMessType);
            
            if (intMessType == 0) {
                obj.put("title", "Friend request.");
            } else if (intMessType == 1) {
                obj.put("title", "Friend request confirm.");
            }
        } catch (JSONException e) {
            e.printStackTrace();
            
            Toast toast = Toast.makeText(contest,
                    "Internal error!", Toast.LENGTH_LONG);
                  toast.setGravity(Gravity.CENTER, 0, 0);
                  LinearLayout toastView = (LinearLayout) toast.getView();
                  ImageView imageCodeProject = new ImageView(contest);
                  imageCodeProject.setImageResource(R.drawable.ic_action_accept);
                  toastView.addView(imageCodeProject, 0);
                  toast.show();
            return;      
        }

        ParsePush push = new ParsePush();
        push.setChannel(userID);
        push.setData(obj);
        push.sendInBackground();
    }
    
    
    public void sendInComingCallPush(String caller_username, String callee_username, String message) {
    	// send a notification to add the contact.
    	JSONObject obj=new JSONObject();
    	try {
    		obj.put("action","edu.cmu.smartphone.telemedicine.INCOMING_CALL");
    		
    		// tell friend your user id.
    		obj.put("caller_username", caller_username);
    		obj.put("callee_username", callee_username);
    		obj.put("message", message);
    		
    	} catch (JSONException e) {
    		e.printStackTrace();
    		
    		Toast toast = Toast.makeText(contest,
    				"Internal error!", Toast.LENGTH_LONG);
    		toast.setGravity(Gravity.CENTER, 0, 0);
    		LinearLayout toastView = (LinearLayout) toast.getView();
    		ImageView imageCodeProject = new ImageView(contest);
    		imageCodeProject.setImageResource(R.drawable.ic_action_accept);
    		toastView.addView(imageCodeProject, 0);
    		toast.show();
    		return;      
    	}
    	
    	ParsePush push = new ParsePush();
    	push.setChannel(callee_username);
    	push.setData(obj);
    	push.sendInBackground();
    }
    

}
