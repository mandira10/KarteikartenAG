package com.swp.Persistence;

import com.swp.DataModel.Card;
import com.swp.DataModel.Category;
import com.swp.DataModel.Tag;

import java.util.ArrayList;
import java.util.List;

public class CardRepository {

    public List<Card> findCardsBy(Category category1) {
    return new ArrayList<>();
    }

    public List<Card> findCardsBy(Tag tag1) {
        return new ArrayList<>();
    }

    public List<Card> findCardsWith(String searchWords) {
        return new ArrayList<>();
    }

    public void saveTrueFalseCard(String question, boolean answer, boolean visibility) {
    }

    public Card findCardByName(String card) {
        return null;
    }

    public int findNumberOfDecksToCard(Card specificCard) {
        return 0;
    }
}
