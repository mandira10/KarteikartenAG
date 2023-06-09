package com.swp.GUI.Extras;

import com.gumse.gui.Basics.Switch;
import com.gumse.gui.Basics.TextBox.Alignment;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.List.ColumnInfo;
import com.gumse.gui.List.ColumnInfo.ColumnType;
import com.gumse.gui.List.List;
import com.gumse.gui.List.ListCell;
import com.gumse.gui.List.ListEntry;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.maths.ivec2;
import com.gumse.system.io.Mouse;
import com.swp.DataModel.CardOverview;
import com.swp.GUI.Cards.ViewSingleCardPage;
import com.swp.GUI.PageManager;
import com.swp.GUI.PageManager.PAGES;

import java.util.ArrayList;
import java.util.function.Predicate;

/**
 * CardList wird dazu verwendet um 
 * eine gegebene liste von Karten anzuzeigen.
 * 
 * @author Tom Beuke
 */
public class CardList extends RenderGUI
{
    /**
     * Das Callback interface welches den Status des Auswahlmodus wiedergibt
     */
    public interface CardListSelectmodeCallback
    {
        /**
         * Wird beim Aktivieren des Auswahlmodus ausgeführt
         */
        void enterSelectmode();

        /**
         * Wird beim Deaktivieren des Auswahlmodus ausgeführt
         */
        void exitSelectmode();
    }

    private final List<CardOverview> pList;
    private boolean bIsInSelectmode;
    private final boolean bSingleselectmode;
    private final CardListSelectmodeCallback pCallback;

    private static final int CHECK_COLUMN = 3;

    /**
     * Der Hauptkonstruktor der Klasse CardList
     *
     * @param pos          Position des GUIs in Pixeln
     * @param size         Größe des GUIs in Pixeln
     * @param singleselect Entscheidet, ob nur maximal eine Karte ausgewählt werden darf
     * @param callback     Das Auswahlmodus Callbackinterface
     */
    public CardList(ivec2 pos, ivec2 size, boolean singleselect, CardListSelectmodeCallback callback)
    {
        vPos.set(pos);
        vSize.set(size);
        this.bIsInSelectmode = false;
        this.bSingleselectmode = singleselect;
        this.pCallback = callback;

        ColumnInfo checkcolumn = new ColumnInfo("", ColumnType.BOOLEAN, 5, "");
        checkcolumn.font = FontManager.getInstance().getFont("FontAwesomeRegular");
        checkcolumn.alignment = Alignment.CENTER;
        checkcolumn.onclickcallback = new GUICallback() {
            @Override public void run(RenderGUI gui) 
            {
                if(bSingleselectmode)
                    return;

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
                new ColumnInfo("Title",        ColumnType.STRING,  65, "cardlisttitle"),
                new ColumnInfo("Creationdate", ColumnType.DATE,    20, "cardlistdate"),
                new ColumnInfo("Decks",        ColumnType.INTEGER, 10, "cardlistdecks"),
                checkcolumn
            }
        );
        pList.setSizeInPercent(true, true);
        addElement(pList);

        onHover(null, Mouse.GUM_CURSOR_HAND);


        resize();
        reposition();
    }

    /**
     * Setzt die Auswahl zurück
     */
    public void resetSelection()
    {
        pList.getColumnWhere(CHECK_COLUMN, new Predicate<ListEntry<CardOverview>>() {
            @Override public boolean test(ListEntry<CardOverview> arg0) 
            {
                Switch switchgui = (Switch)arg0.getChild(CHECK_COLUMN).getChild(0);
                switchgui.tick(false);
                return false;
            }
        });
    }

    /**
     * Überprüft, ob die Kartenliste sich im Auswahlmodus befinden soll
     */
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
                pCallback.enterSelectmode();
            bIsInSelectmode = true;

            return;
        }

        if(bIsInSelectmode)
            pCallback.exitSelectmode();
        bIsInSelectmode = false;
    }

    /**
     * Führt ein Callback wenn man in der Liste nach ganz unten Scrollt
     * @param callback Die Callbackfunktion..
     */
    public void onBottomHit(GUICallback callback)
    {
        pList.onBottomHit(callback);
    }

    /**
     * Fügt eine Karte zur Liste hinzu
     * @param cardoverview Die hinzuzufügende Karte
     * @param lastpage     Die Seite zu welcher zurückgegangen werden soll nachdem man zu der ViewSingleCardPage gelangt ist
     */
    public void addCard(CardOverview cardoverview, PAGES lastpage)
    {
        GUICallback commoncallback = new GUICallback() {
            @Override public void run(RenderGUI gui) 
            {   
                if(bSingleselectmode && pCallback != null)
                {
                    Switch switchgui = (Switch)gui.getParent().getChild(CHECK_COLUMN).getChild(0);
                    switchgui.tick(!switchgui.isTicked());
                    pCallback.enterSelectmode();
                    return;
                }
                if(bIsInSelectmode)
                {
                    Switch switchgui = (Switch)gui.getParent().getChild(CHECK_COLUMN).getChild(0);
                    switchgui.tick(!switchgui.isTicked());
                    updateSelectmode();
                }
                else
                {
                    ((ViewSingleCardPage)PageManager.viewPage(PAGES.CARD_SINGLEVIEW)).setCard(cardoverview, lastpage);
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

    /**
     * Fügt mehrere Karten auf einmal hinzu
     * @param cardoverviews Die hinzuzufügenden Karten
     * @param lastpage      Die Seite zu welcher zurückgegangen werden soll nachdem man zu der ViewSingleCardPage gelangt ist
     */
    public void addCards(java.util.List<CardOverview> cardoverviews, PAGES lastpage)
    {
        for(CardOverview cardoverview : cardoverviews)
            addCard(cardoverview, lastpage);
    }

    /**
     * Setzt die Kartenliste zurück
     */
    public void reset()
    {
        pList.reset();
        updateSelectmode();
    }

    //
    // Setter
    //
    public void setSelectMode(boolean mode) { this.bIsInSelectmode = mode; }

    
    //
    // Getter
    //
    public boolean isInSelectmode() { return bIsInSelectmode; }
    public ArrayList<CardOverview> getSelection() 
    {
        ArrayList<CardOverview> foundEntries = pList.getUserdataWhere((ListEntry<CardOverview> arg0) -> {
            Switch switchgui = (Switch)arg0.getChild(CHECK_COLUMN).getChild(0);
            return switchgui.isTicked();
        });

        return foundEntries;
    }

    public ArrayList<CardOverview> getCards() 
    {
        ArrayList<CardOverview> foundEntries = pList.getUserdataWhere((ListEntry<CardOverview> arg0) -> {
            return true;
        });

        return foundEntries;
    }
}