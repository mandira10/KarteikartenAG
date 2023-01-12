package com.swp.DataModel;

import jakarta.persistence.*;
import lombok.Getter;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table
@Getter
@NamedQuery(name = "CardToCategory.allCardsOfCategory",
            query = "SELECT card FROM CardToCategory WHERE category = :category")
@NamedQuery(name = "CardToCategory.allCategoriesOfCard",
            query = "SELECT category FROM CardToCategory WHERE card = :card")
@NamedQuery(name = "CardToCategory.allC2CByCard",
            query = "SELECT c FROM CardToCategory c WHERE c.card = :card")
@NamedQuery(name = "CardToCategory.allC2CByCategory",
            query = "SELECT c FROM CardToCategory c WHERE c.category = :category")
@NamedQuery(name = "CardToCategory.findSpecificC2C",
        query = "SELECT c FROM CardToCategory c WHERE c.category = :category and c.card = :card")
public class CardToCategory implements Serializable
{
    /**
     * Identifier und Primärschlüssel für die
     * Karte-zu-Kategorie Verbindung.
     */
    @Id
   // @GeneratedValue(strategy = GenerationType.UUID)
    protected final String id;

    /**
     * Zugehörige Karte
     */
    @ManyToOne
    //@ForeignKey
    private final Card card;

    /**
     * Zugehörige Kategorie
     */
    @ManyToOne
    private final Category category;

    /**
     * Konstruktor der Klasse CardToCategory
     * @param c: Karte
     * @param d: Kategorie
     */
    public CardToCategory(Card c, Category d)
    {
        this.card = c;
        this.category = d;
        this.id = UUID.randomUUID().toString();
    }

    /**
     * no-arg constructor needed for hibernates `@Entity` tag
     */
    public CardToCategory() {
        this.card = null;
        this.category = null;
        this.id = UUID.randomUUID().toString();
    }

}