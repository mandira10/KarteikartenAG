package com.swp.DataModel;

import jakarta.persistence.*;
import lombok.Getter;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table
@Getter
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
    @Getter
    @OneToOne
    //@ForeignKey
    @Column
    private final Card pCard;

    /**
     * Zugehörige Kategorie
     */
    @Getter
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