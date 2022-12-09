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
    public String sName;

    public Card(String uuid, CardType type)
    {
        this.sUUID = uuid;
        this.iType = type;
        this.iRating = 0;
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
    public void setRating(int rating) { this.iRating = rating; }

    
    //
    // Getter
    //
    public CardType getType() { return iType; }
    public String getUUID()   { return sUUID; }
    public String getContent(){ return sContent; }
}
