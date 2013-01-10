package za.co.bryndivey.thedailyspit;

import java.io.File;

import android.os.Environment;

public class Util {

	public static File getStorageFile(String name) {
		return new File(getStorageRoot(), name);
	}
	
	public static File getStorageRoot() {
		return new File(Environment.getExternalStorageDirectory(),
						"data/za.co.bryndivey.thedailyspit/");
	}
}
