package com.swp.Logic;

import com.swp.DataModel.Card;
import com.swp.DataModel.CardToDeck;
import com.swp.DataModel.Category;
import com.swp.DataModel.Deck;
import com.swp.DataModel.StudySystem;
import com.swp.DataModel.StudySystemType;
import com.swp.Persistence.DeckRepository;

import java.util.HashSet;
import java.util.Set;

public class DeckLogic
{
    public static Set<Deck> getDecks()
    {
        Set<Deck> deckList = DeckLogic.getDecks();
        return deckList;
    }

    public static boolean createCardToDeck(Card card, Deck deck)
    {
        return false;
    }

    public static boolean createCardToDeckForCategory(Category category, Deck deck)
    {
        return false;
    }

    public static Deck getDeckByUUID(String uuid)
    {
        return null;
    }

    public static boolean updateDeckData(Deck olddeck, Deck newdeck)
    {
        if(newdeck.getUUID().isEmpty())
            return DeckRepository.saveDeck(newdeck);
        else
            return DeckRepository.updateDeck(olddeck, newdeck);
    }

    public static boolean updateStudySystem(Deck deck, StudySystem system)
    {
        return DeckRepository.updateStudySystem(deck, system);
    }

    public static boolean addStudySystemTypeAndUpdate(StudySystemType type)
    {
        return DeckRepository.addStudySystemType(type)
            && DeckRepository.updateStudySystemTypes();
    }


    public static boolean deleteDeck(Deck deck)
    {
        return DeckRepository.deleteDeck(deck);
    }

    public static boolean deleteDecks(Deck[] decks)
    {
        boolean ret = true;
        for(Deck d : decks)
        {
            if (!deleteDeck(d)) {
                ret = false;
                break;
            }
        }

        return ret;
    }

    public static Set<Deck> getDecksByCard(Card card)
    {
        Set<Deck> retArr = new HashSet<>();
        for(CardToDeck c2d : DeckRepository.getCardToDecks())
        {
            if(c2d.getCard() == card) //TODO: equals Methode überschrieben
                retArr.add(c2d.getDeck());
        }
        return retArr;
    }

    public static Set<Card> getCardsInDeck(Deck deck)
    {
        Set<Card> retArr = new HashSet<>();
        for(CardToDeck c2d : DeckRepository.getCardToDecks())
        {
            if(c2d.getDeck() == deck)
                retArr.add(c2d.getCard());
        }
        return retArr;
    }

    public static int numCardsInDeck(Deck deck)
    {
        return getCardsInDeck(deck).size();
    }

    public static Set<StudySystemType> getStudySystemTypes()
    {
        return DeckRepository.getStudySystemTypes();
    }
}