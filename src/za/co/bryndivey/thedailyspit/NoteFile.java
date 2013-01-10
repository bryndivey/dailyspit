package za.co.bryndivey.thedailyspit;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.dropbox.client2.DropboxAPI;
import com.dropbox.client2.DropboxAPI.DropboxFileInfo;
import com.dropbox.client2.DropboxAPI.Entry;
import com.dropbox.client2.android.AndroidAuthSession;
import com.dropbox.client2.exception.DropboxException;
import com.dropbox.client2.exception.DropboxUnlinkedException;

class NoteFile {
	private static final String TAG = NoteFile.class.getSimpleName();
	
	private String dayFileName;
	File noteFile;

	private DropboxAPI<AndroidAuthSession> mDBApi;
	
	NoteFile(DropboxAPI<AndroidAuthSession> api) {
		this(api, new Date());
	}
	
	NoteFile(DropboxAPI<AndroidAuthSession> api, Date day)  {
		mDBApi = api;
		dayFileName = getDayFileName(day); 
		noteFile = new File(Environment.getExternalStorageDirectory(),
							"data/za.co.bryndivey.thedailyspit/" + dayFileName);		
	}
	
	private String getDayFileName(Date day) {
		DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
		return df.format(day) + ".txt";		
	}
	
	public void init() throws Exception{
		try {
			if (!noteFile.exists()) {
				createParentDirectory(noteFile);
				noteFile.createNewFile();
			}
		} catch (IOException e) {
			throw new Exception("Error initializing LocalFile", e);
		}
	}

	public void purge() {
		noteFile.delete();
	}
	
	public static void createParentDirectory(File dest) throws Exception {
		File dir = dest.getParentFile();
		if (dir != null && !dir.exists()) {
			createParentDirectory(dir);
		}
		if (!dir.exists()) {
			if (!dir.mkdirs()) {
				Log.e(TAG, "Could not create dirs: " + dir.getAbsolutePath());
				throw new Exception("Could not create dirs: "
						+ dir.getAbsolutePath());
			}
		}
	}

	public String load() throws Exception {
		init();
		if (!noteFile.exists()) {
			Log.w(TAG, noteFile.getAbsolutePath() + " does not exist!");
			throw new Exception(noteFile.getAbsolutePath()
					+ " does not exist!");
		} else {
			try {
				return loadFromFile(noteFile);
			} catch (IOException e) {
				throw new Exception("Error loading from local file", e);
			}
		}
	}
	
	public String loadFromFile(File file)
			throws IOException {
		
		StringBuffer fileData = new StringBuffer();
		InputStream is = null;
		BufferedReader in = null;

		try {	
			is = new FileInputStream(file);
			in = new BufferedReader(new InputStreamReader(is));
			
			char[] buf = new char[1024];
			int numRead=0;
			
	        while((numRead=in.read(buf)) != -1){
	            String readData = String.valueOf(buf, 0, numRead);
	            fileData.append(readData);
	        }
		} finally {
			if (in != null) {
				in.close();
			}
			if (is != null) {
				is.close();
			}
		}
	    return fileData.toString();
	}
	
	public boolean noteFileModifiedSince(Date date) {
		long date_ms = 0l;
		if (date != null) {
			date_ms = date.getTime();
		}
		return date_ms < noteFile.lastModified();
	}
	
	public void appendNote(String note) {
		// append a new note with date time to notes file
		try {
			FileWriter fw = new FileWriter(noteFile, true);
			Date now = new Date();
			
			fw.write(now.toString() + "\n");
			fw.write(note + "\n\n");
			fw.close();
		} catch (IOException e) {
			Log.e(TAG, e.getMessage());
		}
		
	}
	
	public void pushToDropbox() {
		if(mDBApi == null) {
			Log.e(TAG, "No Dropbox API available");
			return;
		}
		
		class UploadFileTask extends AsyncTask<File, Void, Boolean> {
			protected Boolean doInBackground(File... files) {
				FileInputStream inputStream = null;
				try {
				    inputStream = new FileInputStream(noteFile);
				    Entry newEntry = mDBApi.putFileOverwrite("/" + dayFileName, inputStream,
				            files[0].length(), null);
				    Log.e("DbExampleLog", "The uploaded file's rev is: " + newEntry.rev);
				} catch (DropboxUnlinkedException e) {
				    // User has unlinked, ask them to link again here.
				    Log.e("DbExampleLog", "User has unlinked.");
				} catch (DropboxException e) {
				    Log.e("DbExampleLog", "Something went wrong while uploading.");
				} catch (FileNotFoundException e) {
				    Log.e("DbExampleLog", "File not found.");
				} finally {
				    if (inputStream != null) {
				        try {
				            inputStream.close();
				        } catch (IOException e) {}
				    }
				}
				return true;
			}
		}
		new UploadFileTask().execute(noteFile);
	}
	
	public void pullFromDropbox() {
		if(mDBApi == null) {
			Log.e(TAG, "No Dropbox API available");
			return;
		}
		
		class DownloadFileTask extends AsyncTask<Void, Void, Boolean> {
			protected Boolean doInBackground(Void... nothing) {
				FileOutputStream outputStream = null;
				try {
				    outputStream = new FileOutputStream(noteFile);
				    DropboxFileInfo info = mDBApi.getFile("/" + dayFileName, null, outputStream, null);
				    Log.i("DbExampleLog", "The file's rev is: " + info.getMetadata().rev);
				    // /path/to/new/file.txt now has stuff in it.
				} catch (DropboxException e) {
				    Log.e("DbExampleLog", "Something went wrong while downloading.");
				} catch (FileNotFoundException e) {
				    Log.e("DbExampleLog", "File not found.");
				} finally {
				    if (outputStream != null) {
				        try {
				            outputStream.close();
				        } catch (IOException e) {}
				    }
				}
				return true;
			}
		}
		new DownloadFileTask().execute();
	}

}
