package com.swp.Persistence;

import java.util.Set;

import com.swp.DataModel.Card;
import com.swp.DataModel.CardToDeck;
import com.swp.DataModel.Deck;
import com.swp.DataModel.StudySystem;
import com.swp.DataModel.StudySystemType;
import com.swp.DataModel.Card.CardType;
import com.swp.DataModel.CardTypes.AudioCard;
import com.swp.DataModel.Deck.CardOrder;
import com.swp.DataModel.StudySystemTypes.LeitnerSystem;
import com.swp.DataModel.StudySystemTypes.TimingSystem;
import com.swp.DataModel.StudySystemTypes.VoteSystem;

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


        /////////////////////////////////////////////////////////////////
        //
        // TEMPORARY
        //
        for(int i = 0; i < 6; i += 3)
        {
            Deck deck = new Deck("Deck " + i, null, CardOrder.ALPHABETICAL, true);
            deck.setPStudySystem(new LeitnerSystem(deck));
            decks.add(deck);

            deck = new Deck("Deck " + (i+1), null, CardOrder.RANDOM, true); 
            deck.setPStudySystem(new TimingSystem(deck));
            decks.add(deck); 

            deck = new Deck("Deck " + (i+2), null, CardOrder.REVERSED_ALPHABETICAL, true);
            deck.setPStudySystem(new VoteSystem(deck));
            decks.add(deck); 
        }
        Cache.getInstance().setDecks(decks);
        return decks;
        /////////////////////////////////////////////////////////////////

        //server.send("/getdecks", jsonString);
        //return null;
    }

    public static Set<CardToDeck> getCardToDecks()
    {
        Set<CardToDeck> card2decks = Cache.getInstance().getCardToDecks();
        if(!card2decks.isEmpty())
            return card2decks;

        /////////////////////////////////////////////////////////////////
        //
        // TEMPORARY
        //
        int imax = 0;
        for(Deck deck : getDecks())
        {
            for(int i = 0; i < imax ; i++)
            {
                CardToDeck cardtodeck = new CardToDeck(new AudioCard(), deck);
                card2decks.add(cardtodeck);
            }
            imax++;
        }
        Cache.getInstance().setCardToDecks(card2decks);
        return card2decks;
        /////////////////////////////////////////////////////////////////

        //server.send("/getcardtodecks", jsonString);
        //return null;
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