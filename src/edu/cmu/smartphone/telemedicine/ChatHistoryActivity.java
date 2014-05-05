package edu.cmu.smartphone.telemedicine;

import java.util.ArrayList;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.support.v4.widget.SimpleCursorAdapter;
import edu.cmu.smartphone.telemedicine.DBLayout.Dao_Sqlite;
import edu.cmu.smartphone.telemedicine.adapt.BuildContact;
import edu.cmu.smartphone.telemedicine.entities.Contact;
import android.app.ListActivity;
import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.View.OnCreateContextMenuListener;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
//import android.widget.SimpleCursorAdapter;

// extends ListActivity to get the features.
public class ChatHistoryActivity extends ListActivity {
    
    private ListView chatListView;
    
    List<Map<String, Object>> datalist;
    SimpleAdapter adapter;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.chat_history_view);
        
//        datalist = getData();
//        
//        adapter = new SimpleAdapter(this, datalist, R.layout.chat_item_view,
//                new String[]{"title","info","img","time"},
//                new int[]{R.id.title,R.id.recent_chat,R.id.img, R.id.recent_chat_time});
//        setListAdapter(adapter);
        
        Dao_Sqlite dao = new Dao_Sqlite(ChatHistoryActivity.this);
        Cursor c = dao.getRecentContactCursor();
        startManagingCursor(c);   
        ListAdapter adapter = new SimpleCursorAdapter(this,    
                    R.layout.chat_item_view,   
                    c,    
                    new String[] {Dao_Sqlite.KEY_USERID, Dao_Sqlite.KEY_RECORD_TIME} ,   
                    new int[] {R.id.title, R.id.recent_chat_time},
                    0);    
         
        setListAdapter(adapter);
        
        // get the view which show the list of chat.
        chatListView = (ListView) findViewById(android.R.id.list);
        
        // click the contact item to show the profile view. Added by yu zhang
        chatListView.setOnItemClickListener(new OnItemClickListener() {
            
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                    long id) {
                
                // this is just for test. give a parameter to the next activity to chat.
                Contact contact = new Contact("yuzhang", "yuzhang");
                
                Cursor cursor = (Cursor) parent.getItemAtPosition(position);
                String name = cursor.getString(0);
                //Toast.makeText(Activity2.this, name, Toast.LENGTH_SHORT).show();
                
                // show the profile window.
                Intent intent = new Intent(ChatHistoryActivity.this, InfoActivity.class);
                intent.putExtra("username", name);
                intent.putExtra("fullname", name);
                intent.putExtra("email", "null");
                startActivity(intent);
            }
        });
        
        // long tap the chat item, display a delete window.
        chatListView.setOnCreateContextMenuListener(new OnCreateContextMenuListener()
        {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo)
            {
                menu.setHeaderTitle("Chat Session Menu");   
                menu.add(0, 0, 0, "Delete session");
                menu.add(0, 1, 0, "Cancel");   
            }
            
        });
        
    }
    
    @Override
    // delete a recent contact from the recent list.
    public boolean onContextItemSelected(MenuItem item)
    {
        // get which line is pressed.
        int selectedPosition = ((AdapterContextMenuInfo) item.getMenuInfo()).position;
        
        if (item.getItemId() == 0) {
            Map<String, Object> map = datalist.get(selectedPosition);
            //String userID = contact.getUserID();
            
            datalist.remove(selectedPosition); //get the position of the line and remove it.
            adapter.notifyDataSetChanged();
            chatListView.invalidate();
            
            // delete the session from the database.
            Dao_Sqlite dao = new Dao_Sqlite(ChatHistoryActivity.this);
            //dao.delRecentContact(userID);
        }
        
        return super.onContextItemSelected(item);
    }
    
    private List<Map<String, Object>> getData() {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
 
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("title", "G1");
        map.put("info", "Do you want to have lunch now?");
        map.put("img", R.drawable.contact_head1);
        map.put("time", "Yesterday");
        list.add(map);
 
        map = new HashMap<String, Object>();
        map.put("title", "G2");
        map.put("info", "I want to have a rest.");
        map.put("img", R.drawable.contact_head2);
        list.add(map);
 
        map = new HashMap<String, Object>();
        map.put("title", "G3");
        map.put("info", "Hi! Which company do you like?");
        map.put("img", R.drawable.contact_head3);
        list.add(map);
        
        map = new HashMap<String, Object>();
        map.put("title", "G3");
        map.put("info", "Can you introduce some university to me? I want to company.");
        map.put("img", R.drawable.contact_head3);
        list.add(map);
        
        map = new HashMap<String, Object>();
        map.put("title", "G4");
        map.put("info", "google 3");
        map.put("img", R.drawable.contact_head3);
        list.add(map);
        
        map = new HashMap<String, Object>();
        map.put("title", "G5");
        map.put("info", "google 3");
        map.put("img", R.drawable.contact_head1);
        list.add(map);
        
        map = new HashMap<String, Object>();
        map.put("title", "G6");
        map.put("info", "google 3");
        map.put("img", R.drawable.contact_head2);
        list.add(map);
        
        map = new HashMap<String, Object>();
        map.put("title", "G7");
        map.put("info", "google 3");
        map.put("img", R.drawable.contact_head3);
        list.add(map);
         
        return list;
    }
}
