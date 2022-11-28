package com.swp.Logic;

import com.swp.DataModel.Card;
import com.swp.DataModel.Category;
import com.swp.DataModel.Deck;
import com.swp.DataModel.Tag;
import com.swp.DataModel.CardTypes.TrueFalseCard;
import com.swp.Persistence.CardRepository;

import java.util.List;

public class CardLogic
{
    CardRepository cardRepository;

    public List<Card> getCardsForCategory(String category)
	{
        Category category1 = new Category();
        return cardRepository.findCardsByCategory(category1);
    }

    public List<Card> getCardsForTag(String tag)
	{
        Tag tag1 = new Tag();
        return cardRepository.findCardsByTag(tag1);

    }

    public List<Card> getCardsForSearchWords(String searchWords)
	{
        return cardRepository.findCardsWith(searchWords);
    }

    public TrueFalseCard createTrueFalseCard(String question, boolean answer, boolean visibility) 
	{
        TrueFalseCard retCard = new TrueFalseCard();
        cardRepository.saveCard(retCard);
        
        return retCard;
    }

    public Card getAllInfosForCard(String card)
	{
        return cardRepository.findCardByName(card);
    }


    public int getCountOfDecksFor(String card)
	{
        Card specificCard = cardRepository.findCardByName(card);
        return cardRepository.findNumberOfDecksToCard(specificCard);
    }

    public void createCardToDeck(Card card, Deck deck) {
    }

    public void createCardToDeckForCategory(Category category, Deck deck) {
    }

    public void createCardToCategory(Card card, Category category) {
    }
}
