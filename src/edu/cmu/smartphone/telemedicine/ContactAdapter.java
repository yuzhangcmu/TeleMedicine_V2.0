package edu.cmu.smartphone.telemedicine;

import java.util.List;
import java.util.Random;

import edu.cmu.smartphone.telemedicine.entities.Contact;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.SectionIndexer;
import android.widget.TextView;


public class ContactAdapter extends ArrayAdapter<Contact> {

	private int resource;

	private SectionIndexer mIndexer;

	public ContactAdapter(Context context, int textViewResourceId, List<Contact> objects) {
		super(context, textViewResourceId, objects);
		resource = textViewResourceId;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		Contact contact = getItem(position);
		LinearLayout layout = null;
		if (convertView == null) {
			layout = (LinearLayout) LayoutInflater.from(getContext()).inflate(resource, null);
		} else {
			layout = (LinearLayout) convertView;
		}
		
		
		TextView name = (TextView) layout.findViewById(R.id.name);
		LinearLayout sortKeyLayout = (LinearLayout) layout.findViewById(R.id.sort_key_layout);
		TextView sortKey = (TextView) layout.findViewById(R.id.sort_key);
		name.setText(contact.getName());
		
		
		ImageView head_portrait = (ImageView) layout.findViewById(R.id.head_portrait);
		
		Random randomGenerator = new Random();
		int randomInt = randomGenerator.nextInt(9);
	
		
		switch(randomInt) {
		    case 0: 
		        head_portrait.setImageResource(R.drawable.contact_0);
		        break;
		        
		    case 1: 
                head_portrait.setImageResource(R.drawable.contact_1);
                break;    
                
		    case 2: 
                head_portrait.setImageResource(R.drawable.contact_2);
                break;   
                
		    case 3: 
                head_portrait.setImageResource(R.drawable.contact_3);
                break;   
                
		    case 4: 
                head_portrait.setImageResource(R.drawable.contact_4);
                break;   
                
		    case 5: 
                head_portrait.setImageResource(R.drawable.contact_5);
                break;       
		        
		    default:
		        head_portrait.setImageResource(R.drawable.contact_8);
		        break;
		}
		
		
		int section = mIndexer.getSectionForPosition(position);
		if (position == mIndexer.getPositionForSection(section)) {
			sortKey.setText(contact.getSortKey());
			sortKeyLayout.setVisibility(View.VISIBLE);
		} else {
			sortKeyLayout.setVisibility(View.GONE);
		}
		
		return layout;
	}

	public void setIndexer(SectionIndexer indexer) {
		mIndexer = indexer;
	}
}

