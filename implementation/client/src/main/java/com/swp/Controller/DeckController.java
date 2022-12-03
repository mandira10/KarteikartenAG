package com.swp.Controller;

import com.swp.DataModel.*;
import com.swp.Logic.CardLogic;
import com.swp.Logic.DeckLogic;
import com.swp.Persistence.Exporter.ExportFileType;

import java.util.ArrayList;
import java.util.List;

public class DeckController
{
    private DeckController() {}

    
    public static void updateDeckData(Deck olddeck, Deck newdeck) 
    {
        DeckLogic.updateDeckData(olddeck, newdeck);
    }
    public static void deleteDeck(Deck deck) { DeckLogic.deleteDeck(deck); }
    public static void deleteDecks(Deck[] decks) { DeckLogic.deleteDecks(decks); }

    public static void getDecksAndCards(){
        DeckLogic.getDecksAndCards();

    }


    /*public static Card[] getCards()
    {
        ArrayList<Card> retCards = new ArrayList<>();
        for(CardToDeck c2d : DeckLogic.getCardToDecks())
        {
            retCards.add(c2d.getCard());
        }
        
        return (Card[])retCards.toArray();
    }*/

    public static void createCardToDeck(Card card, Deck deck) { CardLogic.createCardToDeck(card, deck); }
    public static void createCardToDeckForCategory(Category category, Deck deck) { CardLogic.createCardToDeckForCategory(category, deck); }
    public static List<Deck> getDecks() { return DeckLogic.getDecks(); }
    public static void getAllInfosForDeck(String deck) { DeckLogic.getAllInfosForDeck(deck); }
    public static void getCountOfCardsFor(Deck deck) { DeckLogic.getCountOfCardsInDeck(deck); }
    public static void exportCards(Card[] cards, ExportFileType filetype)
    {
        CardLogic.exportCards(cards, filetype);
    }
}