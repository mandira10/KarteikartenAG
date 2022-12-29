package com.swp.GUI.Cards.CardRenderer;

import java.util.ArrayList;
import java.util.Arrays;

import com.gumse.gui.Font.Font;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.Box;
import com.gumse.gui.Primitives.Text;
import com.gumse.maths.ivec2;
import com.gumse.maths.vec4;
import com.gumse.tools.Debug;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardTypes.AudioCard;
import com.swp.DataModel.CardTypes.ImageDescriptionCard;
import com.swp.DataModel.CardTypes.ImageTestCard;
import com.swp.DataModel.CardTypes.MultipleChoiceCard;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.DataModel.CardTypes.TrueFalseCard;

public class CardTypesRenderer 
{
    public static void renderFront(Card card, ivec2 resolution)
    {
        Font defaultFont = FontManager.getInstance().getDefaultFont();

        Box background = new Box(new ivec2(0,0), resolution);
        background.setColor(new vec4(0.26f, 0.26f, 0.31f, 1.0f));
        background.render();

        new Text(card.getTitle(), defaultFont, new ivec2(10, 10), 0).render();


        switch(card.getType())
        {
            case AUDIO:          renderAudioCard((AudioCard) card);                       break;
            case IMAGEDESC:      renderImageDescriptionCard((ImageDescriptionCard) card); break;
            case IMAGETEST:      renderImageTestCard((ImageTestCard) card);               break;
            case MULITPLECHOICE: renderMultipleChoiceCard((MultipleChoiceCard) card);     break;
            case TEXT:           renderTextCard((TextCard) card);                         break;
            case TRUEFALSE:      renderTrueFalseCard((TrueFalseCard) card);               break;
            default:             Debug.error("CardTypesRenderer: Unknown card type");     break;
        }
    }

    public static void renderBack(Card card, ivec2 resolution)
    {
        Font defaultFont = FontManager.getInstance().getDefaultFont();

        Box background = new Box(new ivec2(0,0), resolution);
        background.setColor(new vec4(0.26f, 0.26f, 0.31f, 1.0f));
        background.render();

        ArrayList<String> strs = new ArrayList<>();

        switch(card.getType())
        {
            case AUDIO:          strs.add(((AudioCard) card).getAnswer());                             break;
            case IMAGEDESC:      strs.add(((ImageDescriptionCard) card).getAnswer());                  break;
            case IMAGETEST:      strs.add(((ImageTestCard) card).getAnswer());                         break;
            case MULITPLECHOICE: strs.addAll(Arrays.asList(((MultipleChoiceCard) card).getAnswers())); break;
            case TEXT:           strs.add(((TextCard) card).getAnswer());                              break;
            case TRUEFALSE:      strs.add(((TrueFalseCard) card).isBAnswer() ? "True" : "False");       break;
            default:             Debug.error("CardTypesRenderer: Unknown card type");                  break;
        }

        Text answertext = new Text("EEHH", defaultFont, new ivec2(0, 0), 0);
        answertext.setCharacterHeight(100);

        for(int i = 0; i < strs.size(); i++)
        {
            Debug.debug(strs.get(i));
            answertext.setString(strs.get(i));
            answertext.setPosition(new ivec2(20, i * 20));
            answertext.render();
        }
    }

    private static void renderTrueFalseCard(TrueFalseCard card)
    {
        
    }

    private static void renderTextCard(TextCard card)
    {
        
    }

    private static void renderMultipleChoiceCard(MultipleChoiceCard card)
    {
        
    }

    private static void renderImageTestCard(ImageTestCard card)
    {
        
    }

    private static void renderImageDescriptionCard(ImageDescriptionCard card)
    {
        
    }

    private static void renderAudioCard(AudioCard card)
    {
        
    }
}