package net.imagej.mavenupdater;

import net.imagej.mavenupdater.model.UpdateSite;
import net.imagej.mavenupdater.ui.MavenUpdaterWindow;
import net.imagej.mavenupdater.util.MavenUpdaterUtil;
import net.imagej.mavenupdater.versioning.GitVersioningService;
import net.imagej.mavenupdater.versioning.model.FileChange;
import net.imagej.mavenupdater.versioning.model.Session;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.List;

public class MavenUpdater extends AbstractMavenApp {

	private final MavenUpdaterWindow window;

	public MavenUpdater(String baseDir) {
		window = new MavenUpdaterWindow(this);
		versioning = new GitVersioningService(window);
		getVersioning().setBaseDirectory(new File(baseDir));
		try {
			getVersioning().openSession("local");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void start() {
		try {
			loadLocalInstallation();
		} catch (IOException | XmlPullParserException e) {
			e.printStackTrace();
		}
		window.setLocalInstallation(localInstallation);
		window.rebuild();
		window.setVisible(true);
	}

	void loadLocalInstallation() throws IOException, XmlPullParserException {
		Model model = new MavenXpp3Reader().read(new FileInputStream(getVersioning().getBaseDirectory() + File.separator + "pom.xml"));
		localInstallation.setModel(model);
	}

	public void updateInstallation() throws Exception {
		updateInstallation(localInstallation.getUpdateSites(), false);
	}

	public void updateInstallationSilently() throws Exception {
		updateInstallation(localInstallation.getUpdateSites(), true);
	}

	public void updateInstallation(List<UpdateSite> sites) throws Exception {
		updateInstallation(sites, false);
	}

	public void updateInstallationSilently(List<UpdateSite> sites) throws Exception {
		updateInstallation(sites, true);
	}

	private void updateInstallation(List<UpdateSite> sites, boolean autoApprove) throws Exception {
		sites = MavenUpdaterUtil.copy(sites);
		getVersioning().commitCurrentChanges();
		getVersioning().openSession("minimal");
		getVersioning().copyCurrentSession("update-inprogress");
		File pom = buildPOM(sites);
		installFromPOM(pom);
		//TODO keep local changes (that's what the updater branch is for - find out what was locally changed by comparing to the update branch)
		Session local = getVersioning().getSession("local");
		Session updateInprogress = getVersioning().getSession("update-inprogress");
		List<FileChange> changes = getVersioning().getChanges(updateInprogress, local);
		boolean approved = autoApprove || getVersioning().getUI().approveChanges(changes, "Please approve these updates");
		if(approved) {
			//TODO update pom of local installation
			localInstallation.setUpdateSites(sites);
			getVersioning().deleteSession("local");
			getVersioning().copyCurrentSession("local");
			getVersioning().deleteSession("update");
			getVersioning().copyCurrentSession("update");
			getVersioning().deleteSession("update-inprogress");
			getVersioning().openSession("local");
		} else {
			getVersioning().openSession("local");
			getVersioning().deleteSession("update-inprogress");
		}
	}

	public void showSessions() {
		getVersioning().getUI().showSessions();
	}

	public static void main(String... args) {
		new MavenUpdater("/home/random/Fiji").start();
	}

	public void loadAvailableSites() {
		localInstallation.setUpdateSites(getAvailableAndLocalUpdateSites());
	}
}
