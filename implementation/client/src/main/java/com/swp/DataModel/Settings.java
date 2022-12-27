package com.swp.DataModel;

import lombok.Getter;
import lombok.Setter;
import java.io.Serializable;

/**
 * Die Klasse repräsentiert die Einstellungen des Nutzers.
 */

@Getter
@Setter
public class Settings implements Serializable
{
    /**
     * Enum für die verfügbaren Sprachen.
     */
    public enum Language
    {
        ENGLISH,
        GERMAN,
    }

    /**
     * Hilfsattribut, um sicherzustellen, dass Setttings nur einmal
     * konfiguriert werden.
     */
    private static Settings oSettingsInstance = null;

    /**
     * Language des Users
     */
    private Language iLanguage;

    /**
     * Serveradresse
     */
    private String sServerAddress;

    /**
     * DarkTheme Einstellung des Nutzers
     */
    private boolean bDarkTheme;

    /**
     * ServerPort
     */
    private int iServerPort;

    /**
     * Konstruktor der Settings Klasse
     */
    private Settings()
    {
        //Toolbox.loadFile();
    }

    /**
     * Gibt die Instanz von Settings wider, falls es bereits eine gibt
     * @return oSettingsInstance
     */
    public static Settings getInstance()
    {
        if(oSettingsInstance == null)
            oSettingsInstance = new Settings();
        return oSettingsInstance;
    }

    /**
     * Wandelt die Sprache in einen String um
     * @return Spracheinstellung
     */

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
