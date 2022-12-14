package com.swp.DataModel;

import java.util.ArrayList;

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
    };

    /**
     * Zugehörige Karte
     */
    private final Card pCard;

    /**
     * Zugehöriges Deck
     */
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


    //
    // Setter
    //
    public void setStatus(CardStatus status) { this.iStatus = status; }


    //
    // Getter
    //
    public Card getCard()         { return pCard; }
    public Deck getDeck()         { return pDeck; }
    public CardStatus getStatus() { return iStatus; }
}