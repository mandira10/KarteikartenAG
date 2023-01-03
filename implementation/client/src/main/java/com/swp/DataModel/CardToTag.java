package com.swp.DataModel;

import jakarta.persistence.*;
import lombok.Getter;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table
@Getter
@NamedQuery(name = "CardToTag.allCardsWithTag",
            query = "SELECT card FROM CardToTag WHERE tag = :tag")
@NamedQuery(name = "CardToTag.allC2TByCard",
            query = "SELECT ct FROM CardToTag ct WHERE ct.card = :card")
public class CardToTag implements Serializable
{
    /**
     * Zugehörige Karte
     */
    @OneToOne
    private final Card card;

    /**
     * Zugehöriger Tag
     */
    @OneToOne
    private final Tag tag;

    /**
     * Identifier und Primärschlüssel für die
     * Karte-zu-Tag Verbindung.
     */
    @Id
    //@GeneratedValue
    protected final String id;

    /**
     * Konstruktor der Klasse CardToTag
     * @param c: Karte
     * @param t: Tag
     */
    public CardToTag(Card c, Tag t)
    {
        this.card = c;
        this.tag = t;
        this.id = UUID.randomUUID().toString();
    }

    /**
     * no-arg constructor needed for hibernates `@Entity` tag
     */
    public CardToTag() {
        this.card = null;
        this.tag = null;
        this.id = UUID.randomUUID().toString();
    }


}