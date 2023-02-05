package com.swp.DataModel.Language;

import com.gumse.gui.Locale;

/**
 * Englische version aller Strings in dem Projekt
 */
public class English extends Language
{
    private static English pInstance = null;

    private English()
    {
        this.locale = new Locale("English", "en");
        loadLocale("locale/en_US.UTF-8");
    }

    public static English getInstance()
    {
        if(pInstance == null)
            pInstance = new English();
        return pInstance;
    }
}