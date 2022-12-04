package com.swp.Persistence;

import com.gumse.tools.Debug;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardToTag;
import com.swp.DataModel.Category;
import com.swp.DataModel.Tag;
import com.swp.DataModel.CardTypes.TrueFalseCard;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

public class CardRepository
{
    public static Set<Card> getCards(long from, long to)
    {
        /*TODO*/ return null;
    }

    public static Set<Card> findCardsByCategory(Category category)
    {
        return null;
    }

    public static Set<Card> findCardsByTag(Tag tag)
    {
        return null;
    }

    public static Set<Card> findCardsContaining(String searchWords)
    {
        return null;
    }

    public static Card getCardByUUID(String uuid)
    {
        return null;
    }

    /**
     *
     * @param oldcard
     * @param newcard
     */
    public static boolean updateCard(Card oldcard, Card newcard)
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

        //return server.send("/updatecarddata", jsonString);
        return false;
    }

    public static boolean saveCard(Card card)
    {
        //return server.send("/createcard", jsonString);
        return false;
    }

    public static boolean deleteCard(Card card)
    {
        //return server.send("/deletecard", jsonString);
        return false;
    }




    //
    // Tags
    //
    public static boolean saveTag(String value)
    {
        //return server.send("/createtag", jsonString);
        return false;
    }

    public static Set<Tag> getTags()
    {
        Set<Tag> tags = Cache.getInstance().getTags();
        if(!tags.isEmpty())
            return tags;

        //server.send("/gettags", jsonString);
        return null;
    }

    public static Set<CardToTag> getCardToTags()
    {
        Set<CardToTag> tags = Cache.getInstance().getCardToTags();
        if(!tags.isEmpty())
            return tags;

        //server.send("/getcardtotags", jsonString);
        return null;
    }
}
