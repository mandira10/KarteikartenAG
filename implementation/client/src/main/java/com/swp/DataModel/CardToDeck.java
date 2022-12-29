package com.swp.DataModel;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Table
@Entity
@Getter
@Setter
@NamedQuery(name = "CardToDeck.allC2DByCard",
            query = "SELECT CardToDeck FROM CardToDeck WHERE card = :card")
public class CardToDeck implements Serializable
{
    /**
     * Enum des CardStatus
     */
    public enum CardStatus
    {
        LEARNED,
        RELEARN,
        NEW
    }

    /**
     * Zugehörige Karte
     */
    @OneToOne
    @Setter(AccessLevel.NONE)
    private final Card card;

    /**
     * Zugehöriges Deck
     */
    @OneToOne
    @Setter(AccessLevel.NONE)
    private final Deck deck;

    @Id
    @GeneratedValue
    /**
     * Identifier und Primärschlüssel für
     * Karte-zu-Deck Verbindung
     */
    protected final String id;

    /**
     * Status der Karte im Deck. Wird beim Lernen aktualisiert.
     */
    @Column
    @Enumerated(EnumType.STRING)
    private CardStatus status;

    /**
     * Konstruktor der Klasse CardToDeck
     * @param c: Karte
     * @param d: Deck
     */
    public CardToDeck(Card c, Deck d)
    {
        this.card = c;
        this.deck = d;
        this.id = UUID.randomUUID().toString();
    }

    /**
     * no-arg constructor needed for hibernates `@Entity` tag
     */
    public CardToDeck() {
        this.card = null;
        this.deck = null;
        this.id = UUID.randomUUID().toString();
    }

}