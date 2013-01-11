package za.co.bryndivey.thedailyspit;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.util.Log;

public class Note {
	public Date date;
	public String string;
	
	Note(Date d, String s) {
		date = d;
		string = s;
	}
	
	Note(String s) {
		Log.e("note", s);
		String dateString = s.split("\n")[0];
		String rest = s.replace(dateString + "\n", "");
		
		try {
			date = new SimpleDateFormat("EEE MMM dd HH:mm:ss zzzz yyyy").parse(dateString);
		} catch (ParseException e) {
			date = null;
		}
		string = rest;
	}
	
	public String toString() {
		return date.toString() + "\n" + string;
	}
	
}
