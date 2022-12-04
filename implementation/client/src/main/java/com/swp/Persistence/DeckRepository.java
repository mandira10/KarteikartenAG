package com.swp.Persistence;

import java.util.List;
import java.util.Set;

import com.swp.DataModel.CardToDeck;
import com.swp.DataModel.Deck;
import com.swp.DataModel.StudySystem;

public class DeckRepository
{
    /**
     * 
     * @param olddeck
     * @param newdeck
     */
    public static boolean updateDeck(Deck olddeck, Deck newdeck)
    {

        //server.send("/updatedeckdata", jsonString);
        return false;
    }

    public static boolean updateStudySystem(Deck deck, StudySystem system)
    {

        //server.send("/updatestudysystem", jsonString);
        return false;
    }

    public static boolean saveDeck(Deck deck)
    {
        //server.send("/createdeck", jsonString);
        return false;
    }

    public static boolean deleteDeck(Deck deck)
    {
        //server.send("/deletedeck", jsonString);
        return false;
    }
    
    public static Set<Deck> getDecks()  
    { 
        Set<Deck> decks = Cache.getInstance().getDecks();
        if(!decks.isEmpty())
            return decks;

        //server.send("/getdecks", jsonString);
        return null; 
    }

    public static Set<CardToDeck> getCardToDecks()  
    { 
        Set<CardToDeck> decks = Cache.getInstance().getCardToDecks();
        if(!decks.isEmpty())
            return decks;

        //server.send("/getcardtodecks", jsonString);
        return null; 
    }
}