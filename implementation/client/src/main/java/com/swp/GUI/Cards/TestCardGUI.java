package com.swp.GUI.Cards;

import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import com.gumse.gui.GUI;
import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Radiobutton;
import com.gumse.gui.Basics.RadiobuttonOption;
import com.gumse.gui.Basics.Scroller;
import com.gumse.gui.Basics.TextBox;
import com.gumse.gui.Basics.TextField;
import com.gumse.gui.Basics.TextBox.Alignment;
import com.gumse.gui.Font.Font;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.Box;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.maths.ivec2;
import com.gumse.maths.vec4;
import com.gumse.textures.Texture;
import com.gumse.tools.Output;
import com.swp.DataModel.*;
import com.swp.DataModel.CardTypes.AudioCard;
import com.swp.DataModel.CardTypes.ImageDescriptionCard;
import com.swp.DataModel.CardTypes.ImageDescriptionCardAnswer;
import com.swp.DataModel.CardTypes.ImageTestCard;
import com.swp.DataModel.CardTypes.MultipleChoiceCard;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.DataModel.CardTypes.TrueFalseCard;
import com.swp.GUI.Extras.AudioGUI;

public class TestCardGUI extends RenderGUI
{
    private Card pCard;
    private TextBox pQuestionBox;
    private Scroller pQuestionScroller;
    private TextBox pCorrectAnswerBox;
    private Predicate<Card> pAnswerCheckerFunc;
    private String sActualAnswer;
    private boolean bCurrentTrueFalseAnswer;
    private static final vec4 FALSE_COLOR = new vec4(0.93f, 0.32f, 0.33f, 1);
    private static final vec4 TRUE_COLOR  = new vec4(0.18f, 0.80f, 0.44f, 1);

    public TestCardGUI(Card card)
    {
        this.pCard = card;
        this.vSize = new ivec2(100, 100);
        this.sActualAnswer = "";

        Font defaultFont = FontManager.getInstance().getDefaultFont();

        pQuestionScroller = new Scroller(new ivec2(5, 40), new ivec2(90, 50));
        pQuestionScroller.setSizeInPercent(true, true);
        pQuestionScroller.setPositionInPercent(true, false);
        addGUI(pQuestionScroller);

        pQuestionBox = new TextBox(card.getQuestion(), defaultFont, new ivec2(0, 0), new ivec2(100, 100));
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

        switch(pCard.getType())
        {
            case AUDIO:          createAudioCardTest();                        break;
            case IMAGEDESC:      createImageDescCardTest();                    break;
            case IMAGETEST:      createImageTestCardTest();                    break;
            case MULITPLECHOICE: createMultipleChoiceCardTest();               break;
            case TEXT:           createTextCardTest();                         break;
            case TRUEFALSE:      createTrueFalseCardTest();                    break;
            default:             Output.error("TestCardGUI: Unknown Cardtype"); break;
        }

        setSizeInPercent(true, true);
        reposition();
    }

    private TextField addAnswerTextField()
    {
        Font defaultFont = FontManager.getInstance().getDefaultFont();
        TextField answerField = new TextField("", defaultFont, new ivec2(5, 100), new ivec2(90, 50));
        answerField.setSizeInPercent(true, false);
        answerField.setPositionInPercent(true, true);
        answerField.setOrigin(new ivec2(0, 70));
        answerField.setHint("Answer");
        addGUI(answerField);

        return answerField;
    }

    private void createAudioCardTest()
    {
        AudioCard card = (AudioCard)pCard;

        AudioGUI audioGUI = new AudioGUI(new ivec2(5, 55), new ivec2(80, 80));
        audioGUI.loadAudio(card.getAudio());
        audioGUI.setPositionInPercent(true, true);
        audioGUI.setSizeInPercent(false, false);
        audioGUI.setOrigin(new ivec2(0, -50));
        addGUI(audioGUI);

        sActualAnswer = card.getAnswer();
        TextField answerfield = addAnswerTextField();
        pAnswerCheckerFunc = (Card acard) -> {
            return answerfield.getString().equals(sActualAnswer);
        };
    }

    private void createImageTestCardTest()
    {
        ImageTestCard card = (ImageTestCard)pCard;

        pQuestionScroller.setPosition(new ivec2(5, 5));
        pQuestionScroller.setSize(new ivec2(40, 60));
        pQuestionScroller.setSizeInPercent(true, true);

        Box cardImage = new Box(new ivec2(55, 5), new ivec2(40, 60));
        cardImage.setPositionInPercent(true, true);
        cardImage.setSizeInPercent(true, true);
        Texture tex = new Texture();
        tex.loadMemory(ByteBuffer.wrap(card.getImage()));
        cardImage.setColor(new vec4(1, 1, 1, 1));
        cardImage.setTexture(tex);
        cardImage.invertTexcoordY(true);
        addGUI(cardImage);

        sActualAnswer = card.getAnswer();
        TextField answerfield = addAnswerTextField();
        pAnswerCheckerFunc = (Card acard) -> {
            return answerfield.getString().equals(sActualAnswer);
        };
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

        Texture tex = new Texture();
        tex.loadMemory(ByteBuffer.wrap(card.getImage()));
        cardImage.setColor(new vec4(1, 1, 1, 1));
        cardImage.setTexture(tex);
        cardImage.invertTexcoordY(true);
        addGUI(cardImage);

        Scroller scroller = new Scroller(new ivec2(5, 70), new ivec2(90, 35));
        scroller.setSizeInPercent(true, true);
        scroller.setPositionInPercent(true, true);

        int i = 1;
        int ypos = 0;
        List<TextField> answerFields = new ArrayList<>();
        for(ImageDescriptionCardAnswer answer : card.getAnswers())
        {
            TextBox imageIndexBox = new TextBox(String.valueOf(i), defaultFont, new ivec2(answer.xpos, answer.ypos), new ivec2(20));
            imageIndexBox.setPositionInPercent(true, true);
            cardImage.addGUI(imageIndexBox);

            TextBox answerIndexBox = new TextBox(String.valueOf(i++), defaultFont, new ivec2(0, ypos), new ivec2(30));
            scroller.addGUI(answerIndexBox);

            TextField answerField = new TextField("", defaultFont, new ivec2(40, ypos), new ivec2(100, 30));
            answerField.setSizeInPercent(true, false);
            answerField.setMargin(new ivec2(-40, 0));
            scroller.addGUI(answerField);
            answerFields.add(answerField);

            ypos += 40;
        }
        addGUI(scroller);
        scroller.resize();


        sActualAnswer = "Incorrect";
        pAnswerCheckerFunc = (Card acard) -> {
            boolean correctAnswer = true;
            for(int j = 0; j < answerFields.size(); j++)
            {
                ImageDescriptionCardAnswer acorrectAnswer = card.getAnswers()[j];
                TextField currentAnswerField = answerFields.get(j);
                
                if(currentAnswerField.equals(acorrectAnswer.answertext))
                {
                    currentAnswerField.getBox().setTextColor(TRUE_COLOR);
                    correctAnswer = false;
                }
                else
                {
                    currentAnswerField.getBox().setTextColor(FALSE_COLOR);
                }
                currentAnswerField.setString(acorrectAnswer.answertext);
            }

            return correctAnswer;
        };
    }

    private void createTextCardTest()
    {
        TextCard card = (TextCard)pCard;

        sActualAnswer = card.getAnswer();
        TextField answerfield = addAnswerTextField();
        pAnswerCheckerFunc = (Card acard) -> {
            return answerfield.getString().equals(sActualAnswer);
        };
    }

    private void createMultipleChoiceCardTest()
    {
        Font defaultFont = FontManager.getInstance().getDefaultFont();
        MultipleChoiceCard card = (MultipleChoiceCard)pCard;

        Scroller answerScroller = new Scroller(new ivec2(5, 55), new ivec2(90, 100));
        answerScroller.setSizeInPercent(true, true);
        answerScroller.setPositionInPercent(true, true);
        answerScroller.setMargin(new ivec2(0, -200));
        answerScroller.setOrigin(new ivec2(0, -50));
        addGUI(answerScroller);

        Radiobutton radiobutton = new Radiobutton(new ivec2(0, 0), 100, defaultFont, 30);
        List<RadiobuttonOption> options = new ArrayList<>();
        
        for(String answer : card.getAnswers())
            options.add(radiobutton.addOption(answer));
        radiobutton.setSizeInPercent(true, false);
        answerScroller.addGUI(radiobutton);

        List<Integer> correctAnswers = Arrays.stream(card.getCorrectAnswers()).boxed().toList();

        sActualAnswer = "Incorrect";
        pAnswerCheckerFunc = (Card acard) -> {
            boolean correctAnswer = true;
            for(int i = 0; i < options.size(); i++)
            {
                RadiobuttonOption option = options.get(i);
                if(correctAnswers.contains(i))
                {
                    option.getTextBox().setTextColor(TRUE_COLOR);
                    if(!option.isSelected())
                        correctAnswer = false;
                }
                else
                {
                    option.getTextBox().setTextColor(FALSE_COLOR);
                }
            }

            return correctAnswer;
        };
    }

    private void createTrueFalseCardTest()
    {
        TrueFalseCard card = (TrueFalseCard)pCard;
        Font defaultFont = FontManager.getInstance().getDefaultFont();

        Button trueButton = new Button(new ivec2(50, 100), new ivec2(100, 50), "True", defaultFont);
        trueButton.setPositionInPercent(true, true);
        trueButton.setOrigin(new ivec2(110, 70));
        addGUI(trueButton);

        bCurrentTrueFalseAnswer = false;

        Button falseButton = new Button(new ivec2(50, 100), new ivec2(100, 50), "False", defaultFont);
        falseButton.setPositionInPercent(true, true);
        falseButton.setOrigin(new ivec2(-10, 70));

        trueButton.onClick((RenderGUI gui) -> {
            falseButton.setColor(new vec4(0.2f, 0.2f, 0.2f, 1.0f));
            trueButton.setColor(GUI.getTheme().accentColor);
            bCurrentTrueFalseAnswer = true;
        });
        falseButton.onClick((RenderGUI gui) -> {
            trueButton.setColor(new vec4(0.2f, 0.2f, 0.2f, 1.0f));
            falseButton.setColor(GUI.getTheme().accentColor);
            bCurrentTrueFalseAnswer = false;
        });
        addGUI(falseButton);

        sActualAnswer = card.isAnswer() ? "True" : "False";
        pAnswerCheckerFunc = (Card acard) -> {
            return bCurrentTrueFalseAnswer == card.isAnswer();
        };
    }

    public boolean checkAnswers()
    {
        boolean isAnswerCorrect = pAnswerCheckerFunc != null && pAnswerCheckerFunc.test(pCard);

        if(isAnswerCorrect)
        {
            pCorrectAnswerBox.setTextColor(TRUE_COLOR);
            pCorrectAnswerBox.setString("Correct");
        }
        else
        {
            pCorrectAnswerBox.setTextColor(FALSE_COLOR);
            pCorrectAnswerBox.setString(sActualAnswer);
        }


        return isAnswerCorrect;
    }

    @Override
    public void updateOnSizeChange()
    {
        pQuestionBox.setSize(new ivec2(100, pQuestionBox.getText().getSize().y));
        pQuestionScroller.updateContent();
    }
}