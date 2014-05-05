package edu.cmu.smartphone.telemedicine.test;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.text.format.DateFormat;
import android.util.Log;
import edu.cmu.smartphone.telemedicine.ContactActivity;
import edu.cmu.smartphone.telemedicine.R;
import edu.cmu.smartphone.telemedicine.DBLayout.Dao_Sqlite;
import edu.cmu.smartphone.telemedicine.adapt.BuildContact;
import edu.cmu.smartphone.telemedicine.entities.ChatRecord;
import edu.cmu.smartphone.telemedicine.entities.Contact;
import edu.cmu.smartphone.telemedicine.ws.remote.Notification;

public class DatabaseTest {
    private static final String LOGTEST = "DatabaseTest";
    
    public static void databaseChatRecordTest(Context context) {
        Dao_Sqlite dao = new Dao_Sqlite(context, "y", null, 1);
        ChatRecord record = new ChatRecord();
        
        // Logcat tag
        String date_s = "2011-01-18 00:00:00.0";

        // *** note that it's "yyyy-MM-dd hh:mm:ss" not "yyyy-mm-dd hh:mm:ss"  
//        SimpleDateFormat dt = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//        Date date = null;
//        try {
//            date = (Date) dt.parse(date_s);
//        } catch (java.text.ParseException e) {
//            // TODO Auto-generated catch block
//            e.printStackTrace();
//        }

//        // *** same for the format String below
//        SimpleDateFormat dt1 = new SimpleDateFormat("yyyy-MM-dd");
//        System.out.println(dt1.format(date));
        
        //Date date = "1985-10-29";
        record.setChatUserID("yuzhang");
        record.setDate(null);
        record.setStatus(true);
        record.setMessage("I love you1");
        dao.addChatRecord(record);
        
        // Create an instance of SimpleDateFormat used for formatting 
        // the string representation of date (month/day/year)
        SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
        
        // Get the date today using Calendar object.
        Date today = Calendar.getInstance().getTime();        
        // Using DateFormat format method we can create a string 
        // representation of a date with the defined format.
        String reportDate = df.format(today);
        
        // Print what date is today!
        System.out.println("Report Date: " + reportDate);
        alert(context, reportDate);
        
        
        for (int i = 0; i < 100; i++) {            
            ChatRecord recordi = new ChatRecord("test message" + i, true, null, "yuzhang", true, 0);
            dao.addChatRecord(recordi);
        }
        
        for (int i = 0; i < 20; i++) {            
            ChatRecord recordi = new ChatRecord("test message" + i, true, null, "binfeng", true, 0);
            dao.addChatRecord(recordi);
            
        }
        
        ArrayList<ChatRecord> list = new ArrayList<ChatRecord>();
        ArrayList<ChatRecord> list2 = new ArrayList<ChatRecord>();
        dao.getChatRecord("y", list, 0);
        dao.getChatRecord("y", list2, 1);
        
        ArrayList<ChatRecord> list3 = new ArrayList<ChatRecord>();
        ArrayList<ChatRecord> list4 = new ArrayList<ChatRecord>();
        dao.getChatRecord("yuzhang", list3, 0);
        dao.getChatRecord("yuzhang", list4, 1);
        
        Log.d(LOGTEST, "Print list1");
        for (int i = 0; i < list.size(); i++) {
            Log.d(LOGTEST, list.get(0).getTime().toString());
        }
        
        for (int i = 0; i < list2.size(); i++) {
            Log.d(LOGTEST, list2.get(0).getTime().toString());
        }
        
        for (int i = 0; i < list3.size(); i++) {
            Log.d(LOGTEST, list3.get(0).getTime().toString());
        }
        
        for (int i = 0; i < list4.size(); i++) {
            Log.d(LOGTEST, list4.get(0).getTime().toString());
        }
        
        //alert(context, list.toString());
        //alert(context, list2.toString());
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
