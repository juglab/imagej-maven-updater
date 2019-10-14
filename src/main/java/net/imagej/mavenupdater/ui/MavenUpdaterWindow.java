package net.imagej.mavenupdater.ui;

import net.imagej.mavenupdater.MavenUpdater;
import net.imagej.mavenupdater.model.LocalInstallation;
import net.imagej.mavenupdater.model.UpdateSite;
import net.imagej.mavenupdater.ui.model.LocalUpdateSiteTableModel;
import net.imagej.mavenupdater.ui.model.SessionsTableModel;
import net.imagej.mavenupdater.versioning.VersioningService;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import javax.swing.plaf.basic.BasicTabbedPaneUI;
import java.awt.*;
import java.util.List;

public class MavenUpdaterWindow extends AbstractMavenAppWindow {

	private final MavenUpdater parent;

	private LocalInstallation localInstallation;

	public MavenUpdaterWindow(MavenUpdater updater) {
		super(updater);
		this.parent = updater;
		setDefaultCloseOperation(DISPOSE_ON_CLOSE);
	}

	List<UpdateSite> getAvailableUpdateSites() {
		return parent.getAvailableUpdateSites();
	}

	public void setLocalInstallation(LocalInstallation localInstallation) {
		this.localInstallation = localInstallation;
	}

	private void updateInstallation() {
		try {
			parent.updateInstallation();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private List<UpdateSite> getAvailableAndLocalUpdateSites() {
		return parent.getAvailableAndLocalUpdateSites();
	}

	public VersioningService getVersioning() {
		return parent.getVersioning();
	}

	public void rebuild() {
		main.removeAll();
		main.setLayout(new MigLayout());
		main.add(new JLabel("<html><h2>Fiji / ImageJ Manager</h2>"), "span, wrap");
		main.add(createTabs(), "push, span, grow");
		pack();
	}

	private Component createTabs() {
		JTabbedPane tabs = new JTabbedPane();
		BasicTabbedPaneUI tabbedPaneUI = new BasicTabbedPaneUI();
		tabs.setUI(tabbedPaneUI);
		tabs.addTab("User", createUserTab());
		tabs.addTab("Uploader", createUploaderTab());
		tabs.addTab("Maintainer", createMaintainerTab());
		return tabs;
	}

	private Component createUserTab() {
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout());
		//TODO
		//1. check for change in current version of update site repository
		//2. if there are changes, enable update button, show changed repos
		//create update button
		panel.add(new JLabel("<html><h2>Update Sites</h2>"), "");
		panel.add(createManageSitesBtn(), "wrap");
		panel.add(createUpdateSitesTable(), "span, grow, push, wrap");
		add(panel, "grow, push, span");
		panel.add(createUpdateBtn(), "newline, span, grow");
		return panel;
	}

	private JScrollPane createUpdateSitesTable() {
		JTable table = new JTable(new LocalUpdateSiteTableModel(localInstallation.getActiveUpdateSites()));
		table.setBorder(BorderFactory.createEmptyBorder());
//		table.setShowHorizontalLines(false);
		table.setShowVerticalLines(false);
		table.setBackground(null);
		table.setOpaque(false);
		table.setRowHeight(30);
		table.getColumn("updateable").setMaxWidth(30);
		table.getTableHeader().setVisible(false);
		table.setPreferredScrollableViewportSize(table.getPreferredSize());
		return scroll(table);
	}

	private Component createManageSitesBtn() {
		JButton btn = new JButton("Manage update sites");
		btn.addActionListener( e-> new Thread(() -> editSites()).start());
		return btn;
	}

	private Component createUpdateBtn() {
		JButton btn = new JButton("update");
		btn.addActionListener( e-> new Thread(() -> updateInstallation()).start());
		btn.setEnabled(localInstallation.canBeUpdated());
		return btn;
	}

	private void editSites() {
		List<UpdateSite> sites = null;
		sites = getAvailableAndLocalUpdateSites();
		SitesDialog sitesDialog = new SitesDialog(this, sites);
		sitesDialog.setLocationRelativeTo(this);
		sitesDialog.setModal(true);
		sitesDialog.setVisible(true);
		if (sitesDialog.hasChanges()) {
			try {
				parent.updateInstallation(sites);
			} catch (Exception e) {
				e.printStackTrace();
			}
			rebuild();
		}
	}

	private Component createUploaderTab() {
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout());
		panel.add(new JLabel("<html><h2>TODO</h2>"));
		return panel;
	}

	private Component createMaintainerTab() {
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout());
		panel.add(new JLabel("<html><h2>Sessions</h2>"), "");
		panel.add(createManageSessionsBtn(), "wrap");
		panel.add(createSessionsTable());
		return panel;
	}

	private Component createManageSessionsBtn() {
		JButton btn = new JButton("Manage sessions");
		btn.addActionListener(e -> new Thread(() -> parent.showSessions()).start());
		return btn;
	}

	private Component createSessionsTable() {
		JTable table;
		try {
			table = new JTable(new SessionsTableModel(parent.getVersioning()));
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
		table.getColumn("active").setMaxWidth(30);
		table.getColumn("active").setResizable(false);
		table.getTableHeader().setVisible(false);
		table.setBorder(BorderFactory.createEmptyBorder());
//		table.setShowHorizontalLines(false);
		table.setShowVerticalLines(false);
		table.setBackground(null);
		table.setOpaque(false);
		return scroll(table);
	}

	private JScrollPane scroll(Component c) {
		JScrollPane scroll = new JScrollPane(c);
		scroll.setOpaque(false);
		scroll.setBorder(BorderFactory.createEmptyBorder());
		return scroll;
	}

}
