package com.swp.GUI.Cards;

import com.swp.Controller.Controller;
import com.swp.DataModel.Card;

public class CardOverviewPage {
    Controller controller;

    public void showCardsWithCategory(){
        String category  ="";
        controller.getCardsWithCategory(category);}
    public void showCardsWithTag(){
        String tag = "";
        controller.getCardsWithTag(tag);}
    public void showCardsWithSearchWords(){
        String searchWords="";
        controller.getCardsWithSearchWords(searchWords);}

    public void getCountOfDecksToCard(){
       String card = "";
        controller.getCountOfDecksFor(card);
    }

    
}
