package za.co.bryndivey.thedailyspit;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.session.AccessTokenPair;
import com.dropbox.client2.session.AppKeyPair;
import com.dropbox.client2.session.Session.AccessType;

import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class DropboxComms {
	
    final static private String APP_KEY = "gv2dp8zg8jran11";
    final static private String APP_SECRET = "zrq0u1kr7ndz8ng";

    //final static private AccessType ACCESS_TYPE = AccessType.APP_FOLDER;
    final static private String ACCOUNT_PREFS_NAME = "prefs";
    final static private String ACCESS_KEY_NAME = "ACCESS_KEY";
    final static private String ACCESS_SECRET_NAME = "ACCESS_SECRET";
    
    private SharedPreferences sharedPreferences;
    
    DropboxComms(SharedPreferences sp) {
    	sharedPreferences = sp;        
    }
    
    DropboxAPI<AndroidAuthSession> getAPI(Activity activity) {
    	AppKeyPair appKeys = new AppKeyPair(APP_KEY, APP_SECRET);
        AndroidAuthSession session = new AndroidAuthSession(appKeys, AccessType.APP_FOLDER);
        DropboxAPI<AndroidAuthSession> mDBApi = new DropboxAPI<AndroidAuthSession>(session);
        
        AccessTokenPair token = getStoredKeys();
    	if (token != null) {
    		mDBApi.getSession().setAccessTokenPair(token);
    	} else {
    		mDBApi.getSession().startAuthentication(activity);
    	}
    	return mDBApi;
    }
    
	void storeKeys(String accessTokenKey, String accessTokenSecret) {
		Editor editor = sharedPreferences.edit();
		editor.putString(ACCESS_KEY_NAME, accessTokenKey);
		editor.putString(ACCESS_SECRET_NAME, accessTokenSecret);
		editor.commit();
	}
	
	AccessTokenPair getStoredKeys() {
		String key = null;
		String secret = null;
		
		key = sharedPreferences.getString(ACCESS_KEY_NAME, null);
		secret = sharedPreferences.getString(ACCESS_SECRET_NAME, null);
		
		if (key != null && secret != null) {
			return new AccessTokenPair(key, secret);
		}
		return null;
	}     
	
	void finishAuthentication(DropboxAPI<AndroidAuthSession> mDBApi) {
		if (mDBApi.getSession().authenticationSuccessful()) {
            try {
                // MANDATORY call to complete auth.
                // Sets the access token on the session
                mDBApi.getSession().finishAuthentication();

                AccessTokenPair tokens = mDBApi.getSession().getAccessTokenPair();

                // Provide your own storeKeys to persist the access token pair
                // A typical way to store tokens is using SharedPreferences
                storeKeys(tokens.key, tokens.secret);
                Log.i("DbAuthLog", "Authenticated");
            } catch (IllegalStateException e) {
                Log.i("DbAuthLog", "Error authenticating", e);
            }
        }

	}
    
}
