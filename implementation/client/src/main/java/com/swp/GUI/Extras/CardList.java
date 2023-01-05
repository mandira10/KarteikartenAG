package com.swp.GUI.Extras;

import java.util.Set;

import com.gumse.gui.Basics.Scroller;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.maths.ivec2;
import com.gumse.tools.Debug;
import com.swp.DataModel.Card;

public class CardList extends RenderGUI
{
    private Scroller pScroller;
    private boolean bIsInSelectmode;

    private static final int ENTRY_HEIGHT = 40;
    private static final int GAP_SIZE = 5;

    public CardList(ivec2 pos, ivec2 size)
    {
        vPos.set(pos);
        vSize.set(size);
        this.bIsInSelectmode = false;

        pScroller = new Scroller(new ivec2(0, 0), new ivec2(100, 100));
        pScroller.setSizeInPercent(true, true);
        addElement(pScroller);


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

    public void addCard(Card card)
    {
        CardListEntry entry = new CardListEntry(card, new ivec2(0, pScroller.numChildren() * (ENTRY_HEIGHT + GAP_SIZE)), new ivec2(100, ENTRY_HEIGHT), this);
        entry.setSizeInPercent(true, false);
        pScroller.addGUI(entry);
    }

    public void addCards(Set<Card> cards)
    {
        for(Card card : cards)
            addCard(card);
    }

    public void reset()
    {
        pScroller.destroyChildren();
    }


    //
    // Getter
    //
    public boolean isInSelectmode() { return bIsInSelectmode; }
}