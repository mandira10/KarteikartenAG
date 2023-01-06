package com.swp.Controller;

import com.swp.DataModel.Card;
import com.swp.Persistence.CardRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@Slf4j
public class FilterSearchTest {

    @Test
    public void testFilterAll3() {

        HashMap<String, Object> map1 = new HashMap<>() {
            {
                put("answer", "Testantwort5");
                put("question", "Testfrage5");
                put("title", "Testtitel5");
                put("visibility", true);
            }
        };
        HashMap<String, Object> map2 = new HashMap<>() {
            {
                put("answer", "nein");
                put("question", "ja");
                put("title", "vielleicht");
                put("visibility", true);
            }
        };
        HashMap<String, Object> map3 = new HashMap<>() {
            {
                put("answer", "ho");
                put("question", "ho");
                put("title", "ho");
                put("visibility", true);
            }
        };
        Set<String> tagsToAdd = new HashSet<>() {
            {
                add("tagTest1");
                add("tagTest2");
            }
        };
        Set<String> categoriesToAdd = new HashSet<>() {
            {
                add("categorietest1");
                add("categorietest2");
            }
        };

        assertTrue(CardController.updateCardData(null, "TEXT", map1, tagsToAdd, categoriesToAdd));
        assertTrue(CardController.updateCardData(null, "TEXT", map2, tagsToAdd, null));
        assertTrue(CardController.updateCardData(null, "TEXT", map3, null, categoriesToAdd));
        Optional<Card> optTxCard = CardRepository.findCardByTitle("Testtitel5");
        assertNotNull(optTxCard);
        Set<Card> cardsToSearchTerms = CardController.getCardsBySearchterms("antwort5");
        assertEquals(1, cardsToSearchTerms.size());
        Set<Card> cardsToSearchTerms1 = CardController.getCardsBySearchterms("ho");
        assertEquals(1, cardsToSearchTerms1.size());
        Set<Card> cardsToCategory = CategoryController.getCardsInCategory("categorietest1");
        assertEquals(2, cardsToCategory.size());
        Set<Card> cardsToTag = CardController.getCardsByTag("tagTest1");
        assertEquals(2, cardsToTag.size());
        assertTrue(cardsToTag.stream().filter(c -> c.getTitle().equals("vielleicht")).findAny().isPresent());
        assertTrue(cardsToTag.stream().filter(c -> c.getTitle().equals("Testtitel5")).findAny().isPresent());
        assertTrue(cardsToCategory.stream().filter(c -> c.getTitle().equals("Testtitel5")).findAny().isPresent());
        assertTrue(cardsToCategory.stream().filter(c -> c.getTitle().equals("ho")).findAny().isPresent());
        //cards = CardController.getCardsBySearchterms("antwrt"); //TODO: Wie hier testen? GUINotification kann nicht ausgespielt werden
        //assertTrue(cards.isEmpty());

    }
    //TODO: Testing the exceptions handling, more scenarios, GUI handling etc.



}
