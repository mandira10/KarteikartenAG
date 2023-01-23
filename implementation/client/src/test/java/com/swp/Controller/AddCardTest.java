package com.swp.Controller;


import com.swp.DataModel.Card;
import com.swp.DataModel.CardOverview;
import com.swp.DataModel.CardTypes.MultipleChoiceCard;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.Logic.CardLogic;
import com.swp.Persistence.CardRepository;
import com.swp.Persistence.CategoryRepository;
import com.swp.Persistence.PersistenceManager;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.*;

import java.util.HashMap;
import java.util.List;

import static com.swp.DataModel.Card.copyCard;
import static com.swp.TestData.importTestData;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class AddCardTest {

    private static final PersistenceManager pm = new PersistenceManager();

    @BeforeEach
    public void setup(){
            var cardRepository = CardRepository.getInstance();
            CardRepository.startTransaction();
            cardRepository.delete(cardRepository.getAll());
            CardRepository.commitTransaction();

            var categoryRepository = CategoryRepository.getInstance();
            CategoryRepository.startTransaction();
            categoryRepository.delete(categoryRepository.getAll());
            CategoryRepository.commitTransaction();
    }

    private CardLogic cardLogic = CardLogic.getInstance();

    /**
     * Testing the working of BeanUtils for different CardTypes
     * //TODO: incorporate image and audiofile as soon as incorporated
     */
    @Test
    public void testCardCreateAndUpdateNoTagsNoCategories()  {
        //TEXT
        Card card1 = new TextCard("Testfrage","Testantwort","Testtitel",true);
        cardLogic.updateCardData(card1,true);
        Card txcard = cardLogic.getCardByUUID(card1.getUuid());
        TextCard textCard = (TextCard) txcard;
        assertNotNull(textCard);
        assertEquals("Testtitel", textCard.getTitle());
        assertEquals("Testfrage", textCard.getQuestion());
        assertEquals("Testantwort", textCard.getAnswer());
        assertTrue(textCard.isVisibility());
        assertEquals(textCard.getTitle() + "\n" + textCard.getQuestion() + "\n" + textCard.getAnswer(), textCard.getContent());

        //MULTIPLE CHOICE

        String[] answers = {"Testantwort 1", "Testantwort2", "Testantwort 3"};
        int[] correctAnswers = {0,2};
        HashMap<String, Object> mcmap = new HashMap<>() {
            {
                put("answers", answers);
                put("question", "Testfrage");
                put("title", "Testtitel1");
                put("visibility", false);
                put("correctAnswers", correctAnswers);
            }
        };
        Card card2 = new MultipleChoiceCard("Testfrage",answers,correctAnswers,"Testtitel1",false);
        cardLogic.updateCardData(card2, true);
        Card card = cardLogic.getCardByUUID(card2.getUuid());
        assertNotNull(card);
        MultipleChoiceCard mcCard = (MultipleChoiceCard) card;
        assertNotNull(mcCard);
        assertEquals("Testtitel1", mcCard.getTitle());
        assertEquals("Testfrage", mcCard.getQuestion());
        assertArrayEquals(answers, mcCard.getAnswers());
        assertArrayEquals(correctAnswers, mcCard.getCorrectAnswers());
        assertFalse(mcCard.isVisibility());
        assertEquals(mcCard.getTitle() + "\n" + mcCard.getQuestion(), mcCard.getContent());

    }

//    @Test
//    public void testCardCreateAndUpdateWithTags() throws InvocationTargetException, IllegalAccessException {
//        HashMap<String, Object> txmap = new HashMap<>() {
//            {
//                put("answer", "Testantwort");
//                put("question", "Testfrage");
//                put("title", "Testtitel3");
//                put("visibility", false);
//            }
//        };
//        List<Tag> tagsToAdd = new ArrayList<>() {
//            {
//                add(new Tag("tag1"));
//                add(new Tag("tag2"));
//            }
//        };
//        Card card1 = new TextCard();
//        BeanUtils.populate(card1,txmap);
//        cardLogic.updateCardData(card1,true);
//        cardLogic.setTagsToCard(card1,tagsToAdd);
//        List<Tag> tags = cardLogic.getTags();
//        assertTrue(tags.size()>=2); //in case more than one
//        Optional<Tag> tagOpt1 = tags.stream().filter( t -> t.getVal().equals("tag1")).findFirst();
//        Optional<Tag>  tagOpt2 = tags.stream().filter( t -> t.getVal().equals("tag2")).findFirst();
//        assertTrue(tagOpt2.isPresent());
//        assertTrue(tagOpt1.isPresent());
//        Tag tag1 = tagOpt1.get();
//        Tag tag2 = tagOpt2.get();
//        //List<Card> CardsToTag1 = cardLogic.getCardsByTag(tag1.getVal());
//        /List<Card> CardsToTag2 = cardLogic.getCardsByTag(tag2.getVal());
//        assertEquals(1,CardsToTag1.size()); //FAILS
//        assertEquals(1,CardsToTag2.size());
//        card1 = CardsToTag1.iterator().next();
//        Card card2 = CardsToTag2.iterator().next();
//        assertEquals(card1.getUuid(), card2.getUuid());
//        List<Tag> tagToCard1 = cardLogic.getTagsToCard(card1);
//        List<Tag> tagToCard2 = cardLogic.getTagsToCard(card2);
//        assertFalse(tagToCard1.isEmpty());
//        Optional<Tag> tagOptional1 = tagToCard1.stream().filter(t -> t.getVal().equals("tag1")).findAny();
//        assertTrue(tagOptional1.isPresent());
//        assertFalse(tagToCard2.isEmpty());
//        Optional<Tag> tagOptional2 = tagToCard2.stream().filter(t -> t.getVal().equals("tag2")).findAny();
//        assertTrue(tagOptional2.isPresent());
//    }



    @Test
    public void testCardOverview()  {
        Card card1 = new TextCard("","AntwortCard1","TitelCard1",false);
        Card card2 = new TextCard("FrageCard2","AntwortCard2","",false);
        cardLogic.updateCardData(card1,true);
        cardLogic.updateCardData(card2,true);

        //TEST CARDOVERVIEW CONTAINS CARDS:
        EntityManager em = pm.getEntityManager();
        em.getTransaction().begin();
        List<CardOverview> co = em.createQuery("SELECT c FROM CardOverview c", CardOverview.class).getResultList();
        em.getTransaction().commit();
        em.close();
        assertNotNull(co);
        assertTrue(co.stream().anyMatch(c -> c.getTitelToShow().equals("TitelCard1")));
        assertTrue(co.stream().anyMatch(c -> c.getTitelToShow().equals("FrageCard2")));

    }

    /**
     * Testet die Kopierfunktion einer Kartenkopie in Hibernate.
     */
    @Test
    public void testCopyCard() {
        String[] answers = {"Testantwort 1", "Testantwort2", "Testantwort 3"};
        int[] correctAnswers = {0,2};
        Card text1 = new TextCard("F1", "nein doch", "Titel für die Karte 1", false);
        Card text2 = new MultipleChoiceCard("F2", answers, correctAnswers, "Titel für die Karte 2", true);
       cardLogic.updateCardData(text1, true);
        cardLogic.updateCardData(text2, true);
        Card card1 = cardLogic.getCardByUUID(text1.getUuid());
        Card card2 = cardLogic.getCardByUUID(text2.getUuid());
        assertNotNull(card1);
        assertNotNull(card2);
        Card copy1 = copyCard(card1);
        Card copy2 = copyCard(card2);
        assertEquals(card1.getUuid(), copy1.getUuid());
        assertEquals(card2.getUuid(), copy2.getUuid());
        text1.setTitle("Titel234");
        text2.setRating(5);
        cardLogic.updateCardData(text1, false);
        cardLogic.updateCardData(text2, false);
        card1 = cardLogic.getCardByUUID(text1.getUuid());
        card2 = cardLogic.getCardByUUID(text2.getUuid());
        assertTrue(card1.getTitle().equals(text1.getTitle()));
        assertEquals(card2.getRating(),text2.getRating());
    }



    @Test
    public void testGetCards(){
        importTestData();
        CardController.getInstance().getCardsToShow(1,30, new DataCallback<>() {
            @Override
            public void onSuccess(List<CardOverview> data) {

            }

            @Override
            public void onFailure(String msg) {
                log.error(msg);
            }

            @Override
            public void onInfo(String msg) {
                //nada
            }

        });
    }
//    @Test
//     public void getDecks(){
//        DeckController.getInstance().getDecks(new DataCallback<Deck>() {
//            @Override
//            public void onSuccess(List<Deck> data) {
//
//            }
//
//            @Override
//            public void onFailure(String msg) {
//
//            }
//
//            @Override
//            public void onInfo(String msg) {
//
//            }
//        });
//    }


}






