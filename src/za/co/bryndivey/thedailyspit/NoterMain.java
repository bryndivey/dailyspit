package za.co.bryndivey.thedailyspit;

import za.co.bryndivey.thedailyspit.NoteFile;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.*;
//import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import za.co.bryndivey.thedailyspit.R;

public class NoterMain extends Activity {
	private static final String TAG = "Noter";
	
    final static private String APP_KEY = "gv2dp8zg8jran11";
    final static private String APP_SECRET = "zrq0u1kr7ndz8ng";

    //final static private AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
    final static private String ACCOUNT_PREFS_NAME = "prefs";
    final static private String ACCESS_KEY_NAME = "ACCESS_KEY";
    final static private String ACCESS_SECRET_NAME = "ACCESS_SECRET";

    DropboxAPI<AndroidAuthSession> mDBApi;

    private boolean mLoggedIn;    

    public SharedPreferences sharedPreferences; 
    
	private EditText noteText;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        
        setContentView(R.layout.activity_noter_main);
        noteText = (EditText) findViewById(R.id.noteText);
        
        AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys, AccessType.APP_FOLDER);
        mDBApi = new DropboxAPI<AndroidAuthSession>(session);
        
        AccessTokenPair token = getStoredKeys();
    	if (token != null) {
    		mDBApi.getSession().setAccessTokenPair(token);
    	} else {
    		mDBApi.getSession().startAuthentication(NoterMain.this);
    	}
    }

	void storeKeys(String accessTokenKey, String accessTokenSecret) {
		Editor editor = sharedPreferences.edit();
		editor.putString(ACCESS_KEY_NAME, accessTokenKey);
		editor.putString(ACCESS_SECRET_NAME, accessTokenSecret);
		editor.commit();
	}
	
	private AccessTokenPair getStoredKeys() {
		String key = null;
		String secret = null;

		key = sharedPreferences.getString(ACCESS_KEY_NAME, null);
		secret = sharedPreferences.getString(ACCESS_SECRET_NAME, null);
		
		if (key != null && secret != null) {
			return new AccessTokenPair(key, secret);
		}
		return null;
	}
    
    protected void onResume() {
        super.onResume();

        if (mDBApi.getSession().authenticationSuccessful()) {
            try {
                // MANDATORY call to complete auth.
                // Sets the access token on the session
                mDBApi.getSession().finishAuthentication();

                AccessTokenPair tokens = mDBApi.getSession().getAccessTokenPair();

                // Provide your own storeKeys to persist the access token pair
                // A typical way to store tokens is using SharedPreferences
                storeKeys(tokens.key, tokens.secret);
                mLoggedIn = true;
            } catch (IllegalStateException e) {
                Log.i("DbAuthLog", "Error authenticating", e);
            }
        }
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_noter_main, menu);
        return true;
    }
    
    public void postClickHandler(View view) {
    	NoteFile noteFile = new NoteFile(mDBApi);
    	
    	try {
    		noteFile.appendNote(noteText.getText().toString());
    		noteFile.uploadFile();
    		noteText.getText().clear();
    	} catch (Exception e) {
    		Toast.makeText(this, "SOMETHING WENT WRONG " + e.toString(), Toast.LENGTH_LONG).show();
    	}
    	
    	//Toast.makeText(this, "You entered text: " + noteText.getText(), Toast.LENGTH_LONG).show();
    }
    
	public void viewClickHandler(View v) {
		Intent intent = new Intent(v.getContext(), ViewActivity.class);
		startActivity(intent);
	}
    
}
