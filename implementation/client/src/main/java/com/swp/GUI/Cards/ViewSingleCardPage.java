package com.swp.GUI.Cards;

import com.swp.Controller.CardController;
import com.swp.DataModel.Card;

public class ViewSingleCardPage 
{
    //RatingGUI pRatingGUI; //TODO

    public ViewSingleCardPage(Card card)
    {
        //pRatingGUI = new RatingGUI(card);
    }


    public void displayInformationToCard(){
        String card="";
        CardController.getAllInfosToCard(card);
    }
}
