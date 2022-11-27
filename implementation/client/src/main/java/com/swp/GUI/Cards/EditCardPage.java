package com.swp.GUI.Cards;

import com.swp.Controller.Controller;
import com.swp.DataModel.Card;

public class EditCardPage {

    Controller controller;

    public void finishCreateCardTrueFalse() {
        String question = "";
        Boolean answer = true;
        Boolean visibility = true;
        //Read in
        controller.createTrueFalseCard(question, answer, visibility);
                //TODO: CategoryToCard wie umzusetzen? separate Methode?
                //TODO: TagToCard wie umzusetzen? separate Methode?
            }


    //TODO weitere Kartentypen

        public void finishChangeCardType(){
            //TODO
        }

        public void finishAddCategoryToCard(){
        //TODO
        }

    }
