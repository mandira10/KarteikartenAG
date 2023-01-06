package com.swp.GUI.Extras;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.gumse.gui.Basics.Scroller;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.maths.ivec2;
import com.gumse.tools.Debug;
import com.swp.DataModel.Card;

public class CardList extends RenderGUI
{
    public interface CardListSelectmodeCallback
    {
        public void enterSelectmod();
        public void exitSelectmod();
    }

    private Scroller pScroller;
    private boolean bIsInSelectmode;
    private CardListSelectmodeCallback pCallback;

    private static final int ENTRY_HEIGHT = 40;
    private static final int GAP_SIZE = 5;

    public CardList(ivec2 pos, ivec2 size, CardListSelectmodeCallback callback)
    {
        vPos.set(pos);
        vSize.set(size);
        this.bIsInSelectmode = false;
        this.pCallback = callback;

        pScroller = new Scroller(new ivec2(0, 0), new ivec2(100, 100));
        pScroller.setSizeInPercent(true, true);
        addElement(pScroller);


        resize();
        reposition();
    }

    public void updateSelectmode()
    {
        for(RenderGUI entry : pScroller.getChildren())
        {
            CardListEntry cardEntry = (CardListEntry)entry;
            if(cardEntry.isSelected())
            {
                if(!bIsInSelectmode)
                    pCallback.enterSelectmod();
                bIsInSelectmode = true;
                return;
            }
        }
        if(bIsInSelectmode)
            pCallback.exitSelectmod();
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
    public List<Card> getSelection() 
    {
        List<Card> retList = new ArrayList<>();
        for(RenderGUI child : pScroller.getChildren())
        {
            CardListEntry entry = (CardListEntry)child;
            if(entry.isSelected())
                retList.add(entry.getCard());
        }

        return retList;
    }
}