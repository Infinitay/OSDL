package osjr;

import java.io.File;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.tree.ClassNode;

import deob.clientver.ClientVersionMain;
import osjr.RSAppletStub;

public class JarLoader extends ClassLoader {

	public RSAppletStub appleStub;
	public ClassLoader classLoader;
	private Hashtable<String, ClassNode> classnodes;

	public static String gamepackUrl;

	public JarLoader() {
		appleStub = new RSAppletStub();
		classnodes = new Hashtable<String, ClassNode>();
		gamepackUrl = appleStub.getLink() + appleStub.getParameter("initial_jar");
		Util.checkUpdate();
		
		loadJar();

		JarInjector injector = new JarInjector(classnodes);
		injector.run();
		classnodes = injector.getClassnodes();

		try {
			classLoader = URLClassLoader.newInstance(new URL[] { injector.getInjectedJar().toURI().toURL() });
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	public RSAppletStub getAppletStub() {
		return appleStub;
	}

	public Hashtable<String, ClassNode> getClassnodes() {
		return classnodes;
	}

	@Override
	public Class<?> loadClass(String name) throws ClassNotFoundException {
		return classLoader.loadClass(name);
	}

	private void loadJar() {
		try {
			JarFile jar = new JarFile(new File("deob.jar"));
			System.out.println("Loaded Deob Revision " + ClientVersionMain.getClientVersion(new File(jar.getName())));
			Enumeration<JarEntry> en = jar.entries();
			while (en.hasMoreElements()) {
				JarEntry entry = en.nextElement();
				if (entry.getName().endsWith(".class")) {
					ClassReader cr = new ClassReader(jar.getInputStream(entry));
					ClassNode cn = new ClassNode();
					cr.accept(cn, ClassReader.SKIP_DEBUG | ClassReader.SKIP_FRAMES);
					classnodes.put(cn.name, cn);
				}
			}
			jar.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
}