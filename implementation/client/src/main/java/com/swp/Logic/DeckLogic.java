package com.swp.Logic;

import com.swp.DataModel.Card;
import com.swp.DataModel.CardToDeck;
import com.swp.DataModel.Category;
import com.swp.DataModel.Deck;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.Persistence.DeckRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
@Slf4j
public class DeckLogic extends BaseLogic<Deck>
{
    /**
     * Konstruktor für eine DeckLogic-Instanz.
     */
    private DeckLogic() {
        super(DeckRepository.getInstance());
    }

    private final DeckRepository deckRepository = DeckRepository.getInstance();
    private static DeckLogic deckLogic;
    public static DeckLogic getInstance() {
        if (deckLogic == null)
            deckLogic = new DeckLogic();
        return deckLogic;
    }

    public List<Deck> getDecks()
    {
        return execTransactional(() -> deckRepository.getDecks());
    }

    public boolean createCardToDeck(Card card, Deck deck)
    {
        return execTransactional(() -> deckRepository.createCardToDeck(card,deck));
    }

    //Wofür?
    public boolean createCardToDeckForCategory(Category category, Deck deck)
    {
        return false;
    }

    public Deck getDeckByUUID(String uuid)
    {
        return execTransactional(() -> deckRepository.getDeckByUUID(uuid));
    }

    public Set<Card> getCardsByDeck(Deck deck)
    {
        return execTransactional(() -> {
            Set<Card> retArr = new HashSet<>();
            for(CardToDeck c2c : deckRepository.getCardToDecks())
            {
                if(c2c.getDeck() == deck)
                    retArr.add(c2c.getCard());
            }
            return retArr;
        });
    }

    public boolean updateDeckData(Deck olddeck, Deck newdeck, boolean neu)
    {
        if(neu) {
            //TODO: klären was mit der initialen Kartenvergabe ans StudySystem ist
            //TODO: vorher updateStudySystem aufrufen?
             if(!deckRepository.saveDeck(newdeck))
             {
                 //nach Saven müssen alle CardToDecks erstellt werden, Handling tbd.
                 for(Card c : newdeck.getStudySystem().getAllCardsInStudySystem()){
                     deckRepository.createCardToDeck(c,newdeck);
                 }
             }
        }

        if(!neu && newdeck.getStudySystem().equals(olddeck.getStudySystem()))
            resetStudySystem(olddeck,newdeck.getStudySystem());

        //TODO: vorher updateStudySystem aufrufen?
            return deckRepository.updateDeck(newdeck);
    }

    /**
     * Wird aufgerufen, wenn ein StudySystem im EditModus geändert wurde und setzt das gesamte Deck zurück,
     * d.h. alle Karten werden wieder in Box 1 gepackt.
     */
    private void resetStudySystem(Deck deck, StudySystem newStudyS) {
        //first get all Cards for specific deck
        List<Card> cardsToDeck = deckRepository.getCardsInDeck(deck);
        //then move them to the other StudySystem
        newStudyS.moveAllCardsForDeckToFirstBox(cardsToDeck);
    }

    public boolean updateStudySystem(Deck deck, StudySystem system)
    {
        return deckRepository.updateStudySystem(deck, system);
    }

    public boolean addStudySystemTypeAndUpdate(StudySystem.StudySystemType type)
    {
        return deckRepository.addStudySystemType(type)
            && deckRepository.updateStudySystemTypes();
    }


    public boolean deleteDeck(Deck deck)
    {
        return deckRepository.deleteDeck(deck);
    }

    public boolean deleteDecks(Deck[] decks)
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

    public Set<Deck> getDecksByCard(Card card)
    {
        Set<Deck> retArr = new HashSet<>();
        for(CardToDeck c2d : deckRepository.getCardToDecks())
        {
            if(c2d.getCard() == card) //TODO: equals Methode überschrieben
                retArr.add(c2d.getDeck());
        }
        return retArr;
    }

    public Set<Card> getCardsInDeck(Deck deck)
    {
        return execTransactional(() -> {
            Set<Card> retArr = new HashSet<>();
            for(CardToDeck c2d : deckRepository.getCardToDecks())
            {
                if(c2d.getDeck() == deck)
                    retArr.add(c2d.getCard());
            }
            return retArr;
        });
    }

    public int numCardsInDeck(Deck deck)
    {
        return getCardsInDeck(deck).size();
    }

    public List<Deck> getDecksBySearchterm(String searchterm) {
        return execTransactional(() -> deckRepository.getDecksWithSearchterm(searchterm));
        //analog wie bei Card
    }

    public void removeCardsFromDeck(List<Card> cards, Deck deck) {
        //TODO: removeCardsAusStudySystemBoxes
        //UpdateDeck
        execTransactional(() -> {
            for(Card c : cards) {
                deckRepository.removeCardToDeck(c, deck);
            }
            return null; // Lambda braucht immer einen return
        });
    }

    public void addCardsTodeck(List<Card> cards, Deck deck) {
        execTransactional(() -> {
            deck.getStudySystem().moveAllCardsForDeckToFirstBox(cards);
            //UpdateDeck?
            for(Card c : cards){
                deckRepository.createCardToDeck(c,deck);
            }
            return null; // Lambda braucht immer einen return
        });
    }

    public void updateDeckCards(List<Card> cards,Deck deck){
        execTransactional(() -> {
            List<Card> oldCards = new ArrayList<>();
            oldCards.addAll(deckLogic.getCardsInDeck(deck));
            removeCardsFromDeck(oldCards, deck);
            addCardsTodeck(cards, deck);
            deckRepository.updateDeckCards(deck);
            return null;
        });
    }
}