package com.swp.Controller;

import com.swp.DataModel.*;
import com.swp.Logic.CardLogic;
import com.swp.Persistence.Exporter.ExportFileType;

import java.util.List;

public class CardController 
{
    private CardController() {}

    public static List<Card> getCardsByCategory(Category category) {
    return CardLogic.getCardsByCategory(category);
    }

    public static List<Card> getCardsByTag(Tag tag) {
        return CardLogic.getCardsByTag(tag);
    }

    public static List<Card> getCardsBySearchterms(String searchterms) {
        return CardLogic.getCardsBySearchterms(searchterms);
    }

    public static void deleteCard(Card card)    { 
        CardLogic.deleteCard(card);
    }
    public static void deleteCards(Card[] card) { 
        CardLogic.deleteCards(card);
     }

    public void createTag(String value)
    {
        CardLogic.createTag(value);
    }
    
    /**
     * Gets called by the EditCardPage GUI upon clicking apply
     * @param card
     */
    public static void editCard(Card oldcard, Card newcard)
    { 
        CardLogic.updateCardData(oldcard, newcard);
    } //Override entry based on UUID

    public static void getAllInfosToCard(String card) {
        CardLogic.getAllInfosForCard(card);
    }

    public static void createCardToTag(Card card, Tag tag) {
        //TODO
    }

    public static void getCountOfDecksFor(String card) {
        int numberOfDecks = CardLogic.getCountOfDecksFor(card);
    }

    public static void getCardsToShow(long begin, long end){
        CardLogic.getCardsToShow(begin, end);
    }

    public static void exportCards(Card[] cards, ExportFileType filetype)
    {
        CardLogic.exportCards(cards, filetype);
    }
}
