package net.imagej.mavenupdater.ui;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class InstallerDestinationView extends JPanel {

	private final JButton confirmBtn;
	private final JButton cancelBtn;
	private final MavenInstallerWindow parent;
	File destination;

	public InstallerDestinationView(MavenInstallerWindow parent) {
		this.parent = parent;
		setLayout(new MigLayout("fill", "", "[][]push[]"));
		add(new JLabel("<html><h2>Please choose a destination!</h2></html>"), "wrap, span");
		add(createDestinationChoice());
		cancelBtn = new JButton("Cancel");
		cancelBtn.addActionListener(e ->  parent.dispose());
		confirmBtn = new JButton("OK");
		confirmBtn.addActionListener( e-> new Thread(() -> parent.destinationChosen(getDestination())).start());
		confirmBtn.setEnabled(false);
		JPanel south = new JPanel();
		south.add(confirmBtn);
		south.add(cancelBtn);
		add(south, "south");
	}

	private Component createDestinationChoice() {
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout());
		panel.add(new JLabel("<html>Where should your new installation, managed by maven, live?<br/>Please select an empty repository."), "wrap, span");
		JFileChooser destinationPath = new JFileChooser();
		destinationPath.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		JLabel destinationPathView = new JLabel();
		JButton chooseDestination = new JButton("Choose path");
		chooseDestination.addActionListener( e -> {
			int returnVal = destinationPath.showOpenDialog(InstallerDestinationView.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				destination = destinationPath.getSelectedFile();
				destinationPathView.setText(destination.getAbsolutePath());
				confirmBtn.setEnabled(true);
			}
		});
		panel.add(chooseDestination);
		panel.add(destinationPathView);
		return panel;
	}

	public File getDestination() {
		return destination;
	}
}
