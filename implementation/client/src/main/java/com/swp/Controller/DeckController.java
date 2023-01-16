package com.swp.Controller;

import com.swp.DataModel.*;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.Logic.DeckLogic;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;
@Slf4j
public class DeckController
{
    private DeckController() {}

    private static DeckController deckController;
    public static DeckController getInstance() {
        if (deckController == null)
            deckController = new DeckController();
        return deckController;
    }

    private final DeckLogic deckLogic = DeckLogic.getInstance();

    public List<Deck> getDecks() {
        return deckLogic.getDecks();
    }

    /**
     * Wird verwendet, um das Deck zu updaten.
     * @param olddeck Deck im vorherigen Zustand, benötigt, um festzustellen, ob das StudySystem gewechselt wurde und Handling
     * @param newdeck Neue Deck Eigenschaften
     * @param neu Ist true, wenn das Deck neu angelegt wurde
     */
    public boolean updateDeckData(Deck olddeck, Deck newdeck, boolean neu) {
        return deckLogic.updateDeckData(olddeck, newdeck,neu);
    }

    /**
     * Wird verwendet, um ein komplett neues StudySystem anzulegen
     * @param type Typ des StudySystems
     * @return true, wenn erfolgreich
     */
    public boolean addStudySystemTypeAndUpdate(StudySystem.StudySystemType type) {
        return deckLogic.addStudySystemTypeAndUpdate(type);
    }

    public boolean deleteDeck(Deck deck) {
        return deckLogic.deleteDeck(deck);
    }

    public boolean deleteDecks(Deck[] decks) {

        return deckLogic.deleteDecks(decks);
    }

    public boolean createCardToDeck(Card card, Deck deck) {
        return deckLogic.createCardToDeck(card, deck);
    } //manuelles Aufrufen notwendig???

    public boolean createCardToDeckForCategory(Category category, Deck deck) {
        return deckLogic.createCardToDeckForCategory(category, deck);
        //TODO: wofür?
    }


    //TO IMPLEMENT
    public List<Deck> getDecksBySearchterm(String searchterm) {
        return deckLogic.getDecksBySearchterm(searchterm);
    }

    /**
     * Wird benutzt, um einzelne Karten aus dem Deck zu löschen
     * @param cards
     * @param deck
     */
    public void removeCardsFromDeck(List<Card> cards, Deck deck) {
        deckLogic.removeCardsFromDeck(cards, deck);
    }

    /**
     * Für nachträgliches Hinzufpgen von Karten
     * @param cards
     * @param deck
     */
  public void addCardsToDeck(List<Card> cards, Deck deck){
        deckLogic.addCardsTodeck(cards,deck);
  }

    public Deck getDeckByUUID(String uuid) {
        return deckLogic.getDeckByUUID(uuid);
    }

    public Set<Card> getCardsInDeck(Deck deck) {
        return deckLogic.getCardsByDeck(deck);
    }

    public int numCardsInDeck(Deck deck) {
        return deckLogic.numCardsInDeck(deck);
    }



    public boolean updateStudySystemData(Deck deck, StudySystem system) {
        return deckLogic.updateStudySystem(deck, system);
    } //NEEDED? or directly in Deck Update?
}