package net.imagej.mavenupdater;

import net.imagej.mavenupdater.model.UpdateSite;
import org.apache.commons.io.FileUtils;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.shared.utils.xml.pull.XmlPullParserException;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

public class DependencyTest {

	@Rule
	public TemporaryFolder destination = new TemporaryFolder();

	@Before


	@Test
	public void testPomScijavaVersionDifferences() throws IOException, XmlPullParserException {
		MavenInstaller installer = new MavenInstaller(destination.getRoot());
		List<UpdateSite> sites = new ArrayList<>();
		Model extensionNewerProperty = buildAndGetModel(installer, "/extension-defining-property-version-newer.pom");
		Model extensionOlderProperty = buildAndGetModel(installer, "/extension-defining-property-version-older.pom");
		sites.add(new UpdateSite(extensionNewerProperty));
		sites.add(new UpdateSite(extensionOlderProperty));
		File pom = null;
		try {
			pom = installer.buildPOM(sites);
		} catch (org.codehaus.plexus.util.xml.pull.XmlPullParserException e) {
			e.printStackTrace();
		}
		System.out.println(pom);
	}

	@Test
	public void testDifferentVersionDefinedInProperty() throws IOException, org.codehaus.plexus.util.xml.pull.XmlPullParserException {

		MavenInstaller installer = new MavenInstaller(destination.getRoot());

		// add update sites to environment

		List<UpdateSite> sites = buildUpdateSites( installer,
				"/extension-defining-property-version-newer.pom",
				"/extension-defining-property-version-older.pom"
		);

		Model environment = buildEnvironment(installer, sites);

		Dependency imglib2New = getDependency(sites.get(0).getModel(), "net.imglib2", "imglib2");
		Dependency imglib2Old = getDependency(sites.get(1).getModel(), "net.imglib2", "imglib2");

		// update sites have imglib2 dependency
		assertNotNull(imglib2New);
		assertNotNull(imglib2Old);

		// both update sites don't have direct imglib2 versions defined
		assertNull(imglib2New.getVersion());
		assertNull(imglib2Old.getVersion());

		// environment POM has both update site dependencies
		assertEquals(2, environment.getDependencies().size());
		// environment POM has no direct imglib2 dependency
		assertNull(getDependency(environment, "net.imglib2", "imglib2"));

		// get environment dependencies
		List<Dependency> environmentDependencies = getDependencies(installer, environment);
		List<Dependency> siteNewImglib2Dependencies = getDependencies(installer, sites.get(0).getModel());
		List<Dependency> siteOldImglib2Dependencies = getDependencies(installer, sites.get(1).getModel());

		System.out.println("\n"+sites.get(0).getModel().getArtifactId());
		siteNewImglib2Dependencies.forEach(System.out::println);
		System.out.println("\n"+sites.get(1).getModel().getArtifactId());
		siteOldImglib2Dependencies.forEach(System.out::println);
		System.out.println("\n"+environment.getArtifactId());
		environmentDependencies.forEach(System.out::println);

		// compare versions
		Dependency resolvedImglib2New = getDependency(siteNewImglib2Dependencies, "net.imglib2", "imglib2");
		Dependency resolvedImglib2Old = getDependency(siteOldImglib2Dependencies, "net.imglib2", "imglib2");
		Dependency imglib2Environment = getDependency(environmentDependencies, "net.imglib2", "imglib2");
		assertNotNull(imglib2Environment);
		assertNotNull(resolvedImglib2New);
		assertNotNull(resolvedImglib2Old);
		assertEquals("5.8.0", resolvedImglib2New.getVersion());
		assertEquals("5.0.0", resolvedImglib2Old.getVersion());
		assertEquals("5.8.0", imglib2Environment.getVersion());
	}

	private Model buildEnvironment(MavenInstaller installer, List<UpdateSite> sites) throws IOException, org.codehaus.plexus.util.xml.pull.XmlPullParserException {
		File environmentPOM = installer.buildPOM(sites);
		Model model = new MavenXpp3Reader().read(new FileInputStream(environmentPOM));
		model.setPomFile(environmentPOM);
		return model;
	}

	private List<UpdateSite> buildUpdateSites(MavenInstaller installer, String... poms) throws IOException {

		List<UpdateSite> sites = new ArrayList<>();
		for (int i = 0; i < poms.length; i++) {
			Model extensionNewerProperty = buildAndGetModel(installer, poms[i]);
			UpdateSite site = new UpdateSite(extensionNewerProperty);
			site.setActive(true);
			site.setType("pom");
			sites.add(site);
		}
		return sites;
	}

	private List<Dependency> getDependencies(MavenInstaller installer, Model model) {
		File pomCopy = buildTempMavenDir(model.getPomFile());
		System.setProperty("maven.multiModuleProjectDirectory", pomCopy.getParent());
		String[] args = {"dependency:list"};
		AbstractMavenApp.printMavenCommand(args);
		MyPrintStream reader = new MyPrintStream(System.out);
		installer.getMaven().doMain(args,
				pomCopy.getParent(), reader, reader);
		return reader.getList();
	}

	private static File buildTempMavenDir(File pom) {
		File pomCopy = null;
		TemporaryFolder folder = new TemporaryFolder();
		try {
			folder.create();
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			pomCopy = folder.newFile("pom.xml");
		} catch (IOException e) {
			e.printStackTrace();
		}
		try {
			FileUtils.copyFile(pom, pomCopy);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return pomCopy;
	}

	private File install(MavenInstaller installer, File pom) {
		File pomCopy = buildTempMavenDir(pom);
		System.setProperty("maven.multiModuleProjectDirectory", pomCopy.getParent());
		String[] args = {
				"install",
				"-Denforcer.skip=true"
		};
		AbstractMavenApp.printMavenCommand(args);
		installer.getMaven().doMain(args,
				pomCopy.getParent(), System.out, System.err);
		return pomCopy;
	}

	private Dependency getDependency(Model model, String groupId, String artifactId) {
		return getDependency(model.getDependencies(), groupId, artifactId);
	}

	private Dependency getDependency(List<Dependency> dependencies, String groupId, String artifactId) {
		for (Dependency dependency : dependencies) {
			if(dependency.getArtifactId().equals(artifactId) && dependency.getGroupId().equals(groupId)) {
				return dependency;
			}
		}
		return null;
	}

	private Model buildAndGetModel(MavenInstaller installer, String modelString) throws IOException, XmlPullParserException {
		File pom = new File(getClass().getResource(modelString).getPath());
		install(installer, pom);
		try {
			Model model = new MavenXpp3Reader().read(new FileInputStream(pom));
			model.setPomFile(pom);
			return model;
		} catch (org.codehaus.plexus.util.xml.pull.XmlPullParserException e) {
			e.printStackTrace();
		}
		return null;
	}

	private class MyPrintStream extends PrintStream {
		MyPrintStream(OutputStream outputStream) {
			super(outputStream);
		}

		List<String> lines = new ArrayList<>();

		@Override
		public void println(String s) {
			lines.add(s);
			super.println(s);
		}

		List<Dependency> getList() {
			List<Dependency> res = new ArrayList<>();
			String listMojoBeginning = "[main] INFO org.apache.maven.plugins.dependency.resolvers.ListMojo -    ";
			for (String line : lines) {
				if(line.startsWith(listMojoBeginning)) {
					line = line.replace(listMojoBeginning, "");
					String[] lineParts = line.split(":");
					Dependency dependency = new Dependency();
					dependency.setGroupId(lineParts[0]);
					dependency.setArtifactId(lineParts[1]);
					dependency.setType(lineParts[2]);
					dependency.setVersion(lineParts[3]);
					dependency.setScope(lineParts[4]);
					res.add(dependency);
				}
			}
			return res;
		}
	}
}
