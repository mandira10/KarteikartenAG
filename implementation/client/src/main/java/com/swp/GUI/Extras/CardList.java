package com.swp.GUI.Extras;

import java.util.ArrayList;
import java.util.function.Predicate;

import com.gumse.gui.Basics.Switch;
import com.gumse.gui.Basics.TextBox.Alignment;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.List.ColumnInfo;
import com.gumse.gui.List.List;
import com.gumse.gui.List.ListCell;
import com.gumse.gui.List.ListEntry;
import com.gumse.gui.List.ColumnInfo.ColumnType;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.maths.ivec2;
import com.gumse.system.io.Mouse;
import com.swp.DataModel.CardOverview;
import com.swp.GUI.PageManager;
import com.swp.GUI.Cards.ViewSingleCardPage;
import com.swp.GUI.PageManager.PAGES;

public class CardList extends RenderGUI
{
    public interface CardListSelectmodeCallback
    {
        public void enterSelectmod();
        public void exitSelectmod();
    }

    private List<CardOverview> pList;
    private boolean bIsInSelectmode;
    private CardListSelectmodeCallback pCallback;

    private static final int CHECK_COLUMN = 3;

    public CardList(ivec2 pos, ivec2 size, CardListSelectmodeCallback callback)
    {
        vPos.set(pos);
        vSize.set(size);
        this.bIsInSelectmode = false;
        this.pCallback = callback;

        ColumnInfo checkcolumn = new ColumnInfo("", ColumnType.BOOLEAN, 5, "");
        checkcolumn.font = FontManager.getInstance().getFont("FontAwesomeRegular");
        checkcolumn.alignment = Alignment.CENTER;
        checkcolumn.onclickcallback = new GUICallback() {
            @Override public void run(RenderGUI gui) 
            {
                ArrayList<CardOverview> list = pList.getUserdataWhere(new Predicate<ListEntry<CardOverview>>() {
                    @Override public boolean test(ListEntry<CardOverview> arg0) 
                    {
                        Switch switchgui = (Switch)arg0.getChild(CHECK_COLUMN).getChild(0);
                        return switchgui.isTicked();
                    }
                });

                final boolean tickall = list.size() != pList.numEntries();
                pList.getUserdataWhere(new Predicate<ListEntry<CardOverview>>() {
                    @Override public boolean test(ListEntry<CardOverview> arg0) 
                    {
                        Switch switchgui = (Switch)arg0.getChild(CHECK_COLUMN).getChild(0);
                        switchgui.tick(tickall);
                        return false;
                    }
                });

                updateSelectmode();
            }
        };

        pList = new List<>(
            new ivec2(0, 0), 
            new ivec2(100, 100), 
            new ColumnInfo[] {
                new ColumnInfo("Title",        ColumnType.STRING,  65, "CardOverviewlisttitle"),
                new ColumnInfo("Creationdate", ColumnType.DATE,    20, "CardOverviewlistdate"),
                new ColumnInfo("Decks",        ColumnType.INTEGER, 10, "CardOverviewlistdecks"),
                checkcolumn
            }
        );
        pList.setSizeInPercent(true, true);
        addElement(pList);

        onHover(null, Mouse.GUM_CURSOR_HAND);


        resize();
        reposition();
    }

    public void updateSelectmode()
    {
        ArrayList<Boolean> foundEntries = pList.getColumnWhere(CHECK_COLUMN, new Predicate<ListEntry<CardOverview>>() {
            @Override public boolean test(ListEntry<CardOverview> arg0) 
            {
                Switch switchgui = (Switch)arg0.getChild(CHECK_COLUMN).getChild(0);
                return switchgui.isTicked();
            }
        });

        if(foundEntries.size() > 0)
        {
            if(!bIsInSelectmode)
                pCallback.enterSelectmod();
            bIsInSelectmode = true;

            return;
        }

        if(bIsInSelectmode)
            pCallback.exitSelectmod();
        bIsInSelectmode = false;
    }

    public void addCard(CardOverview cardoverview)
    {
        GUICallback commoncallback = new GUICallback() {
            @Override public void run(RenderGUI gui) 
            {
                if(bIsInSelectmode)
                {
                    Switch switchgui = (Switch)gui.getParent().getChild(CHECK_COLUMN).getChild(0);
                    switchgui.tick(!switchgui.isTicked());
                    updateSelectmode();
                }
                else
                {
                    ((ViewSingleCardPage)PageManager.viewPage(PAGES.CARD_SINGLEVIEW)).setCard(cardoverview);
                }
            }
        };

        ListCell titlecell = new ListCell(cardoverview.getTitelToShow());
        titlecell.alignment = Alignment.LEFT;
        titlecell.onclickcallback = commoncallback;

        ListCell creationcell = new ListCell(cardoverview.getCardCreated());
        creationcell.alignment = Alignment.LEFT;
        creationcell.onclickcallback = commoncallback;

        ListCell numdeckscell = new ListCell(cardoverview.getCountDecks());
        numdeckscell.alignment = Alignment.LEFT;
        numdeckscell.onclickcallback = commoncallback;

        ListCell checkcell = new ListCell(false);
        checkcell.onclickcallback = (RenderGUI gui) -> { updateSelectmode(); };

        pList.addEntry(new ListCell[] { titlecell, creationcell, numdeckscell, checkcell }, cardoverview);  
    }

    public void addCards(java.util.List<CardOverview> cardoverviews)
    {
        for(CardOverview cardoverview : cardoverviews)
            addCard(cardoverview);
    }

    public void reset()
    {
        pList.reset();
        updateSelectmode();
    }


    //
    // Getter
    //
    public boolean isInSelectmode() { return bIsInSelectmode; }
    public ArrayList<CardOverview> getSelection() 
    {
        ArrayList<CardOverview> foundEntries = pList.getUserdataWhere(new Predicate<ListEntry<CardOverview>>() {
            @Override public boolean test(ListEntry<CardOverview> arg0) 
            {
                Switch switchgui = (Switch)arg0.getChild(CHECK_COLUMN).getChild(0);
                return switchgui.isTicked();
            }
        });

        return foundEntries;
    }
}