package com.swp.Controller;

import com.swp.DataModel.Card;
import com.swp.DataModel.Category;
import com.swp.Logic.CardLogic;

import java.util.List;

public class Controller {

    CardLogic cardLogic;

    public List<Card> getCardsWithCategory(String category) {
    return cardLogic.getCardsForCategory(category);
    }

    public List<Card> getCardsWithTag(String tag) {
        return cardLogic.getCardsForTag(tag);
    }

    public List<Card> getCardsWithSearchWords(String searchWords) {
        return cardLogic.getCardsForSearchWords(searchWords);
    }

    public void createTrueFalseCard(String question, boolean answer, boolean visibility) {
        cardLogic.createTrueFalseCard(question,answer,visibility);
    }

    public void deleteCard(Card card)    { /*TODO*/ }
    public void deleteCards(Card[] card) { /*TODO*/ }
    public void editCard(Card card)      { /*TODO*/ } //Override entry based on UUID
    


    public void getAllInfosToCard(String card) {
        cardLogic.getAllInfosForCard(card);
    }

    public void createCardToCategory(Card card, String category) {
        //TODO
    }

    public void createCardToTag(Card card, String tag) {
        //TODO
    }

    public void getCountOfDecksFor(String card) {
        int numberOfDecks = cardLogic.getCountOfDecksFor(card);
    }
}
