package com.swp.GUI.Cards.CardRenderer;

import com.gumse.gui.Font.Font;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.Text;
import com.gumse.maths.ivec2;
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
    public static void render(Card card)
    {
        Font defaultFont = FontManager.getInstance().getDefaultFont();

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