package net.imagej.mavenupdater;

import net.imagej.mavenupdater.model.UpdateSite;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class MavenInstallerTest {

	@Rule
	public TemporaryFolder sourceFolder = new TemporaryFolder();
	@Rule
	public TemporaryFolder destinationFolder = new TemporaryFolder();

	@Test
	public void install() throws Exception {

		//init installer
		MavenInstaller installer = new MavenInstaller();

		installer.setBaseDir(destinationFolder.getRoot());

		//test if a new git branch got initialized
		assertEquals("base", installer.getVersioning().getCurrentSession().toString());

		File[] files = destinationFolder.getRoot().listFiles();
		Arrays.sort(files);
		assertEquals(1, files.length);
		assertEquals(".git", files[0].getName());

		//create new branch for maven installation
		installer.prepareMavenBranch();

		//check if new branch got created
		assertEquals("update", installer.getVersioning().getCurrentSession().toString());

		//activate all available update sites
		List<UpdateSite> availableSites = installer.getAvailableUpdateSites();
		installer.localInstallation.setUpdateSites(availableSites);
		availableSites.forEach(site -> site.setActive(true));
		installer.buildPOM();

		//check if local Fiji pom exists
		File pom = new File(destinationFolder.getRoot(), "pom.xml");
		assertTrue(pom.exists());

		//check if no local plugins folder exists
		assertFalse(new File(destinationFolder.getRoot(), "plugins").exists());

		//install all assets from the newly built fiji pom
		installer.installFromPOM(pom);

		//check if local plugins folder now does exist
		assertTrue(new File(destinationFolder.getRoot(), "plugins").exists());

		assertFalse(installer.getVersioning().hasUnsavedChanges());

	}

	@Test
	public void testClassPath() throws IOException {
		destinationFolder.newFolder("lib").mkdirs();
		destinationFolder.newFolder("lab").mkdirs();
		destinationFolder.newFile("lab/a.jar");
		destinationFolder.newFile("a.jar");
		String classpath = new MavenInstaller().getClassPath(destinationFolder.getRoot(), ":");
		String root = destinationFolder.getRoot().getAbsolutePath();
		assertEquals(root + "/*:" + root + "/lab/*", classpath);
	}

}
