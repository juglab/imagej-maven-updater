package net.imagej.mavenupdater.ui;

import net.imagej.mavenupdater.AbstractMavenApp;
import net.imagej.mavenupdater.model.UpdateSite;

import javax.swing.*;
import java.util.List;

public class AbstractMavenAppWindow extends JFrame {

	protected final JPanel main = new JPanel();
	AbstractMavenApp parent;

	AbstractMavenAppWindow(AbstractMavenApp parent) {
		this.parent = parent;
		setContentPane(main);
	}

	List<UpdateSite> getAvailableUpdateSites() {
		return parent.getAvailableUpdateSites();
	}

}
