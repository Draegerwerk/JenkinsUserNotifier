/*
	The MIT License

	Copyright (c) 2017, Draegerwerk AG & Co. KGaA , Yannik Petersen

	Permission is hereby granted, free of charge, to any person obtaining a copy
	of this software and associated documentation files (the "Software"), to deal
	in the Software without restriction, including without limitation the rights
	to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
	copies of the Software, and to permit persons to whom the Software is
	furnished to do so, subject to the following conditions:

	The above copyright notice and this permission notice shall be included in
	all copies or substantial portions of the Software.

	THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
	IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
	FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
	AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
	LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
	OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
	THE SOFTWARE.
*/

package org.jenkinsci.plugins;

import hudson.Extension;
import hudson.model.PageDecorator;
import hudson.util.FormValidation;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Extension
public class JenkinsUserNotifierDecorator extends PageDecorator{
	private boolean aktive;
	private String information;
	private String date;
	private String uuid;

	private String day;
	private String month;
	private String year;
	/**
	 * Default Constructor
	 */
	public JenkinsUserNotifierDecorator() {
		super(JenkinsUserNotifierDecorator.class);
		load();
	}

	public static  JenkinsUserNotifierDecorator getConfig() {
		return PageDecorator.all().get(JenkinsUserNotifierDecorator.class);
	}

	/**
	 * Configuration function called when a new configuration is saved inside jenkins
	 * @param req The StaplerRequest
	 * @param formData The new configuration data stored as an JSONObject
	 * @return success state
	 * @throws FormException
	 */
	@Override
	public boolean configure(StaplerRequest req, JSONObject formData)
			throws FormException {
		aktive = formData.getBoolean("aktive");
		information = formData.getString("information");
		date = formData.getString("date");

		day = formData.getString("day");
		month = formData.getString("month");
		year = formData.getString("year");

		// generate uuid for the cookie that is saved for any client that hides the notification bar
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			md.update(date.getBytes("UTF-8"));
			byte[] bytes = md.digest(information.getBytes("UTF-8"));
			StringBuilder sb = new StringBuilder();
			for (byte aByte : bytes) {
				sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
			}
			uuid = sb.toString();
		}
		catch (NoSuchAlgorithmException | UnsupportedEncodingException e){
			e.printStackTrace();
		}

		save();
		return super.configure(req, formData);
	}

	/**
	 * Getter for the saved notification aktivated state
	 * @return aktive
	 */
	public Boolean getAktive() { return aktive; }
	/**
	 * Getter for the saved Information that is displayed inside the notification bar
	 * @return the saved information
	 */
	public String getInformation() {
		return information;
	}

	/**
	 * Getter for the saved date the notification will be shown
	 * @return the saved date
	 */
	public String getDate() {
		return date;
	}

	/**
	 * Getter for the saved date the notification will be shown
	 * @return the saved date
	 */
	public String getDay() {
		return day;
	}

	/**
	 * Getter for the saved date the notification will be shown
	 * @return the saved date
	 */
	public String getMonth() {
		return month;
	}

	/**
	 * Getter for the saved date the notification will be shown
	 * @return the saved date
	 */
	public String getYear() {
		return year;
	}

	/**
	 * Getter for the notification uuid (timestamp)
	 * @return uuid
	 */
	public String getNotificationUUID() {
		return uuid;
	}

	/**
	 * Compares the current date with the date that was saved,
	 * to evaluate if the Notification Banner should be loaded or not
	 * @return true if current date is smaller than the saved one
	 */
	public boolean getNotificationActiveStatus() throws ParseException {
		// if there is no date given, then show the notification
		if(this.date != null && !Objects.equals(this.date, ""))
		{
			// otherwise check if date is past already
			SimpleDateFormat simpleDateFormat = new SimpleDateFormat("MM/dd/yyyy");
			Date convertedDate = null;

			convertedDate = simpleDateFormat.parse(this.date);

			long currentEpoch = System.currentTimeMillis() / 1000;
			assert convertedDate != null;
			return currentEpoch < (convertedDate.getTime() / 1000) && this.aktive;
		}
		return true;
	}

	/**
	 * validation function that is automatically called, when when the user unfocuses the configuration textbox
	 * @param date the date string that is to be validated
	 * @return the type of validation status: ok, error
	 */
	public FormValidation doCheckDate(@QueryParameter(fixEmpty=true) String date) {
		if (date != null && !Objects.equals(date, "")) {
			Pattern p = Pattern.compile("^(0[123456789]|1[012])/(0[123456789]|[12]\\d|3[01])/\\d\\d\\d\\d$");
			Matcher m = p.matcher(date);
			if(!m.matches()) {
				return FormValidation.error(String.format("%s is not a valid date!", date));
			}
		}
		return FormValidation.ok();
	}

	public FormValidation doCheckDay(String day) {
		System.out.println(day);
		return FormValidation.ok();
	}
}
