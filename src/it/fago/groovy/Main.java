package it.fago.groovy;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;

import sample.module.RenderingModule;

public class Main {

	public static void main(String[] args) throws Exception {
		URL[] groovyLibs = new URL[] { new File(
				"libraries/groovy-all-2.2.2.jar").toURI().toURL() };

		// testDirectUseOfLoader(groovyLibs);
		// testLoaderTotalReset(groovyLibs);
		// testLoaderPartialReset(groovyLibs);

		groovyLibs = null;
		waitToEndAndTryToCleanEnv();
	}

	// =============================================================
	//
	//
	//
	// =============================================================

	/**
	 * @throws Exception
	 */
	private static void testDirectUseOfLoader(URL[] groovyLibs)
			throws Exception {

		String script = Utils.fromFile(new File("libraries/module1.groovy"));
		System.out.println("SCRIPT:\n" + script + "\n");

		DelegatingClassLoader loader = new DelegatingClassLoader();
		loader.init(groovyLibs);

		Map<String, String> info = Utils.map();

		URL[] libs = new URL[] { new File("libraries/mod_lib.jar").toURI().toURL() };

		loader.addURL(libs[0]);

		Class<? extends RenderingModule> clz = loader
				.<RenderingModule> generateClassFromScript(script);
		System.out.println("Generated Class: " + clz);

		RenderingModule mod = clz.newInstance();
		mod.metadata(info);

		System.out.println(mod.render());

		clz = (Class<? extends RenderingModule>) loader
				.loadClass("sample.module.SampleModule");
		System.out.println("Loaded Class: " + clz);
		mod = clz.newInstance();
		mod.metadata(info);

		System.out.println(mod.render());

		loader.destroy();
		Arrays.fill(libs, null);
		info.clear();
		info = null;
		loader = null;
		libs = null;
		script = null;
	}

	/**
	 * @throws Exception
	 */
	private static void testLoaderTotalReset(URL[] groovyLibs) throws Exception {

		String script = Utils.fromFile(new File("libraries/module1.groovy"));
		System.out.println("SCRIPT: module1 ");

		DelegatingClassLoader loader = new DelegatingClassLoader();
		loader.init(groovyLibs);

		Map<String, String> info = Utils.map();

		URL[] libs = new URL[] { new File("libraries/mod_lib.jar").toURI().toURL() };

		loader.addURL(libs[0]);

		Class<? extends RenderingModule> clz = loader
				.<RenderingModule> generateClassFromScript(script);
		System.out.println("Generated Class: " + clz);

		RenderingModule mod = clz.newInstance();
		mod.metadata(info);

		System.out.println(mod.render());

		loader.resetTotally();
		blockingGC();

		script = Utils.fromFile(new File("libraries/module2.groovy"));
		System.out.println("SCRIPT: module2 ");

		loader.addURL(libs[0]);

		clz = loader.<RenderingModule> generateClassFromScript(script);
		System.out.println("Generated Class: " + clz);

		clz = (Class<? extends RenderingModule>) loader
				.loadClass("sample.module.SampleModule");
		System.out.println("Loaded Class: " + clz);
		mod = clz.newInstance();
		mod.metadata(info);

		System.out.println(mod.render());

		loader.destroy();
		Arrays.fill(libs, null);
		info.clear();
		info = null;
		loader = null;
		libs = null;
		script = null;
	}

	/**
	 * @throws Exception
	 */
	private static void testLoaderPartialReset(URL[] groovyLibs)
			throws Exception {

		String script = Utils.fromFile(new File("libraries/module1.groovy"));
		System.out.println("SCRIPT: module1");

		DelegatingClassLoader loader = new DelegatingClassLoader();
		loader.init(groovyLibs);

		Map<String, String> info = Utils.map();

		URL[] libs = new URL[] { new File("libraries/mod_lib.jar").toURI().toURL() };

		loader.addURL(libs[0]);

		part1(script, loader, info);
		blockingGC();
		blockingGC();
		
		System.out.println("SCRIPT: module2 ");
		script = Utils.fromFile(new File("libraries/module2.groovy"));
		loader.addURL(libs[0]);

		part2(script, loader, info);

		loader.destroy();
		Arrays.fill(libs, null);
		info.clear();
		info = null;
		loader = null;
		libs = null;
		script = null;
	}

	// =============================================================
	//
	//
	//
	// =============================================================

	/**
	 * @param script
	 * @param loader
	 * @param info
	 * @throws Exception
	 * @throws ClassNotFoundException
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private static void part2(String script, DelegatingClassLoader loader,
			Map<String, String> info) throws Exception, ClassNotFoundException,
			InstantiationException, IllegalAccessException {
		Class<? extends RenderingModule> clz;
		RenderingModule mod;
		clz = loader.<RenderingModule> generateClassFromScript(script);
		System.out.println("Generated Class: " + clz);

		clz = (Class<? extends RenderingModule>) loader
				.loadClass("sample.module.SampleModule");
		System.out.println("Loaded Class: " + clz);
		mod = clz.newInstance();
		mod.metadata(info);

		System.out.println(mod.render());
	}

	/**
	 * @param script
	 * @param loader
	 * @param info
	 * @throws Exception
	 * @throws InstantiationException
	 * @throws IllegalAccessException
	 */
	private static void part1(String script, DelegatingClassLoader loader,
			Map<String, String> info) throws Exception, InstantiationException,
			IllegalAccessException {
		Class<? extends RenderingModule> clz = loader
				.<RenderingModule> generateClassFromScript(script);
		System.out.println("Generated Class: " + clz);

		RenderingModule mod = clz.newInstance();
		mod.metadata(info);

		System.out.println(mod.render());
		loader.resetPartially();
	}

	// =============================================================
	//
	//
	//
	// =============================================================

	private static void waitToEndAndTryToCleanEnv() {

		blockingGC();
		System.out.println("ANY KEY TO END...");
		try {
			System.in.read();
		} catch (IOException e) {
			e.printStackTrace();
		}
		blockingGC();
	}

	private static void blockingGC() {
		int c = 30;
		while (c-- > 0) {
			System.gc();
		}
	}

}// END