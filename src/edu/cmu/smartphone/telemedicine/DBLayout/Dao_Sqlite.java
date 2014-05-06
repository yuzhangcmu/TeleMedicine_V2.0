package edu.cmu.smartphone.telemedicine.DBLayout;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Formatter;
import java.util.LinkedList;
import java.util.List;
import java.util.Locale;

import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import edu.cmu.smartphone.telemedicine.ContactActivity;
import edu.cmu.smartphone.telemedicine.InfoActivity;
import edu.cmu.smartphone.telemedicine.LoginActivity;
import edu.cmu.smartphone.telemedicine.UserInfoActivity;
import edu.cmu.smartphone.telemedicine.constants.Constant;
import edu.cmu.smartphone.telemedicine.entities.ChatRecord;
import edu.cmu.smartphone.telemedicine.entities.Contact;
import edu.cmu.smartphone.telemedicine.entities.RecentChat;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;
import android.util.Log;
import android.view.Gravity;
import android.widget.Toast;

public class Dao_Sqlite extends SQLiteOpenHelper {
    // Logcat tag
    private static final String LOG = "DatabaseHelper";
 
    // Database Version
    // May 6, I upgrade it from 1 to 3.
    public static final int DATABASE_VERSION = 3;
 
    // Database table name
    private static final String TABLE_CONTACT = "TableContact";
    private static final String TABLE_CHATRECORD = "TableChatRecord";
    private static final String TABLE_PATIENT = "TablePatient";
    private static final String TABLE_DOCTOR = "TableDoctor";
    private static final String TABLE_RECENTCHAT = "TableRecentChat";
    
    // contact Table - column names
    private static final String KEY_TYPE = "type";
    private static final String KEY_EMAIL = "email";
    private static final String KEY_PHONE = "phone";
    private static final String KEY_NAME = "name";
    public static final String KEY_USERID = "userid";
    private static final String KEY_INTRO = "intro";
    private static final String KEY_HEADPORTRAIT = "headportrait";
    
    // Chat record Table - column names
    private static final String KEY_ID = "id";
    private static final String KEY_MESSAGE = "message";
    private static final String KEY_STATUS = "status";
    public static final String KEY_RECORD_TIME = "time";
    private static final String KEY_DIRECTION = "direction";
    private static final String KEY_MESSAGETYPE = "message_type_id";
    
    // when show by page, the size of every page.
    static final int PAGE_SIZE = 3;
    
    // parse.com database.
    public static final String KEY_FULLNAME = "fullname";
    public static final String KEY_FRIEND_USER_NAME_CLOUD = "friend_username";
    
    public static final String KEY_USERTABLE = "User"; // this table stored all the users.
    public static final String KEY_USERNAME_CLOUD = "username"; // the keyword of "username"
    public static final String KEY_FULLNAME_CLOUD = "fullname"; // the keyword of "username"

    private Context context;
    
    SQLiteDatabase myDB;
    
    
    public Dao_Sqlite(Context context, String name, CursorFactory factory,
            int version) {
        super(context, name, factory, version);
        this.context = context;
        
        myDB = this.getWritableDatabase();
        onCreate(myDB);
        this.context = context;
    }
    
    public Dao_Sqlite(Context context) {
        super(context, Contact.getCurrentUserID(), null, DATABASE_VERSION);
        this.context = context;
        myDB = this.getWritableDatabase();
    }
    
    public LinkedList<Contact> getContactList() {
        LinkedList<Contact> contactList = new LinkedList<Contact>();
        
        String sql = "SELECT * FROM " + TABLE_CONTACT;
        Log.d(LOG, sql);
        
        Cursor c = myDB.rawQuery(sql, null);
        
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                Contact contact = new Contact(c.getString(c.getColumnIndex("name")), 
                        c.getString(c.getColumnIndex("userid")));
                
                // adding to contact list.
                contactList.add(contact);
            } while (c.moveToNext());
        }
        
        return contactList;
    }
    
    public Cursor getContactCursor() {
        String sql = "SELECT " + KEY_NAME + "," + KEY_USERID +
                " FROM " + TABLE_CONTACT + " ORDER BY " + KEY_NAME;
        Log.d(LOG, sql);
        
        Cursor c = myDB.rawQuery(sql, null);
        return c;
    }
    
    public Cursor getRecentContactCursor() {
        String sql = "SELECT " + KEY_USERID + " AS _id, " + KEY_USERID + "," + KEY_RECORD_TIME +
                " FROM " + TABLE_RECENTCHAT + " ORDER BY " + KEY_RECORD_TIME + " DESC;";
        
        // change the level to debug, not error.
        Log.d(LOG, sql);
        
        Cursor c = myDB.rawQuery(sql, null);
        return c;
    }
    
    public long getContactNumber() {
        String sql = "SELECT COUNT(*) FROM " + TABLE_CONTACT;
        SQLiteStatement statement = myDB.compileStatement(sql);
        long count = statement.simpleQueryForLong();
        return count;
    }
    
    public Cursor addContactToArrayList(List<Contact> contacts) {
        Cursor c = getContactCursor();
        
        // looping through all rows and adding to list
        if (c.moveToFirst()) {
            do {
                int colum = c.getColumnIndex(KEY_NAME);
                if (colum < 0) {
                    return c;
                }
                
                String name = c.getString(colum);
                
                colum = c.getColumnIndex(KEY_USERID);
                if (colum < 0) {
                    return c;
                }
                String userid = c.getString(colum);
                
                Contact contact = new Contact(name, userid);
                
                // set the first character to be key.
                String sortKey = ContactActivity.getSortKey(name);
                contact.setSortKey(sortKey);
                
                // adding to contact list.
                contacts.add(contact);
            } while (c.moveToNext());
        }
        
        return c;
    }
    
    public void close() {
        myDB.close();
    }
    
    // load a user's data to the database.
    public void loadDataFromCloud(String userName, final DataLoadCallback callback) {
        ParseQuery<ParseObject> query = ParseQuery.getQuery(userName);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> contactList, ParseException e) {
                if (e == null) {
                    Log.d("contacts", "Retrieved " + contactList.size() + " contacts");
                    
                    // delete the local database;
                    // because may be the database is not the same.
                    //onUpgrade(myDB, 0, 0);
                    
                    for (ParseObject o: contactList) {
                        String name = o.getString(KEY_FULLNAME);
                        String userid = o.getString(KEY_FRIEND_USER_NAME_CLOUD);
                        
                        // because we need to check another table, just keep it to be userid now.
                        // but later we need to modify it.
                        // later we should use "relation" to realize the feature.
                        Contact contact = new Contact(userid, userid);
                        contact.setSortKey(userid);
                        
                        addContact(contact);
                    }
                    
                    if (callback != null) {
                        callback.dataloadCallback();
                    }
                    
                } else {
                    Log.d("contacts", "Error: " + e.getMessage());
                }
            }
            
        });
        
    }
    
    // search the cloud to get if the contact exit.    
    public int searchContactCloud(String key) {
        // open the user table.
        ParseQuery<ParseUser> friendQuery = ParseUser.getQuery();
        
        friendQuery.whereEqualTo(KEY_USERNAME_CLOUD, key);
        
        friendQuery.findInBackground(new FindCallback<ParseUser>() {
            @Override
            public void done(List<ParseUser> contactList, ParseException e) {
                if (e == null) {
                    Log.d("contacts", "Retrieved " + contactList.size() + " contacts");
                    
                    if (contactList == null || contactList.size() == 0) {
                        if (context == null) {
                            return;
                        }
                        
                        // show error.
                        Toast toast = Toast.makeText(context, "No such user!", Toast.LENGTH_LONG);
                         
                        // change the position to show the message.
                        toast.setGravity(Gravity.CENTER|Gravity.CENTER, 0, 0);
                        toast.show();
                        return;
                    }
                    
                    String username = contactList.get(0).getUsername();
                    String fullname = contactList.get(0).getString(KEY_FULLNAME_CLOUD);
                    
                    
                    Intent intent = new Intent(context, UserInfoActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("fullname", fullname);
                    
                    context.startActivity(intent);
                    
                } else {
                    Log.d("contacts", "Error: " + e.getMessage());
                    
                    // show error.
                    Toast toast = Toast.makeText(context, e.getMessage(), Toast.LENGTH_LONG);
                     
                    // change the position to show the message.
                    toast.setGravity(Gravity.CENTER|Gravity.LEFT, 0, 0);
                    toast.show();
                    
                }
            }
            
        });
        
        return -1;
    }
    
    public int updateContact(Contact contact) {
        
        ContentValues values = new ContentValues();
        
        values.put(KEY_TYPE, contact.getType());
        values.put(KEY_EMAIL, contact.getEmail());
        values.put(KEY_PHONE, contact.getPhone());
        values.put(KEY_NAME, contact.getName());
        values.put(KEY_INTRO, contact.getIntro());
        values.put(KEY_HEADPORTRAIT, contact.getHeadPortrait());
     
        // updating row
        return myDB.update(TABLE_CONTACT, values, KEY_USERID + " = ?",
                new String[] { contact.getUserID() });
    }
    
    public void cleanTable() {
        try {
            // drop the contact table.
            String sql = "DROP TABLE " + TABLE_CONTACT + ";";
            myDB.execSQL(sql);
 
        } catch (Exception e) {
            Log.e("Error", "Error", e);
        }
    }
    
    // add a chat record.
    public void addChatRecord(ChatRecord record) {
        // refresh the recent chat list.
        RecentChat chat = new RecentChat(record.getChatUserID());
        addRecentContact(chat);
        
        try {
            StringBuilder sb = new StringBuilder();
            
            Formatter formatter = new Formatter(sb, Locale.US);
            
            // Explicit argument indices may be used to re-order output.
            formatter.format("REPLACE INTO %s (" 
                    + KEY_USERID + ", "
                    + KEY_MESSAGE + ", " 
                    + KEY_STATUS + ", "
                    + KEY_DIRECTION + ", "
                    + KEY_MESSAGETYPE + ", "
                    + KEY_RECORD_TIME
                    + ") "           
                    + "VALUES ('%s', '%s', '%d', '%d', '%d', %s);",
                    TABLE_CHATRECORD,
                    record.getChatUserID(),
                    record.getMessage(),
                    record.getStatus() ? 1: 0,
                    record.getDirection() ? 1: 0,
                    record.getMessageType(),
                    "strftime('%Y-%m-%d %H:%M:%f', 'now', 'utc')"
                    );
            
            formatter.close();
            String sql = sb.toString();
            myDB.execSQL(sql);
            Log.d(LOG, sql);
 
        } catch (Exception e) {
            Log.e("Error", "Error", e);
 
        }
        
    }
    
    public void delRecentContact(String userID) {
        myDB.delete(TABLE_RECENTCHAT, KEY_USERID + " = ?",
                new String[] { userID });     
    }
    
    // add a chat record.
    public void addRecentContact(RecentChat record) {
        try {
            StringBuilder sb = new StringBuilder();
            
            Formatter formatter = new Formatter(sb, Locale.US);
            
            // Explicit argument indices may be used to re-order output.
            formatter.format("REPLACE INTO %s (" 
                    + KEY_USERID + ", "                  
                    + KEY_RECORD_TIME
                    + ") "           
                    + "VALUES ('%s', %s);",
                    TABLE_RECENTCHAT,
                    record.getUserID(),
                    "strftime('%Y-%m-%d %H:%M:%f', 'now', 'utc')"
                    );
            
			Log.d(LOG, "sb:" + sb.toString());
            formatter.close();
            String sql = sb.toString();
            myDB.execSQL(sql);
            Log.d(LOG, sql);
 
        } catch (Exception e) {
            Log.e("Error", "Error", e);
        }
    }
    
    public int getPageNumber(String userID) {
        return 10;
    }
    
    // get a page of chat record.
    public void getChatRecord(String userID, ArrayList<ChatRecord> chatRecordList, int pageID) {
//        String sql= "select * from " + TABLE_CHATRECORD 
//                + " WHERE "
//                + KEY_USERID
//                + " = '" + userID + "' "
//                + " order  by datetime(" 
//                + KEY_RECORD_TIME 
//                + ") DESC "
//                + " Limit "+String.valueOf(PAGE_SIZE)+ " Offset " +String.valueOf(pageID*PAGE_SIZE);    
        String sql= "select * from " + TABLE_CHATRECORD 
                + " WHERE "
                + KEY_USERID
                + " = '" + userID + "' "
                + " order  by " 
                + KEY_RECORD_TIME 
                + " ASC "
                + " Limit "+String.valueOf(PAGE_SIZE)+ " Offset " +String.valueOf(pageID*PAGE_SIZE);
        
        Cursor rec = myDB.rawQuery(sql, null);    
    
        // looping through all rows and adding to list
        if (rec.moveToFirst()) {
            do {
                ChatRecord record = new ChatRecord();
                
                record.setMessage(rec.getString(rec.getColumnIndex(KEY_MESSAGE)));
                record.setChatUserID(rec.getString(rec.getColumnIndex(KEY_USERID)));
                record.setMessageType(rec.getInt(rec.getColumnIndex(KEY_USERID)));
                
                // get the status.
                Boolean status = rec.getInt(rec.getColumnIndex(KEY_STATUS)) > 0;
                
                // set the status.
                record.setStatus(status);
                
                // get the direction.
                Boolean direction = rec.getInt(rec.getColumnIndex(KEY_DIRECTION)) > 0;
                
                // set the status.
                record.setDirection(direction);
                
                // set time.
                // attention: no we just use string to store time.
                String time = rec.getString(rec.getColumnIndex(KEY_RECORD_TIME));
                record.setTime(time);
                
                chatRecordList.add(record);
            } while (rec.moveToNext());
        }
    
        rec.close();    
    }
    
    public void addContact(Contact contact) { 
        String tableContact = TABLE_CONTACT;
 
        try {
            // Get the database if database is not exists create new database 
            // Database name is " test " 
            
            StringBuilder sb = new StringBuilder();
            
            // Send all output to the Appendable object sb
            Formatter formatter = new Formatter(sb, Locale.US);
            
            formatter.format("REPLACE INTO %s (name, userid) VALUES ('%s', '%s');",
                    tableContact,
                    contact.getName(),
                    contact.getUserID()
                    );
            
            formatter.close();
            String sql = sb.toString();
            myDB.execSQL(sql);
            Log.d(LOG, sql);
 
        } catch (Exception e) {
            Log.e("Error", "Error", e);
 
        }
    }
    
    public void deleteContact(String userid) { 
        myDB.delete(TABLE_CONTACT, KEY_USERID + " = ?",
                new String[] { userid });                
    }

    @Override
    public void onCreate(SQLiteDatabase DB) {
        try {
            String sql = "CREATE TABLE IF NOT EXISTS " + TABLE_CONTACT
                    + " (" + KEY_USERID + " VARCHAR PRIMARY KEY, name VARCHAR);";
            DB.execSQL(sql);
            
            DB.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_CHATRECORD
                    + " (" 
                    + KEY_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                    + KEY_MESSAGE + " varchar(255), "
                    + KEY_STATUS + " bit, "
                    //+ KEY_RECORD_TIME + " datetime, "
                    + KEY_USERID + " varchar(255),"
                    + KEY_DIRECTION + " bit,"
                    + KEY_MESSAGETYPE + " integer, "
                    + KEY_RECORD_TIME + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ");");
            
            // create recentChat table.
            DB.execSQL("CREATE TABLE IF NOT EXISTS " + TABLE_RECENTCHAT
                    + " (" 
                    + KEY_USERID + " varchar(255) PRIMARY KEY, " // userID as the primary key.
                    + KEY_RECORD_TIME + " DATETIME DEFAULT CURRENT_TIMESTAMP"
                    + ");");
 
        } catch (Exception e) {
            Log.e("Error", "Error", e);
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // on upgrade drop older tables
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CONTACT);
        
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CHATRECORD);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_RECENTCHAT);
        
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PATIENT);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DOCTOR);
 
        // create new tables
        onCreate(db);
    }
}
