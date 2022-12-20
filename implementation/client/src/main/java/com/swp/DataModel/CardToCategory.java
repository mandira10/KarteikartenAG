package com.swp.DataModel;

import lombok.Getter;

@Getter
public class CardToCategory 
{
    /**
     * Zugehörige Karte
     */
    private final Card pCard;

    /**
     * Zugehörige Kategorie
     */
    private final Category pCategory;

    /**
     * Konstruktor der Klasse CardToCategory
     * @param c: Karte
     * @param d: Kategorie
     */
    public CardToCategory(Card c, Category d)
    {
        this.pCard = c;
        this.pCategory = d;
    }

}