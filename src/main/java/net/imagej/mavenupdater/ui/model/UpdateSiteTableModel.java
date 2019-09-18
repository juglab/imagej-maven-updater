package net.imagej.mavenupdater.ui.model;

import net.imagej.mavenupdater.model.UpdateSite;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class UpdateSiteTableModel extends AbstractTableModel {
	private final List<UpdateSite> availableUpdateSites;

	public UpdateSiteTableModel(List<UpdateSite> availableUpdateSites) {
		this.availableUpdateSites = availableUpdateSites;
	}

	@Override
	public int getRowCount() {
		return availableUpdateSites.size();
	}

	@Override
	public int getColumnCount() {
		return 5;
	}

	@Override
	public Object getValueAt(int row, int col) {
		if(col == 0) return availableUpdateSites.get(row).isActive();
		if(col == 1) return availableUpdateSites.get(row).getGroupId();
		if(col == 2) return availableUpdateSites.get(row).getArtifactId();
		if(col == 3) return availableUpdateSites.get(row).allowSnapshots();
		if(col == 4) return availableUpdateSites.get(row).getVersion();
		return null;
	}

	@Override
	public String getColumnName(int col) {
		if(col == 0) return "Active";
		if(col == 1) return "groupId";
		if(col == 2) return "artifactId";
		if(col == 3) return "snapshots";
		if(col == 4) return "version";
		return null;
	}

	@Override
	public boolean isCellEditable(int row, int col) {
		return col == 0 || col == 3;
	}

	@Override
	public Class getColumnClass(int column) {
		return getValueAt(0, column).getClass();
	}

	@Override
	public void setValueAt(Object value, int row, int col) {
		if (col == 0) {
			availableUpdateSites.get(row).setActive((Boolean) value);
		}
		else if (col == 3) {
			availableUpdateSites.get(row).setUseSnapshots((Boolean) value);
		}
	}

}
