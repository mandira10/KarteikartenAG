package com.swp.Controller;

import com.swp.DataModel.*;
import com.swp.Logic.DeckLogic;

import java.util.Set;

public class DeckController
{
    private DeckController() {}
    
    public static boolean updateDeckData(Deck olddeck, Deck newdeck)                { return DeckLogic.updateDeckData(olddeck, newdeck); }
    public static boolean deleteDeck(Deck deck)                                     { return DeckLogic.deleteDeck(deck); }
    public static boolean deleteDecks(Deck[] decks)                                 { return DeckLogic.deleteDecks(decks); }
    public static boolean createCardToDeck(Card card, Deck deck)                    { return DeckLogic.createCardToDeck(card, deck); }
    public static boolean createCardToDeckForCategory(Category category, Deck deck) { return DeckLogic.createCardToDeckForCategory(category, deck); }
    public static Set<Deck> getDecks()                                              { return DeckLogic.getDecks(); }
    public static Deck getDeckByUUID(String uuid)                                   { return DeckLogic.getDeckByUUID(uuid); }
    public static int numCardsInDeck(Deck deck)                                     { return DeckLogic.numCardsInDeck(deck); }
}