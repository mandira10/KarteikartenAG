package com.swp.DataModel;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CascadeType;

import java.io.Serializable;

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
    @JoinColumn(name = "card_uuid", referencedColumnName = "CARD_ID")
    @Cascade({CascadeType.PERSIST, CascadeType.MERGE})
    private final Card card;

    /**
     * Zugehöriger Tag
     */
    @ManyToOne
    @JoinColumn(name = "tag_uuid", referencedColumnName = "TAG_VALUE")
    @Cascade({CascadeType.PERSIST, CascadeType.MERGE})
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
        // gleiche Verbindungen haben gleiche ID
        this.id = String.format("%s:%s", c.getUuid(), t.getVal());
    }

    /**
     * no-arg constructor needed for hibernates `@Entity` tag
     */
    public CardToTag() {
        this.card = null;
        this.tag = null;
        this.id = null;
    }
}