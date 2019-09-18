package net.imagej.mavenupdater.ui;

import net.imagej.mavenupdater.model.UpdateSite;
import net.imagej.mavenupdater.ui.model.UpdateSiteTableModel;
import net.imagej.mavenupdater.util.MavenUpdaterUtil;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class InstallerComponentsView extends JPanel {

	private final JButton confirmBtn;
	private final JButton cancelBtn;

	public InstallerComponentsView(MavenInstallerWindow parent) {
		setLayout(new MigLayout("fill"));
		add(new JLabel("<html><h2>Update Site selection</h2></html>"), "wrap, span");
		List<UpdateSite> sites = MavenUpdaterUtil.copy(parent.getAvailableUpdateSites());
		createComponentsChoice(sites);
		cancelBtn = new JButton("Cancel");
		cancelBtn.addActionListener(e ->  parent.dispose());
		confirmBtn = new JButton("OK");
		confirmBtn.addActionListener( e-> new Thread(() -> parent.componentsChosen(sites)).start());
		JPanel south = new JPanel();
		south.add(confirmBtn);
		south.add(cancelBtn);
		add(south, "south");
	}

	private void createComponentsChoice(List<UpdateSite> availableUpdateSites) {
		add(new JLabel("<html>I will now steal the java folder and executable from the provided Fiji and get the rest via maven.<br/>What exactly should I download?"), "wrap, span");

		JTable updateSites = new JTable();
		updateSites.setModel(new UpdateSiteTableModel(availableUpdateSites));
		add(new JScrollPane(updateSites), "push, span, grow");
	}

}
