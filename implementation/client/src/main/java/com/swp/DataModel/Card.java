package com.swp.DataModel;

public abstract class Card
{
    public enum CardType
    {
        TRUEFALSE,
        IMAGETEST,
        IMAGEDESC,
        MULITPLECHOICE,
        TEXT,
        AUDIO
    };

    protected final String sUUID;

    public Card(String uuid)
    {
        this.sUUID = uuid;
    }
}
