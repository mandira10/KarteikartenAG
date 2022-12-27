package com.swp.DataModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.Getter;

@Entity
@Table
@Getter
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

    /**
     * no-arg constructor needed for hibernates `@Entity` tag
     */
    public CardToTag() {}


}