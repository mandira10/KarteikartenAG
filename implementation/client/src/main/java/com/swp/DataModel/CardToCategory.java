package com.swp.DataModel;

import jakarta.persistence.*;
import lombok.Getter;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table
@Getter
@NamedQuery(name = "CardToCategory.allCardsOfCategory",
            query = "SELECT pCard FROM CardToCategory WHERE pCategory = :category")
@NamedQuery(name = "CardToCategory.allCategoriesOfCard",
            query = "SELECT pCategory FROM CardToCategory WHERE pCard = :card")
public class CardToCategory implements Serializable
{
    /**
     * Identifier und Primärschlüssel für die
     * Karte-zu-Kategorie Verbindung.
     */
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    protected final String id;

    /**
     * Zugehörige Karte
     */
    @OneToOne
    //@ForeignKey
    @Column
    private final Card pCard;

    /**
     * Zugehörige Kategorie
     */
    @OneToOne
    @Column
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
        this.id = UUID.randomUUID().toString();
    }

    /**
     * no-arg constructor needed for hibernates `@Entity` tag
     */
    public CardToCategory() {
        this.pCard = null;
        this.pCategory = null;
        this.id = UUID.randomUUID().toString();
    }

}