package com.swp.Controller;

import com.swp.DataModel.*;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.Logic.DeckLogic;
import jakarta.persistence.NoResultException;
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

    public void getDecks(DataCallback<Deck> dataCallback) {

       try {
           List<Deck> decks = deckLogic.getDecks();
           if (decks.isEmpty()) {
               dataCallback.onInfo("Es gibt derzeit Decks");
           }

           dataCallback.onSuccess(decks);
       }
       catch(Exception ex){
           dataCallback.onFailure(ex.getMessage());
               }
    }

    /**
     * Wird verwendet, um das Deck zu updaten.
     * @param olddeck Deck im vorherigen Zustand, benötigt, um festzustellen, ob das StudySystem gewechselt wurde und Handling
     * @param newdeck Neue Deck Eigenschaften
     * @param neu Ist true, wenn das Deck neu angelegt wurde
     */
    public void updateDeckData(Deck olddeck, Deck newdeck, boolean neu, SingleDataCallback<Boolean> singleDataCallback) {
         try{deckLogic.updateDeckData(olddeck, newdeck,neu);}
         catch(Exception ex){
             singleDataCallback.onFailure(ex.getMessage());
         }
    }

    /**
     * Wird verwendet, um ein komplett neues StudySystem anzulegen
     * @param type Typ des StudySystems
     * @return true, wenn erfolgreich
     */
    public void addStudySystemTypeAndUpdate(StudySystem.StudySystemType type, SingleDataCallback<Boolean> singleDataCallback) {
         try{deckLogic.addStudySystemTypeAndUpdate(type);}
         catch(Exception ex){
             singleDataCallback.onFailure(ex.getMessage());
         }
    }

    public void deleteDeck(Deck deck, SingleDataCallback<Boolean> singleDataCallback) {
         try{deckLogic.deleteDeck(deck);}
         catch(IllegalStateException ex){
             singleDataCallback.onFailure(ex.getMessage());
         }
         catch(Exception ex){
             singleDataCallback.onFailure(ex.getMessage());
         }
    }

    public void deleteDecks(Deck[] decks, SingleDataCallback<Boolean> singleDataCallback) {

         try{deckLogic.deleteDecks(decks);}
         catch(Exception ex){
             singleDataCallback.onFailure(ex.getMessage());
         }
    }



    public void createCardToDeckForCategory(Category category, Deck deck, SingleDataCallback<Boolean> singleDataCallback) {
         try{deckLogic.createCardToDeckForCategory(category, deck);}
         catch(Exception ex){
             singleDataCallback.onFailure(ex.getMessage());
         }
    }


    public void getDecksBySearchterm(String searchterm, DataCallback<Deck> dataCallback) {
         try{
             List<Deck> decks = deckLogic.getDecksBySearchterm(searchterm);
         if(decks.isEmpty()){
             dataCallback.onInfo("Es gibt keine Decks zu diesem Suchbegriff");
         }
         dataCallback.onSuccess(decks);
         }
         catch(IllegalArgumentException ex){
             dataCallback.onFailure(ex.getMessage());
         }
         catch(Exception ex){
             dataCallback.onFailure(ex.getMessage());
         }
    }

    /**
     * Wird benutzt, um einzelne Karten aus dem Deck zu löschen
     * @param cards
     * @param deck
     */
    public void removeCardsFromDeck(List<Card> cards, Deck deck, SingleDataCallback<Boolean> singleDataCallback) {
        try {
            deckLogic.removeCardsFromDeck(cards, deck);
        }
        catch(Exception ex){
            singleDataCallback.onFailure(ex.getMessage());
        }
    }

    /**
     * Für nachträgliches Hinzufpgen von Karten
     * @param cards
     * @param deck
     */
  public void addCardsToDeck(List<Card> cards, Deck deck, SingleDataCallback<Boolean> singleDataCallback){
        try{deckLogic.addCardsToDeck(cards,deck);}
        catch(Exception ex){
            singleDataCallback.onFailure(ex.getMessage());
        }

  }

    public void getDeckByUUID(String uuid,SingleDataCallback<Deck> singleDataCallback) {
        try{
            singleDataCallback.onSuccess(deckLogic.getDeckByUUID(uuid));
        }
        catch (IllegalArgumentException ex) {//übergebener Wert ist leer
            singleDataCallback.onFailure(ex.getMessage());
        }
        catch(NoResultException ex){
           singleDataCallback.onFailure("Es konnte kein Deck zur UUID gefunden werden");
        }
        catch(Exception ex){
            singleDataCallback.onFailure("Beim Abrufen des Decks ist ein Fehler aufgetreten");
        }
    }

    public void getCardsInDeck(Deck deck,DataCallback<Card> dataCallback) {
        try{
            List<Card> cards = deckLogic.getCardsByDeck(deck);
            if(cards.isEmpty()){
                dataCallback.onInfo("Es gibt keine Karten für dieses Deck");
            }
            dataCallback.onSuccess(cards);
        }
        catch(Exception ex){
            dataCallback.onFailure(ex.getMessage());
        }
    }

    public void numCardsInDeck(Deck deck, SingleDataCallback<Integer> singleDataCallback) {
       try{
           singleDataCallback.onSuccess(deckLogic.numCardsInDeck(deck));
       }
       catch(Exception ex){
          log.error("Beim Abrufen von der Anzahl der Decks ist ein Fehler aufgetreten");
       }
    }




    public void createCardToDeck(Card card, Deck deck,  SingleDataCallback<Boolean> singleDataCallback) {
        try{deckLogic.createCardToDeck(card, deck);}
        catch(Exception ex){
            singleDataCallback.onFailure(ex.getMessage());
        }
    } //TODO manuelles Aufrufen notwendig???
}