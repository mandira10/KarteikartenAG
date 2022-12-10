package com.swp.DataModel;

import com.swp.DataModel.CardTypes.AudioCard;

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
    protected final CardType iType;
    protected int iRating;
    protected String sContent;
    protected String sTitle;

    public Card(String uuid, CardType type)
    {
        this.sUUID = uuid;
        this.iType = type;
        this.iRating = 0;
        this.sTitle = "";
    }

    public static Card copyCard(Card card)
    {
        Card retCard = card;
        /*switch(card.iType)
        {
            case AUDIO: retCard = new AudioCard((AudioCard)card); break;
            case IMAGEDESC:
                break;
            case IMAGETEST:
                break;
            case MULITPLECHOICE:
                break;
            case TEXT:
                break;
            case TRUEFALSE:
                break;
            default:
                break;
            
        }*/

        return retCard;
    }

    //
    // Setter
    //
    public void setRating(int rating)  { this.iRating = rating; }
    public void setTitle(String title) { this.sTitle = title; }

    
    //
    // Getter
    //
    public CardType getType() { return iType; }
    public String getUUID()   { return sUUID; }
    public String getContent(){ return sContent; }
    public String getTitle()  { return sTitle; }
}
