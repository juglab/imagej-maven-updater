package net.imagej.mavenupdater.model;

import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;

public class UpdateSite extends Dependency {
	private boolean active;

	private boolean useSnapshots;

	private boolean mandatory;
	private boolean outOfDate;

	private String availableVersion;

	private Model model;

	public UpdateSite(String groupId, String artifactId, String version, String type) {
		setGroupId(groupId);
		setArtifactId(artifactId);
		setVersion(version);
		setType(type);
	}

	public UpdateSite(Dependency dependency) {
		this.setVersion(dependency.getVersion());
		this.setArtifactId(dependency.getArtifactId());
		this.setGroupId(dependency.getGroupId());
		this.setType(dependency.getType());
	}

	public UpdateSite(UpdateSite site) {
		this((Dependency) site);
		this.active = site.active;
		this.useSnapshots = site.useSnapshots;
		this.mandatory = site.mandatory;
		this.outOfDate = site.outOfDate;
		this.availableVersion = site.availableVersion;
	}

	public UpdateSite(Model model) {
		setModel(model);
		setVersion(model.getVersion());
		setArtifactId(model.getArtifactId());
		setGroupId(model.getGroupId());
	}

	public boolean isActive() {
		return active || mandatory;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean allowSnapshots() {
		return useSnapshots;
	}

	public void setUseSnapshots(boolean useSnapshots) {
		this.useSnapshots = useSnapshots;
	}

	public boolean getUseSnapshots() {
		return useSnapshots;
	}

	public void setMandatory(boolean mandatory) {
		this.mandatory = mandatory;
	}

	public boolean isMandatory() {
		return mandatory;
	}

	public boolean isOutOfDate() {
		return getAvailableVersion() != null && !getAvailableVersion().equals(getVersion());
	}

	public String getAvailableVersion() {
		return availableVersion;
	}

	public void setAvailableVersion(String availableVersion) {
		this.availableVersion = availableVersion;
	}

	@Override
	public String toString() {
		return getGroupId() + ":" + getArtifactId();
	}

	@Override
	public boolean equals(Object o) {
		UpdateSite other = (UpdateSite) o;
		return getGroupId().equals(other.getGroupId()) && getArtifactId().equals(other.getArtifactId());
	}

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
	}
}
