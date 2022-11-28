package com.swp.GUI.Decks;

import com.swp.Controller.Controller;
import com.swp.DataModel.Deck;

public class DeckOverviewPage {
    Controller controller;
    public void showDecks()
    {
        controller.getDecks();
    }

    public void getCountOfCardToDeck()
    {
        Deck deck = null;
        controller.getCountOfCardsFor(deck);
    }
}
