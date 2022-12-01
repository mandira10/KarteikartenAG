package com.swp.Persistence;

import com.gumse.tools.Debug;
import com.swp.DataModel.Card;
import com.swp.DataModel.Category;
import com.swp.DataModel.Tag;
import com.swp.DataModel.CardTypes.TrueFalseCard;

import java.util.ArrayList;
import java.util.List;

public class CardRepository 
{
    public static List<Card> findCardsByCategory(Category category1) 
    {
        return new ArrayList<>();
    }

    public static List<Card> findCardsByTag(Tag tag1) 
    {
        return new ArrayList<>();
    }

    public static List<Card> findCardsWith(String searchWords) 
    {
        return new ArrayList<>();
    }

    /**
     * 
     * @param oldcard
     * @param newcard
     */
    public static void updateCard(Card oldcard, Card newcard)
    {
        String jsonString = "";
        if(!oldcard.sName.equals(newcard.sName))
            jsonString += "\"name\":" + newcard.sName;

        switch(newcard.getType())
        {
            case AUDIO:
                break;
            case IMAGEDESC:
                break;
            case IMAGETEST:
                break;
            case MULITPLECHOICE:
                break;
            case TEXT:
                break;
            case TRUEFALSE:
                break;
            default:
                Debug.error("Unknown cardtype!"); 
                break;
            
        }

        //server.send("/updatecarddata", jsonString);
    }

    public static void saveCard(Card card)
    {
        //server.send("/createcard", jsonString);
    }

    public static void deleteCard(Card card)
    {
        //server.send("/deletecard", jsonString);
    }

    public static List<Card> getCards(long from, long to) { /*TODO*/ return null; }

    public static Card findCardByName(String card) 
    {
        return null;
    }

    public static int findNumberOfDecksToCard(Card specificCard) 
    {
        return 0;
    }
}
