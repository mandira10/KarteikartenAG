package com.swp.DataModel;

import lombok.Getter;

@Getter
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


}