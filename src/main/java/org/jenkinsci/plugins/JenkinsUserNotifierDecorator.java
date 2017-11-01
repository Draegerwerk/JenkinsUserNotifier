package org.jenkinsci.plugins;

import hudson.Extension;
import hudson.model.PageDecorator;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.StaplerRequest;

@Extension
public class JenkinsUserNotifierDecorator extends PageDecorator{
	private String information;
	private String date;

	public JenkinsUserNotifierDecorator() {
		super(JenkinsUserNotifierDecorator.class);
		load();
	}

	@Override
	public boolean configure(StaplerRequest req, JSONObject formData)
			throws FormException {
		information = formData.getString("information");
		date = formData.getString("date");
		save();
		return super.configure(req, formData);
	}

	public String getInformation() {
		return information;
	}

	public String getDate() {
		return date;
	}
}
