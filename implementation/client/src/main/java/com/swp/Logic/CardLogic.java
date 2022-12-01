package com.swp.Logic;

import com.swp.DataModel.Card;
import com.swp.DataModel.Category;
import com.swp.DataModel.Deck;
import com.swp.DataModel.Tag;
import com.swp.Persistence.CardRepository;
import com.swp.Persistence.Exporter;
import com.swp.Persistence.Exporter.ExportFileType;

import java.util.List;

public class CardLogic
{
    public static List<Card> getCardsByCategory(Category category)
	{
        return CardRepository.findCardsByCategory(category);
    }

    public static List<Card> getCardsByTag(Tag tag)
	{
        return CardRepository.findCardsByTag(tag);

    }

    /**
     * 
     * @param terms Space separated string containing searchterms
     * @return
     */
    public static List<Card> getCardsBySearchterms(String terms)
	{
        return CardRepository.findCardsWith(terms);
    }

    public static Card getAllInfosForCard(String card)
	{
        return CardRepository.findCardByName(card);
    }

    public static int getCountOfDecksFor(String card)
	{
        Card specificCard = CardRepository.findCardByName(card);
        return CardRepository.findNumberOfDecksToCard(specificCard);
    }

    public static List<Card> getCardsToShow(long begin, long end){
        return CardRepository.getCards(begin, end);
    }

    public static void createCardToDeck(Card card, Deck deck) {
    }

    public static void createCardToDeckForCategory(Category category, Deck deck) {
    }

    public static void createCardToCategory(Card card, Category category) {
    }

    public static void createCardToTag(Card card, Tag category) 
    {
    }

    public static void createTag(String value)
    {

    }

    //private void changeCard() {}

    /**
     * Updates Database entry of given card
     * @param card
     */
    public static void updateCardData(Card oldcard, Card newcard)
    {
        if(newcard.getUUID().isEmpty())
            CardRepository.saveCard(newcard);
        else
            CardRepository.updateCard(oldcard, newcard);
    }

    public static void deleteCard(Card card)
    { 
        CardRepository.deleteCard(card); 
    }

    public static void deleteCards(Card[] cards)
    {
        for(Card c : cards)
            deleteCard(c);
    }


    public static void exportCards(Card[] cards, ExportFileType filetype)
    {
        new Exporter(cards, filetype);
    }
}
