package za.co.bryndivey.thedailyspit;

import za.co.bryndivey.thedailyspit.R;

import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.Menu;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

public class ViewActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_view);
        TextView textView = (TextView) findViewById(R.id.textView);
        
    	NoteFile noteFile = new NoteFile();
    	
    	try {
    		textView.setText(noteFile.load());
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
