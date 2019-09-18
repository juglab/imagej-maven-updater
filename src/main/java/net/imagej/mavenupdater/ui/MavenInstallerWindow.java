package net.imagej.mavenupdater.ui;

import net.imagej.mavenupdater.MavenInstaller;
import net.imagej.mavenupdater.model.UpdateSite;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.io.File;
import java.util.List;

public class MavenInstallerWindow extends AbstractMavenAppWindow {

	private final MavenInstaller parent;
	private File sourceDir;
	private File destinationDir;
	private List<UpdateSite> sites;
	private JTabbedPane tabs;
	private InstallerProgressView progressView;

	public MavenInstallerWindow(MavenInstaller installer) {
		super(installer);
		this.parent = installer;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
		rebuild();
	}

	private void rebuild() {
		main.removeAll();
		main.add(createTabs(), "span, grow, push");
		main.revalidate();
		main.repaint();
		pack();
	}

	private Component createTabs() {
		tabs = new JTabbedPane();
		BasicTabbedPaneUI tabbedPaneUI = new BasicTabbedPaneUI();
		tabs.setUI(tabbedPaneUI);
		tabs.setForeground(Color.black);
		tabs.setEnabled(false);
		tabs.addTab("Source", new InstallerSourceView(this));
		tabs.addTab("Destination", new InstallerDestinationView(this));
		tabs.addTab("Components", new InstallerComponentsView(this));
		progressView = new InstallerProgressView(this);
		tabs.addTab("Installation", progressView);
		return tabs;
	}

	public void showInstaller() {
		tabs.setSelectedIndex(0);
	}

	public void chooseComponents(File source, File destination) {
		sourceDir = source;
		destinationDir = destination;
		tabs.setSelectedIndex(2);
	}

	void sourceChosen(File dir) {
		sourceDir = dir;
		tabs.setSelectedIndex(1);
	}

	void destinationChosen(File dir) {
		destinationDir = dir;
		tabs.setSelectedIndex(2);
	}

	void componentsChosen(List<UpdateSite> sites) {
		this.sites = sites;
		install();
	}

	void install() {
		tabs.setSelectedIndex(3);
		if(destinationDir.isDirectory()) {
			if(destinationDir.listFiles().length != 0) {
				System.out.println("please choose an empty output folder");
				tabs.setSelectedIndex(1);
			}
		} else {
			System.out.println("destination not a folder");
		}
		try {
			progressView.setStatus("Installing Fiji via Maven...");
			parent.createMavenInstallation(sourceDir, destinationDir, sites);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setDone() {
		progressView.setDone();
	}

	public void switchToUpdater() {
		parent.launchUpdater();
		dispose();
	}
}
