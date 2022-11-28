package com.swp.GUI.Decks;

import com.swp.Controller.Controller;

public class DeckOverviewPage {
    Controller controller;
    public void showDecks()
    {
        controller.getDecks();
    }

    public void getCountOfCardToDeck()
    {
        String deck  ="";
        controller.getCountOfCardsFor(deck);
    }
}
