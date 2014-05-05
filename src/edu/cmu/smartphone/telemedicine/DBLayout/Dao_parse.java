package edu.cmu.smartphone.telemedicine.DBLayout;

import java.util.List;

import android.content.Context;
import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseACL;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import edu.cmu.smartphone.telemedicine.LoginActivity;
import edu.cmu.smartphone.telemedicine.entities.Contact;

public class Dao_parse {
    private Context context;
    
    public Dao_parse(Context context) {
        this.context = context;
    }
    
    // add a friend to cloud service.
    public void addContactCloud(Contact contact) { 
        // save a new friend to the cloud.
        ParseObject newFriend = new ParseObject(Contact.getCurrentUserID());
        newFriend.put(Dao_Sqlite.KEY_FRIEND_USER_NAME_CLOUD, contact.getUserID());   
        newFriend.saveInBackground();
        
        // also add current to that user's contact list.
        newFriend = new ParseObject(contact.getUserID());
        ParseACL acl = new ParseACL();
        acl.setPublicWriteAccess(true);
        acl.setPublicReadAccess(true);
        newFriend.setACL(acl);
        
        newFriend.put(Dao_Sqlite.KEY_FRIEND_USER_NAME_CLOUD, Contact.getCurrentUserID());   
        newFriend.saveInBackground();
    }
    
    // delete a friend from cloud service.
    public void deleteContactCloud(String UserID) { 
        ParseQuery<ParseObject> query = ParseQuery.getQuery(Contact.getCurrentUserID());
        query.whereEqualTo(Dao_Sqlite.KEY_FRIEND_USER_NAME_CLOUD, UserID);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> contactList, ParseException e) {
                if (e == null) {
                    for (ParseObject o: contactList) {
                        o.deleteInBackground();
                    }
                } else {
                    Log.d("contacts", "Error: " + e.getMessage());
                }
            }
        });
    }
    
    public String getFullNameFromUserID(String userID) {
        return null;
    }
}
