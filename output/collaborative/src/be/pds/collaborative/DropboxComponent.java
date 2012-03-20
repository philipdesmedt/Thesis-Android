package be.pds.collaborative;


import java.util.HashMap;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import be.pds.thesis.AndroidComponent;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;

public class DropboxComponent extends AndroidComponent {

	final static private String ACCOUNT_PREFS_NAME = "prefs";
	final static private String ACCESS_KEY_NAME = "ACCESS_KEY";
	final static private String ACCESS_SECRET_NAME = "ACCESS_SECRET";
	final static private String APP_KEY = 1yb8hkeirab3iq9;
	final static private String APP_SECRET = "pk2w9wbnhnfh112";
	final static private AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
	private final String TAG = getClass().getName();
	
	private HashMap<String, Object> properties;
	private boolean loggedIn;
	DropboxAPI<AndroidAuthSession> api;
	
	private Button dropboxButton;
	private Button getContentButton;
	private ListView dirContent;
	
	private final String DIR = /Thesis/;
	
	public DropboxComponent(Activity a) {
		super(a);
		
		AndroidAuthSession session = buildSession();
		api = new DropboxAPI<AndroidAuthSession>(session);
		properties = new HashMap<String, Object>();
		dropboxButton = (Button) parent.findViewById(R.id.dropboxButton);
		getContentButton = (Button) parent.findViewById(R.id.dirContentButton);
		dirContent = (ListView) parent.findViewById(R.id.dirContent);
		
		dropboxButton.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				Log.i(TAG, "Clicked Dropbox Auth Button");
				if (loggedIn) {
					logout();
				} else {
					action(properties);
				}
			}
			
		});
		
		getContentButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				ListDropboxContent c = new ListDropboxContent(parent, api, DIR, dirContent);
				c.execute();
			}
			
		});
		
		dirContent.setOnItemClickListener(new AdapterView.OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> a, View v, int pos, long id) {
				String file = (String) dirContent.getItemAtPosition(pos);
				Log.i(TAG, "File " + file + " was clicked.");
				Intent i = new Intent(parent.getApplicationContext(), DropboxItemActivity.class);
				i.putExtra("file", file);
				try {
					i.putExtra("uid", api.accountInfo().uid);
				} catch (DropboxException e) { }
				parent.startActivityForResult(i, 0);
				
				// Write to a string:
				//try {
					//DropboxInputStream dis = api.getFileStream(file, null);
					//String c = new Scanner(dis).useDelimiter("\\A").next();
					//Log.i(TAG, c);
				//} catch (DropboxException e) { }
			}
			
		});
		
		setLoggedIn(api.getSession().isLinked());
	}
	
	public DropboxAPI<AndroidAuthSession> getApi() {
		return this.api;
	}
	
	@Override
	public void action(HashMap<String, Object> properties) {
		authenticate();
	}

	private void authenticate() {
		Log.i(TAG, "Authenticating Dropbox");
		api.getSession().startAuthentication(this.parent.getApplicationContext());
		Log.i(TAG, "Authenticated Dropbox");
	}
	
	private void logout() {
		api.getSession().unlink();
		clearKeys();
		setLoggedIn(false);
	}
	
	/**
	 * Convenience function to change UI state based on being logged in
	 */
	public void setLoggedIn(boolean loggedIn) {
		this.loggedIn = loggedIn;
		if (loggedIn) {
			dropboxButton.setText("Unlink from Dropbox");
			//mDisplay.setVisibility(View.VISIBLE);
		} else {
			dropboxButton.setText("Link with Dropbox");
			//mDisplay.setVisibility(View.GONE);
	        //mImage.setImageDrawable(null);
		}
	}
	
    /**
     * Shows keeping the access keys returned from Trusted Authenticator in a local
     * store, rather than storing user name & password, and re-authenticating each
     * time (which is not to be done, ever).
     *
     * @return Array of [access_key, access_secret], or null if none stored
     */
    private String[] getKeys() {
        SharedPreferences prefs = parent.getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
        String key = prefs.getString(ACCESS_KEY_NAME, null);
        String secret = prefs.getString(ACCESS_SECRET_NAME, null);
        if (key != null && secret != null) {
        	String[] ret = new String[2];
        	ret[0] = key;
        	ret[1] = secret;
        	return ret;
        } else {
        	return null;
        }
    }
    
	/**
	 * Shows keeping the access keys returned from Trusted Authenticator in a local
	 * store, rather than storing user name & password, and re-authenticating each
	 * time (which is not to be done, ever).
	 */
	public void storeKeys(String key, String secret) {
		SharedPreferences prefs = parent.getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
		Editor edit = prefs.edit();
		edit.putString(ACCESS_KEY_NAME, key);
		edit.putString(ACCESS_SECRET_NAME, secret);
		edit.commit();
	}
    
    private void clearKeys() {
    	SharedPreferences prefs = parent.getSharedPreferences(ACCOUNT_PREFS_NAME, 0);
    	Editor edit = prefs.edit();
    	edit.clear();
    	edit.commit();
    }
    
	private AndroidAuthSession buildSession() {
		AppKeyPair appKeyPair = new AppKeyPair(APP_KEY, APP_SECRET);
		AndroidAuthSession session;
		
		String[] stored = getKeys();
		if (stored != null) {
			AccessTokenPair accessToken = new AccessTokenPair(stored[0], stored[1]);
			session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE, accessToken);
		} else {
			session = new AndroidAuthSession(appKeyPair, ACCESS_TYPE);
		}
		
		return session;
	}
	
	public void onMessage(String message) { }
}
