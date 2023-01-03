package com.swp.GUI.Extras;

import com.gumse.gui.Basics.Switch;
import com.gumse.gui.Basics.TextBox;
import com.gumse.gui.Basics.Switch.OnSwitchTicked;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.maths.ivec2;
import com.gumse.system.Window;
import com.gumse.system.io.Mouse;
import com.gumse.tools.Debug;
import com.swp.DataModel.Card;
import com.swp.GUI.PageManager;
import com.swp.GUI.Cards.ViewSingleCardPage;
import com.swp.GUI.PageManager.PAGES;

public class CardListEntry extends RenderGUI
{
    private Card pCard;
    private TextBox pCardNameBox;
    private Switch pSelectSwitch;
    private CardList pParentList;

    public CardListEntry(Card card, ivec2 pos, ivec2 size, CardList list)
    {
        this.vSize = size;
        this.vPos = pos;
        this.pCard = card;
        this.pParentList = list;
        FontManager fonts = FontManager.getInstance();

        this.pCardNameBox = new TextBox(card.getSTitle(), fonts.getDefaultFont(), new ivec2(0, 0), new ivec2(100, 100));
        pCardNameBox.setSizeInPercent(true, true);
        pCardNameBox.setAlignment(TextBox.Alignment.LEFT);
        pCardNameBox.setTextSize(20);
        pCardNameBox.setTextOffset(new ivec2(-10, 0));
        //pCardNameBox.setCornerRadius(new vec4(cornerradius));
        addElement(pCardNameBox);

        pSelectSwitch = new Switch(new ivec2(100, 10), new ivec2(20, 20), 0.0f);
        pSelectSwitch.setPositionInPercent(true, false);
        pSelectSwitch.setOrigin(new ivec2(30, 0));
        pSelectSwitch.onTick(new OnSwitchTicked() {
            @Override public void run(boolean ticked) {
                pParentList.updateSelectmode();
            }
        });
        addElement(pSelectSwitch);

        reposition();
        resize();
    }

    @Override
    public void update()
    {
        if(bIsHidden)
            return;

        updatechildren();

        //Debug.info(pParentList.isInSelectmode());

        if(pSelectSwitch.isMouseInside())
        {
            if(pSelectSwitch.isTicked())
            {
                pParentList.updateSelectmode();
            }
        }
        else if(isMouseInside())
        {
            Mouse.setActiveHovering(true);
            Mouse mouse = Window.CurrentlyBoundWindow.getMouse();
            mouse.setCursor(Mouse.GUM_CURSOR_HAND);

            if(isClicked())
            {
                if(pParentList.isInSelectmode())
                {
                    pSelectSwitch.tick(!pSelectSwitch.isTicked());
                    pParentList.updateSelectmode();
                }
                else
                {
                    ViewSingleCardPage page = (ViewSingleCardPage)PageManager.getPage(PAGES.CARD_SINGLEVIEW);
                    page.setCard(pCard);
                    PageManager.viewPage(PAGES.CARD_SINGLEVIEW);
                }
            }
        }
    }

    public boolean isSelected()
    {
        return pSelectSwitch.isTicked();
    }
};