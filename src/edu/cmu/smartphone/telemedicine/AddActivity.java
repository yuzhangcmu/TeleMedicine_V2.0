package edu.cmu.smartphone.telemedicine;

import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseInstallation;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.PushService;

import edu.cmu.smartphone.telemedicine.DBLayout.Dao_Sqlite;
import edu.cmu.smartphone.telemedicine.entities.Contact;

public class AddActivity extends Activity {

	EditText addviewSearchEditText;
	
	// the button which go to display the search result of contact.
	public void dispSearchContactResult(View v) {
	    
	    addviewSearchEditText = (EditText) findViewById(R.id.addviewSearchEditText);
	    
	    // get the string to search. We should support search by name, userID, or email.
	    String searchText = addviewSearchEditText.getEditableText()
                .toString();
	    
	    // search the cloud database to know if the user exit.
	    Dao_Sqlite dao = new Dao_Sqlite(AddActivity.this);
	    
	    if (searchText.equals(Contact.getCurrentUserID())) {
	        // JUST FOR TEST;
	        dao.searchContactCloud(searchText);
	        
	        Toast toast = Toast.makeText(getApplicationContext(),
                    "Can not add yourself. Please try again.", Toast.LENGTH_LONG);
                  toast.setGravity(Gravity.CENTER, 0, 0);
                  LinearLayout toastView = (LinearLayout) toast.getView();
                  ImageView imageCodeProject = new ImageView(getApplicationContext());
                  imageCodeProject.setImageResource(R.drawable.ic_action_accept);
                  toastView.addView(imageCodeProject, 0);
                  toast.show();
	    } else {
	        dao.searchContactCloud(searchText);
	    }
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.addview);
		
		addviewSearchEditText = (EditText)findViewById(R.id.addviewSearchEditText);
		
		
		
	}

}
