package com.swp.GUI.Cards;

import com.gumse.gui.Primitives.RenderGUI;
import com.swp.DataModel.*;
import com.swp.GUI.Page;

public class TestCardPage extends Page
{
    private Card pCard = null;

    public TestCardPage()
    {
        super("Test Card");
        /*load card at progress state*/
    };

    void startTest(Card card)
    {
        this.pCard = card;
    }

    public void cancel(){/*save current state and exit to DeckOverviewPage*/};
    public void checkAnswers(){};
}
