package net.imagej.mavenupdater.ui;

import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;

public class InstallerJavaView extends JPanel {

	private final JButton confirmBtn;
	private final JButton cancelBtn;
	private JCheckBox downloadJavaCheck;
	private final MavenInstallerWindow parent;

	public InstallerJavaView(MavenInstallerWindow parent) {
		this.parent = parent;
		setLayout(new MigLayout("fill", "", "[][]push[]"));
		add(new JLabel("<html><h2>Which Java to use?</h2><p>Please check this box if you want to download a JRE which will be located in your application directory.<br/>Otherwise the default system Java executable will be used.</html>"), "wrap, span");
		add(createSourceChoice());
		cancelBtn = new JButton("Cancel");
		cancelBtn.addActionListener(e ->  parent.dispose());
		confirmBtn = new JButton("OK");
		confirmBtn.addActionListener( e-> new Thread(() -> parent.javaChosen(downloadJavaCheck.isSelected())).start());
		JPanel south = new JPanel();
		south.add(confirmBtn);
		south.add(cancelBtn);
		add(south, "south");
	}

	private Component createSourceChoice() {
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout());
		downloadJavaCheck = new JCheckBox("Download JRE");
//		downloadJavaCheck.setSelected(true);
		panel.add(downloadJavaCheck);
		return panel;
	}
}
