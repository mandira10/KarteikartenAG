package com.swp.DataModel.Language;

import com.gumse.gui.Locale;

public class German extends Language
{
    private static German pInstance = null;

    private German()
    {
        this.locale = new Locale("German", "de");
        loadLocale("locale/de_DE.UTF-8");
    }

    public static German getInstance()
    {
        if(pInstance == null)
            pInstance = new German();
        return pInstance;
    }
}