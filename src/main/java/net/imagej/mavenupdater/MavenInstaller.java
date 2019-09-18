package net.imagej.mavenupdater;

import net.imagej.mavenupdater.model.UpdateSite;
import net.imagej.mavenupdater.ui.MavenInstallerWindow;
import net.imagej.mavenupdater.versioning.GitVersioningService;
import org.apache.commons.io.FileUtils;
import org.apache.maven.cli.MavenCli;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.jgit.api.errors.GitAPIException;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MavenInstaller extends AbstractMavenApp {

	protected final MavenInstallerWindow window;

	public MavenInstaller() {
		window = new MavenInstallerWindow(this);
		versioning = new GitVersioningService(window);
	}

	public void start() {
		String baseDir = System.getProperty("net.imagej.mavenupdater.basedir");
		if(baseDir == null) {
			window.setVisible(true);
			window.showInstaller();
		}
	}

	public void install(File source, File destination) {
		window.setVisible(true);
		window.chooseComponents(source, destination);
	}

	private void downloadFiji(File destinationDir) throws Exception {
		setBaseDir(destinationDir);
		versioning.downloadFreshSession("base");
	}

	void copyFiji(File sourceDir, File destinationDir) throws Exception {
		setBaseDir(destinationDir);
		versioning.importSessionFromFolder(sourceDir, "base");
	}

	void prepareMavenBranch() throws Exception {
		versioning.forceCommitCurrentChanges();
		versioning.copyCurrentSession("minimal");
//		versioning.openSession("maven");
		for(File file : versioning.getBaseDirectory().listFiles()) {
			if(file.isDirectory() && !file.getName().equals("java") && !file.getName().equals(".git")) {
				System.out.println("Deleting " + file.getAbsolutePath());
				FileUtils.deleteDirectory(file);
			}
		}
		versioning.commitCurrentChanges();
		versioning.copyCurrentSession("update");
	}

	public void createMavenInstallation(File sourceDir, File destinationDir, List<UpdateSite> sites) throws Exception {
		if(sourceDir == null) {
			downloadFiji(destinationDir);
		} else {
			copyFiji(sourceDir, destinationDir);
		}
		prepareMavenBranch();
		localInstallation.setUpdateSites(sites);
		File pom = buildPOM();
		getVersioning().commitCurrentChanges();
		installFromPOM(pom);
		getVersioning().copyCurrentSession("local");
		window.setDone();
	}

	public void launchUpdater() {
		new MavenUpdater(getBaseDir().getAbsolutePath()).start();
	}

	public static void main(String... args) throws IOException {
		File destination = new File("/home/random/Fiji");
		FileUtils.deleteDirectory(destination);
		destination.mkdirs();
		new MavenInstaller().install(new File("/home/random/Programs/Fiji.app"), destination);
//		new MavenInstaller().start();
	}
}
