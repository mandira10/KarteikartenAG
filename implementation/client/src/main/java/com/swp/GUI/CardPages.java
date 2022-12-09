package com.swp.GUI;

import com.swp.GUI.Cards.CardOverviewPage;
import com.swp.GUI.Cards.EditCardPage;
import com.swp.GUI.Cards.TestCardPage;
import com.swp.GUI.Cards.ViewSingleCardPage;

public class CardPages 
{
    private CardOverviewPage pCardOverviewPage;
    private EditCardPage pEditCardPage;
    private TestCardPage pTestCardPage;
    private ViewSingleCardPage pViewCardPage;
    
    public CardPages()
    {
        pCardOverviewPage = new CardOverviewPage();
        PageManager.addPage("CardOverview", pCardOverviewPage);

        pViewCardPage = new ViewSingleCardPage();
        PageManager.addPage("ViewSingleCard", pViewCardPage);

        pEditCardPage = new EditCardPage();
        PageManager.addPage("EditCard", pEditCardPage);

        pTestCardPage = new TestCardPage();
        PageManager.addPage("TestCard", pTestCardPage);
    }
}