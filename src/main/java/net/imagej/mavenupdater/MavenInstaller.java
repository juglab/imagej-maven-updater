package net.imagej.mavenupdater;

import net.imagej.mavenupdater.model.UpdateSite;
import net.imagej.mavenupdater.ui.MavenInstallerWindow;
import net.imagej.mavenupdater.util.MavenUpdaterUtil;
import net.imagej.mavenupdater.versioning.GitCommands;
import net.imagej.mavenupdater.versioning.GitVersioningService;
import org.apache.commons.io.FileUtils;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.List;

public class MavenInstaller extends AbstractMavenApp {

	protected final MavenInstallerWindow window;
	private String javaExecutable = "java";

	MavenInstaller() {
		window = new MavenInstallerWindow(this);
		versioning = new GitVersioningService(window);
	}

	public MavenInstaller(File basedir) {
		this();
		setBaseDir(basedir);
	}

	public void start() {
		window.setVisible(true);
		window.showInstaller();
	}

	public void install(File destination) {
		window.setVisible(true);
		window.chooseComponents(destination);
	}

	void prepareMavenBranch() throws Exception {
		System.out.println("Preparing maven branch");
		versioning.forceCommitCurrentChanges();
		versioning.copyCurrentSession("minimal");
//		versioning.openSession("maven");
//		for(File file : versioning.getBaseDirectory().listFiles()) {
//			if(file.isDirectory() && !file.getName().equals("java") && !file.getName().equals(".git")) {
//				System.out.println("Deleting " + file.getAbsolutePath());
//				FileUtils.deleteDirectory(file);
//			}
//		}
		versioning.commitCurrentChanges();
		versioning.copyCurrentSession("update");
		System.out.println("Done preparing maven branch");
	}

	public void createMavenInstallation(File destinationDir, boolean downloadJava, List<UpdateSite> sites) throws Exception {
		setBaseDir(destinationDir);
		if(downloadJava) this.downloadJava(destinationDir);
		prepareMavenBranch();
		localInstallation.setUpdateSites(sites);
		File pom = buildPOM();
		getVersioning().commitCurrentChanges();
		installFromPOM(pom);
		writeLaunchers(destinationDir);
		getVersioning().copyCurrentSession("local");
		window.setDone();
	}

	private void writeLaunchers(File destinationDir) throws IOException {
		switch (MavenUpdaterUtil.getPlatform()) {
			case "linux32":
			case "linux64":
				writeShellScript(destinationDir);
				writeLinuxLauncher(destinationDir);
				break;
			case "win32":
			case "win64":
				writeWindowsLauncher(destinationDir);
				break;
			case "macosx":
				writeShellScript(destinationDir);
				writeMacOSXLauncher(destinationDir);
				break;
		}
	}

	private void writeShellScript(File destinationDir) throws IOException {
		String shellScriptContent = "cd " + destinationDir.getAbsolutePath() + "\n"
				+ getJavaExecutable() + " -classpath \""
				+ getClassPath(destinationDir, ":")
				+ "\" net.imagej.Main";
		File shellScript = new File(destinationDir, "run.sh");
		Files.write(shellScript.toPath(), shellScriptContent.getBytes());
		shellScript.setExecutable(true);
	}

	private void writeWindowsLauncher(File destinationDir) throws IOException {
		String batContent = "cd " + destinationDir.getAbsolutePath() + "\n"
				+ getJavaExecutable() + " -classpath \""
				+ getClassPath(destinationDir, ";")
				+ "\" net.imagej.Main";
		File bat = new File(destinationDir, "run.bat");
		Files.write(bat.toPath(), batContent.getBytes());
		bat.setExecutable(true);
	}

	private void writeMacOSXLauncher(File destinationDir) {
		String infoContent = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
				"<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
				"<plist version=\"1.0\">\n" +
				"<dict>\n" +
				"        <key>CFBundleGetInfoString</key>\n" +
				"        <string>My Scijava App</string>\n" +
				"        <key>CFBundleIconFile</key>\n" +
				"        <string>Fiji.icns</string>\n" +
				"        <key>CFBundleIdentifier</key>\n" +
				"        <string>org.scijava</string>\n" +
				"        <key>ProgramArguments</key>\n" +
				"        <array>\n" +
				"                <string>" + destinationDir.getAbsolutePath() + "/run.sh</string>\n" +
				"        </array>\n" +
				"</dict>\n" +
				"</plist>";
		InputStream iconStream = getClass().getResourceAsStream("/launcher/macosx/Resources/Fiji.icns");
		try {
			File infoFile = new File(destinationDir, "Contents/Info.plist");
			File iconFile = new File(destinationDir, "Contents/Resources/Fiji.icns");
			infoFile.getParentFile().mkdirs();
			iconFile.getParentFile().mkdirs();
			Files.write(infoFile.toPath(), infoContent.getBytes());
			FileUtils.copyInputStreamToFile(iconStream, iconFile);
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	private void writeLinuxLauncher(File destinationDir) {
		String desktopContent = "[Desktop Entry]\n" +
				"Name=My scijava application\n" +
				"Comment=My scijava application\n" +
				"Exec=" + destinationDir.getAbsolutePath() + "/run.sh\n" +
				"Icon=" + destinationDir.getAbsolutePath() + "/images/icon.png\n" +
				"Terminal=false\n" +
				"Type=Application";
		try {
			File desktopFile = new File(destinationDir, "my-scijava-application.desktop");
			Files.write(desktopFile.toPath(), desktopContent.getBytes());
			desktopFile.setExecutable(true);

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	String getClassPath(File destinationDir, String separator) {
		if(destinationDir.getName().equals(".git")) return "";
		String[] subdirs = destinationDir.list((current, name) -> new File(current, name).isDirectory());
		boolean hasJars = Arrays.stream(destinationDir.list()).anyMatch(file -> file.endsWith(".jar"));
		StringBuilder res = new StringBuilder();
		if(hasJars) res.append(destinationDir.getAbsolutePath() + "/*");
		if(subdirs != null) {
			for (int i = 0; i < subdirs.length; i++) {
				String subdir = subdirs[i];
				String path = getClassPath(new File(destinationDir, subdir), separator);
				if (path.length() > 0) res.append((res.length() == 0? "" : separator) + path);
			}
		}
		return res.toString();
	}

	private String getJavaExecutable() {
		return javaExecutable;
	}

	public void downloadJava(File destinationDir) throws IOException {
		System.out.println("Downloading and unpacking JRE..");
		File javaFolder = new File(destinationDir, "java");
		javaFolder.mkdirs();
		URL javaURL = MavenUpdaterUtil.getJavaDownloadURL();
		MavenUpdaterUtil.decompress(javaURL.openStream(), javaFolder);
		File[] files = javaFolder.listFiles();
		if(files.length != 1) {
			System.err.println("Something went wrong during JRE download");
			return;
		}
		File jre = files[0];
		String jdkName = jre.getName().replace("jre", "jdk");
		Path newJrePath = Paths.get(javaFolder.getAbsolutePath(), MavenUpdaterUtil.getJavaDownloadPlatform(), jdkName, "jre");
		newJrePath.toFile().getParentFile().mkdirs();
		Files.move(jre.toPath(), newJrePath);
		File executable = new File(newJrePath.toFile(), "bin/java");
		if(executable.exists()) javaExecutable = executable.getAbsolutePath();
		System.out.println("JRE installed to " + newJrePath.toAbsolutePath());
	}

	public void launchUpdater() {
		new MavenUpdater(getBaseDir().getAbsolutePath()).start();
	}

	public static void main(String... args) throws IOException {
//		File destination = new File("/home/random/Fiji");
//		FileUtils.deleteDirectory(destination);
//		destination.mkdirs();
//		new MavenInstaller().install(destination);
		new MavenInstaller().start();
	}
}
