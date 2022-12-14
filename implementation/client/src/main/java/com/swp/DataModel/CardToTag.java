package com.swp.DataModel;

public class CardToTag 
{
    /**
     * Zugehörige Karte
     */
    private final Card pCard;

    /**
     * Zugehöriger Tag
     */
    private final Tag pTag;

    /**
     * Konstruktor der Klasse CardToTag
     * @param c: Karte
     * @param t: Tag
     */
    public CardToTag(Card c, Tag t)
    {
        this.pCard = c;
        this.pTag = t;
    }


    //
    // Getter
    //
    public Card getCard() { return pCard; }
    public Tag getTag()   { return pTag; }
}