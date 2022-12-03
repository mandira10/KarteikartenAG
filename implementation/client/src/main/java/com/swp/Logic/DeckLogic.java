package com.swp.Logic;

import com.swp.DataModel.CardToDeck;
import com.swp.DataModel.Deck;
import com.swp.DataModel.StudySystem;
import com.swp.Persistence.DeckRepository;

import java.util.List;

public class DeckLogic
{
    public static List<Deck> getDecks()
    {
        List<Deck> deckList = DeckRepository.getDecks();
        return deckList;
    }

    public static void updateDeckData(Deck olddeck, Deck newdeck)
    {
        if(newdeck.getUUID().isEmpty())
            DeckRepository.saveDeck(newdeck);
        else
            if(olddeck.getStudySystem().equals(newdeck.getStudySystem()))
            DeckRepository.updateDeck(olddeck, newdeck);
            else
                updateStudySystem(newdeck, newdeck.getStudySystem());
    }

    public static void updateStudySystem(Deck deck, StudySystem system)
    {
        DeckRepository.updateStudySystem(deck, system);
    }

    public static void deleteDeck(Deck deck)
    {

    }

    public static void deleteDecks(Deck[] decks)
    {
        for(Deck d : decks)
            deleteDeck(d);
    }

    public static void getAllInfosForDeck(String deck)  { /*TODO*/ }
    public static void getCountOfCardsInDeck(Deck deck) { /*TODO*/ }
    public static List<CardToDeck> getCardToDecks()     { return DeckRepository.getCardToDecks(); }
}
