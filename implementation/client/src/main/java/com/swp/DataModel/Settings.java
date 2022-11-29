package com.swp.DataModel;

public class Settings 
{
    private static Settings oSettingsInstance = null;
    private String sLanguage;
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


    //
    // Setter
    //
    public void setLanguage(String lang)      { this.sLanguage      = lang; }
    public void setServerAddress(String addr) { this.sServerAddress = addr; }
    public void setDarkTheme(boolean dark)    { this.bDarkTheme     = dark; }
    public void setServerPort(int port)       { this.iServerPort    = port; }


    //
    // Getter
    //
    public String getLanguage()               { return sLanguage; }
    public String getServerAddress()          { return sServerAddress; }
    public boolean isDarkTheme()              { return bDarkTheme; }
    public int getServerPort()                { return iServerPort; }
}
