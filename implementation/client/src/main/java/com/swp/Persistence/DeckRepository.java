package com.swp.Persistence;

import java.util.Set;

import com.swp.DataModel.CardToDeck;
import com.swp.DataModel.Deck;
import com.swp.DataModel.StudySystem;
import com.swp.DataModel.StudySystemType;

public class DeckRepository
{

    public static boolean saveDeck(Deck deck)
    {
        //server.send("/createdeck", jsonString);
        return false;
    }

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

    //
    // StudySystem
    //
    public static boolean updateStudySystem(Deck deck, StudySystem system)
    {

        //server.send("/updatestudysystem", jsonString);
        return false;
    }

    public static boolean addStudySystemType(StudySystemType type)
    {
        //True if successfully added to cache
        return false;
    }

    public static boolean updateStudySystemTypes()
    {

        //server.send("/updatestudysystemtypes", jsonString);
        return false;
    }
    
    public static StudySystem getStudySystem(Deck deck)
    {
        //server.send("/getstudysystem", jsonString);
        return null;
    }
    
    public static Set<StudySystemType> getStudySystemTypes()
    {
        Set<StudySystemType> types = Cache.getInstance().getStudySystemTypes();
        if(!types.isEmpty())
            return types;

        //server.send("/getstudysystemtypes", jsonString);
        return null;
    }
}