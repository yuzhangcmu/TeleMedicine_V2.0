package edu.cmu.smartphone.telemedicine;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseInstallation;
import com.parse.ParseUser;
import com.parse.PushService;

import edu.cmu.smartphone.telemedicine.constants.Constant;

import android.app.Application;

public class TeleMedicineApplication extends com.openclove.ovx.OVX {

	@Override
	public void onCreate() {
		super.onCreate();
		
		// Add your initialization code here
		Parse.initialize(this, Constant.PARSE_ID, Constant.PARSE_KEY);

		ParseUser.enableAutomaticUser();
		ParseACL defaultACL = new ParseACL();
	    
		// If you would like all objects to be private by default, remove this line.
		defaultACL.setPublicReadAccess(true);
		
		ParseACL.setDefaultACL(defaultACL, true);
		
		PushService.setDefaultPushCallback(this, ContactActivity.class);
		ParseInstallation.getCurrentInstallation().saveInBackground();
	}

}
