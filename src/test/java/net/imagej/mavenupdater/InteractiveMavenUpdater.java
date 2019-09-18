package net.imagej.mavenupdater;

import java.io.IOException;

public class InteractiveMavenUpdater {

	public static void main(String...args) throws IOException {
		MavenUpdater updater = new MavenUpdater("/home/random/Fiji");
		updater.start();
	}
}
