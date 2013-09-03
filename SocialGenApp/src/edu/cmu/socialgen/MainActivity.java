package edu.cmu.socialgen;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.util.Log;
import android.view.Menu;

public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        try {
        	ControlComManager CM = new ControlComManager(this.getWiFiInfo());
        	new Thread(CM).start();
        } catch (Exception e) {
        	/* if getWiFiInfo() fails, stop the application! */
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
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
