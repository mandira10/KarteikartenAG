package com.swp.GUI.Decks;

import com.gumse.gui.Basics.Scroller;
import com.gumse.gui.Font.Font;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.Box;
import com.gumse.gui.Primitives.Text;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.gumse.maths.vec4;
import com.gumse.system.Window;
import com.gumse.system.io.Mouse;
import com.swp.Controller.DeckController;
import com.swp.DataModel.Deck;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;
import com.swp.GUI.Cards.CardExportPage;
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

            Text deckName = new Text(deck.getSName(), defaultFont, new ivec2(10,10), 0);
            deckName.setCharacterHeight(35);  
            addElement(deckName);

            Text numcardsText = new Text("", defaultFont, new ivec2(10, 50), 0);
            numcardsText.setCharacterHeight(35);

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
            addElement(numcardsText);
            addElement(iconText);
        }

        @Override
        public void update() 
        {
            if(isMouseInside())
            {
                Mouse.setActiveHovering(true);
                Window.CurrentlyBoundWindow.getMouse().setCursor(Mouse.GUM_CURSOR_HAND);

                if(isClicked())
                {
                    ViewSingleDeckPage page = (ViewSingleDeckPage)PageManager.getPage(PAGES.DECK_SINGLEVIEW);
                    page.setDeck(pDeck);
                    PageManager.viewPage(PAGES.DECK_SINGLEVIEW);
                }
            }
        }
    };

    public DeckOverviewPage()
    {
        super("Decks");
        this.vSize = new ivec2(100,100);

        addGUI(XMLGUI.loadFile("guis/decks/deckoverviewpage.xml"));

        Scroller canvas = (Scroller)findChildByID("canvas");

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

            canvas.addGUI(container);
        }

        this.setSizeInPercent(true, true);
        reposition();
        resize();
    }
    

    private void exportCards()
    {
        CardExportPage.setToExport(null);
        PageManager.viewPage(PAGES.CARD_EXPORT);
    }
}
