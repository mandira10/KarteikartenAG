package com.swp.Logic;

import com.swp.DataModel.Card;
import com.swp.DataModel.Category;
import com.swp.DataModel.Tag;
import com.swp.Persistence.CardRepository;

import java.util.List;

public class CardLogic {

    CardRepository cardRepository;

    public List<Card> getCardsForCategory(String category) {
        Category category1 = new Category();
        return cardRepository.findCardsBy(category1);
    }

    public List<Card> getCardsForTag(String tag) {
        Tag tag1 = new Tag();
        return cardRepository.findCardsBy(tag1);

    }

    public List<Card> getCardsForSearchWords(String searchWords) {
        return cardRepository.findCardsWith(searchWords);
    }

    public void createTrueFalseCard(String question, boolean answer, boolean visibility) {
    cardRepository.saveTrueFalseCard(question,answer,visibility);
    }

    public Card getAllInfosForCard(String card) {
        return cardRepository.findCardByName(card);
    }


    public int getCountOfDecksFor(String card) {
        Card specificCard = cardRepository.findCardByName(card);
        return cardRepository.findNumberOfDecksToCard(specificCard);

    }
}
