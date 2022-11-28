package com.swp.GUI.Decks;

import com.swp.Controller.Controller;

public class ViewDeckPage {
    Controller controller;

    public void displayInformationToCard(){
        String deck="";
        controller.getAllInfosForDeck(deck);
    }
}
