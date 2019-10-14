package net.imagej.mavenupdater.util;

import net.imagej.mavenupdater.model.UpdateSite;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NexusUtil {

	public static String mavenURL = "https://dais-maven.mpi-cbg.de/";
	public static String mavenRepository = "test-maven";

	public static Model getModel(UpdateSite site) throws IOException, XmlPullParserException {
		if(site.getModel() != null) return site.getModel();
		return getModel(site.getGroupId(), site.getArtifactId(), site.getVersion());
	}

	public static Model getModel(String groupId, String artifactId, String version) throws IOException, XmlPullParserException {
		String apiCall = mavenURL + "service/rest/v1/search/assets?repository=" + mavenRepository + "&"
			+ "version=" + version + "&group=" + groupId + "&name=" + artifactId;
		URL compareUrl = new URL(apiCall);
		JSONObject json = new JSONObject(fromURL(compareUrl));
		JSONArray items = (JSONArray) json.get("items");
		for (Object o : items) {
			JSONObject asset = (JSONObject) o;
			String url = asset.getString("downloadUrl");
			if (url.endsWith(".pom")) {
				return new MavenXpp3Reader().read(new URL(url).openStream());
			}
		}
		return null;
	}

	private static String fromURL(URL url) throws IOException {
		System.out.println("Loading " + url);
		HttpURLConnection conn = (HttpURLConnection) url.openConnection();
		conn.setRequestMethod("GET");
		conn.setRequestProperty("Accept", "application/json");

		if (conn.getResponseCode() != 200) {
			throw new RuntimeException("Failed : HTTP error code : "
					+ conn.getResponseCode());
		}

		BufferedReader br = new BufferedReader(new InputStreamReader(
				(conn.getInputStream())));

		String line;
		String output = "";
		while ((line = br.readLine()) != null) {
			output += line;
		}
		if(output.isEmpty()) return null;
		conn.disconnect();
		return output;
	}
}
