package com.swp.DataModel.Language;

public class German extends Language
{
    private static German pInstance = null;

    private German()
    {
        this.shortName = "de";
        this.name = "German";
    }

    public static German getInstance()
    {
        if(pInstance == null)
            pInstance = new German();
        return pInstance;
    }
}