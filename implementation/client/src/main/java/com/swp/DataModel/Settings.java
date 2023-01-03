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
    private static Settings settingsInstance = null;

    /**
     * Language des Users
     */
    private Language language;

    /**
     * Serveradresse
     */
    private String serverAddress;

    /**
     * DarkTheme Einstellung des Nutzers
     */
    private boolean darkTheme;

    /**
     * ServerPort
     */
    private int serverPort;

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
        if(settingsInstance == null)
            settingsInstance = new Settings();
        return settingsInstance;
    }

    /**
     * Wandelt die Sprache in einen String um
     * @return Spracheinstellung
     */

    public String languageToString()
    {
        switch(language)
        {
            case ENGLISH: return "English";
            case GERMAN:  return "German";
        }
        return "Unknown";
    }


}
