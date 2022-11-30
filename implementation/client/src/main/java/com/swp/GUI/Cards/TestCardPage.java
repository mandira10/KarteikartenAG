package com.swp.GUI.Cards;

public class TestCardPage {

    private Deck Deck = null;
    private Card Card = null;
    private float progress = 0.0;
    private float rating = 0.0;


    TestCardPage(){/*load card at progress state*/};

    public void getNextCard(){/*load and display next Card of Deck (only possible if answers != null) (display prompt to skip card) */};

    public void getCardOfNextDeck(){/*load and display next Card of the next Deck */};

    public float getProgress(){/*Get the progress specific to Studysystem */};

    public float getRating(){/*Timed Rating or User Rating*/};

    public void setRating(){/*Rating by User*/};

    public void interruptTesting(){/*save current state and exit to DeckOverviewPage*/};

    public void checkAnswers(){};

    //public void getPrevCard(){};

    //public void changeStudysystem(){};

    
}
