package com.swp.GUI.Cards;

import com.gumse.gui.Basics.Switch;
import com.gumse.gui.Basics.TextBox;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.maths.ivec2;
import com.gumse.system.Window;
import com.gumse.system.io.Mouse;
import com.swp.DataModel.Card;
import com.swp.GUI.PageManager;
import com.swp.GUI.PageManager.PAGES;

public class CardOverviewEntry extends RenderGUI
{
    private Card pCard;
    private TextBox pCardNameBox;
    private Switch pSelectSwitch;

    public CardOverviewEntry(Card card, ivec2 pos, ivec2 size)
    {
        this.vSize = size;
        this.vPos = pos;
        this.pCard = card;
        FontManager fonts = FontManager.getInstance();

        this.pCardNameBox = new TextBox(card.getTitle(), fonts.getDefaultFont(), new ivec2(0, 0), new ivec2(100, 100));
        pCardNameBox.setSizeInPercent(true, true);
        pCardNameBox.setAlignment(TextBox.Alignment.LEFT);
        pCardNameBox.setTextSize(20);
        pCardNameBox.setTextOffset(new ivec2(-10, 0));
        //pCardNameBox.setCornerRadius(new vec4(cornerradius));
        addElement(pCardNameBox);

        pSelectSwitch = new Switch(new ivec2(100, 10), new ivec2(20, 20), 0.0f);
        pSelectSwitch.setPositionInPercent(true, false);
        pSelectSwitch.setOrigin(new ivec2(30, 0));
        addElement(pSelectSwitch);

        reposition();
        resize();
    }

    @Override
    public void update()
    {
        if(bIsHidden)
            return;

        if(isMouseInside())
        {
            Mouse.setActiveHovering(true);
            Mouse mouse = Window.CurrentlyBoundWindow.getMouse();
            mouse.setCursor(Mouse.GUM_CURSOR_HAND);

            if(isClicked())
            {
                ViewSingleCardPage page = (ViewSingleCardPage)PageManager.getPage(PAGES.CARD_SINGLEVIEW);
                page.setCard(pCard);
                PageManager.viewPage(PAGES.CARD_SINGLEVIEW);
            }
        }

        updatechildren();
    }
};