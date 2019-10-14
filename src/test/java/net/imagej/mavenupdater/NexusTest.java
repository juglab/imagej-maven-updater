//package net.imagej.mavenupdater;
//
//import juglab.nexus.client.NexusReSTClient;
//import juglab.nexus.client.NexusReSTClientException;
//import juglab.nexus.client.domain.Asset;
//import juglab.nexus.client.domain.Component;
//import juglab.nexus.client.domain.Query;
//import org.junit.Test;
//
//import java.util.List;
//
//import static org.junit.Assert.assertEquals;
//import static org.junit.Assert.assertTrue;
//
//public class NexusTest {
//
////	private final static String BASE_URL = "https://maven.scijava.org";
//	private final static String BASE_URL = "https://dais-maven.mpi-cbg.de";
//
//	@Test
//	public void nexusTest() throws NexusReSTClientException {
//		Query q = new Query();
//		q.setRepository( "maven-central" );
//		q.setMavenArtifactId( "ant" );
//		q.setMavenGroupId( "org.apache.ant" );
//		q.setMavenBaseVersion( "1.8.1" );
//		q.setMavenExtension( "jar" );
//		List<Asset> assets = NexusReSTClient.searchAssets( BASE_URL, q );
//		assertEquals(1, assets.size());
//	}
//
//	@Test
//	public void findComponentsOnScijavaMaven() throws NexusReSTClientException {
//		Query q = new Query();
//		q.setRepository( "public" );
//		q.setMavenArtifactId("scijava-common");
//		List<Component> components = NexusReSTClient.searchComponents( "https://maven.scijava.org", q );
//		components.forEach(component -> System.out.println(component.getName() + " " + component.getVersion()));
//	}
//
//	@Test
//	public void nexusFindUpdateSites() throws NexusReSTClientException {
////		q.setKeyword("scijava-common");
//
//		Query q = new Query();
//		q.setRepository( "test-maven" );
//		q.setMavenArtifactId("updatesite-imagej");
//		List<Component> components = NexusReSTClient.searchComponents( BASE_URL, q );
//		for (Component component : components) {
//			System.out.println(component.getName() + " " + component.getVersion());
//			for (Asset asset : component.getAssets()) {
//				String url = asset.getDownloadUrl();
//				String baseVersion = null;
//				String[] urlParts = url.split("/");
//				for (int i = 0; i < urlParts.length; i++) {
//					if(urlParts[i].equals(component.getName())) {
//						baseVersion = urlParts[i+1];
//					}
//				}
//				System.out.println(baseVersion);
//			}
//		}
////		List<Dependency> dependencies = new ArrayList<>();
////		for (Component component : components) {
////			boolean exists = false;
////			for (int i = 0; i < dependencies.size(); i++) {
////				Dependency dep = dependencies.get(i);
////				if(dep.getArtifactId().equals(component.getName()) && dep.getGroupId().equals(component.getGroup())) {
////					exists = true;
////					break;
////				}
////			}
////			if(!exists) {
////				Dependency dep = new Dependency();
////				dep.setGroupId(component.getGroup());
////				dep.setArtifactId(component.getName());
////				dep.setVersion(component.getVersion());
////				dependencies.add(dep);
////			}
////		}
////		dependencies.forEach(System.out::println);
////		assert(dependencies.size() >= 4);
//
//	}
//
//	@Test
//	public void getUpdateSitePOMUrl() throws NexusReSTClientException {
//		Query q = new Query();
//		q.setRepository( "test-maven" );
//		q.setMavenArtifactId( "updatesite-imagej" );
//		q.setMavenGroupId( "net.imagej" );
//		q.setMavenExtension( "pom" );
//		List<Asset> assets = NexusReSTClient.searchAssets( BASE_URL, q );
//		assertTrue( assets.size() > 0 );
//	}
//
//	@Test
//	public void getCSBDeepUpdateSitePOMUrl() throws NexusReSTClientException {
//		Query q = new Query();
//		q.setRepository( "test-maven" );
//		q.setMavenArtifactId( "updatesite-csbdeep" );
//		q.setMavenGroupId( "net.imagej" );
//		q.setMavenExtension( "pom" );
//		List<Asset> assets = NexusReSTClient.searchAssets( BASE_URL, q );
//		assertTrue( assets.size() > 0 );
//	}
//
//
//}
