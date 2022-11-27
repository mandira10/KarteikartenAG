package com.swp.GUI.Cards;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Switch;
import com.gumse.gui.Basics.TextBox;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.maths.ivec2;
import com.gumse.maths.vec4;
import com.swp.Controller.Controller;

public class CardOverviewPage extends RenderGUI {
    Controller controller;

    public void showCardsWithCategory()
    {
        String category  ="";
        controller.getCardsWithCategory(category);
    }

    public void showCardsWithTag()
    {
        String tag = "";
        controller.getCardsWithTag(tag);
    }

    public void showCardsWithSearchWords()
    {
        String searchWords="";
        controller.getCardsWithSearchWords(searchWords);
    }

    public void getCountOfDecksToCard()
    {
        String card = "";
        controller.getCountOfDecksFor(card);
    }

    public CardOverviewPage()
    {
        this.vSize = new ivec2(100,100);

        FontManager fonts = FontManager.getInstance();
        float cornerradius = 7.0f;

        int buttonWidth = 200;
        int textSize = 24;

        Button filterCardsButton = new Button(new ivec2(20, 10), new ivec2(buttonWidth, 60), "Filter Cards", fonts.getDefaultFont());
        //filterCardsButton.setSizeInPercent(true, false);
        filterCardsButton.setMinSize(vActualSize);
        filterCardsButton.setCornerRadius(new vec4(cornerradius));
        //filterCardsButton.setColor(new vec4(0.2f, 0.2f, 0.4f, 1.0f));
        filterCardsButton.getBox().setTextSize(textSize);
        addElement(filterCardsButton);

        Button addCardButton = new Button(new ivec2(buttonWidth + 30, 10), new ivec2(buttonWidth, 60), "Add Card", fonts.getDefaultFont());
        //addCardButton.setSizeInPercent(true, false);
        addCardButton.setCornerRadius(new vec4(cornerradius));
        addCardButton.getBox().setTextSize(textSize);
        addElement(addCardButton);

        Button editCardButton = new Button(new ivec2(2 * buttonWidth + 40, 10), new ivec2(buttonWidth, 60), "Edit Card", fonts.getDefaultFont());
        //addCardButton.setSizeInPercent(true, false);
        editCardButton.setCornerRadius(new vec4(cornerradius));
        editCardButton.getBox().setTextSize(textSize);
        addElement(editCardButton);

        Button deleteCardButton = new Button(new ivec2(3 * buttonWidth + 50, 10), new ivec2(buttonWidth, 60), "Delete Card", fonts.getDefaultFont());
        //addCardButton.setSizeInPercent(true, false);
        deleteCardButton.setCornerRadius(new vec4(cornerradius));
        deleteCardButton.getBox().setTextSize(textSize);
        addElement(deleteCardButton);


        ivec2 offset = new ivec2(20, 90);
        int entryHeight = 40;
        int gapSize = 5;

        for(int i = 0; i < 10; i++)
        {
            ivec2 pos = ivec2.add(offset, new ivec2(0, i * (entryHeight + gapSize)));
            Switch cardSwitch = new Switch(ivec2.add(pos, new ivec2(0, 10)), new ivec2(20, 20), 0.0f);
            addElement(cardSwitch);

            TextBox cardEntry = new TextBox("Some Card" + i, fonts.getDefaultFont(), ivec2.add(pos, new ivec2(40, 0)), new ivec2(760, entryHeight));
            cardEntry.setCornerRadius(new vec4(cornerradius));
            addElement(cardEntry);
        }

        this.setSizeInPercent(true, true);
        reposition();
        resize();
    }
}
