package com.swp.DataModel;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

public abstract class StudySystem 
{
    private String[] asProfiles; //TODO
    protected Deck pDeck;
    protected ArrayList<Set<Card> > alBoxes;
    protected StudySystemType pType;

    private ArrayList<Set<Card> > initArray(int size)
    {
        ArrayList<Set<Card> > retArr = new ArrayList<>();
        for(int i = 0; i < size; i++)
            retArr.add(new HashSet<Card>());

        return retArr;
    }

    public StudySystem(Deck deck, StudySystemType type, int nboxes)
    {
        this.pDeck = deck;
        this.pType = type;
        alBoxes = initArray(nboxes);
    }

    public void giveAnswer(boolean answer) { }
    public Card getNextCard()              { return null; }

    public void moveCardToBox(Card card, int boxindex)
    {
        if(boxindex >= alBoxes.size() || boxindex < 0)
            return;

        for(Set<Card> set : alBoxes)
        {
            if(set.contains(card))
                set.remove(card);
        }

        alBoxes.get(boxindex).add(card);
    }
}