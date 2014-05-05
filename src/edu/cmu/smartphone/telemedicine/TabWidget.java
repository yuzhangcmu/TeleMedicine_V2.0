package edu.cmu.smartphone.telemedicine;


import android.app.TabActivity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
 
public class TabWidget extends TabActivity {
 
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.tab);
        Resources res = getResources();
        TabHost tabHost = getTabHost();
        TabSpec spec;
        Intent intent;
        
        Drawable image1 = this.getResources().getDrawable(R.drawable.sym_action_chat);
 
        //  first tab, display the history chatting.
        intent = new Intent(this,ChatHistoryActivity.class);
        spec = tabHost.newTabSpec("tab1")
        .setIndicator("Message", image1)
        .setContent(intent);
        tabHost.addTab(spec);
        
        Drawable image2 = this.getResources().getDrawable(R.drawable.ic_menu_friendslist);
 
        // second tab, display the contact
        intent = new Intent(this,ContactActivity.class);
        spec = tabHost.newTabSpec("tab2")
        .setIndicator("Contacts", image2)
        .setContent(intent);
        tabHost.addTab(spec);
        
        Drawable image3 = this.getResources().getDrawable(R.drawable.ic_action_settings);
        
        // the third tab.
        intent = new Intent(this,SettingActivity.class); 
        spec = tabHost.newTabSpec("tab2")
        .setIndicator("Setting", image3)
        .setContent(intent);
        tabHost.addTab(spec);
 
        // hide the keyboard.
        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        tabHost.setCurrentTab(1);
        
        tabHost.getTabWidget().getChildAt(0).setVisibility(View.GONE);
    }
 
}