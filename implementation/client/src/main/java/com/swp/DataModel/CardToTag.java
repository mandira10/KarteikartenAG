package com.swp.DataModel;

import jakarta.persistence.*;
import lombok.Getter;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table
@Getter
@NamedQuery(name = "CardToTag.allCardsWithTag",
            query = "SELECT pCard FROM CardToTag WHERE pTag = :tag")
public class CardToTag implements Serializable
{
    /**
     * Zugehörige Karte
     */
    @OneToOne
    @Column
    private final Card pCard;

    /**
     * Zugehöriger Tag
     */
    @OneToOne
    @Column
    private final Tag pTag;

    /**
     * Identifier und Primärschlüssel für die
     * Karte-zu-Tag Verbindung.
     */
    @Id
    @GeneratedValue
    protected final String id;

    /**
     * Konstruktor der Klasse CardToTag
     * @param c: Karte
     * @param t: Tag
     */
    public CardToTag(Card c, Tag t)
    {
        this.pCard = c;
        this.pTag = t;
        this.id = UUID.randomUUID().toString();
    }

    /**
     * no-arg constructor needed for hibernates `@Entity` tag
     */
    public CardToTag() {
        this.pCard = null;
        this.pTag = null;
        this.id = UUID.randomUUID().toString();
    }


}