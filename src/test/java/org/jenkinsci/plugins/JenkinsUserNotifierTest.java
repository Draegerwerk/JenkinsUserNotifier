package org.jenkinsci.plugins;

import hudson.util.FormValidation;
import net.sf.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.*;
import org.jvnet.hudson.test.JenkinsRule;

public class JenkinsUserNotifierTest {
	@Rule
	public JenkinsRule j = new JenkinsRule();

	@Test
	public void testGetDate() throws Exception {
		JenkinsUserNotifierDecorator decorator = new JenkinsUserNotifierDecorator();

		String TestInformation = "This is a random Text to be displayed !!!";
		String TestDate = "12/12/2017";

		JSONObject json = new JSONObject();
		json.put("date", TestDate);
		json.put("information", TestInformation);

		decorator.configure(null, json);

		assertEquals(TestDate, decorator.getDate());
	}

	@Test
	public void testGetInformation() throws Exception {
		JenkinsUserNotifierDecorator decorator = new JenkinsUserNotifierDecorator();

		String TestInformation = "This is a random Text to be displayed !!!";
		String TestDate = "12/12/2017";

		JSONObject json = new JSONObject();
		json.put("date", TestDate);
		json.put("information", TestInformation);

		decorator.configure(null, json);

		assertEquals(TestInformation, decorator.getInformation());
	}

	@Test
	public void testNotificationActiveStatusActive() throws Exception {
		final JenkinsUserNotifierDecorator decorator = JenkinsUserNotifierDecorator.getConfig();

		String TestInformation = "This is a random Text to be displayed !!!";
		// Date that will be reached when this Project doesn't exist anymore
		String TestDate = "12/12/9999";

		JSONObject json = new JSONObject();
		json.put("date", TestDate);
		json.put("information", TestInformation);

		decorator.configure(null, json);

		assertTrue(decorator.getNotificationActiveStatus());
	}

	@Test
	public void testNotificationActiveStatusInactive() throws Exception {
		final JenkinsUserNotifierDecorator decorator = JenkinsUserNotifierDecorator.getConfig();

		String TestInformation = "This is a random Text to be displayed !!!";
		// Date that already passed
		String TestDate = "12/12/1970";

		JSONObject json = new JSONObject();
		json.put("date", TestDate);
		json.put("information", TestInformation);

		decorator.configure(null, json);

		assertFalse(decorator.getNotificationActiveStatus());
	}

	@Test
	public void testCheckDateOK() throws Exception {
		JenkinsUserNotifierDecorator decorator = new JenkinsUserNotifierDecorator();

		String TestInformation = "This is a random Text to be displayed !!!";
		String TestDate = "12/12/2017";

		JSONObject json = new JSONObject();
		json.put("date", TestDate);
		json.put("information", TestInformation);

		decorator.configure(null, json);

		assertEquals(FormValidation.Kind.OK, decorator.doCheckDate(TestDate).kind);
	}

	@Test
	public void testCheckDateHighDay() throws Exception {
		JenkinsUserNotifierDecorator decorator = new JenkinsUserNotifierDecorator();

		String TestInformation = "This is a random Text to be displayed !!!";
		String TestDate = "12/40/2017";

		JSONObject json = new JSONObject();
		json.put("date", TestDate);
		json.put("information", TestInformation);

		decorator.configure(null, json);

		assertEquals(FormValidation.Kind.ERROR, decorator.doCheckDate(TestDate).kind);
	}

	@Test
	public void testCheckDateHighMonth() throws Exception {
		JenkinsUserNotifierDecorator decorator = new JenkinsUserNotifierDecorator();

		String TestInformation = "This is a random Text to be displayed !!!";
		String TestDate = "20/12/2017";

		JSONObject json = new JSONObject();
		json.put("date", TestDate);
		json.put("information", TestInformation);

		decorator.configure(null, json);

		assertEquals(FormValidation.Kind.ERROR, decorator.doCheckDate(TestDate).kind);
	}

	@Test
	public void testCheckDateShortYear() throws Exception {
		JenkinsUserNotifierDecorator decorator = new JenkinsUserNotifierDecorator();

		String TestInformation = "This is a random Text to be displayed !!!";
		String TestDate = "12/12/17";

		JSONObject json = new JSONObject();
		json.put("date", TestDate);
		json.put("information", TestInformation);

		decorator.configure(null, json);

		assertEquals(FormValidation.Kind.ERROR, decorator.doCheckDate(TestDate).kind);
	}

	@Test
	public void testCheckDateWrongSeparator() throws Exception {
		JenkinsUserNotifierDecorator decorator = new JenkinsUserNotifierDecorator();

		String TestInformation = "This is a random Text to be displayed !!!";
		String TestDate = "12-12-17";

		JSONObject json = new JSONObject();
		json.put("date", TestDate);
		json.put("information", TestInformation);

		decorator.configure(null, json);

		assertEquals(FormValidation.Kind.ERROR, decorator.doCheckDate(TestDate).kind);
	}
}
