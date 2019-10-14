package net.imagej.mavenupdater.model;

import java.util.ArrayList;
import java.util.List;

public class AvailableSites {

	private final static String BASE_URL = "https://dais-maven.mpi-cbg.de";

	public static List<UpdateSite> get() {
		List<UpdateSite> sites = new ArrayList<>();
//		Query q = new Query();
//		q.setRepository( "test-maven" );
//		q.setMavenExtension( "pom" );
//		q.setSortBy(Query.Sort.NAME);
//		q.setKeyword("updatesite-");
//		List<Component> components = NexusReSTClient.searchComponents( BASE_URL, q );
//		for (Component component : components) {
//			if(component.getAssets().size() == 0) continue;
//			Asset asset = component.getAssets().get(0);
//			String url = asset.getDownloadUrl();
//			String baseVersion = null;
//			String[] urlParts = url.split("/");
//			for (int i = 0; i < urlParts.length; i++) {
//				if(urlParts[i].equals(component.getName())) {
//					baseVersion = urlParts[i+1];
//				}
//			}
//			boolean exists = false;
//			for (int i = 0; i < sites.size(); i++) {
//				Dependency dep = sites.get(i);
//				if(dep.getArtifactId().equals(component.getName()) && dep.getGroupId().equals(component.getGroup())) {
//					exists = true;
//					break;
//				}
//			}
//			if(!exists) {
//				if(component.getName().equals("updatesite-imagej")
//				 || component.getName().equals("updatesite-ij1")) {
//					addMandatorySite(sites, component.getGroup(), component.getName(), baseVersion, "pom");
//				} else {
//					addSite(sites, component.getGroup(), component.getName(), baseVersion, "pom");
//				}
//			}
//		}

		addMandatorySite(sites, "net.imagej", "updatesite-imagej", "1", "pom");
		addMandatorySite(sites, "net.imagej", "updatesite-ij1", "1", "pom");
		addSite(sites, "net.imagej", "updatesite-fiji", "1", "pom");
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
