package za.co.bryndivey.thedailyspit;

import java.io.File;
import java.io.FilenameFilter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.android.AndroidAuthSession;

public class NoteFileFactory {
	public static NoteFile getNoteFileForToday(DropboxAPI<AndroidAuthSession> api) {
		return new NoteFile(api, new Date());
	}
	
	public static NoteFile getNoteFileForDate(DropboxAPI<AndroidAuthSession> api, Date date) {
		return new NoteFile(api, date);
	}
	
	public static List<NoteFile> listLocalNoteFiles(DropboxAPI<AndroidAuthSession> api) throws ParseException {
		FilenameFilter filter = new FilenameFilter() {
			public boolean accept(File directory, String filename) {
				return filename.matches("[0-9]{4}-[0-9]{2}-[0-9]{2}.txt");
			}
		};
		
		List<NoteFile> noteFiles = new ArrayList<NoteFile>();
		for(String f : Util.getStorageRoot().list(filter)) {
			noteFiles.add(new NoteFile(api, f.replace(".txt", "")));
		}
		return noteFiles;
	}
}
