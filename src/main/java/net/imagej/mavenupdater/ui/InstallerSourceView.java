package net.imagej.mavenupdater.ui;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class InstallerSourceView extends JPanel {

	private final JButton confirmBtn;
	private final JButton cancelBtn;
	private final MavenInstallerWindow parent;
	File source;

	public InstallerSourceView(MavenInstallerWindow parent) {
		this.parent = parent;
		setLayout(new MigLayout("fill", "", "[][]push[]"));
		add(new JLabel("<html><h2>Hi! I'm the maven Fiji / ImageJ installer.</h2></html>"), "wrap, span");
		add(createSourceChoice());
		cancelBtn = new JButton("Cancel");
		cancelBtn.addActionListener(e ->  parent.dispose());
		confirmBtn = new JButton("OK");
		confirmBtn.addActionListener( e-> new Thread(() -> parent.sourceChosen(getSource())).start());
		confirmBtn.setEnabled(false);
		JPanel south = new JPanel();
		south.add(confirmBtn);
		south.add(cancelBtn);
		add(south, "south");
	}

	private Component createSourceChoice() {
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout());
		panel.add(new JLabel("I need a running Fiji installation to begin with. Would you like to..."), "wrap, span");
		JFileChooser existingFijiPath = new JFileChooser();
		existingFijiPath.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		JLabel existingPathField = new JLabel();
		JButton chooseExistingFiji = new JButton("Choose path");
		chooseExistingFiji.addActionListener( e -> {
			int returnVal = existingFijiPath.showOpenDialog(InstallerSourceView.this);
			if (returnVal == JFileChooser.APPROVE_OPTION) {
				source = existingFijiPath.getSelectedFile();
				existingPathField.setText(source.getAbsolutePath());
				confirmBtn.setEnabled(true);
			}
		});
		JRadioButton freshFiji = new JRadioButton("Download a fresh Fiji");
		freshFiji.addActionListener(e -> {
			confirmBtn.setEnabled(true);
			chooseExistingFiji.setVisible(false);
			existingPathField.setVisible(false);
		});
		JRadioButton existingFiji = new JRadioButton("Use an existing Fiji");
		existingFiji.addActionListener( e -> {
			confirmBtn.setEnabled(!existingPathField.getText().isEmpty());
			chooseExistingFiji.setVisible(true);
			existingPathField.setVisible(true);
		});
		chooseExistingFiji.setVisible(false);
		existingPathField.setVisible(false);

		ButtonGroup group = new ButtonGroup();
		group.add(freshFiji);
		group.add(existingFiji);
		panel.add(freshFiji, "wrap, span");
		panel.add(existingFiji, "wrap, span");
		panel.add(chooseExistingFiji);
		panel.add(existingPathField);
		return panel;
	}

	public File getSource() {
		return source;
	}
}
