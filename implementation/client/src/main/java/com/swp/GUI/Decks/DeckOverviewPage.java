package com.swp.GUI.Decks;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Scroller;
import com.gumse.gui.Basics.Button.ButtonCallback;
import com.gumse.gui.Font.Font;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.Box;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.Primitives.Text;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.gumse.maths.vec4;
import com.gumse.system.Window;
import com.gumse.system.io.Mouse;
import com.gumse.tools.Debug;
import com.swp.Controller.DeckController;
import com.swp.DataModel.Deck;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;
import com.swp.GUI.Cards.CardExportPage;
import com.swp.GUI.Extras.Searchbar;
import com.swp.GUI.Extras.Searchbar.SearchbarCallback;
import com.swp.GUI.PageManager.PAGES;

public class DeckOverviewPage extends Page
{
    private class DeckContainer extends Box
    {
        Deck pDeck;

        public DeckContainer(Deck deck)
        {
            super(new ivec2(), new ivec2());
            pDeck = deck;

            Font defaultFont = FontManager.getInstance().getDefaultFont();
            Font fontAwesome = FontManager.getInstance().getFont("FontAwesome");
            
            Text deckName = new Text(deck.getName() + " - " + deck.getStudySystem().getType().getTypeName(), defaultFont, new ivec2(10,10), 0);
            deckName.setCharacterHeight(35);  
            addElement(deckName);

            Text numcardsText = new Text("", defaultFont, new ivec2(10, 50), 0);
            numcardsText.setCharacterHeight(35);

            int percentage = (int)(deck.getStudySystem().getProgress() * 100.0f);
            Text progressText = new Text("Progress: " + percentage + "%", defaultFont, new ivec2(100, 10), 0);
            progressText.setPositionInPercent(true, false);
            progressText.setCharacterHeight(35);
            progressText.setOrigin(new ivec2(200, 0));

            Text iconText = new Text("", fontAwesome, new ivec2(100, 100), 0);
            iconText.setPositionInPercent(true, true);
            iconText.setOrigin(new ivec2(45, 40));
            iconText.setCharacterHeight(30);
            iconText.setColor(new vec4(0.13f, 0.13f, 0.14f, 1));

            int numCards = DeckController.numCardsInDeck(deck);
            if(numCards > 0)
            {
                numcardsText.setString("Cards: " + numCards);
                iconText.setString("");
            }
            else
            {
                numcardsText.setString("Empty");
                iconText.setString("");
                iconText.setOrigin(new ivec2(50, 40));
            }
            addElement(progressText);
            addElement(numcardsText);
            addElement(iconText);

            onHover(null, Mouse.GUM_CURSOR_HAND);

            onClick(new GUICallback() {
                @Override public void run(RenderGUI gui) 
                {
                    ViewSingleDeckPage page = (ViewSingleDeckPage)PageManager.getPage(PAGES.DECK_SINGLEVIEW);
                    page.setDeck(pDeck);
                    PageManager.viewPage(PAGES.DECK_SINGLEVIEW);
                }
            });
        }
    };

    private Scroller pCanvas;

    public DeckOverviewPage()
    {
        super("Decks");
        this.vSize = new ivec2(100,100);

        addGUI(XMLGUI.loadFile("guis/decks/deckoverviewpage.xml"));

        pCanvas = (Scroller)findChildByID("canvas");
        
        RenderGUI optionsMenu = findChildByID("menu");
        Button newButton = (Button)optionsMenu.findChildByID("adddeckbutton");
        newButton.setCallbackFunction(new ButtonCallback() {
            @Override public void run() 
            {
                EditDeckPage page = (EditDeckPage)PageManager.getPage(PAGES.DECK_EDIT);
                page.editDeck(new Deck());
                PageManager.viewPage(PAGES.DECK_EDIT);
            }
        });

        Searchbar searchbar = new Searchbar(new ivec2(20, 100), new ivec2(40, 30), "Search Deck", new SearchbarCallback() {
            @Override public void run(String query) 
            {
                loadDecks(query);
            }
        });
        searchbar.setPositionInPercent(false, true);
        searchbar.setSizeInPercent(true, false);
        searchbar.setOrigin(new ivec2(0, 50));
        addGUI(searchbar);

        this.setSizeInPercent(true, true);
        reposition();
    }
    
    public void loadDecks()
    {
        pCanvas.destroyChildren();
        int y = 0;
        for(Deck deck : DeckController.getDecks())
        {
            DeckContainer container = new DeckContainer(deck);
            container.setPosition(new ivec2(5, y++ * 110));
            container.setSize(new ivec2(90, 100));
            container.setColor(new vec4(0.18f, 0.19f, 0.2f, 1.0f));
            container.setSizeInPercent(true, false);
            container.setPositionInPercent(true, false);
            container.setCornerRadius(new vec4(7.0f));

            pCanvas.addGUI(container);
        }
    }
    
    public void loadDecks(String searchterm)
    {
        pCanvas.destroyChildren();
        int y = 0;
        for(Deck deck : DeckController.getDecksBySearchterm(searchterm))
        {
            DeckContainer container = new DeckContainer(deck);
            container.setPosition(new ivec2(5, y++ * 110));
            container.setSize(new ivec2(90, 100));
            container.setColor(new vec4(0.18f, 0.19f, 0.2f, 1.0f));
            container.setSizeInPercent(true, false);
            container.setPositionInPercent(true, false);
            container.setCornerRadius(new vec4(7.0f));

            pCanvas.addGUI(container);
        }
    }

    private void exportCards()
    {
        CardExportPage.setToExport(null);
        PageManager.viewPage(PAGES.CARD_EXPORT);
    }
}
