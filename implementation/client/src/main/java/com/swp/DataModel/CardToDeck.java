package com.swp.DataModel;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import java.util.ArrayList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

@Table
@Entity
@Getter
@Setter
public class CardToDeck 
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
    @Column(name = "card")
    @Setter(AccessLevel.NONE)
    private final Card pCard;

    /**
     * Zugehöriges Deck
     */
    @OneToOne
    @Column(name = "deck")
    @Setter(AccessLevel.NONE)
    private final Deck pDeck;

    /**
     * Status der Karte im Deck. Wird beim Lernen aktualisiert.
     */
    @OneToOne
    @Column(name = "status")
    private CardStatus iStatus;

    /**
     * Konstruktor der Klasse CardToDeck
     * @param c: Karte
     * @param d: Deck
     */
    public CardToDeck(Card c, Deck d)
    {
        this.pCard = c;
        this.pDeck = d;
    }

    /**
     * no-arg constructor needed for hibernates `@Entity` tag
     */
    public CardToDeck() {}

}