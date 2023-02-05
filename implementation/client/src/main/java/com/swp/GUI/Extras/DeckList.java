package com.swp.GUI.Extras;

import java.util.List;

import com.gumse.gui.GUI;
import com.gumse.gui.Locale;
import com.gumse.gui.Basics.Scroller;
import com.gumse.gui.Font.Font;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.Box;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.Primitives.Text;
import com.gumse.maths.ivec2;
import com.gumse.maths.vec4;
import com.gumse.system.io.Mouse;
import com.swp.Controller.SingleDataCallback;
import com.swp.Controller.StudySystemController;
import com.swp.DataModel.StudySystem.StudySystem;

/**
 * DeckList wird dazu verwendet um 
 * eine gegebene liste von Decks anzuzeigen.
 * 
 * @author Tom Beuke
 */
public class DeckList extends RenderGUI
{
    /**
     * Gibt das ausgewählte Deck wieder
     */
    public interface DeckListCallback
    {
        /**
         * Wird ausgeführt, wenn auf ein Deck gedrückt wird
         * @param deck Das ausgewählte Deck
         */
        void run(StudySystem deck);
    }

    private class DeckContainer extends Box
    {
        public DeckContainer(StudySystem deck)
        {
            super(new ivec2(), new ivec2());
            setColor(GUI.getTheme().primaryColor);
            setBorderThickness(GUI.getTheme().borderThickness);
            setCornerRadius(GUI.getTheme().cornerRadius);

            Font defaultFont = FontManager.getInstance().getDefaultFont();
            Font fontAwesome = FontManager.getInstance().getFont("FontAwesome");
            
            Text deckName = new Text(deck.getName() + " - " + deck.getType().toString(), defaultFont, new ivec2(10,10), 0);
            deckName.setCharacterHeight(35);
            addElement(deckName);

            Text numcardsText = new Text("", defaultFont, new ivec2(10, 50), 0);
            numcardsText.setCharacterHeight(35);

            int percentage = (int) (deck.getProgress());
            Text progressText = new Text(Locale.getCurrentLocale().getString("deckprogress") + ": " + percentage + "%", defaultFont, new ivec2(100, 10), 0);
            progressText.setPositionInPercent(true, false);
            progressText.setCharacterHeight(35);
            progressText.setOrigin(new ivec2(200, 0));

            Text iconText = new Text("", fontAwesome, new ivec2(100, 100), 0);
            iconText.setPositionInPercent(true, true);
            iconText.setOrigin(new ivec2(45, 40));
            iconText.setCharacterHeight(30);
            iconText.setColor(new vec4(0.13f, 0.13f, 0.14f, 1));

            final int[] numCards = {0};
                    StudySystemController.getInstance().numCardsInDeck(deck, new SingleDataCallback<>() {
                        @Override public void onSuccess(Integer data) {
                            numCards[0] = data;

                            if (data > 0) {
                                numcardsText.setString(numCards[0] + " " + Locale.getCurrentLocale().getString("deckentrycards"));
                                iconText.setString("");
                            } else {
                                numcardsText.setString(Locale.getCurrentLocale().getString("deckempty"));
                                iconText.setString("");
                                iconText.setOrigin(new ivec2(50, 40));
                            }
                            addElement(progressText);
                            addElement(numcardsText);
                            addElement(iconText);
                        }

                        @Override public void onFailure(String msg) {
                            //do nothing
                        }
                    });


            onHover(null, Mouse.GUM_CURSOR_HAND);

            onClick((RenderGUI gui) -> {
                if(pCallback != null)
                    pCallback.run(deck);
            });
        }

        @Override
        protected void updateOnThemeChange() 
        {
            setColor(GUI.getTheme().primaryColor);
            setBorderThickness(GUI.getTheme().borderThickness);
            setCornerRadius(GUI.getTheme().cornerRadius);
        }
    }

    private final Scroller pScroller;
    private final DeckListCallback pCallback;

    /**
     * Der Hauptkonstruktor der Klasse DeckList
     *
     * @param pos     Position des GUIs in Pixeln
     * @param size    Größe des GUIs in Pixeln
     * @param onclick Wird ausgeführt, wenn ein Deck ausgewählt wird
     */
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


    /**
     * Setzt die Liste zurück
     */
    public void reset()
    {
        pScroller.destroyChildren();
    }

    /**
     * Fügt eine Liste an decks hinzu
     *
     * @param decks Die hinzuzufügenden Decks
     */
    public void addDecks(List<StudySystem> decks)
    {
        int y = 0;
        for(StudySystem deck : decks)
        {
            DeckContainer container = new DeckContainer(deck);
            container.setPosition(new ivec2(5, y++ * 110));
            container.setSize(new ivec2(90, 100));
            container.setSizeInPercent(true, false);
            container.setPositionInPercent(true, false);

            pScroller.addGUI(container);
        }
    }
}
