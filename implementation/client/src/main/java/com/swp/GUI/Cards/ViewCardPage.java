package com.swp.GUI.Cards;

import com.swp.Controller.Controller;
import com.swp.DataModel.Card;

public class ViewCardPage {
    Controller controller;

    public void displayInformationToCard(){
        String card="";
        controller.getAllInfosToCard(card);
    }
    
}
