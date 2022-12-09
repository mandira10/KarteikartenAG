package com.swp.GUI.Cards;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Dropdown;
import com.gumse.gui.Basics.Button.ButtonCallback;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.gumse.tools.Debug;
import com.swp.Controller.CardController;
import com.swp.DataModel.Card;
import com.swp.GUI.Page;
import com.swp.GUI.PageManager;
import com.swp.GUI.Cards.EditCardPages.EditMultipleChoiceCardPage;
import com.swp.GUI.Cards.EditCardPages.EditTextCardPage;
import com.swp.GUI.Cards.EditCardPages.EditTrueFalseCardPage;
import com.swp.GUI.Extras.ListTextField;

public class EditCardPage extends Page
{
    private EditTextCardPage pEditTextCardPage;
    private EditTrueFalseCardPage pEditTrueFalseCardPage;
    private EditMultipleChoiceCardPage pEditMultipleChoiceCardPage;

    private Dropdown pDropdown;
    private ListTextField pListTextField;
    private Card pOldCard, pNewCard;
    private RenderGUI pCanvas;

    public EditCardPage()
    {
        super("Edit Card");
        this.vSize = new ivec2(100,100);
        pOldCard = null;
        pNewCard = null;
        pListTextField = new ListTextField();
        //oDropdown = new Dropdown(null, null, null, null, 0)


        addGUI(XMLGUI.loadFile("guis/cardeditpage.xml"));

        pCanvas = findChildByID("canvas");
        
        pEditTextCardPage = new EditTextCardPage();
        pCanvas.addGUI(pEditTextCardPage);

        pEditTrueFalseCardPage = new EditTrueFalseCardPage();
        pCanvas.addGUI(pEditTrueFalseCardPage);

        pEditMultipleChoiceCardPage = new EditMultipleChoiceCardPage();
        pCanvas.addGUI(pEditMultipleChoiceCardPage);

        Button applyButton = (Button)findChildByID("applybutton");
        if(applyButton != null)
        {
            applyButton.setCallbackFunction(new ButtonCallback() {
                @Override public void run()
                {
                    Debug.info("Apply Button");
                }
            });
        }
        Button cancelButton = (Button)findChildByID("cancelbutton");
        if(cancelButton != null)
        {
            cancelButton.setCallbackFunction(new ButtonCallback() {
                @Override public void run()
                {
                    PageManager.viewLastPage();
                }
            });
        }

        this.setSizeInPercent(true, true);
        reposition();
        resize();
    }

    public void editCard(String uuid) { editCard(CardController.getCardByUUID(uuid)); }
    public void editCard(Card card)
    {
        if(card == null)
            return;
        
        pOldCard = card;
        pNewCard = Card.copyCard(card);

        switch(pNewCard.getType())
        {
            case TRUEFALSE:      setPage(pEditTrueFalseCardPage); break;
            case IMAGETEST:      setPage(pEditTextCardPage); break;
            case IMAGEDESC:      setPage(pEditTextCardPage); break;
            case MULITPLECHOICE: setPage(pEditMultipleChoiceCardPage); break;
            case TEXT:           setPage(pEditTextCardPage); break;
            case AUDIO:          setPage(pEditTextCardPage); break;
        }
    }

    private void setPage(RenderGUI page)
    {
        for(RenderGUI child : pCanvas.getChildren())
            child.hide(true);
        page.hide(false);
    }

    private void deleteCard()
    {
        CardController.deleteCard(pOldCard);
    }

    private void applyChanges()
    {
        CardController.updateCardData(pOldCard, pNewCard);
    }
}