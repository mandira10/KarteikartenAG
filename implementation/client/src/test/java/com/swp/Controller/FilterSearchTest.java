package com.swp.Controller;

import com.swp.DataModel.Card;
import com.swp.Persistence.CardRepository;
import lombok.extern.slf4j.Slf4j;
import org.junit.Test;

import java.util.HashMap;
import java.util.Optional;
import java.util.Set;

import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

@Slf4j
public class FilterSearchTest {

    @Test
    public void testFilterToSearchWords() {

        HashMap<String, Object> txmap = new HashMap<>() {
            {
                put("answer", "Testantwort");
                put("question", "Testfrage");
                put("title", "Testtitel");
                put("visibility", true);
            }
        };

        assertTrue(CardController.updateCardData(null, "TEXT", txmap, null, null));
        Optional<Card> optTxCard = CardRepository.findCardByTitle("Testtitel");
        assertNotNull(optTxCard);
        Set<Card> cards = CardController.getCardsBySearchterms("antwort");
        assertEquals(1, cards.size());
        //cards = CardController.getCardsBySearchterms("antwrt"); //TODO: Wie hier testen? GUINotification kann nicht ausgespielt werden
        //assertTrue(cards.isEmpty());

    }
}
