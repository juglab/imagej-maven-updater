package net.imagej.mavenupdater.ui;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;

public class InstallerProgressView extends JPanel {

	private final JProgressBar progress;
	private final JLabel text;
	private final JButton startUpdater;
	private final MavenInstallerWindow parent;

	public InstallerProgressView(MavenInstallerWindow parent) {
		this.parent = parent;
		setLayout(new MigLayout("fill", "", "[]push[][]push[]"));
		add(new JLabel("<html><h2>No more questions!</h2></html>"), "wrap, span");
		progress = new JProgressBar();
		progress.setIndeterminate(true);
		text = new JLabel();
		startUpdater = new JButton("Switch to Updater");
		startUpdater.addActionListener(e -> new Thread(() -> parent.switchToUpdater()).start());
		startUpdater.setVisible(false);
		add(progress, "wrap");
		add(text);
		add(startUpdater);

	}

	public void setStatus(String text, int progress, int max) {

	}

	public void setStatus(String text) {
		this.text.setText(text);
		invalidate();
	}

	public void setDone() {
		progress.setVisible(false);
		startUpdater.setVisible(true);
		text.setText("Done!");
	}
}
