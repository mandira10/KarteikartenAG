package com.swp.Logic;

import com.swp.DataModel.Card;
import com.swp.DataModel.Category;
import com.swp.DataModel.Deck;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.Persistence.CardRepository;
import com.swp.Persistence.CardToDeckRepository;
import com.swp.Persistence.DeckRepository;
import com.swp.Persistence.StudySystemRepository;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import static com.swp.Validator.checkNotNullOrBlank;

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
    private final CardRepository cardRepository = CardRepository.getInstance();

    private final StudySystemRepository studySystemRepository = StudySystemRepository.getInstance();

    private final CardToDeckRepository cardToDeckRepository = CardToDeckRepository.getInstance();
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
        return execTransactional(() -> { cardToDeckRepository.createCardToDeck(card,deck);
                    return false;});
    }

    //Wofür?
    public boolean createCardToDeckForCategory(Category category, Deck deck)
    {
        return false;
    }

    public Deck getDeckByUUID(String uuid)
    {
        checkNotNullOrBlank(uuid, "UUID",true);
        return execTransactional(() -> deckRepository.getDeckByUUID(uuid));
    }

    public List<Card> getCardsByDeck(Deck deck)
    {
        return execTransactional(() -> cardRepository.getCardsToDeck(deck));
    }

    public void updateDeckData(Deck olddeck, Deck newdeck, boolean neu)
    {
         execTransactional(() -> {
        if(neu) {
             deckRepository.save(newdeck);
                 //nach Saven müssen alle CardToDecks erstellt werden, Handling tbd.
                 for(Card c : newdeck.getStudySystem().getAllCardsInStudySystem()){
                     cardToDeckRepository.createCardToDeck(c,newdeck);
                 }

        }

        else if(!neu && newdeck.getStudySystem().equals(olddeck.getStudySystem()))
            resetStudySystem(olddeck,newdeck.getStudySystem());

        //TODO: vorher updateStudySystem aufrufen?
          else
              deckRepository.update(newdeck);
            return true;
    });
    }

    /**
     * Wird aufgerufen, wenn ein StudySystem im EditModus geändert wurde und setzt das gesamte Deck zurück,
     * d.h. alle Karten werden wieder in Box 1 gepackt.
     */
    private void resetStudySystem(Deck deck, StudySystem newStudyS) {
        execTransactional(() -> {
            //first get all Cards for specific deck
            List<Card> cardsToDeck = cardRepository.getCardsToDeck(deck);
            //then move them to the other StudySystem
            newStudyS.moveAllCardsForDeckToFirstBox(cardsToDeck);
        return true;
        });
    }

    public void updateStudySystem(Deck deck, StudySystem system)
    {
        execTransactional(() -> {
            deckRepository.updateStudySystem(deck, system);
            return null;
        });
    }

    public void addStudySystemTypeAndUpdate(StudySystem.StudySystemType type)
    {
        execTransactional(() -> {
            studySystemRepository.addStudySystemType(type);
            studySystemRepository.updateStudySystemTypes();
            return null;
        });
    }


    public void deleteDeck(Deck deck)
    {

        if(deck == null){
            throw new IllegalStateException("Karte existiert nicht");
        }
        execTransactional(() -> {
            deckRepository.delete(deck);
            return null; // Lambda braucht immer einen return
        });
    }

    public void deleteDecks(Deck[] decks)
    {
        for(Deck d : decks)
        deleteDeck(d);
    }

    public List<Deck> getDecksByCard(Card card)
    {
        return execTransactional(() -> deckRepository.findDecksByCard(card));
    }

    public int numCardsInDeck(Deck deck)
    {
        return getCardsByDeck(deck).size();
    }

    public List<Deck> getDecksBySearchterm(String searchterm) {
        checkNotNullOrBlank(searchterm,"Suchbegriff",true);
        return execTransactional(() -> deckRepository.findDecksContaining(searchterm));
    }

    public void removeCardsFromDeck(List<Card> cards, Deck deck) {
        //TODO: removeCardsAusStudySystemBoxes
        //UpdateDeck
        execTransactional(() -> {
            for(Card c : cards) {
                cardToDeckRepository
                        .delete(cardToDeckRepository.getAllC2DForCard(c));
            }
            return null; // Lambda braucht immer einen return
        });
    }

    public void addCardsTodeck(List<Card> cards, Deck deck) {
        execTransactional(() -> {
            deck.getStudySystem().moveAllCardsForDeckToFirstBox(cards);
            //UpdateDeck?
            for(Card c : cards){
                cardToDeckRepository.createCardToDeck(c,deck);
            }
            return null; // Lambda braucht immer einen return
        });
    }

    public void updateDeckCards(List<Card> cards,Deck deck){
        execTransactional(() -> {
            List<Card> oldCards = new ArrayList<>();
            oldCards.addAll(deckLogic.getCardsByDeck(deck));
            removeCardsFromDeck(oldCards, deck);
            addCardsTodeck(cards, deck);
            deckRepository.updateDeckCards(deck);
            return null;
        });
    }
}