package net.imagej.mavenupdater.ui.model;

import net.imagej.mavenupdater.model.UpdateSite;

import javax.swing.*;
import javax.swing.table.AbstractTableModel;
import java.util.List;

public class LocalUpdateSiteTableModel extends AbstractTableModel {
	private final List<UpdateSite> updateSites;
	private ImageIcon updatedIcon = new ImageIcon(LocalUpdateSiteTableModel.class.getResource("/icons/updated.png"));
	private ImageIcon updateableIcon = new ImageIcon(LocalUpdateSiteTableModel.class.getResource("/icons/updateable.png"));


	public LocalUpdateSiteTableModel(List<UpdateSite> localUpdateSites) {
		this.updateSites = localUpdateSites;
	}

	@Override
	public int getRowCount() {
		return updateSites.size();
	}

	@Override
	public int getColumnCount() {
		return 4;
	}

	@Override
	public Object getValueAt(int row, int col) {
		if(col == 0) return updateSites.get(row).isOutOfDate() ? updateableIcon : updatedIcon;
		if(col == 1) return updateSites.get(row).getGroupId();
		if(col == 2) return updateSites.get(row).getArtifactId();
		if(col == 3) return updateSites.get(row).getVersion();
		return null;
	}

	@Override
	public String getColumnName(int col) {
		if(col == 0) return "updateable";
		if(col == 1) return "groupId";
		if(col == 2) return "artifactId";
		if(col == 3) return "version";
		return null;
	}

	@Override
	public Class getColumnClass(int column) {
		return getValueAt(0, column).getClass();
	}

}
