package net.imagej.mavenupdater.ui;

import net.imagej.mavenupdater.versioning.model.FileChange;
import net.miginfocom.swing.MigLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.util.List;
import java.util.Vector;

public class FileChangesConfirmationDialog extends JDialog {

	private boolean applyChanges = false;

//	public FileChangesConfirmationDialog(FilesCollection files) {
//		JPanel panel = new JPanel();
//		panel.setLayout(new MigLayout());
//		panel.add(new JScrollPane(createChangesPanel(files)), "span, grow");
//		panel.add(createFooter(), "south");
//		this.setContentPane(panel);
//		setModalityType(ModalityType.DOCUMENT_MODAL);
//		setLocationRelativeTo(null);
//		pack();
//	}

	public FileChangesConfirmationDialog(List<FileChange> changes, Frame parent) {
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout());
		panel.add(createChangesPanel(changes), "span, grow");
		panel.add(createFooter(), "south");
		this.setContentPane(panel);
		setModal(true);
		pack();
		setLocationRelativeTo(parent);
	}

	private Component createChangesPanel(List<FileChange> changes) {
		JList commitDetails = new JList();
		commitDetails.setListData(asVector(changes));
		return new JScrollPane(commitDetails);
	}

	private Vector asVector(List list) {
		Vector res = new Vector();
		res.addAll(list);
		return res;
	}

//	private Component createChangesPanel(FilesCollection files) {
//		FileTable table = new FileTable(files);
//		table.setFiles(files.changes());
//		return table;
//	}

	private Component createFooter() {
		JPanel panel = new JPanel();
		panel.setLayout(new MigLayout());
		panel.add(new JButton(new AbstractAction("Apply") {
			@Override
			public void actionPerformed(ActionEvent e) {
				applyChanges = true;
				dispose();
			}
		}));
		panel.add(new JButton(new AbstractAction("Cancel") {
			@Override
			public void actionPerformed(ActionEvent e) {
				//reset changes?
				applyChanges = false;
				dispose();
			}
		}));
		return panel;
	}

	public boolean fileChangesApproved() {
		setVisible(true);
		requestFocus();
		return applyChanges;
	}
}
