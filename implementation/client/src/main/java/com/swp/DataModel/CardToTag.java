package com.swp.DataModel;

import jakarta.persistence.*;
import lombok.Getter;
import java.io.Serializable;
import java.util.UUID;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(name = "uniqueCardTag",columnNames = {"tag_uuid","card_uuid"}))
@Getter
@NamedQuery(name = "CardToTag.allCardsWithTag",
            query = "SELECT ct.card FROM CardToTag ct WHERE ct.tag = :tag")
@NamedQuery(name = "CardToTag.allTagsWithCards",
        query = "SELECT ct.tag FROM CardToTag ct WHERE ct.card = :card")
@NamedQuery(name = "CardToTag.allC2TByCard",
            query = "SELECT ct FROM CardToTag ct WHERE ct.card = :card")
@NamedQuery(name = "CardToTag.findSpecificC2T",
        query = "SELECT ct FROM CardToTag ct WHERE ct.card = :card AND ct.tag = :tag")
public class CardToTag implements Serializable
{
    /**
     * Zugehörige Karte
     */
    @ManyToOne
    @JoinColumn(name = "card_uuid")
    private final Card card;

    /**
     * Zugehöriger Tag
     */
    @ManyToOne
    @JoinColumn(name = "tag_uuid")
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