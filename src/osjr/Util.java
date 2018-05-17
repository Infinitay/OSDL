package osjr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;

import org.apache.commons.io.FileUtils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonIOException;
import com.google.gson.JsonSyntaxException;

import deob.Deob;

public class Util {
	
	static File vanillaJar = new File("vanilla.jar");
	static File deobJar = new File("deob.jar");
	
	private static int getFileSize(URL url) {
	    URLConnection conn = null;
	    try {
	        conn = url.openConnection();
	        if(conn instanceof HttpURLConnection) {
	            ((HttpURLConnection)conn).setRequestMethod("HEAD");
	        }
	        conn.getInputStream();
	        return conn.getContentLength();
	    } catch (IOException e) {
	        throw new RuntimeException(e);
	    } finally {
	        if(conn instanceof HttpURLConnection) {
	            ((HttpURLConnection)conn).disconnect();
	        }
	    }
	}
	
	public static void checkUpdate() {
		try {
			URL gamepackURL = new URL(JarLoader.gamepackUrl);
			if (OSJR.settings.gamepackSize!=getFileSize(gamepackURL)) {
				System.out.println("--Update Required--");
				FileUtils.copyURLToFile(gamepackURL, vanillaJar);
				OSJR.settings.gamepackSize = getFileSize(gamepackURL);
				Util.saveSettings();
				System.out.println("---Deobfuscating updated gmaepack---");
				Deob.main(vanillaJar.getName(), deobJar.getName());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static Settings loadSettings() {
		Gson gson = new Gson();
		File settingsFile = new File("Settings.json");
		if (settingsFile.exists()) {
			try {
				return gson.fromJson(new FileReader(settingsFile), Settings.class);
			} catch (JsonSyntaxException | JsonIOException | FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return null;
	}
	
	public static void saveSettings() {
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		File settingsFile = new File("Settings.json");
		if (!settingsFile.exists()) {
			try {
				settingsFile.createNewFile();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		try {
			FileWriter writer = new FileWriter(settingsFile);
			gson.toJson(OSJR.settings, writer);
			writer.close();
			System.out.println("--Settings Updated--");
		} catch (JsonIOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
}
