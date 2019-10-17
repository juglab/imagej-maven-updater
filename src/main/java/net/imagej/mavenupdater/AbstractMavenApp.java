package net.imagej.mavenupdater;

import net.imagej.mavenupdater.model.AvailableSites;
import net.imagej.mavenupdater.model.LocalInstallation;
import net.imagej.mavenupdater.model.UpdateSite;
import net.imagej.mavenupdater.util.NexusUtil;
import net.imagej.mavenupdater.versioning.VersioningService;
import org.apache.commons.io.FileUtils;
import org.apache.maven.cli.MavenCli;
import org.apache.maven.model.DependencyManagement;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.codehaus.plexus.classworlds.ClassWorld;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.jgit.api.errors.GitAPIException;

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
	private static ClassLoader classLoader;


	public AbstractMavenApp() {
		localInstallation = new LocalInstallation();
	}

	public static void printMavenCommand(String[] args) {
		StringBuilder command = new StringBuilder("mvn ");
		for (int i = 0; i < args.length; i++) {
			command.append(args[i] + " ");
		}
		System.out.println(command.toString());
	}

	public static MavenCli getMaven() {
		if(classLoader == null) {
			classLoader = Thread.currentThread().getContextClassLoader();
		}
		return new MavenCli(new ClassWorld("maven", classLoader));
	}

	public List<UpdateSite> getAvailableUpdateSites() {
		if(availableSites == null) availableSites = AvailableSites.get();
		return availableSites;
	}

	void setBaseDir(File baseDirectory) {
		versioning.setBaseDirectory(baseDirectory);
		try {
			versioning.forceCommitCurrentChanges();
			versioning.copyCurrentSession("base");
		} catch (Exception e) {
			e.printStackTrace();
		}
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
		for (UpdateSite site : sites) {
			final Model extensionModel = NexusUtil.getModel(site);
			if (site.isActive()) {
				model.addDependency(site);
				if(model.getDependencyManagement() == null) model.setDependencyManagement(new DependencyManagement());
				model.getDependencyManagement().addDependency(site);
			}
			//TODO make copy of dependency, set scope import
			// TODO no I think we rather said that I would always chose the newest pom-scijava version of an upate site and set this as parent (and scope import?)
		}
		new MavenXpp3Writer().write(new FileOutputStream(localPom), model);
		return localPom;
	}

	File buildPOM() throws IOException, XmlPullParserException {
		return buildPOM(localInstallation.getUpdateSites());
	}

	void installFromPOM(File pom) throws Exception {
		System.out.println("Installing Fiji from POM file " + pom.getAbsolutePath());
		runMaven(pom,
//				"scijava:copy-jars",
//				"scijavaclone:extract-resources",
				"install",
//				"-DignoreSnapshots=true",
				"-Denforcer.skip=true",
				"-Dscijava.app.directory=" + pom.getParent(),
				"-Ddelete.other.versions=true");
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
		System.out.println("Done building Fiji, saving changes");
		getVersioning().commitCurrentChanges();
		System.out.println("Done saving changes");
	}

	public void runMaven(File pom, String... args) {
		System.setProperty("maven.multiModuleProjectDirectory", pom.getParent());
		printMavenCommand(args);
		getMaven().doMain(args,
				pom.getParent(), System.out, System.err);
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
