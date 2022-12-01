package com.swp.DataModel;

import java.util.ArrayList;

public class CardToDeck 
{
    public enum CardStatus
    {
        LEARNED,
        RELEARN,
        NEW
    };

    private Card pCard;
    private Deck pDeck;

    public CardToDeck(Card c, Deck d)
    {
        this.pCard = c;
        this.pDeck = d;
    }

    public Card getCard() { return pCard; }
    public Deck getDeck() { return pDeck; }
}