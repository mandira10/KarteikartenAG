package com.swp.Controller;

import com.swp.DataModel.*;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.DataModel.StudySystem.StudySystemType;
import com.swp.Logic.DeckLogic;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Set;
@Slf4j
public class DeckController
{
    private DeckController() {}


    public static List<Deck> getDecks() {
        return DeckLogic.getDecks();
    }

    /**
     * Wird verwendet, um das Deck zu updaten.
     * @param olddeck Deck im vorherigen Zustand, benötigt, um festzustellen, ob das StudySystem gewechselt wurde und Handling
     * @param newdeck Neue Deck Eigenschaften
     * @param neu Ist true, wenn das Deck neu angelegt wurde
     */
    public static boolean updateDeckData(Deck olddeck, Deck newdeck, boolean neu) {
        return DeckLogic.updateDeckData(olddeck, newdeck,neu);
    }

    /**
     * Wird verwendet, um ein komplett neues StudySystem anzulegen
     * @param type Typ des StudySystems
     * @return true, wenn erfolgreich
     */
    public static boolean addStudySystemTypeAndUpdate(StudySystemType type) {
        return DeckLogic.addStudySystemTypeAndUpdate(type);
    }

    public static boolean deleteDeck(Deck deck) {
        return DeckLogic.deleteDeck(deck);
    }

    public static boolean deleteDecks(Deck[] decks) {

        return DeckLogic.deleteDecks(decks);
    }

    public static boolean createCardToDeck(Card card, Deck deck) {
        return DeckLogic.createCardToDeck(card, deck);
    } //manuelles Aufrufen notwendig???

    public static boolean createCardToDeckForCategory(Category category, Deck deck) {
        return DeckLogic.createCardToDeckForCategory(category, deck);
        //TODO: wofür?
    }


    //TO IMPLEMENT
    public static List<Deck> getDecksBySearchterm(String searchterm) {
        return DeckLogic.getDecksBySearchterm(searchterm);
    }

    /**
     * Wird benutzt, um einzelne Karten aus dem Deck zu löschen
     * @param cards
     * @param deck
     */
    public static void removeCardsFromDeck(List<Card> cards, Deck deck) {
        DeckLogic.removeCardsFromDeck(cards, deck);
    }

    /**
     * Für nachträgliches Hinzufpgen von Karten
     * @param cards
     * @param deck
     */
  public static void addCardsToDeck(List<Card> cards, Deck deck){
        DeckLogic.addCardsTodeck(cards,deck);
  }

    public static Deck getDeckByUUID(String uuid) {
        return DeckLogic.getDeckByUUID(uuid);
    }

    public static Set<Card> getCardsInDeck(Deck deck) {
        return DeckLogic.getCardsByDeck(deck);
    }

    public static int numCardsInDeck(Deck deck) {
        return DeckLogic.numCardsInDeck(deck);
    }

    public static Set<StudySystemType> getStudySystemTypes() {
        return DeckLogic.getStudySystemTypes();
    }


    public static boolean updateStudySystemData(Deck deck, StudySystem system) {
        return DeckLogic.updateStudySystem(deck, system);
    } //NEEDED? or directly in Deck Update?
}