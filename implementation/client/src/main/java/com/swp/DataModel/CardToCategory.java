package com.swp.DataModel;

import jakarta.persistence.*;

@Entity
@Table
public class CardToCategory 
{
    /**
     * Zugehörige Karte
     */
    @OneToOne
    //@ForeignKey
    @Column(name = "card")
    private final Card pCard;

    /**
     * Zugehörige Kategorie
     */
    @OneToOne
    @Column(name = "category")
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


    //
    // Getter
    //
    public Card getCard()         { return pCard; }
    public Category getCategory() { return pCategory; }
}