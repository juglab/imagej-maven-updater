package net.imagej.mavenupdater.ui.model;

import net.imagej.mavenupdater.versioning.VersioningService;
import net.imagej.mavenupdater.versioning.model.Session;

import javax.swing.table.AbstractTableModel;
import java.util.List;

public class SessionsTableModel extends AbstractTableModel {
		private final List<Session> sessions;
		private final Session currentSession;

		public SessionsTableModel(VersioningService versioning) throws Exception {
			this.sessions = versioning.getSessions();
			this.currentSession = versioning.getCurrentSession();
		}

		@Override
		public int getRowCount() {
			return sessions.size();
		}

		@Override
		public int getColumnCount() {
			return 2;
		}

		@Override
		public String getColumnName(int col) {
			if(col == 0) return "active";
			if(col == 1) return "name";
			return "";
		}

		@Override
		public Object getValueAt(int row, int col) {
			Session session = sessions.get(row);
			if(session == null) return "";
			if(col == 0) return session.name.equals(currentSession.name);
			if(col == 1) return session.toString();
			return "";
		}

		@Override
		public Class<?> getColumnClass(int i) {
			return getValueAt(0, i).getClass();
		}
	}