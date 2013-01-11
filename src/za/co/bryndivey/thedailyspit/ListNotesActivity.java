package za.co.bryndivey.thedailyspit;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;

import android.os.Bundle;
import android.preference.PreferenceManager;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class ListNotesActivity extends Activity {

    DropboxAPI<AndroidAuthSession> mDBApi;

    public SharedPreferences sharedPreferences;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_list_notes);
		
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        mDBApi = new DropboxComms(sharedPreferences).getAPI(this);

        java.util.List<NoteFile> noteFiles = null;
        
        try {
        	noteFiles = NoteFileFactory.listLocalNoteFiles(mDBApi);
        } catch (Exception e) {
        	
        }
		
    	for(NoteFile nf : noteFiles) {
    		Log.e("notefile", nf.toString());
    	}
    	
		ArrayAdapter<NoteFile> adapter = new ArrayAdapter<NoteFile>(this,
				  android.R.layout.simple_list_item_1, android.R.id.text1, noteFiles);
		ListView listView = (ListView) findViewById(R.id.noteListView);
		listView.setAdapter(adapter);
		
		listView.setOnItemClickListener(new OnItemClickListener() {
			  @Override
			  public void onItemClick(AdapterView<?> parent, View view,
			    int position, long id) {
				  NoteFile noteFile = (NoteFile) parent.getItemAtPosition(position);
			        Intent intent = new Intent(view.getContext(), ViewActivity.class);
			        intent.putExtra("za.co.bryndivey.thedailyspit.notefile", noteFile);
			        startActivity(intent);
			  }
			}); 
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_list_notes, menu);
		return true;
	}

}
