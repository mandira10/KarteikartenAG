package com.swp.GUI.Cards;

import org.hibernate.engine.spi.CascadeStyles.MultipleCascadeStyle;

import com.gumse.gui.Basics.TextBox;
import com.gumse.gui.Basics.TextBox.Alignment;
import com.gumse.gui.Font.Font;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.maths.ivec2;
import com.gumse.tools.Debug;
import com.swp.DataModel.*;
import com.swp.DataModel.CardTypes.AudioCard;
import com.swp.DataModel.CardTypes.ImageDescriptionCard;
import com.swp.DataModel.CardTypes.ImageTestCard;
import com.swp.DataModel.CardTypes.MultipleChoiceCard;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.DataModel.CardTypes.TrueFalseCard;

public class TestCardGUI extends RenderGUI
{
    private Card pCard;

    public TestCardGUI(Card card)
    {
        this.pCard = card;
        this.vSize = new ivec2(100, 100);
        this.setSizeInPercent(true, true);

        switch(pCard.getIType())
        {
            case AUDIO:          createAudioCardTest();                        break;
            case IMAGEDESC:      createImageDescCardTest();                    break;
            case IMAGETEST:      createImageTestCardTest();                    break;
            case MULITPLECHOICE: createMultipleChoiceCardTest();               break;
            case TEXT:           createTextCardTest();                         break;
            case TRUEFALSE:      createTrueFalseCardTest();                    break;
            default:             Debug.error("TestCardGUI: Unknown Cardtype"); break;
        }

        resize();
        reposition();
    }

    private void createAudioCardTest()
    {
        Font defaultFont = FontManager.getInstance().getDefaultFont();
        
        AudioCard _card = (AudioCard)pCard;
        TextBox questionBox = new TextBox(_card.getSQuestion(), defaultFont, new ivec2(5, 10), new ivec2(90, 180));
        questionBox.setSizeInPercent(true, false);
        questionBox.setPositionInPercent(true, true);
        questionBox.setAutoInsertLinebreaks(true);
        questionBox.setTextSize(25);
        questionBox.getBox().hide(true);
        questionBox.setAlignment(Alignment.LEFT);
        addGUI(questionBox);
    }

    private void createImageTestCardTest()
    {
        Font defaultFont = FontManager.getInstance().getDefaultFont();
        
        ImageTestCard _card = (ImageTestCard)pCard;
        TextBox questionBox = new TextBox(_card.getSQuestion(), defaultFont, new ivec2(10, 10), new ivec2(5, 90));
        addGUI(questionBox);
    }

    private void createImageDescCardTest()
    {
        Font defaultFont = FontManager.getInstance().getDefaultFont();
        
        ImageDescriptionCard _card = (ImageDescriptionCard)pCard;
        TextBox questionBox = new TextBox(_card.getSQuestion(), defaultFont, new ivec2(10, 10), new ivec2(5, 90));
        addGUI(questionBox);
    }

    private void createTextCardTest()
    {
        Font defaultFont = FontManager.getInstance().getDefaultFont();
        
        TextCard _card = (TextCard)pCard;
        TextBox questionBox = new TextBox(_card.getSQuestion(), defaultFont, new ivec2(10, 10), new ivec2(5, 90));
        addGUI(questionBox);
    }

    private void createMultipleChoiceCardTest()
    {
        Font defaultFont = FontManager.getInstance().getDefaultFont();
        
        MultipleChoiceCard _card = (MultipleChoiceCard)pCard;
        TextBox questionBox = new TextBox(_card.getSQuestion(), defaultFont, new ivec2(10, 10), new ivec2(5, 90));
        addGUI(questionBox);
    }

    private void createTrueFalseCardTest()
    {
        Font defaultFont = FontManager.getInstance().getDefaultFont();
        
        TrueFalseCard _card = (TrueFalseCard)pCard;
        TextBox questionBox = new TextBox(_card.getSQuestion(), defaultFont, new ivec2(10, 10), new ivec2(5, 90));
        addGUI(questionBox);
    }

    public boolean checkAnswers()
    {

        return false;
    }
}
