package com.swp.GUI.Decks;

import com.swp.Controller.Controller;
import com.swp.DataModel.Card;
import com.swp.DataModel.Category;
import com.swp.DataModel.Deck;
import com.swp.DataModel.StudySystem;

import java.util.List;

public class EditDeckPage {
    Controller controller;

    public void createDeck()
    {
        String name = "";
        StudySystem studySystem = null;
        Deck.CardOrder order = null;
        controller.createDeck(name, studySystem, order);
    }

    public void editDeck()
    {
        Category deck = null;
        String name = "";
        StudySystem studySystem = null;
        Deck.CardOrder order = null;
        boolean visibility = false;
        controller.editDeck(deck, name, studySystem, order, visibility);
    }

    public void deleteDeck()
    {
        Deck deck = null;
        controller.deleteDeck(deck);
    }

    public void addCardToDeck()
    {
        Card card = null;
        Deck deck = null;
        controller.createCardToDeck(card,deck);
    }

    public void addCardsOfCategoryToDeck()
    {
        Category category = null;
        Deck deck = null;
        controller.createCardToDeckForCategory(category,deck);
    }
}
