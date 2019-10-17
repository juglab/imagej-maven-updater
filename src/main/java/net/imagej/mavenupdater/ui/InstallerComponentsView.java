package net.imagej.mavenupdater.ui;

import net.imagej.mavenupdater.model.UpdateSite;
import net.imagej.mavenupdater.ui.model.UpdateSiteTableModel;
import net.imagej.mavenupdater.util.MavenUpdaterUtil;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.List;

public class InstallerComponentsView extends JPanel {

	private final JButton confirmBtn;
	private final JButton cancelBtn;

	public InstallerComponentsView(MavenInstallerWindow parent) {
		setLayout(new MigLayout("fill"));
		add(new JLabel("<html><h2>Update Site selection</h2></html>"), "wrap, span");
		List<UpdateSite> sites = null;
		sites = MavenUpdaterUtil.copy(parent.getAvailableUpdateSites());
		createComponentsChoice(sites);
		cancelBtn = new JButton("Cancel");
		cancelBtn.addActionListener(e ->  parent.dispose());
		confirmBtn = new JButton("OK");
		List<UpdateSite> finalSites = sites;
		confirmBtn.addActionListener(e-> new Thread(() -> parent.componentsChosen(finalSites)).start());
		JPanel south = new JPanel();
		south.add(confirmBtn);
		south.add(cancelBtn);
		add(south, "south");
	}

	private void createComponentsChoice(List<UpdateSite> availableUpdateSites) {
		add(new JLabel("<html>What exactly should be installed?"), "wrap, span");

		JTable updateSites = new JTable();
		updateSites.setModel(new UpdateSiteTableModel(availableUpdateSites));
		add(new JScrollPane(updateSites), "push, span, grow");
	}

}
