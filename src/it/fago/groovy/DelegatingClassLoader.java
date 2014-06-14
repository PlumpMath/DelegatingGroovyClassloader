package it.fago.groovy;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.Enumeration;

public class DelegatingClassLoader extends URLClassLoader {
	//
	private static URL[] NO_URL = Utils.<URL> array();
	//
	private URLClassLoader groovyLoaderLoader;
	//
	private URLClassLoader groovyLoader;
	//
	private Method ADD_URL;
	//
	private Method LOADED_CLASSES;
	//
	private Method PARSE_CLASS;
	//
	private URL[] groovyLibraries;

	public DelegatingClassLoader() {
		super(NO_URL, (ClassLoader) null);
	}

	public void init(URL[] groovyLibraries) {
		this.groovyLibraries = groovyLibraries;
		groovyLoaderLoader = new URLClassLoader(groovyLibraries,
				(ClassLoader) null);
		groovyLoader = createLoader();
	}

	public void addURL(URL url) {
		try {
			ADD_URL.invoke(groovyLoader, Utils.<Object> array(url));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@SuppressWarnings("unchecked")
	public <T> Class<? extends T> generateClassFromScript(String script)
			throws Exception {
		return (Class<? extends T>) PARSE_CLASS.invoke(groovyLoader,
				Utils.<Object> array(script));
	}

	public void destroy() {
		try {
			cleanGroovyCacheAndMetaclasses();
		} catch (Exception e) {
			e.printStackTrace();
		}
		groovyLoader = null;
		groovyLoaderLoader = null;
		ADD_URL = LOADED_CLASSES = PARSE_CLASS = null;
		Arrays.fill(groovyLibraries, null);
		groovyLibraries = null;
	}

	public void resetTotally() {
		try {
			cleanGroovyCacheAndMetaclasses();
		} catch (Exception e) {
			e.printStackTrace();
		}
		groovyLoader = null;
		groovyLoaderLoader = null;
		groovyLoaderLoader = new URLClassLoader(groovyLibraries,
				(ClassLoader) null);
		groovyLoader = createLoader();
	}

	public void resetPartially() {
		try {
			cleanGroovyCacheAndMetaclasses();
		} catch (Exception e) {
			e.printStackTrace();
		}
		groovyLoader = null;
		groovyLoader = createLoader();
	}

	// ============================================================
	//
	//
	//
	// ============================================================

	protected Class<?> findClass(String name) throws ClassNotFoundException {
		return groovyLoader.loadClass(name);
	}

	public URL findResource(final String name) {
		return groovyLoader.findResource(name);
	}

	public Enumeration<URL> findResources(final String name) throws IOException {
		return groovyLoader.getResources(name);
	}

	// ============================================================
	//
	//
	//
	// ============================================================

	private URLClassLoader createLoader() {
		try {
			Class<?> gl = groovyLoaderLoader
					.loadClass("groovy.lang.GroovyClassLoader");
			Object result = gl.getConstructor(
					Utils.<Class<?>> array(ClassLoader.class)).newInstance(
					Thread.currentThread().getContextClassLoader());
			ADD_URL = gl.getMethod("addURL", Utils.<Class<?>> array(URL.class));
			LOADED_CLASSES = gl
					.getMethod("getLoadedClasses", (Class<?>[]) null);
			PARSE_CLASS = gl.getMethod("parseClass",
					Utils.<Class<?>> array(String.class));
			gl = null;
			return (URLClassLoader) result;
		} catch (Exception exc) {
			exc.printStackTrace();
			return null;
		}
	}

	private void cleanGroovyCacheAndMetaclasses() throws Exception {
		Class<?>[] loadedClasses = (Class[]) LOADED_CLASSES.invoke(
				groovyLoader, (Object[]) null);
		int len = loadedClasses.length;
		Class<?> loadClass = groovyLoaderLoader
				.loadClass("groovy.lang.GroovySystem");
		Object registry = loadClass.getMethod("getMetaClassRegistry",
				(Class<?>[]) null).invoke(null, (Object[]) null);
		Class<?> registryClass = registry.getClass();
		Method regMeth = registryClass
				.getMethod("removeMetaClass", Class.class);
		for (int i = 0; i < len; i++) {
			regMeth.invoke(registry, loadedClasses[i]);
		}
		Arrays.fill(loadedClasses, null);
		NO_URL = null;
		loadedClasses = null;
		loadClass = null;
		registry = null;
		registryClass = null;
		regMeth = null;
	}

}// END