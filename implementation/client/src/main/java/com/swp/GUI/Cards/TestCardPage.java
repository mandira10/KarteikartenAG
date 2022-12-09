package com.swp.GUI.Cards;

import com.gumse.gui.Primitives.RenderGUI;
import com.swp.DataModel.*;

public class TestCardPage extends RenderGUI
{
    private Card pCard = null;

    public TestCardPage()
    {
        /*load card at progress state*/
    };

    void startTest(Card card)
    {
        this.pCard = card;
    }

    public void cancel(){/*save current state and exit to DeckOverviewPage*/};
    public void checkAnswers(){};
}
