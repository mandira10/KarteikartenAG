package com.swp.Controller;

import com.swp.DataModel.Card;
import com.swp.DataModel.Category;
import com.swp.DataModel.Deck;
import com.swp.DataModel.StudySystem;
import com.swp.Logic.CardLogic;
import com.swp.Logic.CategoryLogic;
import com.swp.Logic.DeckLogic;

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
        cardLogic.createCardToCategory(card, category);
    }

    public void createCardToTag(Card card, String tag) {
        //TODO
    }

    public void getCountOfDecksFor(String card) {
        int numberOfDecks = cardLogic.getCountOfDecksFor(card);
    }

    // CATEGORY
    CategoryLogic categoryLogic;
    public void createCategory(String name, List<Category> parents, List<Category> children) {
        categoryLogic.createCategory(name, parents, children);
    }
    public void editCategory(Category category, String name, List<Category> parents, List<Category> children) {
        categoryLogic.editCategory(category, name, parents, children);
    }
    public void deleteCategory(Category category) { categoryLogic.deleteCategory(category); }
    public void getCategories() { categoryLogic.getCategories(); }
    public void getAllInfosFor(Category category) { categoryLogic.getAllInfosFor(category); }
    public List<Category> getParentCategories(Category category) { return categoryLogic.getParentCategories(category); }
    public List<Category> getChildrenCategories(Category category) { return categoryLogic.getChildrenCategories(category); }
    public void getCountOfCardsFor(Category category) { cardLogic.getCardsForCategory(category.toString()); }
    
    // DECK
    DeckLogic deckLogic;
    public void createDeck(String name, StudySystem studySystem, Deck.CardOrder order) {
        deckLogic.createDeck(name, studySystem, order);
    }
    public void editDeck(Category deck, String name, StudySystem studySystem, Deck.CardOrder order, boolean visibility) {
        deckLogic.editDeck(deck, name, studySystem, order, visibility);
    }
    public void deleteDeck(Deck deck) { deckLogic.deleteDeck(deck); }
    public void createCardToDeck(Card card, Deck deck) { cardLogic.createCardToDeck(card, deck); }
    public void createCardToDeckForCategory(Category category, Deck deck) { cardLogic.createCardToDeckForCategory(category, deck); }
    public List<Deck> getDecks() { return deckLogic.getDecks(); }
    public void getAllInfosForDeck(String deck) { deckLogic.getAllInfosForDeck(deck); }
    public void getCountOfCardsFor(Deck deck) { deckLogic.getCountOfCardsFor(deck); }

}
