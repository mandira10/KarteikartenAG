package com.swp.DataModel;

import com.gumse.tools.Output;
import com.swp.DataModel.Language.English;
import com.swp.DataModel.Language.German;
import com.swp.DataModel.Language.Language;
import org.ini4j.Ini;
import org.ini4j.InvalidFileFormatException;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


/**
 * Die Klasse repräsentiert die Einstellungen des Nutzers.
 *  @author Mert As, Efe Carkcioglu, Tom Beuke, Ole-Niklas Mahlstädt, Nadja Cordes
 */
public class Settings
{

    /**
     * Verfügbare Sprachen
     */
    public enum Setting
    {
        /** Dark/Light Theme */  DARK_THEME,
        /** Sprache */           LANGUAGE,
        /** Server Adresse */    SERVER_ADDRESS,
        /** Server Port */       SERVER_PORT,
        /** Benutzer Passwort */ USER_PASSWD,
        /** Benutzername */      USER_NAME
    }

    private final Map<Setting, String> mSettings;
    private Ini pIni;

    /**
     * Hilfsattribut, um sicherzustellen, dass Settings nur einmal
     * konfiguriert werden.
     */
    private static Settings settingsInstance = null;


    /**
     * Konstruktor der Settings Klasse
     */
    private Settings()
    {
        mSettings = new HashMap<>();
        File file = new File("settings.ini");
        Output.info("Loading settingsfile (" + file.getName() + ")");
        try                   { file.createNewFile(); } 
        catch (IOException e) { Output.fatal("Couldnt create settings file", 1); }

        try                                  { pIni = new Ini(file); } 
        catch(InvalidFileFormatException e)  { Output.fatal("Failed to read " + file.getName() + ": invalid fileformat!", 2); return; } 
        catch(IOException e)                 { Output.fatal("Failed to read " + file.getName() + ": " + e.getMessage() + "!", 3); return; }

        mSettings.put(Setting.DARK_THEME,     loadSetting("general", "darktheme", "true"));
        mSettings.put(Setting.LANGUAGE,       loadSetting("general", "language",  "en"));
        mSettings.put(Setting.SERVER_ADDRESS, loadSetting("server",  "address",   ""));
        mSettings.put(Setting.SERVER_PORT,    loadSetting("server",  "port",      ""));
        mSettings.put(Setting.USER_NAME,      loadSetting("user",    "username",  "null"));
        mSettings.put(Setting.USER_PASSWD,    loadSetting("user",    "password",  "null"));
        saveSettings();
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

    private String loadSetting(String category, String name, String defaultvalue)
    {
        String value = pIni.get(category, name);
        if(value == null || value.equals(""))
            value = defaultvalue;

        return value;
    }

    /**
     * Gibt den Wert einer Einstellung wieder
     *
     * @param setting Der Einstellungseintrag
     * @return Der Einstellungswert
     */
    public String getSetting(Setting setting)
    {
        String retvalue = mSettings.get(setting);
        if(retvalue == null)
            return "";
        return retvalue;
    }

    public Language getLanguage()
    {
        switch(getSetting(Setting.LANGUAGE))
        {
            case "de": return German.getInstance();
            case "en": return English.getInstance();
        }

        return English.getInstance();
    }

    /**
     * Setzt eine Einstellung
     *
     * @param setting Der Einstellungseintrag
     * @param value   Der Einstellungswert
     */
    public void setSetting(Setting setting, String value)
    {
        mSettings.put(setting, value);
        saveSettings();
    }

    /**
     * Speichert die Einstellungen
     */
    public void saveSettings()
    {
        pIni.put("general", "darktheme", getSetting(Setting.DARK_THEME));
        pIni.put("general", "language",  getSetting(Setting.LANGUAGE));
        pIni.put("server",  "address",   getSetting(Setting.SERVER_ADDRESS));
        pIni.put("server",  "port",      getSetting(Setting.SERVER_PORT));
        pIni.put("user",    "username",  getSetting(Setting.USER_NAME));
        pIni.put("user",    "password",  getSetting(Setting.USER_PASSWD));
        try                   { pIni.store(); } 
        catch (IOException e) { Output.error("Failed to save settings"); }
    }
}
