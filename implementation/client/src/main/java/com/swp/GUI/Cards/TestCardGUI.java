package com.swp.GUI.Cards;

import org.hibernate.engine.spi.CascadeStyles.MultipleCascadeStyle;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Radiobutton;
import com.gumse.gui.Basics.Scroller;
import com.gumse.gui.Basics.TextBox;
import com.gumse.gui.Basics.TextField;
import com.gumse.gui.Basics.Button.ButtonCallback;
import com.gumse.gui.Basics.TextBox.Alignment;
import com.gumse.gui.Font.Font;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.Box;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.Primitives.Text;
import com.gumse.maths.ivec2;
import com.gumse.maths.vec4;
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
    private TextBox pQuestionBox;
    private Scroller pQuestionScroller;
    private TextBox pCorrectAnswerBox;

    public TestCardGUI(Card card)
    {
        this.pCard = card;
        this.vSize = new ivec2(100, 100);

        Font defaultFont = FontManager.getInstance().getDefaultFont();

        pQuestionScroller = new Scroller(new ivec2(5, 40), new ivec2(90, 100));
        pQuestionScroller.setSizeInPercent(true, false);
        pQuestionScroller.setPositionInPercent(true, false);
        addGUI(pQuestionScroller);

        pQuestionBox = new TextBox(card.getSQuestion(), defaultFont, new ivec2(0, 0), new ivec2(100, 100));
        pQuestionBox.setSizeInPercent(true, false);
        pQuestionBox.setAutoInsertLinebreaks(true);
        pQuestionBox.setTextSize(30);
        pQuestionBox.getBox().hide(true);
        pQuestionBox.setAlignment(Alignment.LEFT);
        pQuestionScroller.addGUI(pQuestionBox);

        pCorrectAnswerBox = new TextBox("", defaultFont, new ivec2(50, 100), new ivec2(50, 60));
        pCorrectAnswerBox.setPositionInPercent(true, true);
        pCorrectAnswerBox.setSizeInPercent(true, false);
        pCorrectAnswerBox.setOrigin(new ivec2(50, 0));
        pCorrectAnswerBox.setOriginInPercent(true, false);
        pCorrectAnswerBox.setTextSize(25);
        pCorrectAnswerBox.getBox().hide(true);
        pCorrectAnswerBox.setAutoInsertLinebreaks(true);
        addGUI(pCorrectAnswerBox);

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

        setSizeInPercent(true, true);
        reposition();
    }

    private void addAnswerTextField()
    {
        Font defaultFont = FontManager.getInstance().getDefaultFont();
        TextField answerField = new TextField("", defaultFont, new ivec2(5, 100), new ivec2(90, 50));
        answerField.setSizeInPercent(true, false);
        answerField.setPositionInPercent(true, true);
        answerField.setOrigin(new ivec2(0, 70));
        answerField.setHint("Answer");
        addGUI(answerField);
    }

    private void createAudioCardTest()
    {
        Font defaultFont = FontManager.getInstance().getDefaultFont();
        AudioCard card = (AudioCard)pCard;
    }

    private void createImageTestCardTest()
    {
        Font defaultFont = FontManager.getInstance().getDefaultFont();
        ImageTestCard card = (ImageTestCard)pCard;

        pQuestionScroller.setPosition(new ivec2(5, 5));
        pQuestionScroller.setSize(new ivec2(40, 60));
        pQuestionScroller.setSizeInPercent(true, true);

        Box cardImage = new Box(new ivec2(55, 5), new ivec2(40, 60));
        cardImage.setPositionInPercent(true, true);
        cardImage.setSizeInPercent(true, true);
        cardImage.setTexture(card.getOImage());
        cardImage.invertTexcoordY(true);
        addGUI(cardImage);

        addAnswerTextField();
    }

    private void createImageDescCardTest()
    {
        Font defaultFont = FontManager.getInstance().getDefaultFont();
        ImageDescriptionCard card = (ImageDescriptionCard)pCard;

        pQuestionScroller.setPosition(new ivec2(5, 5));
        pQuestionScroller.setSize(new ivec2(40, 60));
        pQuestionScroller.setSizeInPercent(true, true);

        Box cardImage = new Box(new ivec2(55, 5), new ivec2(40, 60));
        cardImage.setPositionInPercent(true, true);
        cardImage.setSizeInPercent(true, true);
        cardImage.setTexture(card.getOImage());
        cardImage.invertTexcoordY(true);
        addGUI(cardImage);

        addAnswerTextField();
    }

    private void createTextCardTest()
    {
        Font defaultFont = FontManager.getInstance().getDefaultFont();
        TextCard card = (TextCard)pCard;
        addAnswerTextField();
    }

    private void createMultipleChoiceCardTest()
    {
        Font defaultFont = FontManager.getInstance().getDefaultFont();
        MultipleChoiceCard card = (MultipleChoiceCard)pCard;

        Scroller answerScroller = new Scroller(new ivec2(5, 180), new ivec2(90, 100));
        answerScroller.setSizeInPercent(true, true);
        answerScroller.setPositionInPercent(true, false);
        answerScroller.setMargin(new ivec2(0, -200));
        addGUI(answerScroller);

        Radiobutton radiobutton = new Radiobutton(new ivec2(0, 0), 30, 100, defaultFont, card.getSaAnswers());
        radiobutton.setSizeInPercent(true, false);
        answerScroller.addGUI(radiobutton);
    }

    private void createTrueFalseCardTest()
    {
        Font defaultFont = FontManager.getInstance().getDefaultFont();
        TrueFalseCard card = (TrueFalseCard)pCard;

        Button trueButton = new Button(new ivec2(50, 100), new ivec2(100, 50), "True", defaultFont);
        trueButton.setPositionInPercent(true, true);
        trueButton.setOrigin(new ivec2(110, 70));
        addGUI(trueButton);

        Button falseButton = new Button(new ivec2(50, 100), new ivec2(100, 50), "False", defaultFont);
        falseButton.setPositionInPercent(true, true);
        falseButton.setOrigin(new ivec2(-10, 70));

        trueButton.setCallbackFunction(new ButtonCallback() {
            @Override public void run() {
                falseButton.setColor(new vec4(0.2f, 0.2f, 0.2f, 1.0f));
                trueButton.setColor(new vec4(0.12f, 0.12f, 0.12f, 1.0f));
            }
        });
        falseButton.setCallbackFunction(new ButtonCallback() {
            @Override public void run() {
                trueButton.setColor(new vec4(0.2f, 0.2f, 0.2f, 1.0f));
                falseButton.setColor(new vec4(0.12f, 0.12f, 0.12f, 1.0f));
            }
        });
        addGUI(falseButton);
    }

    public boolean checkAnswers()
    {
        boolean isAnswerCorrect = false;
        //
        // DO ANSWER CHECKING
        //
        isAnswerCorrect = true;


        if(isAnswerCorrect)
        {
            pCorrectAnswerBox.setTextColor(new vec4(0.18f, 0.8f, 0.44f, 1));
            pCorrectAnswerBox.setString("Correct");
        }
        else
        {
            pCorrectAnswerBox.setTextColor(new vec4(0.93f, 0.32f, 0.33f, 1));
            pCorrectAnswerBox.setString("Actual Answer here");
        }


        return isAnswerCorrect;
    }

    @Override
    public void updateOnSizeChange()
    {
        pQuestionBox.setSize(new ivec2(100, pQuestionBox.getText().getSize().y));
    }
}