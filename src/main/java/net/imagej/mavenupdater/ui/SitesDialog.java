package net.imagej.mavenupdater.ui;

import net.imagej.mavenupdater.model.UpdateSite;
import net.imagej.mavenupdater.ui.model.UpdateSiteTableModel;
import net.imagej.mavenupdater.util.MavenUpdaterUtil;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.util.ArrayList;
import java.util.List;

class SitesDialog extends JDialog {

	private boolean hasChanges = false;

	public SitesDialog(JFrame parent, List<UpdateSite> sites) {
		super(parent);
		setLayout(new MigLayout());
		List<UpdateSite> sitesCopy = MavenUpdaterUtil.copy(sites);
		JTable sitesTable = new JTable(new UpdateSiteTableModel(sitesCopy));
		add(new JScrollPane(sitesTable));
		JPanel south = new JPanel();
		JButton cancelBtn = new JButton("cancel");
		JButton okBtn = new JButton("OK");
		cancelBtn.addActionListener(e -> new Thread(() -> dispose()).start());
		okBtn.addActionListener(e -> new Thread(() -> {
			if(!listsEqual(sites, sitesCopy)) {
				System.out.println("CHANGES");
				sites.clear();
				sitesCopy.forEach(site -> sites.add(new UpdateSite(site)));
				hasChanges = true;
				dispose();
			}
			dispose();

		}).start());
		south.add(okBtn);
		south.add(cancelBtn);
		add(south, "south");
		setModal(false);
		pack();

	}

	private boolean listsEqual(List<UpdateSite> sites, List<UpdateSite> sitesCopy) {
		if(sites.size() != sitesCopy.size()) return false;
		for (int i = 0; i < sites.size(); i++) {
			if(!sites.get(i).equals(sitesCopy.get(i))) return false;
			if(!sites.get(i).getVersion().equals(sitesCopy.get(i).getVersion())) return false;
			if(!sites.get(i).isActive() == sitesCopy.get(i).isActive()) return false;
			if(!sites.get(i).getUseSnapshots() == sitesCopy.get(i).getUseSnapshots()) return false;
		}
		return true;
	}

	public boolean hasChanges() {
		return hasChanges;
	}
}
