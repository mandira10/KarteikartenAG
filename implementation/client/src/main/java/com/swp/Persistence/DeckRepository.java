package com.swp.Persistence;

import java.util.List;

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
    public static void updateDeck(Deck olddeck, Deck newdeck)
    {

        //server.send("/updatedeckdata", jsonString);
    }

    public static void updateStudySystem(Deck deck, StudySystem system)
    {

        //server.send("/updatestudysystem", jsonString);
    }

    public static void saveDeck(Deck deck)
    {
        //server.send("/createdeck", jsonString);
    }

    public static void deleteDeck(Deck deck)
    {
        //server.send("/deletedeck", jsonString);
    }

    public static List<Deck> getDecks()             { return null; }
    public static Deck getDeck(int index)           { return null; }
    public static List<CardToDeck> getCardToDecks() { return null; }
}