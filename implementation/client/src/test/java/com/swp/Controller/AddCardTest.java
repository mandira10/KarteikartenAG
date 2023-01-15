package com.swp.Controller;



import com.swp.DataModel.*;
import com.swp.DataModel.CardTypes.MultipleChoiceCard;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.Logic.CardLogic;
import com.swp.Logic.CategoryLogic;
import com.swp.Persistence.CategoryRepository;
import com.swp.Persistence.DataCallback;
import com.swp.Persistence.PersistenceManager;
import jakarta.persistence.EntityManager;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;
import org.junit.Assert;
import org.junit.Test;


import java.lang.reflect.InvocationTargetException;
import java.util.*;


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
    public void testCardCreateAndUpdateNoTagsNoCategories()  {
        //TEXT
        Card card1 = new TextCard("Testfrage","Testantwort","Testtitel",true);
        assertTrue(CardLogic.updateCardData(card1,true));
        Card txcard = CardLogic.getCardByUUID(card1.getUuid());
        TextCard textCard = (TextCard) txcard;
        assertNotNull(textCard);
        assertEquals("Testtitel", textCard.getTitle());
        assertEquals("Testfrage", textCard.getQuestion());
        assertEquals("Testantwort", textCard.getAnswer());
        assertTrue(textCard.isVisibility());
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
        Card card2 = new MultipleChoiceCard("Testfrage",answers,correctAnswers,"Testtitel1",false);
        assertTrue(CardLogic.updateCardData(card2, true));
        Card card = CardLogic.getCardByUUID(card2.getUuid());
        assertNotNull(card);
        MultipleChoiceCard mcCard = (MultipleChoiceCard) card;
        assertNotNull(mcCard);
        assertEquals("Testtitel1", mcCard.getTitle());
        assertEquals("Testfrage", mcCard.getQuestion());
        Assert.assertArrayEquals(answers, mcCard.getAnswers());
        Assert.assertArrayEquals(correctAnswers, mcCard.getCorrectAnswers());
        assertFalse(mcCard.isVisibility());
        assertEquals(mcCard.getTitle() + "\n" + mcCard.getQuestion(), mcCard.getContent());

    }

    @Test
    public void testCardCreateAndUpdateWithTags() throws InvocationTargetException, IllegalAccessException {
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
        Card card1 = new TextCard();
        BeanUtils.populate(card1,txmap);
        assertTrue(CardLogic.updateCardData(card1,true));
        assertTrue(CardLogic.setTagsToCard(card1,tagsToAdd));
        List<Tag> tags = CardLogic.getTags();
        assertTrue(tags.size()>=2); //in case more than one
        Optional<Tag> tagOpt1 = tags.stream().filter( t -> t.getVal().equals("tag1")).findFirst();
        Optional<Tag>  tagOpt2 = tags.stream().filter( t -> t.getVal().equals("tag2")).findFirst();
        assertTrue(tagOpt2.isPresent());
        assertTrue(tagOpt1.isPresent());
        Tag tag1 = tagOpt1.get();
        Tag tag2 = tagOpt2.get();
        List<Card> CardsToTag1 = CardLogic.getCardsByTag(tag1.getVal());
        List<Card> CardsToTag2 = CardLogic.getCardsByTag(tag2.getVal());
        assertEquals(1,CardsToTag1.size()); //FAILS
        assertEquals(1,CardsToTag2.size());
        card1 = CardsToTag1.iterator().next();
        Card card2 = CardsToTag2.iterator().next();
        assertEquals(card1.getUuid(), card2.getUuid());
        List<Tag> tagToCard1 = CardLogic.getTagsToCard(card1);
        List<Tag> tagToCard2 = CardLogic.getTagsToCard(card2);
        assertFalse(tagToCard1.isEmpty());
        Optional<Tag> tagOptional1 = tagToCard1.stream().filter(t -> t.getVal().equals("tag1")).findAny();
        assertTrue(tagOptional1.isPresent());
        assertFalse(tagToCard2.isEmpty());
        Optional<Tag> tagOptional2 = tagToCard2.stream().filter(t -> t.getVal().equals("tag2")).findAny();
        assertTrue(tagOptional2.isPresent());
    }

    @Test
    public void testCardCreateAndUpdateWithCategories(){
        Set<Category> categoriesToAdd = new HashSet<>() {
            {
                add(new Category("categorie1"));
                add(new Category("categorie2"));
            }
        };
        Card card1  = new TextCard("Testfrage","Testantwort","Testtitel2",false);
        assertTrue(CardLogic.updateCardData(card1,true));
        assertTrue(CategoryLogic.setCardToCategories(card1,categoriesToAdd));
        Set<Category> categories = CategoryLogic.getCategories();
        assertTrue(categories.size()>=2); //FAILS
        Optional<Category> c1Opt = categories.stream().filter(n -> n.getName().equals("categorie1")).findFirst();
        Optional<Category> c2Opt = categories.stream().filter(n -> n.getName().equals("categorie2")).findFirst();
        assertTrue(c1Opt.isPresent());
        assertTrue(c2Opt.isPresent());
        Category c1 = c1Opt.get();
        Category c2 = c2Opt.get();
        Set<Card> CardsToC1 = CategoryLogic.getCardsInCategory(c1);
        Set<Card> CardsToC2 = CategoryLogic.getCardsInCategory(c2);
        assertEquals(1,CardsToC1.size()); //FAILS
        assertEquals(1,CardsToC2.size());
         Optional<Card> card1Opt = CardsToC1.stream().filter(n -> n.getTitle().equals("Testtitel2")).findFirst();
        Optional<Card> card2Opt = CardsToC2.stream().filter(n -> n.getTitle().equals("Testtitel2")).findFirst();
        assertTrue(card1Opt.isPresent());
        assertTrue(card2Opt.isPresent());
          card1 = card1Opt.get();
         Card card2 = card2Opt.get();
        assertEquals(card1.getUuid(), card2.getUuid());
    }

    @Test
    public void testCardOverview()  {
        Card card1 = new TextCard("","AntwortCard1","TitelCard1",false);
        Card card2 = new TextCard("FrageCard2","AntwortCard2","",false);
        assertTrue(CardLogic.updateCardData(card1,true));
        assertTrue(CardLogic.updateCardData(card2,true));

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
        int[] correctAnswers = {1, 3};
        Card text1 = new TextCard("F1", "nein doch", "Titel für die Karte 1", false);
        Card text2 = new MultipleChoiceCard("F2", answers, correctAnswers, "Titel für die Karte 2", true);
        assertTrue(CardLogic.updateCardData(text1, true));
        assertTrue(CardLogic.updateCardData(text2, true));
        Card card1 = CardLogic.getCardByUUID(text1.getUuid());
        Card card2 = CardLogic.getCardByUUID(text2.getUuid());
        assertNotNull(card1);
        assertNotNull(card2);
        Card copy1 = copyCard(card1);
        Card copy2 = copyCard(card2);
        assertEquals(card1.getUuid(), copy1.getUuid());
        assertEquals(card2.getUuid(), copy2.getUuid());
        text1.setTitle("Titel234");
        text2.setRating(5);
        assertTrue(CardLogic.updateCardData(text1, false));
        assertTrue(CardLogic.updateCardData(text2, false));
        card1 = CardLogic.getCardByUUID(text1.getUuid());
        card2 = CardLogic.getCardByUUID(text2.getUuid());
        assertTrue(card1.getTitle().equals(text1.getTitle()));
        assertEquals(card2.getRating(),text2.getRating());
    }

    @Test
    public void testCategoryMultiHierarchy() {
        Set<Category> categoriesToAdd = new HashSet<>() {
            {
                add(new Category("categorie3"));
                add(new Category("categorie4"));
                add(new Category("categorie5"));
                add(new Category("categorie6"));
                add(new Category("categorie7"));
            }
        };
        Card card1 = new TextCard("Testfrage für die Kategorien Hierachie","Testantwort für die Kategorien Hierachie","Testtitel für die Kategorien Hierarchie",false);
        assertTrue(CardLogic.updateCardData(card1,true));
        assertTrue(CategoryLogic.setCardToCategories(card1,categoriesToAdd));
        Set<Category> categories = CategoryLogic.getCategories();
        Optional<Category> c1Opt = categories.stream().filter(n -> n.getName().equals("categorie3")).findFirst();
        Optional<Category> c2Opt = categories.stream().filter(n -> n.getName().equals("categorie4")).findFirst();
        Optional<Category> c3Opt = categories.stream().filter(n -> n.getName().equals("categorie5")).findFirst();
        Optional<Category> c4Opt = categories.stream().filter(n -> n.getName().equals("categorie6")).findFirst();
        Optional<Category> c5Opt = categories.stream().filter(n -> n.getName().equals("categorie7")).findFirst();
        assertTrue(c1Opt.isPresent());
        assertTrue(c2Opt.isPresent());
        assertTrue(c3Opt.isPresent());
        assertTrue(c4Opt.isPresent());
        assertTrue(c5Opt.isPresent());
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
        //categoryParents.stream().filter(c -> c.getName().equals("categorie3")).findFirst();
        //categoryParents.stream().filter(c -> c.getName().equals("categorie5")).findFirst();

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
        CardController.getCardsToShow(1,30, new DataCallback<>() {
            @Override
            public void onSuccess(List<Card> data) {

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
    @Test
     public void getDecks(){
        DeckController.getDecks();
    }


}






