package org.jenkinsci.plugins;

import hudson.Extension;
import hudson.markup.MarkupFormatter;
import hudson.model.Describable;
import hudson.model.Descriptor;
import hudson.util.ListBoxModel;
import jenkins.model.Jenkins;
import net.sf.json.JSONObject;
import org.kohsuke.stapler.DataBoundConstructor;
import org.kohsuke.stapler.StaplerRequest;

import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;

public class TranslationConfig implements Describable<TranslationConfig> {
    private String translation;
    private String locale;

    @Override
    public Descriptor<TranslationConfig> getDescriptor() {
        return Jenkins.getInstance().getDescriptorOrDie(getClass());
    }

    @DataBoundConstructor
    public TranslationConfig(String translation, String locale) {
        this.translation = translation;
        this.locale = locale;
    }

    /**
     * Getter for the locale saved for this translation
     * @return locale representation that was selected
     */
    public String getLocale() {
        return this.locale;
    }

    /**
     * Getter for the translation text that will be localized
     * @return translation text
     */
    public String getTranslation() {
        try {
            MarkupFormatter formatter = Jenkins.getInstance().getMarkupFormatter();
            return formatter.translate(this.translation);
        } catch (IOException e) {
            e.printStackTrace();
            return this.translation;
        }
    }

    @Extension(optional = true)
    public static class DescriptorImpl extends Descriptor<TranslationConfig> {

        public DescriptorImpl() {
            load();
        }

        @Override
        public boolean configure(final StaplerRequest req, final JSONObject formData)
                throws FormException {
            save();
            return super.configure(req, formData);
        }

        /**
         * Function for filling the Localization DropDown for Translations
         * @return List of Strings. A String is one Representation of of a 2 or 5 letter locale
         */
        public ListBoxModel doFillLocaleItems() {
            ListBoxModel items = new ListBoxModel();

            Locale[] locales = Locale.getAvailableLocales();
            Arrays.sort(locales, new Comparator<Locale>() {
                @Override
                public int compare(Locale o1, Locale o2) {
                    return o1.toString().compareTo(o2.toString());
                }
            });

            for (Locale l: locales) {
                items.add(l.toString(), l.toString().replace("_", "").toLowerCase());
            }
            return items;
        }
    }
}
