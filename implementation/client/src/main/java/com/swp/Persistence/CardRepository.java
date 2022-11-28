package com.swp.Persistence;

import com.swp.DataModel.Card;
import com.swp.DataModel.Category;
import com.swp.DataModel.Tag;
import com.swp.DataModel.CardTypes.TrueFalseCard;

import java.util.ArrayList;
import java.util.List;

public class CardRepository {

    public List<Card> findCardsByCategory(Category category1) 
    {
        return new ArrayList<>();
    }

    public List<Card> findCardsByTag(Tag tag1) 
    {
        return new ArrayList<>();
    }

    public List<Card> findCardsWith(String searchWords) 
    {
        return new ArrayList<>();
    }
    public void updateCard(Card card){}
    public void saveCard(Card card)                { /*TODO*/ }
    public List<Card> getCards(long from, long to) { /*TODO*/ return null; }

    public Card findCardByName(String card) 
    {
        return null;
    }

    public int findNumberOfDecksToCard(Card specificCard) 
    {
        return 0;
    }
}
