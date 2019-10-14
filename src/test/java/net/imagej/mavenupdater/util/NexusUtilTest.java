package net.imagej.mavenupdater.util;

import org.apache.maven.model.Model;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.junit.Test;

import java.io.IOException;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class NexusUtilTest {

	@Test
	public void getModel() throws IOException, XmlPullParserException {
		Model model = NexusUtil.getModel("net.imagej", "updatesite-imagej", "1");
		assertNotNull(model);
		assertEquals("net.imagej", model.getGroupId());
		assertEquals("updatesite-imagej", model.getArtifactId());
		assertEquals("1", model.getVersion());
	}

}
