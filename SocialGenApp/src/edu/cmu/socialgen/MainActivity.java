package edu.cmu.socialgen;

import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.widget.TabHost;
import edu.cmu.socialgen.tab.ChatsFragment;
import edu.cmu.socialgen.tab.DummyTabContent;
import edu.cmu.socialgen.tab.FolksFragment;
import edu.cmu.socialgen.tab.HistoryFragment;
import edu.cmu.socialgen.tab.ProfileFragment;


public class MainActivity extends FragmentActivity {

	TabHost tHost;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {

        try {
        	ControlComManager CM = new ControlComManager(this.getWiFiInfo());
        	new Thread(CM).start();
        } catch (Exception e) {
        	/* if getWiFiInfo() fails, stop the application! */
        }
        
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
 
        tHost = (TabHost) findViewById(android.R.id.tabhost);
        tHost.setup();
 
        /** Defining Tab Change Listener event. This is invoked when tab is changed */
        TabHost.OnTabChangeListener tabChangeListener = new TabHost.OnTabChangeListener() {
            @Override
            public void onTabChanged(String tabId) {
                android.support.v4.app.FragmentManager fm =   getSupportFragmentManager();
                FolksFragment folksFragment = (FolksFragment) fm.findFragmentByTag("folks");
                ChatsFragment chatsFragment = (ChatsFragment) fm.findFragmentByTag("chats");
                HistoryFragment historyFragment = (HistoryFragment) fm.findFragmentByTag("history");
                ProfileFragment profileFragment = (ProfileFragment) fm.findFragmentByTag("profile");
                android.support.v4.app.FragmentTransaction ft = fm.beginTransaction();
 
                if(folksFragment!=null)
                    ft.detach(folksFragment);
 
                if(chatsFragment!=null)
                    ft.detach(chatsFragment);
 
                if(historyFragment!=null)
                    ft.detach(historyFragment);
                
                if(profileFragment!=null)
                    ft.detach(profileFragment);
                
                if(tabId.equalsIgnoreCase("folks")){
                    if(folksFragment==null){
                        ft.add(R.id.realtabcontent, new FolksFragment(), "folks");
                    }else{
                        ft.attach(folksFragment);
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
        TabHost.TabSpec tSpecFolks = tHost.newTabSpec("folks");
        tSpecFolks.setIndicator("Folks",getResources().getDrawable(R.drawable.folks));
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
