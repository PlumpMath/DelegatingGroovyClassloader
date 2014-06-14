package it.fago.groovy;

import java.io.File;
import java.io.RandomAccessFile;
import java.util.HashMap;
import java.util.Map;

public abstract class Utils {

	private Utils() {
	}

	public static final <T> T[] array(T... objects) {
		if (objects == null) {
			return null;
		}
		return objects;
	}

	public static String fromFile(File scriptFile) {
		try {
			RandomAccessFile raf = new RandomAccessFile(scriptFile, "rw");
			final int len = (int) raf.length();
			byte[] buffer = new byte[len];
			raf.readFully(buffer);
			raf.close();
			return new String(buffer);
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Map<String, String> map() {
		HashMap<String, String> map = new HashMap<String, String>();
		for (int i = 0; i < 10; i++) {
			map.put("item" + i, String.valueOf(i));
		}
		return map;
	}

}// END
