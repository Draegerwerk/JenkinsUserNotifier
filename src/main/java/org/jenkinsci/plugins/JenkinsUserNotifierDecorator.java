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
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;

import org.kohsuke.stapler.QueryParameter;
import org.kohsuke.stapler.StaplerRequest;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Extension
public class JenkinsUserNotifierDecorator extends PageDecorator{
	private boolean aktive;
	private String information;
	private String date;
	private String uuid;

    private List<TranslationConfig> translations;

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
		this.aktive = formData.getBoolean("aktive");
		this.information = formData.getString("information");
		this.date = formData.getString("date");

        this.translations = new ArrayList<TranslationConfig>();

        // check for existence of translations configured
        if( formData.containsKey("translations") ) {
            // are there multiple translations configured
            JSONArray translations = formData.optJSONArray("translations");
            if( translations != null ) {
                for (int i = 0; i < translations.size(); i++) {
                    JSONObject translation = translations.getJSONObject(i);

                    String locale = translation.getString("locale");
                    String text = translation.getString("translation");

                    this.translations.add(new TranslationConfig(text, locale));
                }
            } else {
                // there is probably just one translation configured
                JSONObject translation = formData.optJSONObject("translations");
                if( translation != null ) {
                    String locale = translation.getString("locale");
                    String text = translation.getString("translation");

                    this.translations.add(new TranslationConfig(text, locale));
                }
            }
        }

		// generate uuid for the cookie that is saved for any client that hides the notification bar
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-512");
			md.update(this.date.getBytes("UTF-8"));
			byte[] bytes = md.digest(this.information.getBytes("UTF-8"));
			StringBuilder sb = new StringBuilder();
			for (byte aByte : bytes) {
				sb.append(Integer.toString((aByte & 0xff) + 0x100, 16).substring(1));
			}
			this.uuid = sb.toString();
		}
		catch (NoSuchAlgorithmException | UnsupportedEncodingException e){
			e.printStackTrace();
		}

		save();
		return super.configure(req, formData);
	}

	/**
	 * Getter for the saved Information that is displayed inside the notification bar
	 * @return the saved information
	 */
	public String getInformation() {
		return this.information;
	}

    /**
     * Un use able because of "chicken egg problem" javascript is needed to get browser locale
     * but jelly is interpreted before javascript
     *
     * Getter for the saved Information that is displayed inside the notification bar
     * it looks into the translations and evaluates if the requested locale is available
     * and returns the specific text for that. it defaults to the standard Information (en_GB)
     * @param locale the String representation of the requested locale
     * @return the requested text
     */
    @Deprecated
	public String getInformationByLocale(String locale) {
	    String return_text = this.information;

        for(TranslationConfig translation: translations) {
            if( translation.getLocale().equals(locale)) {
                return_text = translation.getTranslation();
                break;
            }
        }

        return return_text;
    }

	/**
	 * Getter for the saved date the notification will be shown
	 * @return the saved date
	 */
	public String getDate() {
		return this.date;
	}

	/**
	 * Getter for the saved notification aktivated state
	 * @return aktive
	 */
	public Boolean getAktive() { return this.aktive; }

	/**
	 * Getter for the notification uuid (timestamp)
	 * @return uuid
	 */
	public String getNotificationUUID() {
		return this.uuid;
	}

    /**
     * Getter for the translation configurations
     * @return List of Translation Configurations
     */
    public List<TranslationConfig> getTranslations() {
	    return this.translations;
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
}
