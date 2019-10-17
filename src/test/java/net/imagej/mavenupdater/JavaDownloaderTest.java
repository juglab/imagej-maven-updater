package net.imagej.mavenupdater;

import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;

import static junit.framework.TestCase.assertTrue;

public class JavaDownloaderTest {

	@Rule
	public TemporaryFolder destination = new TemporaryFolder();

	@Test
	@Ignore // since this downloads the whole JRE, I don't want to run it all the time
	public void downloadJava() throws IOException {
		new MavenInstaller().downloadJava(destination.getRoot());
		assertTrue(new File(destination.getRoot(), "java/linux-amd64/jdk1.8.0_172/jre").exists());
		assertTrue(new File(destination.getRoot(), "java/linux-amd64/jdk1.8.0_172/jre/plugin").exists());
		assertTrue(new File(destination.getRoot(), "java/linux-amd64/jdk1.8.0_172/jre/man").exists());
		assertTrue(new File(destination.getRoot(), "java/linux-amd64/jdk1.8.0_172/jre/lib").exists());
		assertTrue(new File(destination.getRoot(), "java/linux-amd64/jdk1.8.0_172/jre/bin").exists());
		assertTrue(new File(destination.getRoot(), "java/linux-amd64/jdk1.8.0_172/jre/bin/java").exists());
	}
}
