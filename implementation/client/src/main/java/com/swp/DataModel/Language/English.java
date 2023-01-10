package com.swp.DataModel.Language;

public class English extends Language
{
    private static English pInstance = null;

    private English()
    {
        this.shortName = "en";
        this.name = "English";
    }

    public static English getInstance()
    {
        if(pInstance == null)
            pInstance = new English();
        return pInstance;
    }
}