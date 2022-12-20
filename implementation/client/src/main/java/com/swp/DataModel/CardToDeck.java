package com.swp.DataModel;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;


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
    @Setter(AccessLevel.NONE)
    private final Card pCard;

    /**
     * Zugehöriges Deck
     */
    @Setter(AccessLevel.NONE)
    private final Deck pDeck;

    /**
     * Status der Karte im Deck. Wird beim Lernen aktualisiert.
     */
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

}