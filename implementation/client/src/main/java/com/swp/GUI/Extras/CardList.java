package com.swp.GUI.Extras;

import java.util.List;
import java.util.Set;

import com.gumse.gui.Basics.Scroller;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.maths.ivec2;
import com.gumse.tools.Debug;
import com.swp.Controller.CardController;
import com.swp.DataModel.Card;

public class CardList extends RenderGUI
{
    private Scroller pScroller;
    private boolean bIsInSelectmode;

    public CardList(ivec2 pos, ivec2 size, Set<Card> cards)
    {
        vPos.set(pos);
        vSize.set(size);
        int entryHeight = 40;
        int gapSize = 5;
        int numentries = 0;
        this.bIsInSelectmode = false;

        pScroller = new Scroller(new ivec2(0, 0), new ivec2(100, 100));
        pScroller.setSizeInPercent(true, true);
        addElement(pScroller);

        for(Card card : cards)
        {
            CardListEntry entry = new CardListEntry(card, new ivec2(0, numentries++ * (entryHeight + gapSize)), new ivec2(100, entryHeight), this);
            entry.setSizeInPercent(true, false);
            pScroller.addGUI(entry);
        }

        resize();
        reposition();
    }

    public void updateSelectmode()
    {
        Debug.info("Updating");
        int i = 0;
        for(RenderGUI entry : pScroller.getChildren())
        {
            Debug.info(i++);
            CardListEntry cardEntry = (CardListEntry)entry;
            Debug.info(cardEntry.isSelected());
            if(cardEntry.isSelected())
            {
                bIsInSelectmode = true;
                return;
            }
        }
        bIsInSelectmode = false;
    }


    //
    // Getter
    //
    public boolean isInSelectmode() { return bIsInSelectmode; }
}