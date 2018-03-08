package org.jenkinsci.plugins;

import hudson.util.FormValidation;
import net.sf.json.JSON;
import net.sf.json.JSONArray;
import net.sf.json.JSONObject;
import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.*;
import org.jvnet.hudson.test.JenkinsRule;

public class JenkinsUserNotifierTest {
	@Rule
	public JenkinsRule j = new JenkinsRule();


	// without translation
	@Test
	public void testGetDate() throws Exception {
		JenkinsUserNotifierDecorator decorator = new JenkinsUserNotifierDecorator();

        boolean TestActivation = true;
		String TestInformation = "This is a random Text to be displayed !!!";
		String TestDate = "12/12/2017";

		JSONObject json = new JSONObject();
		json.put("active", TestActivation);
		json.put("date", TestDate);
		json.put("information", TestInformation);

		decorator.configure(null, json);

		assertEquals(TestDate, decorator.getDate());
	}

	@Test
	public void testGetInformation() throws Exception {
		JenkinsUserNotifierDecorator decorator = new JenkinsUserNotifierDecorator();

        boolean TestActivation = true;
        String TestInformation = "This is a random Text to be displayed !!!";
        String TestDate = "12/12/2017";

        JSONObject json = new JSONObject();
        json.put("active", TestActivation);
        json.put("date", TestDate);
        json.put("information", TestInformation);

        decorator.configure(null, json);

		assertEquals(TestInformation, decorator.getInformation());
	}

	@Test
	public void testNotificationActiveStatusActive() throws Exception {
		final JenkinsUserNotifierDecorator decorator = JenkinsUserNotifierDecorator.getConfig();

        boolean TestActivation = true;
		String TestInformation = "This is a random Text to be displayed !!!";
		// Date that will be reached when this Project doesn't exist anymore
		String TestDate = "12/12/9999";

		JSONObject json = new JSONObject();
        json.put("active", TestActivation);
		json.put("date", TestDate);
		json.put("information", TestInformation);

		decorator.configure(null, json);

		assertTrue(decorator.getNotificationActiveStatus());
	}

    @Test
    public void testNotificationActiveStatusInactiveDueActive() throws Exception {
        final JenkinsUserNotifierDecorator decorator = JenkinsUserNotifierDecorator.getConfig();

        boolean TestActivation = false;
        String TestInformation = "This is a random Text to be displayed !!!";
        // Date that already passed
        String TestDate = "12/12/9999";

        JSONObject json = new JSONObject();
        json.put("active", TestActivation);
        json.put("date", TestDate);
        json.put("information", TestInformation);

        decorator.configure(null, json);

        assertFalse(decorator.getNotificationActiveStatus());
    }

	@Test
	public void testNotificationActiveStatusInactiveDueDate() throws Exception {
		final JenkinsUserNotifierDecorator decorator = JenkinsUserNotifierDecorator.getConfig();

        boolean TestActivation = true;
		String TestInformation = "This is a random Text to be displayed !!!";
		// Date that already passed
		String TestDate = "12/12/1970";

		JSONObject json = new JSONObject();
        json.put("active", TestActivation);
		json.put("date", TestDate);
		json.put("information", TestInformation);

		decorator.configure(null, json);

		assertFalse(decorator.getNotificationActiveStatus());
	}

	@Test
	public void testCheckDateOK() throws Exception {
		JenkinsUserNotifierDecorator decorator = new JenkinsUserNotifierDecorator();

        boolean TestActivation = true;
		String TestInformation = "This is a random Text to be displayed !!!";
		String TestDate = "12/12/2017";

		JSONObject json = new JSONObject();
        json.put("active", TestActivation);
		json.put("date", TestDate);
		json.put("information", TestInformation);

		decorator.configure(null, json);

		assertEquals(FormValidation.Kind.OK, decorator.doCheckDate(TestDate).kind);
	}

	@Test
	public void testCheckDateHighDay() throws Exception {
		JenkinsUserNotifierDecorator decorator = new JenkinsUserNotifierDecorator();

        boolean TestActivation = true;
		String TestInformation = "This is a random Text to be displayed !!!";
		String TestDate = "12/40/2017";

		JSONObject json = new JSONObject();
        json.put("active", TestActivation);
		json.put("date", TestDate);
		json.put("information", TestInformation);

		decorator.configure(null, json);

		assertEquals(FormValidation.Kind.ERROR, decorator.doCheckDate(TestDate).kind);
	}

	@Test
	public void testCheckDateHighMonth() throws Exception {
		JenkinsUserNotifierDecorator decorator = new JenkinsUserNotifierDecorator();

        boolean TestActivation = true;
		String TestInformation = "This is a random Text to be displayed !!!";
		String TestDate = "20/12/2017";

		JSONObject json = new JSONObject();
        json.put("active", TestActivation);
		json.put("date", TestDate);
		json.put("information", TestInformation);

		decorator.configure(null, json);

		assertEquals(FormValidation.Kind.ERROR, decorator.doCheckDate(TestDate).kind);
	}

	@Test
	public void testCheckDateShortYear() throws Exception {
		JenkinsUserNotifierDecorator decorator = new JenkinsUserNotifierDecorator();

        boolean TestActivation = true;
		String TestInformation = "This is a random Text to be displayed !!!";
		String TestDate = "12/12/17";

		JSONObject json = new JSONObject();
        json.put("active", TestActivation);
		json.put("date", TestDate);
		json.put("information", TestInformation);

		decorator.configure(null, json);

		assertEquals(FormValidation.Kind.ERROR, decorator.doCheckDate(TestDate).kind);
	}

	@Test
	public void testCheckDateWrongSeparator() throws Exception {
		JenkinsUserNotifierDecorator decorator = new JenkinsUserNotifierDecorator();

        boolean TestActivation = true;
		String TestInformation = "This is a random Text to be displayed !!!";
		String TestDate = "12-12-17";

		JSONObject json = new JSONObject();
        json.put("active", TestActivation);
		json.put("date", TestDate);
		json.put("information", TestInformation);

		decorator.configure(null, json);

		assertEquals(FormValidation.Kind.ERROR, decorator.doCheckDate(TestDate).kind);
	}

    @Test
    public void testNoTranslation() throws Exception {
        JenkinsUserNotifierDecorator decorator = new JenkinsUserNotifierDecorator();

        boolean TestActivation = true;
        String TestInformation = "This is a random Text to be displayed !!!";
        String TestDate = "12-12-17";

        JSONObject json = new JSONObject();
        json.put("active", TestActivation);
        json.put("date", TestDate);
        json.put("information", TestInformation);

        decorator.configure(null, json);

        // count of Translations
        assertTrue(decorator.getTranslations().size() == 0);
    }

	// with translation
    @Test
    public void testGetDateWithTranslation() throws Exception {
        JenkinsUserNotifierDecorator decorator = new JenkinsUserNotifierDecorator();

        boolean TestActivation = true;
        String TestInformation = "This is a random Text to be displayed !!!";
        String TestDate = "12/12/2017";

        // Translation
        String locale = "de";
        String translation = "Dies ist ein zufälliger Text der angezeigt wird !!!";

        JSONObject json = new JSONObject();
        json.put("active", TestActivation);
        json.put("date", TestDate);
        json.put("information", TestInformation);

        JSONArray json_translations = new JSONArray();

        JSONObject json_trans00 = new JSONObject();
        json_trans00.put("locale", locale);
        json_trans00.put("translation", translation);

        json_translations.add(json_trans00);

        json.put("translations", json_translations);

        decorator.configure(null, json);

        assertEquals(TestDate, decorator.getDate());
    }

    @Test
    public void testGetInformationWithTranslation() throws Exception {
        JenkinsUserNotifierDecorator decorator = new JenkinsUserNotifierDecorator();

        boolean TestActivation = true;
        String TestInformation = "This is a random Text to be displayed !!!";
        String TestDate = "12/12/2017";

        // Translation
        String locale = "de";
        String translation = "Dies ist ein zufälliger Text der angezeigt wird !!!";

        JSONObject json = new JSONObject();
        json.put("active", TestActivation);
        json.put("date", TestDate);
        json.put("information", TestInformation);

        JSONArray json_translations = new JSONArray();

        JSONObject json_trans00 = new JSONObject();
        json_trans00.put("locale", locale);
        json_trans00.put("translation", translation);

        json_translations.add(json_trans00);

        json.put("translations", json_translations);

        decorator.configure(null, json);

        assertEquals(TestInformation, decorator.getInformation());
    }

    @Test
    public void testNotificationActiveStatusActiveWithTranslation() throws Exception {
        final JenkinsUserNotifierDecorator decorator = JenkinsUserNotifierDecorator.getConfig();

        boolean TestActivation = true;
        String TestInformation = "This is a random Text to be displayed !!!";
        // Date that will be reached when this Project doesn't exist anymore
        String TestDate = "12/12/9999";

        // Translation
        String locale = "de";
        String translation = "Dies ist ein zufälliger Text der angezeigt wird !!!";

        JSONObject json = new JSONObject();
        json.put("active", TestActivation);
        json.put("date", TestDate);
        json.put("information", TestInformation);

        JSONArray json_translations = new JSONArray();

        JSONObject json_trans00 = new JSONObject();
        json_trans00.put("locale", locale);
        json_trans00.put("translation", translation);

        json_translations.add(json_trans00);

        json.put("translations", json_translations);

        decorator.configure(null, json);

        assertTrue(decorator.getNotificationActiveStatus());
    }

    @Test
    public void testNotificationActiveStatusInactiveDueActiveWithTranslation() throws Exception {
        final JenkinsUserNotifierDecorator decorator = JenkinsUserNotifierDecorator.getConfig();

        boolean TestActivation = false;
        String TestInformation = "This is a random Text to be displayed !!!";
        // Date that already passed
        String TestDate = "12/12/9999";

        // Translation
        String locale = "de";
        String translation = "Dies ist ein zufälliger Text der angezeigt wird !!!";

        JSONObject json = new JSONObject();
        json.put("active", TestActivation);
        json.put("date", TestDate);
        json.put("information", TestInformation);

        JSONArray json_translations = new JSONArray();

        JSONObject json_trans00 = new JSONObject();
        json_trans00.put("locale", locale);
        json_trans00.put("translation", translation);

        json_translations.add(json_trans00);

        json.put("translations", json_translations);

        decorator.configure(null, json);

        assertFalse(decorator.getNotificationActiveStatus());
    }

    @Test
    public void testNotificationActiveStatusInactiveDueDateWithTranslation() throws Exception {
        final JenkinsUserNotifierDecorator decorator = JenkinsUserNotifierDecorator.getConfig();

        boolean TestActivation = true;
        String TestInformation = "This is a random Text to be displayed !!!";
        // Date that already passed
        String TestDate = "12/12/1970";

        // Translation
        String locale = "de";
        String translation = "Dies ist ein zufälliger Text der angezeigt wird !!!";

        JSONObject json = new JSONObject();
        json.put("active", TestActivation);
        json.put("date", TestDate);
        json.put("information", TestInformation);

        JSONArray json_translations = new JSONArray();

        JSONObject json_trans00 = new JSONObject();
        json_trans00.put("locale", locale);
        json_trans00.put("translation", translation);

        json_translations.add(json_trans00);

        json.put("translations", json_translations);

        decorator.configure(null, json);

        assertFalse(decorator.getNotificationActiveStatus());
    }

    @Test
    public void testCheckDateOKWithTranslation() throws Exception {
        JenkinsUserNotifierDecorator decorator = new JenkinsUserNotifierDecorator();

        boolean TestActivation = true;
        String TestInformation = "This is a random Text to be displayed !!!";
        String TestDate = "12/12/2017";

        // Translation
        String locale = "de";
        String translation = "Dies ist ein zufälliger Text der angezeigt wird !!!";

        JSONObject json = new JSONObject();
        json.put("active", TestActivation);
        json.put("date", TestDate);
        json.put("information", TestInformation);

        JSONArray json_translations = new JSONArray();

        JSONObject json_trans00 = new JSONObject();
        json_trans00.put("locale", locale);
        json_trans00.put("translation", translation);

        json_translations.add(json_trans00);

        json.put("translations", json_translations);

        decorator.configure(null, json);

        assertEquals(FormValidation.Kind.OK, decorator.doCheckDate(TestDate).kind);
    }

    @Test
    public void testCheckDateHighDayWithTranslation() throws Exception {
        JenkinsUserNotifierDecorator decorator = new JenkinsUserNotifierDecorator();

        boolean TestActivation = true;
        String TestInformation = "This is a random Text to be displayed !!!";
        String TestDate = "12/40/2017";

        // Translation
        String locale = "de";
        String translation = "Dies ist ein zufälliger Text der angezeigt wird !!!";

        JSONObject json = new JSONObject();
        json.put("active", TestActivation);
        json.put("date", TestDate);
        json.put("information", TestInformation);

        JSONArray json_translations = new JSONArray();

        JSONObject json_trans00 = new JSONObject();
        json_trans00.put("locale", locale);
        json_trans00.put("translation", translation);

        json_translations.add(json_trans00);

        json.put("translations", json_translations);

        decorator.configure(null, json);

        assertEquals(FormValidation.Kind.ERROR, decorator.doCheckDate(TestDate).kind);
    }

    @Test
    public void testCheckDateHighMonthWithTranslation() throws Exception {
        JenkinsUserNotifierDecorator decorator = new JenkinsUserNotifierDecorator();

        boolean TestActivation = true;
        String TestInformation = "This is a random Text to be displayed !!!";
        String TestDate = "20/12/2017";

        // Translation
        String locale = "de";
        String translation = "Dies ist ein zufälliger Text der angezeigt wird !!!";

        JSONObject json = new JSONObject();
        json.put("active", TestActivation);
        json.put("date", TestDate);
        json.put("information", TestInformation);

        JSONArray json_translations = new JSONArray();

        JSONObject json_trans00 = new JSONObject();
        json_trans00.put("locale", locale);
        json_trans00.put("translation", translation);

        json_translations.add(json_trans00);

        json.put("translations", json_translations);

        decorator.configure(null, json);

        assertEquals(FormValidation.Kind.ERROR, decorator.doCheckDate(TestDate).kind);
    }

    @Test
    public void testCheckDateShortYearWithTranslation() throws Exception {
        JenkinsUserNotifierDecorator decorator = new JenkinsUserNotifierDecorator();

        boolean TestActivation = true;
        String TestInformation = "This is a random Text to be displayed !!!";
        String TestDate = "12/12/17";

        // Translation
        String locale = "de";
        String translation = "Dies ist ein zufälliger Text der angezeigt wird !!!";

        JSONObject json = new JSONObject();
        json.put("active", TestActivation);
        json.put("date", TestDate);
        json.put("information", TestInformation);

        JSONArray json_translations = new JSONArray();

        JSONObject json_trans00 = new JSONObject();
        json_trans00.put("locale", locale);
        json_trans00.put("translation", translation);

        json_translations.add(json_trans00);

        json.put("translations", json_translations);

        decorator.configure(null, json);

        assertEquals(FormValidation.Kind.ERROR, decorator.doCheckDate(TestDate).kind);
    }

    @Test
    public void testCheckDateWrongSeparatorWithTranslation() throws Exception {
        JenkinsUserNotifierDecorator decorator = new JenkinsUserNotifierDecorator();

        boolean TestActivation = true;
        String TestInformation = "This is a random Text to be displayed !!!";
        String TestDate = "12-12-17";

        // Translation
        String locale = "de";
        String translation = "Dies ist ein zufälliger Text der angezeigt wird !!!";

        JSONObject json = new JSONObject();
        json.put("active", TestActivation);
        json.put("date", TestDate);
        json.put("information", TestInformation);

        JSONArray json_translations = new JSONArray();

        JSONObject json_trans00 = new JSONObject();
        json_trans00.put("locale", locale);
        json_trans00.put("translation", translation);

        json_translations.add(json_trans00);

        json.put("translations", json_translations);

        decorator.configure(null, json);

        assertEquals(FormValidation.Kind.ERROR, decorator.doCheckDate(TestDate).kind);
    }

    @Test
    public void testSingleTranslation() throws Exception {
        JenkinsUserNotifierDecorator decorator = new JenkinsUserNotifierDecorator();

        boolean TestActivation = true;
        String TestInformation = "This is a random Text to be displayed !!!";
        String TestDate = "12-12-17";

        // Translation
        String locale = "de";
        String translation = "Dies ist ein zufälliger Text der angezeigt wird !!!";

        JSONObject json = new JSONObject();
        json.put("active", TestActivation);
        json.put("date", TestDate);
        json.put("information", TestInformation);

        JSONObject json_trans00 = new JSONObject();
        json_trans00.put("locale", locale);
        json_trans00.put("translation", translation);

        json.put("translations", json_trans00);

        decorator.configure(null, json);

        // count of Translations
        assertTrue(decorator.getTranslations().size() == 1);

        // Translations content check
        assertTrue(decorator.getTranslations().get(0).getLocale().equals(locale));
        assertTrue(decorator.getTranslations().get(0).getTranslation().equals(translation));
    }

    @Test
    public void testMultiTranslation() throws Exception {
        JenkinsUserNotifierDecorator decorator = new JenkinsUserNotifierDecorator();

        boolean TestActivation = true;
        String TestInformation = "This is a random Text to be displayed !!!";
        String TestDate = "12-12-17";

        // Translation
        String locale00 = "de";
        String translation00 = "Dies ist ein zufälliger Text der angezeigt wird !!!";
        String locale01 = "sp";
        String translation01 = "¡Este es un texto aleatorio que se mostrará!";

        JSONObject json = new JSONObject();
        json.put("active", TestActivation);
        json.put("date", TestDate);
        json.put("information", TestInformation);

        JSONArray json_translations = new JSONArray();

        JSONObject json_trans00 = new JSONObject();
        json_trans00.put("locale", locale00);
        json_trans00.put("translation", translation00);

        json_translations.add(json_trans00);

        JSONObject json_trans01 = new JSONObject();
        json_trans01.put("locale", locale01);
        json_trans01.put("translation", translation01);

        json_translations.add(json_trans01);

        json.put("translations", json_translations);

        decorator.configure(null, json);

        // count of Translations
        assertTrue(decorator.getTranslations().size() == 2);

        // Translations content check
        assertTrue(decorator.getTranslations().get(0).getLocale().equals(locale00));
        assertTrue(decorator.getTranslations().get(0).getTranslation().equals(translation00));

        assertTrue(decorator.getTranslations().get(1).getLocale().equals(locale01));
        assertTrue(decorator.getTranslations().get(1).getTranslation().equals(translation01));
    }
}
