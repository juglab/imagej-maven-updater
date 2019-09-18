package net.imagej.mavenupdater;

import net.imagej.mavenupdater.model.AvailableSites;
import net.imagej.mavenupdater.model.LocalInstallation;
import net.imagej.mavenupdater.model.UpdateSite;
import net.imagej.mavenupdater.versioning.GitVersioningService;
import net.imagej.mavenupdater.versioning.VersioningService;
import org.apache.maven.cli.MavenCli;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

import java.awt.*;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class AbstractMavenApp {

	protected VersioningService versioning;
	private List<UpdateSite> availableSites;
	protected final LocalInstallation localInstallation;

	public AbstractMavenApp() {
		localInstallation = new LocalInstallation();
	}

	public List<UpdateSite> getAvailableUpdateSites() {
		if(availableSites == null) availableSites = AvailableSites.get();
		return availableSites;
	}

	void setBaseDir(File baseDirectory) {
		versioning.setBaseDirectory(baseDirectory);
	}

	File getBaseDir() {
		return versioning.getBaseDirectory();
	}


	public VersioningService getVersioning() {
		return versioning;
	}

	File buildPOM(List<UpdateSite> sites) throws IOException, XmlPullParserException {
		InputStream pomTemplate = getClass().getResourceAsStream("/pom-template.xml");
		File localPom = new File(versioning.getBaseDirectory(), "pom.xml");
		localPom.createNewFile();
		final Model model;
		model = new MavenXpp3Reader().read(pomTemplate);
		sites.forEach(site -> {
			if(site.isActive()) model.addDependency(site);
		});
		new MavenXpp3Writer().write(new FileOutputStream(localPom), model);
		return localPom;
	}

	File buildPOM() throws IOException, XmlPullParserException {
		return buildPOM(localInstallation.getUpdateSites());
	}

	void installFromPOM(File pom) throws Exception {
		MavenCli maven = new MavenCli();
		System.setProperty("maven.multiModuleProjectDirectory", pom.getParent());
		String[] args = {
				"-Dscijava.app.directory=" + pom.getParent(),
				"-Ddelete.other.versions=true",
				"-Denforcer.skip=true",
				"install"};
		StringBuilder command = new StringBuilder("mvn ");
		for (int i = 0; i < args.length; i++) {
			command.append(args[i] + " ");
		}
		System.out.println(command.toString());
		maven.doMain(args,
				pom.getParent(), System.out, System.out);

		// this is the invocation variant of the maven install
//		InvocationRequest request = new DefaultInvocationRequest();
//		request.setPomFile(pom);
//		String[] args = {
//				"-Dscijava.app.directory=" + pom.getParent(),
//				"-Ddelete.other.versions=true",
//				"-Denforcer.skip=true ",
//				"install"};
//		request.setGoals(Arrays.asList(args));
//		request.setBaseDirectory(new File(pom.getParent()));
//
//		Invoker invoker = new DefaultInvoker();
//		invoker.setLocalRepositoryDirectory(pom.getParentFile());
//		invoker.setWorkingDirectory(pom.getParentFile());
//		try {
//			invoker.execute(request);
//		} catch (MavenInvocationException e) {
//			e.printStackTrace();
//		}
		getVersioning().commitCurrentChanges();
	}

	public List<UpdateSite> getAvailableAndLocalUpdateSites() {
		List<UpdateSite> localSites = localInstallation.getUpdateSites();
		List<UpdateSite> remoteSites = getAvailableUpdateSites();
		AvailableSites.checkForUpdates(localSites, remoteSites);
		List<UpdateSite> merged = new ArrayList<>();
		merged.addAll(remoteSites);
		for (int i = 0; i < localSites.size(); i++) {
			UpdateSite local = localSites.get(i);
			boolean found = false;
			for (int j = 0; j < merged.size(); j++) {
				if (local.equals(merged.get(j))) {
					merged.set(j, local);
					found = true;
					break;
				}
			}
			if(!found) merged.add(local);
		}
		return merged;
	}
}
