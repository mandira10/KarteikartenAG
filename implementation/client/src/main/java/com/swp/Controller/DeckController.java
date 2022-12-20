package com.swp.Controller;

import com.swp.DataModel.*;
import com.swp.Logic.DeckLogic;
import lombok.extern.slf4j.Slf4j;

import java.util.Set;
@Slf4j
public class DeckController
{
    private DeckController() {}
    
    public static boolean updateDeckData(Deck olddeck, Deck newdeck)                { return DeckLogic.updateDeckData(olddeck, newdeck); }
    public static boolean updateStudySystemData(Deck deck, StudySystem system)      { return DeckLogic.updateStudySystem(deck, system); }
    public static boolean addStudySystemTypeAndUpdate(StudySystemType type)         { return DeckLogic.addStudySystemTypeAndUpdate(type); }
    public static boolean deleteDeck(Deck deck)                                     { return DeckLogic.deleteDeck(deck); }
    public static boolean deleteDecks(Deck[] decks)                                 { return DeckLogic.deleteDecks(decks); }
    public static boolean createCardToDeck(Card card, Deck deck)                    { return DeckLogic.createCardToDeck(card, deck); }
    public static boolean createCardToDeckForCategory(Category category, Deck deck) { return DeckLogic.createCardToDeckForCategory(category, deck); }
    public static Set<Deck> getDecks()                                              { return DeckLogic.getDecks(); }
    public static Deck getDeckByUUID(String uuid)                                   { return DeckLogic.getDeckByUUID(uuid); }
    public static int numCardsInDeck(Deck deck)                                     { return DeckLogic.numCardsInDeck(deck); }
    public static Set<StudySystemType> getStudySystemTypes()                        { return DeckLogic.getStudySystemTypes(); }
}