package com.swp.Logic;

import com.swp.DataModel.Card;
import com.swp.DataModel.CardToDeck;
import com.swp.DataModel.Category;
import com.swp.DataModel.Deck;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.DataModel.StudySystem.StudySystemType;
import com.swp.Persistence.CardRepository;
import com.swp.Persistence.DeckRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Slf4j
public class DeckLogic
{
    public static List<Deck> getDecks()
    {
        return DeckRepository.getDecks();
    }

    public static boolean createCardToDeck(Card card, Deck deck)
    {
        return DeckRepository.createCardToDeck(card,deck);
    }

    //Wofür?
    public static boolean createCardToDeckForCategory(Category category, Deck deck)
    {
        return false;
    }

    public static Deck getDeckByUUID(String uuid)
    {
        return null;
    }

    public static Set<Card> getCardsByDeck(Deck deck)
    {
        Set<Card> retArr = new HashSet<>();
        for(CardToDeck c2c : DeckRepository.getCardToDecks())
        {
            if(c2c.getDeck() == deck)
                retArr.add(c2c.getCard());
        }
        return retArr;
    }

    public static boolean updateDeckData(Deck olddeck, Deck newdeck, boolean neu)
    {
        if(neu) {
            //TODO: klären was mit der initialen Kartenvergabe ans StudySystem ist
            //TODO: vorher updateStudySystem aufrufen?
             if(!DeckRepository.saveDeck(newdeck))
             {
                 //nach Saven müssen alle CardToDecks erstellt werden, Handling tbd.
                 for(Card c : newdeck.getStudySystem().getAllCardsInStudySystem()){
                     DeckRepository.createCardToDeck(c,newdeck);
                 }
             }
        }

        if(!neu && newdeck.getStudySystem().equals(olddeck.getStudySystem()))
            resetStudySystem(olddeck,newdeck.getStudySystem());

        //TODO: vorher updateStudySystem aufrufen?
            return DeckRepository.updateDeck(newdeck);
    }

    /**
     * Wird aufgerufen, wenn ein StudySystem im EditModus geändert wurde und setzt das gesamte Deck zurück,
     * d.h. alle Karten werden wieder in Box 1 gepackt.
     */
    private static void resetStudySystem(Deck deck, StudySystem newStudyS) {
        //first get all Cards for specific deck
        List<Card> cardsToDeck = DeckRepository.getCardsInDeck(deck);
        //then move them to the other StudySystem
        newStudyS.moveAllCardsForDeckToFirstBox(cardsToDeck);
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

    public static List<Deck> getDecksBySearchterm(String searchterm) {
        return DeckRepository.getDecksWithSearchterm(searchterm);
        //analog wie bei Card
    }

    public static void removeCardsFromDeck(List<Card> cards, Deck deck) {
        //TODO: removeCardsAusStudySystemBoxes
        //UpdateDeck
        for(Card c : cards) {
            DeckRepository.removeCardToDeck(c, deck);
        }
    }

    public static void addCardsTodeck(List<Card> cards, Deck deck) {
        deck.getStudySystem().moveAllCardsForDeckToFirstBox(cards);
        //UpdateDeck?
        for(Card c : cards){
            DeckRepository.createCardToDeck(c,deck);
        }
    }
}