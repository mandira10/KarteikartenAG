package com.swp.Controller;


import com.swp.DataModel.Card;
import com.swp.DataModel.CardOverview;
import com.swp.DataModel.CardTypes.MultipleChoiceCard;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.DataModel.Tag;
import com.swp.Persistence.CardRepository;
import com.swp.Persistence.PersistenceManager;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;


import java.lang.reflect.InvocationTargetException;
import java.util.*;


import static org.junit.Assert.*;

@Slf4j
public class AddCardTest {

    private static final PersistenceManager pm = new PersistenceManager();

    /**
     * Testing the working of BeanUtils for different CardTypes
     * //TODO: incorporate image and audiofile as soon as incorporated
     */
    @Test
    public void testCardCreateAndUpdateNoTagsNoCategories() {
        //TEXT
        HashMap<String, Object> txmap = new HashMap<>(){
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
        final Card txcard = optTxCard.get();
        TextCard textCard = (TextCard) txcard;
        assertNotNull(textCard);
        assertEquals("Testtitel", textCard.getTitle());
        assertEquals("Testfrage", textCard.getQuestion());
        assertEquals("Testantwort", textCard.getAnswer());
        assertEquals(true, textCard.isVisibility());
        assertEquals(textCard.getTitle() + "\n" + textCard.getQuestion() + "\n" + textCard.getAnswer(), textCard.getContent());

        //MULTIPLE CHOICE

        String[] answers = {"Testantwort 1", "Testantwort2", "Testantwort 3"};
        int[] correctAnswers = {1, 3};
        HashMap<String, Object> mcmap = new HashMap<>(){
            {
                put("answers", answers);
                put("question", "Testfrage");
                put("title", "Testtitel1");
                put("visibility", false);
                put("correctAnswers", correctAnswers);
            }
        };

        assertTrue(CardController.updateCardData(null, "MULTIPLECHOICE", mcmap, null, null));
        Optional<Card> optMcCard = CardRepository.findCardByTitle("Testtitel1");
        assertNotNull(optMcCard);
        final Card card = optMcCard.get();
        MultipleChoiceCard mcCard = (MultipleChoiceCard) card;
        assertNotNull(mcCard);
        assertEquals("Testtitel1", mcCard.getTitle());
        assertEquals("Testfrage", mcCard.getQuestion());
        Assert.assertArrayEquals(answers, mcCard.getAnswers());
        Assert.assertArrayEquals(correctAnswers, mcCard.getCorrectAnswers());
        assertEquals(false, mcCard.isVisibility());
        assertEquals(mcCard.getTitle() + "\n" + mcCard.getQuestion(), mcCard.getContent());

        //Update CARD
        HashMap popMap = new HashMap<>() {
            {
                put("answers", null);
                put("correctAnswers", null);
                put("title", null);
                put("visibility", null);
            }
        };
        assertTrue(CardController.updateCardData(mcCard, null, popMap, null, null)); //TODO Check directly after entering on nulls
    }

    @Test
    public void testCardCreateAndUpdateWithTags() {
        HashMap<String, Object> txmap = new HashMap<>() {
            {
                put("answer", "Testantwort");
                put("question", "Testfrage");
                put("title", "Testtitel");
                put("visibility", false);
            }
        };
        Set<String> tagsToAdd = new HashSet<>() {
            {
                add("tag1");
                add("tag2");
            }
        };

        assertTrue(CardController.updateCardData(null, "TEXT", txmap, tagsToAdd, null));
        Set<Tag> tags = CardRepository.getTags();
         assertEquals(2,tags.size());
        Tag tag1 = tags.iterator().next();
        Tag tag2 = tags.iterator().next();
        Set<Card> CardsToTag1 = CardRepository.findCardsByTag(tag1);
        Set<Card> CardsToTag2 = CardRepository.findCardsByTag(tag2);
        assertEquals(1,CardsToTag1.size());
        assertEquals(1,CardsToTag2.size());
        Card card1 = CardsToTag1.iterator().next();
        Card card2 = CardsToTag2.iterator().next();
        assertEquals(card1.getUuid(),card2.getUuid());
    }

    @Test
    public void testCardOverview(){
        HashMap<String, Object> mapCard1= new HashMap<>(){{
            put("title", "TitelCard1");
            put("answer", "AntwortCard1");
        }};
        HashMap<String, Object> mapCard2= new HashMap<>(){{
            put("question", "FrageCard2");
            put("answer", "AntwortCard2");
        }};
        assertTrue(CardController.updateCardData(null, "MULTIPLECHOICE", mapCard1, null, null));
        assertTrue(CardController.updateCardData(null, "MULTIPLECHOICE", mapCard2, null, null));

        //TEST CARDOVERVIEW CONTAINS CARDS:
        EntityManager em = pm.getEntityManager();
        em.getTransaction().begin();
        List<CardOverview> co = em.createQuery("SELECT c FROM CardOverview c", CardOverview.class).getResultList();
        em.getTransaction().commit();
        em.close();
        assertNotNull(co);
        assertTrue(co.stream().filter(c -> c.getTitelToShow().equals("TitelCard1")).findAny().isPresent());
        assertTrue(co.stream().filter(c -> c.getTitelToShow().equals("FrageCard2")).findAny().isPresent());

    }
}






