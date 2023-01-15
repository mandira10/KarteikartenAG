package com.swp.Controller;

import com.swp.DataModel.Card;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.DataModel.Category;
import com.swp.DataModel.Tag;
import com.swp.Logic.CardLogic;
import com.swp.Logic.CategoryLogic;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.*;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@Slf4j
public class FilterSearchTest {


    @Test
    public void testFilterAll3() {

        Card card1  = new TextCard("Testfrage5","Testantwort5","Testtitel5",true);
        Card card2  = new TextCard("ja","nein","vielleicht",true);
        Card card3  = new TextCard("ho","ho","ho",true);

        List<Tag> tagsToAdd = new ArrayList<>() {
            {
                add(new Tag("tagTest1"));
                add(new Tag("tagTest2"));
            }
        };
        List<Category> categoriesToAdd = new ArrayList<>() {
            {
                add(new Category("categorietest1"));
                add(new Category("categorietest2"));
            }
        };

        assertTrue(CardLogic.updateCardData(card1,true));
        assertTrue(CardLogic.updateCardData(card2,true));
        assertTrue(CardLogic.updateCardData(card3,true));
        assertTrue(CardLogic.setTagsToCard(card1,tagsToAdd));
        assertTrue(CategoryLogic.setC2COrCH(card1,categoriesToAdd,false));
        assertTrue(CardLogic.setTagsToCard(card2,tagsToAdd));
        assertTrue(CategoryLogic.setC2COrCH(card3,categoriesToAdd,false));
        Card txCard = CardLogic.getCardByUUID(card1.getUuid());
        assertNotNull(txCard);
        List<Card> cardsToSearchTerms = CardLogic.getCardsBySearchterms("antwort5");
        assertEquals(1, cardsToSearchTerms.size());
        List<Card> cardsToCategory = CategoryLogic.getCardsInCategory("categorietest1");
        assertEquals(2, cardsToCategory.size());
        List<Card> cardsToTag = CardLogic.getCardsByTag("tagTest1");
        assertEquals(2, cardsToTag.size());
        assertTrue(cardsToTag.stream().anyMatch(c -> c.getTitle().equals("vielleicht")));
        assertTrue(cardsToTag.stream().anyMatch(c -> c.getTitle().equals("Testtitel5")));
        assertTrue(cardsToCategory.stream().anyMatch(c -> c.getTitle().equals("Testtitel5")));
        assertTrue(cardsToCategory.stream().anyMatch(c -> c.getTitle().equals("ho")));
        //cards = CardController.getCardsBySearchterms("antwrt"); //TODO: Wie hier testen? GUINotification kann nicht ausgespielt werden
        //assertTrue(cards.isEmpty());

    }
    //TODO: Testing the exceptions handling, more scenarios, GUI handling etc.



}
