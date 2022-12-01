package com.swp.GUI.Decks;

import com.gumse.gui.Basics.Dropdown;
import com.gumse.gui.Primitives.RenderGUI;
import com.swp.Controller.DeckController;
import com.swp.DataModel.Card;
import com.swp.DataModel.Category;
import com.swp.DataModel.Deck;
import com.swp.DataModel.StudySystem;

import java.util.List;

public class EditDeckPage extends RenderGUI
{
    private Dropdown pStudySystemDropdown; //TODO


    public void editDeck()
    {
        Deck deck = null;
        String name = "";
        StudySystem studySystem = null;
        Deck.CardOrder order = null;
        boolean visibility = false;
        DeckController.updateDeckData(deck, deck); //TODO
    }

    public void deleteDeck()
    {
        Deck deck = null;
        DeckController.deleteDeck(deck);
    }

    public void addCardToDeck()
    {
        Card card = null;
        Deck deck = null;
        DeckController.createCardToDeck(card,deck);
    }

    public void addCardsOfCategoryToDeck()
    {
        Category category = null;
        Deck deck = null;
        DeckController.createCardToDeckForCategory(category,deck);
    }
}
