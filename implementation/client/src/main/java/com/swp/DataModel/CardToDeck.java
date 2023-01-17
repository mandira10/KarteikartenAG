package com.swp.DataModel;

import jakarta.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Table(uniqueConstraints = @UniqueConstraint(name = "uniqueCardDeck",columnNames = {"card_uuid","deck_uuid"}))
@Entity
@Getter
@Setter
@NamedQuery(name  = "CardToDeck.allC2DByCard",
            query = "SELECT cd FROM CardToDeck cd WHERE cd.card = :card")
@NamedQuery(name  = "CardToDeck.allC2DByDeck",
            query = "SELECT cd FROM CardToDeck cd WHERE cd.deck = :deck")
@NamedQuery(name  = "CardToDeck.allCardsWithDeck",
            query = "SELECT cd.card FROM CardToDeck cd WHERE cd.deck = :deck")
@NamedQuery(name  = "CardToDeck.allDecksWithCard",
            query = "SELECT cd.deck FROM CardToDeck cd WHERE cd.card = :card")
@NamedQuery(name  = "CardToDeck.findSpecificC2C",
            query = "SELECT cd FROM CardToDeck cd WHERE cd.card = :card AND cd.deck = :deck")
@NamedQuery(name  = "CardToDeck.numCardsInDeck",
            query = "SELECT count(*) FROM CardToDeck cd WHERE cd.deck =: deck")
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
    @ManyToOne
    @Setter(AccessLevel.NONE)
    @JoinColumn(name = "card_uuid")
    private final Card card;

    /**
     * Zugehöriges Deck
     */
    @ManyToOne
    @Setter(AccessLevel.NONE)
    @JoinColumn(name = "deck_uuid")
    private final Deck deck;

    @Id
    //@GeneratedValue
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
        this.status = CardStatus.NEW;
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