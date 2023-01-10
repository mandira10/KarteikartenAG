package com.swp.Controller;


import com.swp.DataModel.*;
import com.swp.DataModel.CardTypes.MultipleChoiceCard;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.GUI.Extras.Notification;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.Persistence.CardRepository;
import com.swp.Persistence.CategoryRepository;
import com.swp.Persistence.DataCallback;
import com.swp.Persistence.PersistenceManager;
import com.swp.TestData.*;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.junit.Assert;
import org.junit.Test;


import java.util.*;
import java.util.stream.Collectors;


import static com.swp.DataModel.Card.copyCard;
import static com.swp.TestData.importTestData;
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
        HashMap<String, Object> mcmap = new HashMap<>() {
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

    }

    @Test
    public void testCardCreateAndUpdateWithTags() {
        HashMap<String, Object> txmap = new HashMap<>() {
            {
                put("answer", "Testantwort");
                put("question", "Testfrage");
                put("title", "Testtitel3");
                put("visibility", false);
            }
        };
        Set<Tag> tagsToAdd = new HashSet<>() {
            {
                add(new Tag("tag1"));
                add(new Tag("tag2"));
            }
        };

        assertTrue(CardController.updateCardData(null, "TEXT", txmap, tagsToAdd, null));
        Set<Tag> tags = CardRepository.getTags();
        assertTrue(tags.size()>=2); //in case more than one
        Optional<Tag> tagOpt1 = tags.stream().filter( t -> t.getVal().equals("tag1")).findFirst();
        Optional<Tag>  tagOpt2 = tags.stream().filter( t -> t.getVal().equals("tag2")).findFirst();
        Tag tag1 = tagOpt1.get();
        Tag tag2 = tagOpt2.get();
        Set<Card> CardsToTag1 = CardRepository.findCardsByTag(tag1);
        Set<Card> CardsToTag2 = CardRepository.findCardsByTag(tag2);
        assertEquals(1,CardsToTag1.size()); //FAILS
        assertEquals(1,CardsToTag2.size());
        Card card1 = CardsToTag1.iterator().next();
        Card card2 = CardsToTag2.iterator().next();
        assertEquals(card1.getUuid(), card2.getUuid());
    }

    @Test
    public void testCardCreateAndUpdateWithCategories() {
        HashMap<String, Object> txmap = new HashMap<>() {
            {
                put("answer", "Testantwort");
                put("question", "Testfrage");
                put("title", "Testtitel2");
                put("visibility", false);
            }
        };
        Set<Category> categoriesToAdd = new HashSet<>() {
            {
                add(new Category("categorie1"));
                add(new Category("categorie2"));
            }
        };

        assertTrue(CardController.updateCardData(null, "TEXT", txmap, null, categoriesToAdd));
        Set<Category> categories = CategoryRepository.getCategories();
        assertTrue(categories.size()>=2); //FAILS
        Optional<Category> c1Opt = categories.stream().filter(n -> n.getName().equals("categorie1")).findFirst();
        Optional<Category> c2Opt = categories.stream().filter(n -> n.getName().equals("categorie2")).findFirst();
        Category c1 = c1Opt.get();
        Category c2 = c2Opt.get();
        Set<Card> CardsToC1 = CategoryRepository.getCardsByCategory(c1);
        Set<Card> CardsToC2 = CategoryRepository.getCardsByCategory(c2);
        assertEquals(1,CardsToC1.size()); //FAILS
        assertEquals(1,CardsToC2.size());
         Optional<Card> card1Opt = CardsToC1.stream().filter(n -> n.getTitle().equals("Testtitel2")).findFirst();
        Optional<Card> card2Opt = CardsToC2.stream().filter(n -> n.getTitle().equals("Testtitel2")).findFirst();
         Card card1 = card1Opt.get();
         Card card2 = card2Opt.get();
         assertTrue(card1.getUuid().equals(card2.getUuid()));
    }

    @Test
    public void testCardOverview() {
        HashMap<String, Object> mapCard1 = new HashMap<>() {{
            put("title", "TitelCard1");
            put("answer", "AntwortCard1");
        }};
        HashMap<String, Object> mapCard2 = new HashMap<>() {{
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

    /**
     * Testet die Kopierfunktion einer Kartenkopie in Hibernate.
     */
    @Test
    public void testCopyCard() {
        String[] answers = {"Testantwort 1", "Testantwort2", "Testantwort 3"};
        int[] correctAnswers = {1, 3};
        Card text1 = new TextCard("F1", "nein doch", "Titel für die Karte 1", false);
        Card text2 = new MultipleChoiceCard("F2", answers, correctAnswers, "Titel für die Karte 2", true);
        assertTrue(CardController.updateCardData(text1, true));
        assertTrue(CardController.updateCardData(text2, true));
        Optional<Card> optMcCard1 = CardRepository.findCardByTitle("Titel für die Karte 1");
        Optional<Card> optMcCard2 = CardRepository.findCardByTitle("Titel für die Karte 2");
        assertNotNull(optMcCard1);
        Card card1 = optMcCard1.get();
        assertNotNull(optMcCard2);
        Card card2 = optMcCard2.get();
        Card copy1 = copyCard(card1);
        Card copy2 = copyCard(card2);
        assertTrue(card1.getUuid().equals(copy1.getUuid()));
        assertTrue(card2.getUuid().equals(copy2.getUuid()));
        text1.setTitle("Titel234");
        text2.setRating(5);
        assertTrue(CardController.updateCardData(text1, false));
        assertTrue(CardController.updateCardData(text2, false));
        optMcCard1 = CardRepository.findCardByTitle("Titel für die Karte 1");
        optMcCard2 = CardRepository.findCardByTitle("Titel für die Karte 2");
        assertTrue(optMcCard1.isEmpty());
        assertFalse(optMcCard2.isEmpty());
        card2 = optMcCard2.get();

        optMcCard1 = CardRepository.findCardByTitle("Titel234");
        assertFalse(optMcCard2.isEmpty());
        card1 = optMcCard1.get();

        MultipleChoiceCard cardMC = (MultipleChoiceCard) card2;
        assertEquals(cardMC.getRating(), 5);
    }

    @Test
    public void testCategoryMultiHierarchy() {
        HashMap<String, Object> txmap = new HashMap<>() {
            {
                put("answer", "Testantwort für die Kategorien Hierachie");
                put("question", "Testfrage für die Kategorien Hierachie");
                put("title", "Testtitel für die Kategorien Hierarchie");
                put("visibility", false);
            }
        };
        Set<Category> categoriesToAdd = new HashSet<>() {
            {
                add(new Category("categorie3"));
                add(new Category("categorie4"));
                add(new Category("categorie5"));
                add(new Category("categorie6"));
                add(new Category("categorie7"));
            }
        };

        assertTrue(CardController.updateCardData(null, "TEXT", txmap, null, categoriesToAdd));
        Set<Category> categories = CategoryRepository.getCategories();
        Optional<Category> c1Opt = categories.stream().filter(n -> n.getName().equals("categorie3")).findFirst();
        Optional<Category> c2Opt = categories.stream().filter(n -> n.getName().equals("categorie4")).findFirst();
        Optional<Category> c3Opt = categories.stream().filter(n -> n.getName().equals("categorie5")).findFirst();
        Optional<Category> c4Opt = categories.stream().filter(n -> n.getName().equals("categorie6")).findFirst();
        Optional<Category> c5Opt = categories.stream().filter(n -> n.getName().equals("categorie7")).findFirst();
        Category c1 = c1Opt.get();
        Category c2 = c2Opt.get();
        Category c3 = c3Opt.get();
        Category c4 = c4Opt.get();
        Category c5 = c5Opt.get();
        c1.addChild(c2);
        c1.addChild(c3);
        c3.addChild(c2);
        c5.addChild(c4);
        CategoryRepository.updateCategory(c1);
        CategoryRepository.updateCategory(c2);
        CategoryRepository.updateCategory(c3);
        CategoryRepository.updateCategory(c4);
        CategoryRepository.updateCategory(c5);
        assertEquals(0, c1.getParents().size());
        assertEquals(2, c2.getParents().size());
        Set<Category> categoryParents = CategoryRepository.getParentsForCategory(c2);
        assertEquals(2, categoryParents.size());
        categoryParents.stream().filter(c -> c.getName().equals("categorie3")).findFirst();
        categoryParents.stream().filter(c -> c.getName().equals("categorie5")).findFirst();

        //check what happens if not every parent and child gets updated, DOESNT WORK
        c1.addChild(c4);
        // assertEquals(2,c4.getParents().size());
        // CategoryRepository.updateCategory(c1);
        //  categoryParents = CategoryRepository.getParentsForCategory(c4);
        // assertEquals(2,categoryParents.size());


    }

    @Test
    public void testGetCards(){
        importTestData();
        CardController.getCardsToShow(1,30, new DataCallback<Card>() {
            @Override public void onSuccess(List<Card> data)
            {
                List<Card> cards = data;
            }

            @Override public void onFailure(String msg)
            {
                log.error(msg);
            }

        }, Deck.CardOrder.ALPHABETICAL);
    }
    @Test
     public void getDecks(){
        DeckController.getDecks();
    }

}






