package com.swp.GUI.Cards.CardRenderer;

import com.gumse.gui.GUI;
import com.gumse.gui.Basics.TextBox;
import com.gumse.gui.Basics.TextBox.Alignment;
import com.gumse.gui.Font.Font;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.Box;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.Primitives.Text;
import com.gumse.maths.ivec2;
import com.gumse.maths.vec4;
import com.gumse.textures.Texture;
import com.swp.DataModel.Card;
import com.swp.DataModel.Card.CardType;
import com.swp.DataModel.CardTypes.AudioCard;
import com.swp.DataModel.CardTypes.ImageDescriptionCard;
import com.swp.DataModel.CardTypes.ImageDescriptionCardAnswer;
import com.swp.DataModel.CardTypes.ImageTestCard;
import com.swp.DataModel.CardTypes.MultipleChoiceCard;

/**
 * Rendert eine Karte zu zwei OpenGL Framebuffer,
 * einer Für die Vorderseite (Frage)
 * einer Für die Rückseite (Antwort)
 */
public class CardTypesRenderer 
{
    /**
     * Rendert die Vorderseite einer Karte
     *
     * @param card       Die zu rendernde Karte
     * @param resolution Die Auflösung in Pixeln
     */
    public static void renderFront(Card card, ivec2 resolution)
    {
        Font defaultFont = FontManager.getInstance().getDefaultFont();

        Box background = new Box(new ivec2(0,0), resolution);
        background.setColor(GUI.getTheme().secondaryColor);

        Text titleText = new Text(card.getTitle(), defaultFont, new ivec2(5, 10), 0);
        titleText.setPositionInPercent(true, false);
        float factor = ((float)resolution.x * 0.8f) / (float)titleText.getSize().x;
        titleText.setCharacterHeight(Math.min(titleText.getSize().y * factor, 80));
        background.addGUI(titleText);

        TextBox questionTextbox = new TextBox(card.getQuestion(), defaultFont, new ivec2(5, 10), new ivec2(85, 30));
        questionTextbox.setSizeInPercent(true, true);
        questionTextbox.setPositionInPercent(true, true);
        questionTextbox.setTextSize(50);
        questionTextbox.setAlignment(Alignment.LEFT);
        questionTextbox.getBox().hide(true);
        questionTextbox.setAutoInsertLinebreaks(true);
        background.addGUI(questionTextbox);

        RenderGUI canvas = new RenderGUI();
        canvas.setPosition(new ivec2(5, 45));
        canvas.setSize(new ivec2(90, 50));
        canvas.setSizeInPercent(true, true);
        canvas.setPositionInPercent(true, true);
        background.addGUI(canvas);

        background.resize();
        background.reposition();

        switch(card.getType())
        {
            case AUDIO:          renderAudioCard(canvas, (AudioCard)card);                       break;
            case IMAGEDESC:      renderImageDescriptionCard(canvas, (ImageDescriptionCard)card); break;
            case IMAGETEST:      renderImageTestCard(canvas, (ImageTestCard)card);               break;
            case MULITPLECHOICE: renderMultipleChoiceCard(canvas, (MultipleChoiceCard)card);     break;
            default:             break;
        }

        background.render();
    }

    /**
     * Rendert die Rückseite einer Karte
     *
     * @param card       Die zu rendernde Karte
     * @param resolution Die Auflösung in Pixeln
     */
    public static void renderBack(Card card, ivec2 resolution)
    {
        Font defaultFont = FontManager.getInstance().getDefaultFont();

        Box background = new Box(new ivec2(0,0), resolution);
        background.setColor(GUI.getTheme().secondaryColor);

        Text titleText = new Text("Answer", defaultFont, new ivec2(5, 10), 0);
        titleText.setPositionInPercent(true, false);
        float factor = ((float)resolution.x * 0.8f) / (float)titleText.getSize().x;
        titleText.setCharacterHeight(Math.min(titleText.getSize().y * factor, 80));
        background.addGUI(titleText);


        TextBox answerTextbox = new TextBox(card.getAnswerString(), defaultFont, new ivec2(10, 2), new ivec2(80, 80));
        answerTextbox.setSizeInPercent(true, true);
        answerTextbox.setPositionInPercent(true, true);
        answerTextbox.setTextSize(50);
        answerTextbox.setAlignment(Alignment.LEFT);
        answerTextbox.getBox().hide(true);
        answerTextbox.setAutoInsertLinebreaks(card.getType() != CardType.MULITPLECHOICE);
        background.addGUI(answerTextbox);

        background.resize();
        background.reposition();
        background.render();
    }

    private static void renderMultipleChoiceCard(RenderGUI canvas, MultipleChoiceCard card)
    {
        int maxheight = 0;
        int i = 1;
        for(String answer : card.getAnswers())
        {
            TextBox indexBox = new TextBox(
                String.valueOf(i++) + " " + answer, 
                FontManager.getInstance().getDefaultFont(), 
                new ivec2(0, 0), 
                new ivec2(100, 50)
            );
            indexBox.setAlignment(Alignment.LEFT);
            indexBox.setSizeInPercent(true, false);
            indexBox.setAutoInsertLinebreaks(true);
            indexBox.resize();
            indexBox.setPosition(new ivec2(0, maxheight));
            indexBox.getBox().hide(true);
            maxheight += indexBox.getSize().y + 5;
            canvas.addGUI(indexBox);
        }
    }

    private static void renderImageTestCard(RenderGUI canvas, ImageTestCard card)
    {
        Box imageBox = new Box(new ivec2(0, 0), new ivec2(0, 100));
        imageBox.setSizeInPercent(false, true);

        Texture tex = new Texture();
        if(card.getImage() != null)
            tex.loadMemory(card.getImage());
        imageBox.setTexture(tex);
        imageBox.setColor(new vec4(1, 1, 1, 1));
        imageBox.invertTexcoordY(true);
        canvas.addGUI(imageBox);
        imageBox.resize();
        imageBox.setSize(new ivec2(imageBox.getSize().y, 100));
        imageBox.setPosition(new ivec2((canvas.getSize().x - imageBox.getSize().x) / 2, 0));
    }

    private static void renderImageDescriptionCard(RenderGUI canvas, ImageDescriptionCard card)
    {
        Box imageBox = new Box(new ivec2(0, 0), new ivec2(0, 100));
        imageBox.setSizeInPercent(false, true);

        Texture tex = new Texture();
        if(card.getImage() != null)
            tex.loadMemory(card.getImage());
        imageBox.setTexture(tex);
        imageBox.setColor(new vec4(1, 1, 1, 1));
        imageBox.invertTexcoordY(true);
        canvas.addGUI(imageBox);
        imageBox.resize();
        imageBox.setSize(new ivec2(imageBox.getSize().y, 100));
        imageBox.setPosition(new ivec2((canvas.getSize().x - imageBox.getSize().x) / 2, 0));

        int i = 1;
        for(ImageDescriptionCardAnswer answer : card.getAnswers())
        {
            TextBox indexBox = new TextBox(String.valueOf(i++), FontManager.getInstance().getDefaultFont(), new ivec2(answer.xpos, answer.ypos), new ivec2(50));
            indexBox.setPositionInPercent(true, true);
            imageBox.addGUI(indexBox);
        }
    }

    private static void renderAudioCard(RenderGUI canvas, AudioCard card)
    {
        
    }
}