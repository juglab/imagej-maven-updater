//package net.imagej.mavenupdater;
//
//import juglab.nexus.client.NexusReSTClient;
//import juglab.nexus.client.NexusReSTClientException;
//import juglab.nexus.client.domain.Asset;
//import juglab.nexus.client.domain.Query;
//import org.junit.Test;
//
//import java.util.List;
//
//import static org.junit.Assert.assertEquals;
//
//public class NexusTest {
//
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
//		assertEquals( 1, assets.size() );
//	}
//
//	@Test
//	public void getUpdateSitePOMUrl() throws NexusReSTClientException {
//		Query q = new Query();
//		q.setRepository( "test-maven" );
//		q.setMavenArtifactId( "updatesite-imagej" );
//		q.setMavenGroupId( "net.imagej" );
////		q.setMavenBaseVersion( "1.8.1" );
//		q.setMavenExtension( "pom" );
//		List<Asset> assets = NexusReSTClient.searchAssets( BASE_URL, q );
//		assertEquals( 1, assets.size() );
//	}
//
//	@Test
//	public void getCSBDeepUpdateSitePOMUrl() throws NexusReSTClientException {
//		Query q = new Query();
//		q.setRepository( "test-maven" );
//		q.setMavenArtifactId( "updatesite-csbdeep" );
//		q.setMavenGroupId( "net.imagej" );
////		q.setMavenBaseVersion( "1.8.1" );
//		q.setMavenExtension( "pom" );
//		List<Asset> assets = NexusReSTClient.searchAssets( BASE_URL, q );
//		assertEquals( 1, assets.size() );
//	}
//
//
//}
