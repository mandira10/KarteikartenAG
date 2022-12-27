package com.swp.DataModel;

import jakarta.persistence.*;
import lombok.Getter;

@Entity
@Table
@Getter
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

    /**
     * no-arg constructor needed for hibernates `@Entity` tag
     */
    public CardToCategory() {}

}