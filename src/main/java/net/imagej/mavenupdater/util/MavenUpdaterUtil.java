package net.imagej.mavenupdater.util;

import net.imagej.mavenupdater.model.UpdateSite;

import java.util.ArrayList;
import java.util.List;

public class MavenUpdaterUtil {
	public static String getPlatform() {
		final boolean is64bit =
				System.getProperty("os.arch", "").indexOf("64") >= 0;
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
}
