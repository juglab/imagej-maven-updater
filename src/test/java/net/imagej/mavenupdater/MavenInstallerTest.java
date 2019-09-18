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
	public void installFromExistingFiji() throws Exception {

		//create dummy files simulating existing installation
		new File(sourceFolder.newFolder("jars"), "lib.jar").createNewFile();
		new File(sourceFolder.newFolder("java"), "release").createNewFile();

		//init installer
		MavenInstaller installer = new MavenInstaller();

		//copy fiji into new maven installation directory
		installer.copyFiji(sourceFolder.getRoot(), destinationFolder.getRoot());

		//test if a new git branch got initialized and if the files got copied
		assertEquals("base", installer.getVersioning().getCurrentSession().toString());
		File[] files = destinationFolder.getRoot().listFiles();
		Arrays.sort(files);
		assertEquals(3, files.length);
		assertEquals(".git", files[0].getName());
		assertEquals("jars", files[1].getName());
		assertEquals("java", files[2].getName());

		//create new branch for maven installation
		installer.prepareMavenBranch();

		//check if new branch got created and if everything bit /java and /.git got deleted
		assertEquals("update", installer.getVersioning().getCurrentSession().toString());
		files = destinationFolder.getRoot().listFiles();
		Arrays.sort(files);
		assertEquals(2, files.length);
		assertEquals(".git", files[0].getName());
		assertEquals("java", files[1].getName());

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

}
