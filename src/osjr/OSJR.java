package osjr;

import java.applet.Applet;

import javax.swing.JFrame;

public class OSJR {
	
	public static boolean vanilla = false;
	public static Settings settings;
	public static JarLoader jarLoader;

	public static void main(String args[]) {
		if (Util.loadSettings()==null) {
			settings = new Settings();
		} else {
			settings = Util.loadSettings();
		}
		
		JFrame osFrame = new JFrame("Old School Runescape");
		osFrame.setSize(800, 600);
		
		jarLoader = new JarLoader();
		try {
			Applet gameApplet = (Applet) jarLoader.classLoader.loadClass("client").newInstance();
			gameApplet.setSize(800, 600);
			gameApplet.setStub(jarLoader.appleStub);
			gameApplet.init();
			osFrame.add(gameApplet);
			osFrame.setVisible(true);
			osFrame.requestFocus();
		} catch (InstantiationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
