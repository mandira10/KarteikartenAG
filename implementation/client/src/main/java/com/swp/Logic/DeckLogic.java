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

    /**
     * Wird verwendet, Um die alle Decks zu bekommen. Wird an das DeckRepository weitergegeben.
     @return eine Liste von Decks
     */
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

    /**
     * Wird verwendet, Um ein Deck nach UUID zu bekommen. Wird an das DeckRepository weitergegeben.
     @return Deck
     */
    public Deck getDeckByUUID(String uuid)
    {
        checkNotNullOrBlank(uuid, "UUID",true);
        return execTransactional(() -> deckRepository.getDeckByUUID(uuid));
    }

    /**
     * Wird verwendet, Um die Karten eines Decks zu bekommen. Wird an das DeckRepository weitergegeben.
     @param deck: Deck, um Karten zu bekommen
     @return eine Liste von Karten
     */
    public List<Card> getCardsByDeck(Deck deck)
    {
        return execTransactional(() -> cardRepository.findCardsByDeck(deck));
    }

    /**
     * Wird verwendet, Um Deckinformationen zu updaten. Wird an das CardToDeckRepository weitergegeben.
     * @param olddeck: Deck vor Update
     * @param newdeck: angegebenes Deck zu updaten
     * @param neu: Ob, olddeck und newdeck gleich oder nicht sind zu verstehen
     */
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
            List<Card> cardsToDeck = cardRepository.findCardsByDeck(deck);
            //then move them to the other StudySystem
            newStudyS.moveAllCardsForDeckToFirstBox(cardsToDeck);
        return true;
        });
    }



    /**
     * Wird verwendet, Um StudySystem zu updaten und hinzufügen. Wird an das StudySystemRepository weitergegeben.
     @param type: StudySystem Type zu updaten und hinzufügen
     */
    public void addStudySystemTypeAndUpdate(StudySystem.StudySystemType type)
    {
        execTransactional(() -> {
            studySystemRepository.addStudySystemType(type);
            studySystemRepository.updateStudySystemTypes();
            return null;
        });
    }


    /**
     * Wird verwendet, Um ein Deck zu löschen. Wird an das DeckRepository weitergegeben.
     @param deck: Deck zu löschen
     */
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

    /**
     * Wird verwendet, Um eine Liste von Decks zu löschen
     @param decks: eine Liste von Decks zu löschen
     */
    public void deleteDecks(Deck[] decks)
    {
        for(Deck d : decks)
            //TODO: delete all Card2Decks
            //orphan removal für StudySystem
        deleteDeck(d);
    }

    /**
     * Wird verwendet, Um Decks einer Karte zu bekommen. Wird an das DeckRepository weitergegeben.
     @param card: die Karte, um Decks zu bekommen
     @return eine Liste von Decks
     */
    public List<Deck> getDecksByCard(Card card)
    {
        return execTransactional(() -> deckRepository.findDecksByCard(card));
    }

    /**
     * Wird verwendet, Um Anzahl der Karten eines Deck zu bekommen.
     @param deck: das Deck
     @return Anzahl der Karten im Deck als Integer
     */
    public int numCardsInDeck(Deck deck)
    {
        return getCardsByDeck(deck).size();
    }

    /**
     * Wird verwendet, Um Decks nach Suchbegriff zu bekommen. Wird an das DeckRepository weitergegeben.
     @param searchterm: Suchbegriff zu suchen
     @return eine Liste von Decks
     */
    public List<Deck> getDecksBySearchterm(String searchterm) {
        checkNotNullOrBlank(searchterm,"Suchbegriff",true);
        return execTransactional(() -> deckRepository.findDecksContaining(searchterm));
    }

    /**
     * Wird verwendet, Um Karten in einem Deck zu löschen. Wird an das CardToDeckRepository weitergegeben.
     @param cards: eine Liste von Karten zu löschen
     @param deck: Deck, um darin Karten zu löschen
     */
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

    /**
     * Wird verwendet, Um Karten in einem Deck zu löschen. Wird an das CardToDeckRepository weitergegeben.
     @param cards: eine Liste von Karten zu löschen
     @param deck: Deck, um darin Karten zu löschen
     */
    public void addCardsToDeck(List<Card> cards, Deck deck) {
            if(deck == null){
            throw new IllegalStateException("Karte existiert nicht");
            }
            execTransactional(() -> {
                deck.getStudySystem().moveAllCardsForDeckToFirstBox(cards);
                deckRepository.update(deck);
                for (Card c : cards) {
                    cardToDeckRepository.createCardToDeck(c, deck);
                }
                return null; // Lambda braucht immer einen return
            });
        }


    /**
     * Wird verwendet, Um die Karten eines Decks zu updaten. Wird an das DeckRepository weitergegeben.
     @param cards: eine Liste von Karten zu updaten
     @param deck: Deck, um darin Karten zu updaten
     */
    public void updateDeckCards(List<Card> cards,Deck deck){
        //execTransactional(() -> { not needed, da sehr viele aufgerufen werden
            List<Card> oldCards = new ArrayList<>();
            oldCards.addAll(getCardsByDeck(deck));
            removeCardsFromDeck(oldCards, deck);
            addCardsToDeck(cards, deck);
            deckRepository.updateDeckCards(deck);
          //  return null;
        //});
    }

//    public void updateStudySystem(Deck deck, StudySystem system)
//    {
//        execTransactional(() -> {
//            deckRepository.updateStudySystem(deck, system);
//            return null;
//        });
//    }
}