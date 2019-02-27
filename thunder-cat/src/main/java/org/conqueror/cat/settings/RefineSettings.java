package org.conqueror.cat.settings;

import org.conqueror.cat.config.KeywordAnalyzerConfig;

import java.util.Locale;


public class RefineSettings {

    public static final String REMOVE_URL = "REMOVE_URL";
    public static final String REMOVE_EMAIL = "REMOVE_EMAIL";

    private boolean removeUrl = false;
    private boolean removeEmail = false;

    protected RefineSettings() {
    }

    public static RefineSettings build(KeywordAnalyzerConfig config) {
        RefineSettings settings = new RefineSettings();

        if (config.removeUrl()) settings.setAction(RefineSettings.REMOVE_URL);
        if (config.removeEmail()) settings.setAction(RefineSettings.REMOVE_EMAIL);

        return settings;
    }

    public void setAction(String type) {
        switch (type.toLowerCase(Locale.ENGLISH)) {
            case REMOVE_URL:
                removeUrl();
                break;
            case REMOVE_EMAIL:
                removeEmail();
                break;
        }
    }

    public RefineSettings removeUrl() {
        removeUrl = true;
        return this;
    }

    public RefineSettings removeEmail() {
        removeEmail = true;
        return this;
    }

    public boolean isUrlRemove() {
        return removeUrl;
    }

    public boolean isEmailRemove() {
        return removeEmail;
    }

}
