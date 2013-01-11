package za.co.bryndivey.thedailyspit;

import za.co.bryndivey.thedailyspit.NoteFileFactory;
import za.co.bryndivey.thedailyspit.DropboxComms;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.view.Menu;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import za.co.bryndivey.thedailyspit.R;

public class NoterMain extends Activity {
	private static final String TAG = "Noter";

    DropboxAPI<AndroidAuthSession> mDBApi;

    public SharedPreferences sharedPreferences;
    
    private boolean mLoggedIn;    
   
	private EditText noteText;
	
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
		setContentView(R.layout.activity_noter_main);
        noteText = (EditText) findViewById(R.id.noteText);
        
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mDBApi = new DropboxComms(sharedPreferences).getAPI(this);
    }

	protected void onResume() {
        super.onResume();
        new DropboxComms(sharedPreferences).finishAuthentication(mDBApi);
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.activity_noter_main, menu);
        return true;
    }
    
    public void postClickHandler(View view) {
    	NoteFile noteFile = NoteFileFactory.getNoteFileForToday(mDBApi);
    	
    	try {
    		noteFile.appendNote(noteText.getText().toString());
    		noteFile.pushToDropbox();
    		noteText.getText().clear();
    		Toast.makeText(this, "Saved", Toast.LENGTH_SHORT).show();
    	} catch (Exception e) {
    		Toast.makeText(this, "SOMETHING WENT WRONG " + e.toString(), Toast.LENGTH_LONG).show();
    	}
    }
    
	public void viewClickHandler(View v) {
		Intent intent = new Intent(v.getContext(), ListNotesActivity.class);
		startActivity(intent);
	}
    
}
