package za.co.bryndivey.thedailyspit;

import java.util.List;

import za.co.bryndivey.thedailyspit.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Toast;

public class ViewActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view);
		//final ScrollView scrollView = (ScrollView) findViewById(R.id.scrollNoteView);
	    
        Bundle bundle = getIntent().getExtras();
        NoteFile noteFile = bundle.getParcelable("za.co.bryndivey.thedailyspit.notefile");
    	
    	try {
    		List<Note> notes = noteFile.loadNotes();
    		ArrayAdapter<Note> adapter = new ArrayAdapter<Note>(this,
  				  android.R.layout.simple_list_item_1, android.R.id.text1, notes);
    		ListView listView = (ListView) findViewById(R.id.listView);
    		listView.setAdapter(adapter);
  		
    		//scrollView.post(new Runnable() {
    		//	public void run() {
    		//		scrollView.scrollTo(0, 100000);
    		//	}
    		//});
    	} catch (Exception e) {
    		Toast.makeText(this, "SOMETHING WENT WRONG " + e.toString(), Toast.LENGTH_LONG).show();
    	}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.activity_view, menu);
		return true;
	}
	
	public void postClickHandler(View v) {
		Intent intent = new Intent(v.getContext(), NoterMain.class);
		startActivity(intent);
	}

}
