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
            query = "SELECT CardToCategory FROM CardToCategory WHERE card =: card")
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
    private final Card card;

    /**
     * Zugehörige Kategorie
     */
    @OneToOne
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