package net.imagej.mavenupdater.util;

import net.imagej.mavenupdater.model.UpdateSite;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.utils.IOUtils;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class MavenUpdaterUtil {

	public static String getPlatform() {
		final boolean is64bit =
				System.getProperty("os.arch", "").contains("64");
		final String osName = System.getProperty("os.name", "<unknown>");
		if (osName.equals("Linux")) return "linux" + (is64bit ? "64" : "32");
		if (osName.equals("Mac OS X")) return "macosx";
		if (osName.startsWith("Windows")) return "win" + (is64bit ? "64" : "32");
		// System.err.println("Unknown platform: " + osName);
		return osName.toLowerCase();
	}

	public static List<UpdateSite> copy(List<UpdateSite> sites) {
		ArrayList<UpdateSite> res = new ArrayList<>();
		sites.forEach(site -> res.add(new UpdateSite(site)));
		return res;
	}

	public static String getJavaDownloadPlatform() {
		final boolean is64bit =
				System.getProperty("os.arch", "").contains("64");
		final String osName = System.getProperty("os.name", "<unknown>");
		if (osName.equals("Linux")) return "linux" + (is64bit ? "-amd64" : "");
		if (osName.equals("Mac OS X")) return "macosx";
		if (osName.startsWith("Windows")) return "win" + (is64bit ? "64" : "32");
		throw new RuntimeException("No JRE for platform exists");
	}

	public static URL getJavaDownloadURL() throws MalformedURLException {
		return new URL("https://downloads.imagej.net/java/" + getJavaDownloadPlatform() + ".tar.gz");
	}

	public static void decompress(InputStream in, File out) throws IOException {
		try (TarArchiveInputStream fin = new TarArchiveInputStream(
				new GzipCompressorInputStream(new BufferedInputStream(in)))){
			TarArchiveEntry entry;
			while ((entry = fin.getNextTarEntry()) != null) {
				if (entry.isDirectory()) {
					continue;
				}
				File curfile = new File(out, entry.getName());
				File parent = curfile.getParentFile();
				if (!parent.exists()) {
					parent.mkdirs();
				}
				IOUtils.copy(fin, new FileOutputStream(curfile));
			}
		}
	}

}
