package net.imagej.mavenupdater;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class InteractiveMavenInstaller {

	public static void main(String...args) throws IOException {
		//create dummy files simulating existing installation
		Path source = Files.createTempDirectory("source");
		File jarsFolder = new File(source.toFile(), "jars");
		File javaFolder = new File(source.toFile(), "java");
		jarsFolder.mkdirs();
		javaFolder.mkdirs();
		Path destination = Files.createTempDirectory("destination");
		new File(jarsFolder, "lib.jar").createNewFile();
		new File(javaFolder, "release").createNewFile();

		//init updater
		MavenInstaller installer = new MavenInstaller();
		installer.install(source.toFile(), destination.toFile());
	}
}
