package edu.cmu.socialgen.activity;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TabHost;
import edu.cmu.socialgen.DummyTabContent;
import edu.cmu.socialgen.R;
import edu.cmu.socialgen.tab.ChatsFragment;
import edu.cmu.socialgen.tab.HistoryFragment;
import edu.cmu.socialgen.tab.ProfileFragment;
import edu.cmu.socialgen.tab.UsersFragment;


public class MainActivity extends FragmentActivity {

	TabHost tHost;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {

		/*
        try {
        	ControlComManager CM = new ControlComManager(this.getWiFiInfo());
        	new Thread(CM).start();
        } catch (Exception e) {
        	// if getWiFiInfo() fails, stop the application!
        }
		*/
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
 
        tHost = (TabHost) findViewById(android.R.id.tabhost);
        tHost.setup();
 
        /** Defining Tab Change Listener event. This is invoked when tab is changed */
        TabHost.OnTabChangeListener tabChangeListener = new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                android.support.v4.app.FragmentManager fm =   getSupportFragmentManager();
                UsersFragment usersFragment = (UsersFragment) fm.findFragmentByTag("users");
                ChatsFragment chatsFragment = (ChatsFragment) fm.findFragmentByTag("chats");
                HistoryFragment historyFragment = (HistoryFragment) fm.findFragmentByTag("history");
                ProfileFragment profileFragment = (ProfileFragment) fm.findFragmentByTag("profile");
                android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
 
                if(usersFragment!=null)
                    ft.detach(usersFragment);
 
                if(chatsFragment!=null)
                    ft.detach(chatsFragment);
 
                if(historyFragment!=null)
                    ft.detach(historyFragment);
                
                if(profileFragment!=null)
                    ft.detach(profileFragment);
                
                if(tabId.equalsIgnoreCase("users")){
                    if(usersFragment==null){
                        ft.add(R.id.realtabcontent, new UsersFragment(), "users");
                    }else{
                        ft.attach(usersFragment);
                    }
                }else if(tabId.equalsIgnoreCase("chats")){
                    if(chatsFragment==null){
                        ft.add(R.id.realtabcontent, new ChatsFragment(), "chats");
                     }else{
                        ft.attach(chatsFragment);
                    }
                }else if(tabId.equalsIgnoreCase("history")) {
                	if(historyFragment==null){
                        ft.add(R.id.realtabcontent, new HistoryFragment(), "history");
                     }else{
                        ft.attach(historyFragment);
                    }
                }else{
                	if(profileFragment==null){
                        ft.add(R.id.realtabcontent, new ProfileFragment(), "profile");
                     }else{
                        ft.attach(profileFragment);
                    }
                }
                ft.commit();
            }
        };
 
        /** Setting tabchangelistener for the tab */
        tHost.setOnTabChangedListener(tabChangeListener);
 
        /** Defining tab builder for Folks tab */
        TabHost.TabSpec tSpecFolks = tHost.newTabSpec("users");
        tSpecFolks.setIndicator("Users",getResources().getDrawable(R.drawable.users));
        tSpecFolks.setContent(new DummyTabContent(getBaseContext()));
        tHost.addTab(tSpecFolks);
 
        /** Defining tab builder for Chats tab */
        TabHost.TabSpec tSpecChats = tHost.newTabSpec("chats");
        tSpecChats.setIndicator("Chats",getResources().getDrawable(R.drawable.chats));
        tSpecChats.setContent(new DummyTabContent(getBaseContext()));
        tHost.addTab(tSpecChats);
 
        /** Defining tab builder for History tab */
        TabHost.TabSpec tSpecHistory = tHost.newTabSpec("history");
        tSpecHistory.setIndicator("History",getResources().getDrawable(R.drawable.history));
        tSpecHistory.setContent(new DummyTabContent(getBaseContext()));
        tHost.addTab(tSpecHistory);
        
        /** Defining tab builder for Profile tab */
        TabHost.TabSpec tSpecProfile = tHost.newTabSpec("profile");
        tSpecProfile.setIndicator("Profile",getResources().getDrawable(R.drawable.profile));
        tSpecProfile.setContent(new DummyTabContent(getBaseContext()));
        tHost.addTab(tSpecProfile);
    }
    
    
	public WifiInfo getWiFiInfo() {
		WifiInfo wifiInfo = null;
		try {
			WifiManager wifiMgr = (WifiManager) getSystemService(Context.WIFI_SERVICE);
			wifiInfo = wifiMgr.getConnectionInfo();
		} catch (Exception e) {
			Log.i("Exception", "Exception in GetWiFiInfo"+e);
		}
		return wifiInfo;
	}
}
