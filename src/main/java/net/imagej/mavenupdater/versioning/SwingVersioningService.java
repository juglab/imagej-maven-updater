package net.imagej.mavenupdater.versioning;

import net.imagej.mavenupdater.ui.FileChangesConfirmationDialog;
import net.imagej.mavenupdater.versioning.model.FileChange;
import net.imagej.mavenupdater.versioning.ui.SessionsFrame;
import net.imagej.mavenupdater.versioning.ui.VersioningFrame;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

public class SwingVersioningService implements VersioningUIService {

	private final VersioningService versioningService;
	private final Frame parent;

	public SwingVersioningService(VersioningService versioningService, Frame parent) {
		this.versioningService = versioningService;
		this.parent = parent;
	}

	@Override
	public boolean approveChanges(List<FileChange> changes, String message) {
		if (changes.iterator().hasNext()) {
//			files.markForUpdate(false);
			return changesApproved(changes);
		} else {
			//TODO implement differently
			//SwingTools.showMessageBox(parent, "Nothing to change on your installation.", JOptionPane.INFORMATION_MESSAGE);
		}
		return true;
	}

	private boolean changesApproved(List<FileChange> changes) {
		AtomicReference<FileChangesConfirmationDialog> dialog = new AtomicReference<>();
		if (SwingUtilities.isEventDispatchThread()) {
			dialog.set(new FileChangesConfirmationDialog(changes, parent));
		} else {
			try {
				SwingUtilities.invokeAndWait(() -> {
					dialog.set(new FileChangesConfirmationDialog(changes, parent));
				});
			} catch (InterruptedException | InvocationTargetException e) {
				e.printStackTrace();
			}
		}
		return dialog.get().fileChangesApproved();
	}

//	@Override
//	public boolean approveChanges(FilesCollection files) {
//		Frame parent = null;
//		if(files.changes().iterator().hasNext()) {
////			files.markForUpdate(false);
//			return fileChangesApproved(files);
//		} else {
//			SwingTools.showMessageBox(parent, "Nothing to change on your installation.", JOptionPane.INFORMATION_MESSAGE);
//		}
//		return true;
//	}

//	@Override
//	public Progress getProgressDialog() {
//		return new ProgressDialog(null);
//	}
//
//	private boolean fileChangesApproved(FilesCollection files) {
//		AtomicReference<FileChangesConfirmationDialog> dialog = new AtomicReference<>();
//		if (SwingUtilities.isEventDispatchThread()) {
//			dialog.set(new FileChangesConfirmationDialog(files));
//		} else {
//			try {
//				SwingUtilities.invokeAndWait(() -> {
//					dialog.set(new FileChangesConfirmationDialog(files));
//				});
//			} catch (InterruptedException | InvocationTargetException e) {
//				e.printStackTrace();
//			}
//		}
//		return dialog.get().fileChangesApproved();
//	}

	@Override
	public void showSessionHistory() {
		VersioningFrame frame = new VersioningFrame();
		frame.init();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
		frame.checkForChanges();
	}

	@Override
	public <T> T askFor(String question, Class<T> returnType) {
		return (T) JOptionPane.showInputDialog(question);
	}

	@Override
	public void showSessions() {
		SessionsFrame frame = new SessionsFrame(versioningService);
		frame.init();
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
}
