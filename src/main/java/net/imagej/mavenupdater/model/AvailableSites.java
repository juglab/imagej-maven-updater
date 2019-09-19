package net.imagej.mavenupdater.model;

import java.util.ArrayList;
import java.util.List;

public class AvailableSites {
	public static List<UpdateSite> get() {
		List<UpdateSite> sites = new ArrayList<>();
		addMandatorySite(sites, "net.imagej", "updatesite-imagej", "0.1.0-SNAPSHOT", "pom");
		addMandatorySite(sites, "net.imagej", "updatesite-ij1", "0.1.0-SNAPSHOT", "pom");
		addSite(sites, "net.imagej", "updatesite-fiji", "0.1.0-SNAPSHOT", "pom");
		addSite(sites, "net.imagej", "updatesite-csbdeep", "0.1.0-SNAPSHOT", "pom");
		return sites;
	}

	private static void addMandatorySite(List<UpdateSite> sites, String groupId, String artifactId, String version, String type) {
		UpdateSite site = new UpdateSite(groupId, artifactId, version, type);
		site.setMandatory(true);
		sites.add(site);
	}

	private static void addSite(List<UpdateSite> sites, String groupId, String artifactId, String version, String type) {
		sites.add(new UpdateSite(groupId, artifactId, version, type));
	}

	public static void checkForUpdates(List<UpdateSite> localSites, List<UpdateSite> remoteSites) {
		localSites.forEach(local -> {
			remoteSites.forEach(remote -> {
				if(local.equals(remote)) {
					if(!local.getVersion().equals(remote.getVersion())) {
						local.setAvailableVersion(remote.getVersion());
					}
				}
			});
		});
	}
}
