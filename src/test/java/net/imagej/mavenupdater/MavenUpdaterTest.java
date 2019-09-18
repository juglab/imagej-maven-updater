package net.imagej.mavenupdater;

import net.imagej.mavenupdater.model.AvailableSites;
import net.imagej.mavenupdater.model.UpdateSite;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

public class MavenUpdaterTest {

	@Rule
	public TemporaryFolder sourceFolder = new TemporaryFolder();
	@Rule
	public TemporaryFolder destinationFolder = new TemporaryFolder();

	@Test
	public void listLocalUpdateSites() throws Exception {

		//install
		MavenInstaller installer = new MavenInstaller();
		List<UpdateSite> availableSites = AvailableSites.get();
		availableSites.forEach(site -> site.setActive(true));
		installer.createMavenInstallation(sourceFolder.getRoot(), destinationFolder.getRoot(), availableSites);

		MavenUpdater updater = new MavenUpdater(destinationFolder.getRoot().getAbsolutePath());
		updater.loadLocalInstallation();
		List<UpdateSite> localSites = updater.localInstallation.getUpdateSites();

		assertEquals(availableSites, localSites);
		for (int i = 0; i < availableSites.size(); i++) {
			UpdateSite remote = availableSites.get(i);
			UpdateSite local = localSites.get(i);
			assertEquals(remote.isActive(), local.isActive());
			assertEquals(remote.getVersion(), local.getVersion());
		}

	}

	@Test
	public void addUpdateSite() throws Exception {

		//install
		MavenInstaller installer = new MavenInstaller();
		List<UpdateSite> availableSites = AvailableSites.get();
		installer.createMavenInstallation(sourceFolder.getRoot(), destinationFolder.getRoot(), availableSites);

		MavenUpdater updater = new MavenUpdater(destinationFolder.getRoot().getAbsolutePath());
		updater.loadLocalInstallation();
		updater.loadAvailableSites();
		List<UpdateSite> localSites = updater.localInstallation.getUpdateSites();

		assertEquals(availableSites, localSites);
		for (int i = 0; i < availableSites.size(); i++) {
			UpdateSite remote = availableSites.get(i);
			UpdateSite local = localSites.get(i);
			assertEquals(remote.isActive(), local.isActive());
			assertEquals(remote.getVersion(), local.getVersion());
		}

		UpdateSite fijiSite = null;
		for (UpdateSite site : availableSites) {
			if (site.getArtifactId().equals("updatesite-fiji")) {
				fijiSite = site;
			}
		}
		assertNotNull(fijiSite);
		assertFalse(fijiSite.isActive());
		fijiSite.setActive(true);
		updater.updateInstallationSilently(availableSites);

		updater.loadLocalInstallation();
		updater.loadAvailableSites();
		localSites = updater.localInstallation.getUpdateSites();

		assertEquals(availableSites, localSites);
		for (int i = 0; i < availableSites.size(); i++) {
			UpdateSite remote = availableSites.get(i);
			UpdateSite local = localSites.get(i);
			assertEquals(remote.isActive(), local.isActive());
			assertEquals(remote.getVersion(), local.getVersion());
		}

	}

}
