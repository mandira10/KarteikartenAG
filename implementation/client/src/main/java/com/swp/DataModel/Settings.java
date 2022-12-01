package com.swp.DataModel;

public class Settings 
{
    public enum Language
    {
        ENGLISH,
        GERMAN,
    };
    private static Settings oSettingsInstance = null;
    private Language iLanguage;
    private String sServerAddress;
    private boolean bDarkTheme;
    private int iServerPort;

    private Settings()
    {
        //Toolbox.loadFile();
    }


    public static Settings getInstance()
    {
        if(oSettingsInstance == null)
            oSettingsInstance = new Settings();
        return oSettingsInstance;
    }

    public String languageToString()
    {
        switch(iLanguage)
        {
            case ENGLISH: return "English";
            case GERMAN:  return "German";
        }
        return "Unknown";
    }


    //
    // Setter
    //
    public void setLanguage(Language lang)    { this.iLanguage      = lang; }
    public void setServerAddress(String addr) { this.sServerAddress = addr; }
    public void setDarkTheme(boolean dark)    { this.bDarkTheme     = dark; }
    public void setServerPort(int port)       { this.iServerPort    = port; }


    //
    // Getter
    //
    public Language getLanguage()             { return iLanguage; }
    public String getServerAddress()          { return sServerAddress; }
    public boolean isDarkTheme()              { return bDarkTheme; }
    public int getServerPort()                { return iServerPort; }
}
