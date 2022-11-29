package com.swp.GUI.Settings;

public class ExportSettingsPage {


    private String language;
    private boolean darkTheme;
    private String serverAdress;
    private int serverPort;
    private static ExportSettingsPage settingsInstance;

    private ExportSettingsPage() {
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    public boolean isDarkTheme() {
        return darkTheme;
    }

    public void setDarkTheme(boolean darkTheme) {
        this.darkTheme = darkTheme;
    }

    public String getServerAdress() {
        return serverAdress;
    }

    public void setServerAdress(String serverAdress) {
        this.serverAdress = serverAdress;
    }

    public int getServerPort() {
        return serverPort;
    }

    public void setServerPort(int serverPort) {
        this.serverPort = serverPort;
    }

    public static ExportSettingsPage getSettingsInstance() {
        return settingsInstance;
    }

}
