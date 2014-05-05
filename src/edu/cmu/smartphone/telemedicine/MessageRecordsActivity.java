package edu.cmu.smartphone.telemedicine;

import java.util.ArrayList;
import java.util.HashMap;

import com.parse.ParseUser;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import edu.cmu.smartphone.telemedicine.DBLayout.Dao_Sqlite;
import edu.cmu.smartphone.telemedicine.entities.ChatRecord;
import edu.cmu.smartphone.telemedicine.entities.Contact;

public class MessageRecordsActivity extends Activity {

	String tag = "MessageRecordsActivity";
	ArrayList<HashMap<String, Object>> chatList = null;
	String[] from = { "image", "text" };
	int[] to = { R.id.chatlist_image_me, R.id.chatlist_text_me, R.id.chatlist_image_other, R.id.chatlist_text_other };
	int[] layout = { R.layout.chat_listitem_me, R.layout.chat_listitem_other };
	String userQQ = null;

	public final static int OTHER = 1;
	public final static int ME = 0;
	
	private static int page = 0;

	protected ListView chatListView = null;
	protected TextView chat_contact_name = null;
	protected Button chat_bottom_prevbutton = null;
	protected Button chat_bottom_nextbutton = null;

	protected MyChatAdapter adapter = null;
	Dao_Sqlite dao = null;
	
	String currentUserName = null;
	String callee_username = null;
	String callee_fullname = null;
	String callee_email = null;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(R.layout.message_records_view);
		
		currentUserName = ParseUser.getCurrentUser().getUsername();
		callee_username = getIntent().getStringExtra("callee_username");
		callee_fullname = getIntent().getStringExtra("fullname");
		callee_email = getIntent().getStringExtra("email");
		
		
		dao = new Dao_Sqlite(MessageRecordsActivity.this, Contact.getCurrentUserID(), null, 1);
		
		chatList = new ArrayList<HashMap<String, Object>>();
		chatListView = (ListView) findViewById(R.id.chat_list);
		chat_contact_name = (TextView) findViewById(R.id.chat_contact_name);
		chat_bottom_prevbutton = (Button)findViewById(R.id.chat_bottom_prevbutton);
		chat_bottom_nextbutton = (Button)findViewById(R.id.chat_bottom_nextbutton);		

		adapter = new MyChatAdapter(this, chatList, layout, from, to);
		chatListView.setAdapter(adapter);
		
		loadMessageRecordsFromLocalDB();
		
		chat_bottom_prevbutton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				if(page > 0) {
					page--;
					loadMessageRecordsFromLocalDB();
				}
			}
		});

		chat_bottom_nextbutton.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View v) {
				page++;
				loadMessageRecordsFromLocalDB();
			}
		});
		
		// chat_contact_name.setText();
	}
	
	protected void loadMessageRecordsFromLocalDB() {
		
		// TODO
        ArrayList<ChatRecord> list = new ArrayList<ChatRecord>();
        dao.getChatRecord(callee_username, list, 0);
        
        chatList.clear();
        
        Log.d(tag, "callee_username:" + callee_username);
        alert(MessageRecordsActivity.this, list.toString());
        
        for (ChatRecord chatRecord : list) {
        	String msg = chatRecord.getMessage();
        	if(chatRecord.getDirection()) {		// I send
        		addTextToList(msg, ME);
        	} else {		// I receive
        		addTextToList(msg, OTHER);
        	}
		}
        
		adapter.notifyDataSetChanged();
		chatListView.invalidate();
	}

	protected void addTextToList(String text, int who) {
		HashMap<String, Object> map = new HashMap<String, Object>();
		map.put("person", who);
		map.put("image", who == ME ? R.drawable.contact_0 : R.drawable.contact_1);
		map.put("text", text);
		chatList.add(map);
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

	private class MyChatAdapter extends BaseAdapter {

		Context context = null;
		ArrayList<HashMap<String, Object>> chatList = null;
		int[] layout;
		String[] from;
		int[] to;

		public MyChatAdapter(Context context,
				ArrayList<HashMap<String, Object>> chatList, int[] layout,
				String[] from, int[] to) {
			super();
			this.context = context;
			this.chatList = chatList;
			this.layout = layout;
			this.from = from;
			this.to = to;
		}

		@Override
		public int getCount() {
			return chatList.size();
		}

		@Override
		public Object getItem(int arg0) {
			return null;
		}

		@Override
		public long getItemId(int position) {
			return position;
		}

		class ViewHolder {
			public ImageView imageView = null;
			public TextView textView = null;

		}

		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			ViewHolder holder = null;
			int who = (Integer) chatList.get(position).get("person");

			convertView = LayoutInflater.from(context).inflate(layout[who == ME ? 0 : 1], null);
			holder = new ViewHolder();
			holder.imageView = (ImageView) convertView.findViewById(to[who * 2 + 0]);
			holder.textView = (TextView) convertView.findViewById(to[who * 2 + 1]);

			System.out.println(holder);
			System.out.println("WHYWHYWHYWHYW");
			System.out.println(holder.imageView);
			holder.imageView.setBackgroundResource((Integer) chatList.get(position).get(from[0]));
			holder.textView.setText(chatList.get(position).get(from[1]).toString());
			return convertView;
		}
	}
}
