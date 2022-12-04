package com.swp.DataModel;

public abstract class StudySystem 
{
    private String[] asProfiles; //TODO
    protected Deck pDeck;

    public StudySystem(Deck deck)
    {
        this.pDeck = deck;
    }

    public Card getNextCard()
    {
        return null;
    }
}