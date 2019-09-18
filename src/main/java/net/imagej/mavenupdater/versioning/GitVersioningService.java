package net.imagej.mavenupdater.versioning;

import net.imagej.mavenupdater.util.MavenUpdaterUtil;
import net.imagej.mavenupdater.versioning.model.AppCommit;
import net.imagej.mavenupdater.versioning.model.FileChange;
import net.imagej.mavenupdater.versioning.model.Session;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.FileFilterUtils;
import org.apache.commons.io.filefilter.IOFileFilter;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Ref;

import java.awt.*;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Deborah Schmidt
 */
public class GitVersioningService implements VersioningService, AutoCloseable {

	private File base;
	private Git git;
	private final VersioningUIService uiService;

	public GitVersioningService() {
		this(null);
	}

	public GitVersioningService(Frame parent) {
		uiService = new SwingVersioningService(this, parent);
	}

	public void initialize(File base) {
		this.base = base;
		try {
			commitCurrentChanges();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public void forceCommitCurrentChanges() throws IOException, GitAPIException {
		loadGit();
		GitCommands.commitCurrentStatus(git);
	}

	@Override
	public void commitCurrentChanges() throws IOException, GitAPIException {
		loadGit();
		GitCommands.commitCurrentStatus(git);
	}

	private void loadGit() throws GitAPIException, IOException {
		if(git != null && !git.getRepository().getDirectory().getParentFile().equals(base)) git = null;
		if(git == null) {
			git = GitCommands.initOrLoad(getBaseDirectory());
		}
	}

	@Override
	public List<AppCommit> getCommits() throws GitAPIException, IOException {
		loadGit();
		return GitCommands.getCommits(git);
	}

	@Override
	public void restoreCommit(String id) throws GitAPIException, IOException {
		loadGit();
		GitCommands.restoreStatus(git, id);
	}

	@Override
	public void mergeCommitWithNext(String id) throws GitAPIException, IOException {
		loadGit();
		GitCommands.deleteStatus(git, id);
	}

	@Override
	public List<FileChange> getCurrentChanges() throws GitAPIException, IOException {
		loadGit();
		return GitCommands.getCurrentChanges(git);
	}

	@Override
	public boolean hasUnsavedChanges() throws GitAPIException, IOException {
		loadGit();
		if(getCommits().size() == 0) return true;
		return GitCommands.changedFiles(git);
	}

	@Override
	public void discardChange(FileChange fileChange) throws GitAPIException, IOException {
		loadGit();
		GitCommands.discardChange(git, fileChange);
		GitCommands.commitAmend(git);
	}

	@Override
	public void discardChange(List<FileChange> fileChanges) throws GitAPIException, IOException {
		loadGit();
		for(FileChange fileChange : fileChanges) {
			GitCommands.discardChange(git, fileChange);
		}
		GitCommands.commitAmend(git);
	}

	@Override
	public void undoLastCommit() throws GitAPIException, IOException {
		loadGit();
		GitCommands.undoLastCommit(git);
	}

	@Override
	public List<FileChange> getChanges(String id1, String id2) throws GitAPIException, IOException {
		if(id1 == null || id2 == null) return null;
		loadGit();
		return GitCommands.getChanges(git, id1, id2);
	}

	@Override
	public List<Session> getSessions() throws GitAPIException, IOException {
		loadGit();
		List<Ref> branches = GitCommands.getBranches(git);
		List<Session> result = new ArrayList<>();
		branches.forEach(branch -> {
			Session session = new Session();
			session.name = branch.getName();
			result.add(session);
		});
		return result;
	}

	@Override
	public Session getCurrentSession() throws GitAPIException, IOException {
		loadGit();
		Session session = new Session();
		session.name = GitCommands.getCurrentBranch(git);
		return session;
	}

	@Override
	public void importSessionFromFolder(File dir, String name) throws GitAPIException, IOException {
		loadGit();
		commitCurrentChanges();

		//TODO
		//$ cd /path/to/unrelated
		//$ git init
		//[edit and add files]
		//$ git add .
		//$ git commit -m "Initial commit of unrelated"

		//$ cd /path/to/repo
		//$ git fetch /path/to/unrelated master:unrelated-branch

		//create file indicating that the branch needs to be switched at the beginning of next Fiji startup

		GitCommands.createAndCheckoutEmptyBranch(git, name);
		IOFileFilter gitFilter = FileFilterUtils.prefixFileFilter(".git");
//		IOFileFilter gitFiles = FileFilterUtils.notFileFilter(gitFilter);
		FileUtils.copyDirectory(dir, getBaseDirectory(), true);
		commitCurrentChanges();
	}

	@Override
	public void downloadFreshSession(String name) throws GitAPIException, IOException, ExecutionException, InterruptedException {
		String platform = MavenUpdaterUtil.getPlatform();
		URL url = new URL("https://downloads.imagej.net/fiji/latest/fiji-" + platform + ".zip");
		String downloadDir = Files.createTempDirectory("fiji") + "/fiji-" + platform + ".zip";
		InputStream in = url.openStream();
		Files.copy(in, Paths.get(downloadDir), StandardCopyOption.REPLACE_EXISTING);
		loadGit();
		commitCurrentChanges();

		//TODO
		//$ cd /path/to/unrelated
		//$ git init
		//[edit and add files]
		//$ git add .
		//$ git commit -m "Initial commit of unrelated"

		//$ cd /path/to/repo
		//$ git fetch /path/to/unrelated master:unrelated-branch

		//create file indicating that the branch needs to be switched at the beginning of next Fiji startup


		GitCommands.createAndCheckoutEmptyBranch(git, name);
		unZipIt(downloadDir, getBaseDirectory().getAbsolutePath());
		commitCurrentChanges();
	}

	@Override
	public List<FileChange> getChanges(Session rSession, Session cSession) throws GitAPIException, IOException {
		return getChanges(getCommitID(rSession), getCommitID(cSession));
	}

	@Override
	public VersioningUIService getUI() {
		return uiService;
	}

	@Override
	public Session getSession(String name) throws GitAPIException, IOException {
		loadGit();
		List<Ref> branches = GitCommands.getBranches(git);
		for (Ref branch : branches) {
			Session session = new Session();
			session.name = branch.getName();
			if(session.toString().equals(name))
				return session;
		}
		return null;
	}

	private String getCommitID(Session session) throws GitAPIException, IOException {
		if(session == null) return null;
		loadGit();
		List<Ref> branches = GitCommands.getBranches(git);
		for (Ref branch : branches) {
			if (branch.getName().equals(session.name)) {
				System.out.println(branch.getLeaf().getObjectId().getName());
				return branch.getLeaf().getObjectId().getName();
			}
		}
		return null;
	}

	/**
	 * Unzip it
	 * @param zipFile input zip file
	 * @param outputFolder zip file output folder
	 */
	private void unZipIt(String zipFile, String outputFolder){

		System.out.println("unzip " + zipFile + " to " + outputFolder);

		byte[] buffer = new byte[1024];

		try{

			//create output directory is not exists
			File folder = new File(outputFolder);
			if(!folder.exists()){
				folder.mkdir();
			}

			ZipInputStream zis = new ZipInputStream(new FileInputStream(zipFile));
			ZipEntry ze = zis.getNextEntry();

			while(ze!=null){

				//TODO fix permissions

				boolean executable = false;
				if(ze.getName().contains("Fiji.app/ImageJ-")) {
					executable = true;
				}

				String fileName = ze.getName().replace("Fiji.app/", "");
				File newFile = new File(outputFolder + File.separator + fileName);

//				System.out.println("file unzip : "+ newFile.getAbsoluteFile());

				new File(newFile.getParent()).mkdirs();

				FileOutputStream fos = new FileOutputStream(newFile);

				int len;
				while ((len = zis.read(buffer)) > 0) {
					fos.write(buffer, 0, len);
				}

				fos.close();
				ze = zis.getNextEntry();

				if(executable) {
					newFile.setExecutable(true);
				}

			}

			zis.closeEntry();
			zis.close();

			System.out.println("Done");

		}catch(IOException ex){
			ex.printStackTrace();
		}
	}

	@Override
	public File getBaseDirectory() {
		return base;
	}

	@Override
	public void setBaseDirectory(File dir) {
		base = dir;
	}

	@Override
	public void copyCurrentSession(String newSessionName) throws GitAPIException, IOException {
		loadGit();
		GitCommands.createAndCheckoutBranch(git, newSessionName);
	}

	@Override
	public void openSession(String name) throws GitAPIException, IOException {
		loadGit();
		if(name == "default") name = "master";
		//TODO create file indicating to checkout this branch at the beginning of the next Fiji startup
		GitCommands.checkoutBranch(git, name);
	}

	@Override
	public void renameSession(String oldSessionName, String newSessionName) throws Exception {
		loadGit();
		GitCommands.renameBranch(git, oldSessionName, newSessionName);
	}

	@Override
	public void deleteSession(String sessionName) throws Exception {
		loadGit();
		GitCommands.deleteBranch(git, sessionName);
	}

	@Override
	public void close() {
		git.close();
	}
}
