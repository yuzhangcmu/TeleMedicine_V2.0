package edu.cmu.smartphone.telemedicine;

import java.util.ArrayList;
import java.util.List;

import edu.cmu.smartphone.telemedicine.DBLayout.Dao_Sqlite;
import edu.cmu.smartphone.telemedicine.adapt.BuildContact;
import edu.cmu.smartphone.telemedicine.entities.Contact;
import edu.cmu.smartphone.telemedicine.ws.remote.Notification;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnCreateContextMenuListener;
import android.view.ViewGroup.MarginLayoutParams;
import android.view.WindowManager;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.AdapterContextMenuInfo;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AlphabetIndexer;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class ContactActivity extends Activity{
    
    private LinearLayout titleLayout;

    private TextView title;

    private ListView contactsListView;

    private ContactAdapter adapter;

    private AlphabetIndexer indexer;

    private List<Contact> contacts = new ArrayList<Contact>();

    private String alphabet = "#ABCDEFGHIJKLMNOPQRSTUVWXYZ";

    private int lastFirstVisibleItem = -1;
    
    private EditText searchContactEditText;
    
    public void goToAddContactView(View view) {
        //loadContact();
        
        Intent intent = new Intent(ContactActivity.this, AddActivity.class);
        startActivity(intent);
    }
    
    // show dialog to the user and let user to determine if he want to add the contact.
    private void addContactAlert(final String userID, String message) {
        new AlertDialog.Builder(ContactActivity.this)
                .setIcon(R.drawable.ic_launcher)
                .// the icon
                setTitle("Friend add request")
                .// title
                setMessage(message)
                .// info
                setPositiveButton("Yes", new DialogInterface.OnClickListener() {// ok
                            @Override
                            public void onClick(DialogInterface arg0, int arg1) {
                                
                                // add contact. add contact to local database and cloud.
                                BuildContact buildContact = new BuildContact();
                                Contact contact = buildContact.addContact(ContactActivity.this, userID);
                                
                                addItem(contact);
                                
                                Notification noti = new Notification(ContactActivity.this);
                                noti.sendNotification(userID, Contact.getCurrentUserID() +
                                        " approved your friend request.", 1);
                            }
                        })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {// cancel
                            @Override
                            public void onClick(DialogInterface arg1, int witch) {
                                Notification noti = new Notification(ContactActivity.this);
                                noti.sendNotification(userID, Contact.getCurrentUserID() +
                                        " rejected your friend request.", 1);
                            }
                        }).show();
    }
    
    // add a item to the current view list, and refresh the view.
    private void addItem(Contact contact)  
    {
        if (contact == null) {
            return;
        }
      
        Dao_Sqlite dao = new Dao_Sqlite(ContactActivity.this);
        Cursor cursor = dao.getContactCursor();
        
        // pay attention to this.
        startManagingCursor(cursor);
        indexer = new AlphabetIndexer(cursor, 1, alphabet);
        adapter.setIndexer(indexer);
        
        // must add judge to not add duplicate data.
        if (dao.getContactNumber() > contacts.size()) {
            contacts.add(contact);  
            adapter.notifyDataSetChanged();  
            contactsListView.invalidate();
        }
    }
    
    private void loadContact() {
        adapter = new ContactAdapter(this, R.layout.contact_item, contacts);
        
        //readContactFromPhone();
        
        // load data from www.parse.com and store it in the local database.
        readDataFromLocalDb();
        
        if (contacts.size() > 0) {
            setupContactsListView();
        }
    }
    
    private void setupContactsListView() {
        contactsListView.setAdapter(adapter);
        contactsListView.setOnScrollListener(new OnScrollListener() {

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                    int totalItemCount) {
                int section = indexer.getSectionForPosition(firstVisibleItem);
                int nextSecPosition = indexer.getPositionForSection(section + 1);
                if (firstVisibleItem != lastFirstVisibleItem) {
                    MarginLayoutParams params = (MarginLayoutParams) titleLayout.getLayoutParams();
                    params.topMargin = 0;
                    titleLayout.setLayoutParams(params);
                    title.setText(String.valueOf(alphabet.charAt(section)));
                }
                
                if (nextSecPosition == firstVisibleItem + 1) {
                    android.view.View childView = view.getChildAt(0);
                    if (childView != null) {
                        int titleHeight = titleLayout.getHeight();
                        int bottom = childView.getBottom();
                        MarginLayoutParams params = (MarginLayoutParams) titleLayout
                                .getLayoutParams();
                        if (bottom < titleHeight) {
                            float pushedDistance = bottom - titleHeight;
                            params.topMargin = (int) pushedDistance;
                            titleLayout.setLayoutParams(params);
                        } else {
                            if (params.topMargin != 0) {
                                params.topMargin = 0;
                                titleLayout.setLayoutParams(params);
                            }
                        }
                    }
                }
                lastFirstVisibleItem = firstVisibleItem;
            }

            @Override
            public void onScrollStateChanged(android.widget.AbsListView view,
                    int scrollState) {
                // TODO Auto-generated method stub
                
            }

        });
        
        
        // click the contact item to show the profile view. Added by yu zhang
        contactsListView.setOnItemClickListener(new OnItemClickListener() {
            
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                    long id) {
                // TODO Auto-generated method stub
                Contact contact = contacts.get(position);
                
                // show the profile of the contact.
                Intent intent = new Intent(ContactActivity.this, InfoActivity.class);
                intent.putExtra("username", contact.getUserID());
                intent.putExtra("fullname", contact.getName());
                intent.putExtra("email", contact.getEmail());
                ContactActivity.this.startActivity(intent);
            }
        });
        
        // long tap the contact item, display a delete window.
        contactsListView.setOnCreateContextMenuListener(new OnCreateContextMenuListener()
        {
            @Override
            public void onCreateContextMenu(ContextMenu menu, View v,ContextMenuInfo menuInfo)
            {
                menu.setHeaderTitle("Contact Menu");   

                menu.add(0, 0, 0, "Delete contact");

                menu.add(0, 1, 0, "Cancel");   
            }
            
        });

    }
    
    // press the item to display a menu

    @Override
    public boolean onContextItemSelected(MenuItem item)
    {
        //setTitle("点击了长按菜单里面的第"+item.getItemId()+"个项目"); 
        
        // get which line is pressed.
        int selectedPosition = ((AdapterContextMenuInfo) item.getMenuInfo()).position;
        
        if (item.getItemId() == 0) {
            Contact contact = contacts.get(selectedPosition);
            String userID = contact.getUserID();
            
            contacts.remove(selectedPosition);//选择行的位置
            adapter.notifyDataSetChanged();
            contactsListView.invalidate();
            
            // delete contact from local database and the cloud.
            BuildContact buildContact = new BuildContact();
            buildContact.delContact(ContactActivity.this, userID);
        }
        
        return super.onContextItemSelected(item);
    }
    
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.contactview);
        
        searchContactEditText = (EditText) findViewById(R.id.contactSearchEditText);
        
        titleLayout = (LinearLayout) findViewById(R.id.title_layout);
        title = (TextView) findViewById(R.id.title);
        
        // hide the keyboard.
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        
        // get the view which show the list of contacts.
        contactsListView = (ListView) findViewById(R.id.contactSearchListView);
        
        /**
         * added by yu zhang, when another user add this user, I can see a 
         * alert window to decide whether or not add him/her.
         */
        String messType = getIntent().getStringExtra("messType");
        String userID = getIntent().getStringExtra("username");
        String message = getIntent().getStringExtra("message");
        
        
        if (messType != null && messType.equals("addContactRequest")) {
            // add friend request.
            addContactAlert(userID, message);
        }
        
        // load data to the view.
        loadContact();
    }
    
    
    private void readDataFromLocalDb() {
        // the database is named by the userID.
        String userID = Contact.getCurrentUserID();
        
        // this is just fixed for testing. display the contact list of yuzhang
        Dao_Sqlite dao = new Dao_Sqlite(ContactActivity.this, userID, null, 1);
        
        // load the contact list of the specific user to the database.
        // only load data when login.
        //dao.loadDataFromCloud(userID, null);
        
        Cursor cursor = dao.addContactToArrayList(contacts);
        
        // pay attention to this.
        startManagingCursor(cursor);
        indexer = new AlphabetIndexer(cursor, 1, alphabet);
        adapter.setIndexer(indexer);
    }
    
    private void readContactFromPhone() {
        
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        Cursor cursor = getContentResolver().query(uri,
                new String[] { "display_name", "sort_key" }, null, null, "sort_key");
        
        if (cursor.moveToFirst()) {
            do {
                String name = cursor.getString(0);
                String sortKey = getSortKey(cursor.getString(1));
                
                Contact contact = new Contact();
                contact.setName(name);
                contact.setSortKey(sortKey);
                
                contacts.add(contact);
            } while (cursor.moveToNext());
        }
        
        //startManagingCursor(cursor);
        indexer = new AlphabetIndexer(cursor, 1, alphabet);
        adapter.setIndexer(indexer);
    }
    
    
    
    public static String getSortKey(String sortKeyString) {
        String key = sortKeyString.substring(0, 1).toUpperCase();
        if (key.matches("[A-Z]")) {
            return key;
        }
        return "#";
    }
    
}
