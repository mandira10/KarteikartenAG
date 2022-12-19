package com.swp.DataModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table
public class CardToTag 
{
    /**
     * Zugehörige Karte
     */
    @OneToOne
    @Column(name = "card")
    private final Card pCard;

    /**
     * Zugehöriger Tag
     */
    @OneToOne
    @Column(name = "tag")
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