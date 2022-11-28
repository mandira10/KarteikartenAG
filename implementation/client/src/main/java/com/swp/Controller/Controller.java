package com.swp.Controller;

import com.swp.DataModel.Card;
import com.swp.DataModel.Category;
import com.swp.DataModel.Deck;
import com.swp.DataModel.StudySystem;
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

    public void createCardToCategory(Card card, Category category) {
        //TODO
    }

    public void createCardToTag(Card card, String tag) {
        //TODO
    }

    public void getCountOfDecksFor(String card) {
        int numberOfDecks = cardLogic.getCountOfDecksFor(card);
    }

    // CATEGORY
    public void createCategory(String name, List<Category> parents, List<Category> children) { /*TODO*/ }
    public void editCategory(Category category, String name, List<Category> parents, List<Category> children) { /*TODO*/ }
    public void deleteCategory(Category category) { /*TODO*/ }
    public void getCategories() { /*TODO*/ }
    public void getAllInfosFor(String category) { /*TODO*/ }
    public void getParentCategories(String category) { /*TODO*/ }
    public void getChildrenCategories(String category) { /*TODO*/ }
    public void getCountOfCardsFor(String category) { /*TODO*/ }
    
    // DECK
    public void createDeck(String name, StudySystem studySystem, Deck.CardOrder order) { /*TODO*/ }
    public void editDeck(Category deck, String name, StudySystem studySystem, Deck.CardOrder order, boolean visibility) { /*TODO*/ }
    public void deleteDeck(Deck deck) { /*TODO*/ }
    public void createCardToDeck(Card card, Deck deck) { /*TODO*/ }
    public void createCardToDeckForCategory(Category category, Deck deck) { /*TODO*/ }
    public void getDecks() { /*TODO*/ }
    public void getAllInfosForDeck(String deck) { /*TODO*/ }

}
