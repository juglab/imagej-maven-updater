package net.imagej.mavenupdater.model;

import org.apache.maven.model.Model;

import java.util.ArrayList;
import java.util.List;

public class LocalInstallation {
	private Model model;
	private List<UpdateSite> updateSites = new ArrayList<>();

	public Model getModel() {
		return model;
	}

	public void setModel(Model model) {
		this.model = model;
		updateSites.clear();
		model.getDependencies().forEach(dependency ->  {
			UpdateSite site = new UpdateSite(dependency);
			site.setActive(true);
			updateSites.add(site);
		});
	}

	public List<UpdateSite> getUpdateSites() {
		return updateSites;
	}

	public void setUpdateSites(List<UpdateSite> updateSites) {
		this.updateSites = updateSites;
	}

	public boolean canBeUpdated() {
		//TODO
		return false;
	}

	public List<UpdateSite> getActiveUpdateSites() {
		List<UpdateSite> sites = new ArrayList<>();
		updateSites.forEach(site -> {
			if(site.isActive()) sites.add(site);
		});
		return sites;
	}
}
