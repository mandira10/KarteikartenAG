package com.swp.GUI.Decks;

import com.gumse.gui.Primitives.RenderGUI;
import com.swp.DataModel.Deck;

public class TestDeckPage extends RenderGUI
{
    public TestDeckPage()
    {

    }

    public void startTests(Deck deck)
    {
    }

    private void nextCard()
    {

    }

    private void finishTest()
    {

    }

    @Override
    public void update()
    {
        if(StudySytstem.getNextCard() != null)
        {
            nextCard();
        }
    }
}