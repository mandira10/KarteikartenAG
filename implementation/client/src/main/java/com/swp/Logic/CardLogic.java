package com.swp.Logic;

import com.swp.DataModel.Card;
import com.swp.DataModel.Tag;
import com.swp.Persistence.CardRepository;
import com.swp.Persistence.Exporter;
import com.swp.Persistence.Exporter.ExportFileType;

import java.util.List;
import java.util.Set;

public class CardLogic
{
    public static Set<Card> getCardsByTag(Tag tag)
	{
        return CardRepository.findCardsByTag(tag);

    }

    public static Card getCardByUUID(String uuid) 
    {
        if(uuid == null || uuid == "")
            return null;

        return CardRepository.getCardByUUID(uuid);
    }

    /**
     * 
     * @param terms Space separated string containing searchterms
     * @return
     */
    public static Set<Card> getCardsBySearchterms(String terms)
	{
        return CardRepository.findCardsContaining(terms);
    }

    public static Set<Card> getCardsToShow(long begin, long end){
        return CardRepository.getCards(begin, end);
    }


    public static boolean createCardToTag(Card card, Tag category) 
    {
        return false;
    }

    public static boolean createTag(String value)
    {
        return CardRepository.saveTag(value);
    }

    public static Set<Tag> getTags()
    {
        return CardRepository.getTags();
    }

    //private void changeCard() {}

    
    /**
     * Updates Database entry of given card
     * @param card
     */
    public static boolean updateCardData(Card oldcard, Card newcard)
    {
        if(newcard.getUUID().isEmpty())
            return CardRepository.saveCard(newcard);
        else
            return CardRepository.updateCard(oldcard, newcard);
    }

    public static boolean deleteCard(Card card)
    { 
        return CardRepository.deleteCard(card); 
    }

    public static boolean deleteCards(Card[] cards)
    {
        boolean ret = true;
        for(Card c : cards)
        {
            if(!deleteCard(c))
                ret = false;
        }
        return ret;
    }


    public static boolean exportCards(Card[] cards, ExportFileType filetype)
    {
        return (new Exporter(filetype)).export(cards);
    }
}
