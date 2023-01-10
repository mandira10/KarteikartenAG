package com.swp.GUI.Extras;

import java.util.List;

import com.gumse.gui.Basics.Scroller;
import com.gumse.gui.Font.Font;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.Box;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.Primitives.Text;
import com.gumse.maths.ivec2;
import com.gumse.maths.vec4;
import com.gumse.system.io.Mouse;
import com.swp.Controller.DeckController;
import com.swp.DataModel.Deck;

public class DeckList extends RenderGUI
{
    public interface DeckListCallback
    {
        void run(Deck deck);
    }

    private class DeckContainer extends Box
    {
        public DeckContainer(Deck deck)
        {
            super(new ivec2(), new ivec2());

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
                    if(pCallback != null)
                        pCallback.run(deck);
                }
            });
        }
    };

    private Scroller pScroller;
    private DeckListCallback pCallback;

    public DeckList(ivec2 pos, ivec2 size, DeckListCallback onclick)
    {
        this.vPos.set(pos);
        this.vSize.set(size);
        this.pCallback = onclick;

        pScroller = new Scroller(new ivec2(0, 0), new ivec2(100, 100));
        pScroller.setSizeInPercent(true, true);
        addElement(pScroller);

        resize();
        reposition();
    }

    
    public void reset()
    {
        pScroller.destroyChildren();
    }

    public void addDecks(List<Deck> decks)
    {
        int y = 0;
        for(Deck deck : decks)
        {
            DeckContainer container = new DeckContainer(deck);
            container.setPosition(new ivec2(5, y++ * 110));
            container.setSize(new ivec2(90, 100));
            container.setColor(new vec4(0.18f, 0.19f, 0.2f, 1.0f));
            container.setSizeInPercent(true, false);
            container.setPositionInPercent(true, false);
            container.setCornerRadius(new vec4(7.0f));

            pScroller.addGUI(container);
        }
    }
}
