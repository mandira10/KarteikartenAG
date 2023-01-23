package com.swp.Persistence;

import com.swp.DataModel.Card;
import com.swp.DataModel.CardTypes.*;
import com.swp.DataModel.Category;
import com.swp.DataModel.StudySystem.LeitnerSystem;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.DataModel.StudySystem.TimingSystem;
import com.swp.DataModel.StudySystem.VoteSystem;
import org.junit.Test;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import static org.junit.Assert.*;

public class CrudTest {
    // Repositories die getestet werden
    private final CardRepository cardRepository = CardRepository.getInstance();
    private final StudySystemRepository studySystemRepository = StudySystemRepository.getInstance();
    private final CategoryRepository categoryRepository = CategoryRepository.getInstance();
    private final CardToBoxRepository cardToBoxRepository = CardToBoxRepository.getInstance();
    private final CardToCategoryRepository cardToCategoryRepository = CardToCategoryRepository.getInstance();

    @Test
    public void cardCrudTest() {
        List<Card> cards = exampleCards();

        // CREATE
        CardRepository.startTransaction();
        for (final Card card : cards) {
            cardRepository.save(card);
        }
        CardRepository.commitTransaction();

        // READ
        List<Card> allReadCards = new ArrayList<>();
        CardRepository.startTransaction();
        for (final Card card : cards) {
            final Card readCard = cardRepository.getCardByUUID(card.getUuid());
            assertEquals(card, readCard);
            allReadCards.add(readCard);
        }
        CardRepository.commitTransaction();
        assertEquals("same length", cards.size(), allReadCards.size());
        assertTrue(allReadCards.containsAll(cards));
        allReadCards = new ArrayList<>();

        // UPDATE
        Card changedCard = cards.get(0);
        changedCard.setQuestion("Neue Frage");
        cards.set(0, changedCard);
        CardRepository.startTransaction();
        cardRepository.update(changedCard);
        for (final Card card : cards) {
            final Card readCard = cardRepository.getCardByUUID(card.getUuid());
            assertEquals(card, readCard);
            allReadCards.add(readCard);
        }
        CardRepository.commitTransaction();
        assertEquals("same length", cards.size(), allReadCards.size());
        assertTrue(allReadCards.containsAll(cards));
        allReadCards = new ArrayList<>();

        // DELETE
        CardRepository.startTransaction();
        final String uuid = changedCard.getUuid();
        cardRepository.delete(changedCard);
        //assertThrows(new NoResultException(), cardRepository.getCardByUUID(uuid));
        CardRepository.commitTransaction();
    }

    @Test
    public void deckCrudTest() {
        // wahrscheinlich löschen
    }

    @Test
    public void categoryCrudTest() {
        List<Category> categories = exampleCategories();

        // CREATE
        CategoryRepository.startTransaction();
        for (final Category category : categories) {
            categoryRepository.save(category);
        }
        CategoryRepository.commitTransaction();

        // READ
        List<Category> allReadCategories = new ArrayList<>();
        CategoryRepository.startTransaction();
        for (final Category category : categories) {
            final Category readCategory = categoryRepository.findByUUID(category.getUuid());
            assertEquals(category, readCategory);
            allReadCategories.add(readCategory);
        }
        CategoryRepository.commitTransaction();
        assertEquals("same length", categories.size(), allReadCategories.size());
        assertTrue(allReadCategories.containsAll(categories));
        allReadCategories = new ArrayList<>();

        // UPDATE
        Category changedCategory = categories.get(0);
        changedCategory.setName("Neuer Name");
        categories.set(0, changedCategory);
        CategoryRepository.startTransaction();
        categoryRepository.update(changedCategory);
        for (final Category category : categories) {
            final Category readCategory = categoryRepository.findByUUID(category.getUuid());
            assertEquals(category, readCategory);
            allReadCategories.add(readCategory);
        }
        CategoryRepository.commitTransaction();
        assertEquals("same length", categories.size(), allReadCategories.size());
        assertTrue(allReadCategories.containsAll(categories));
        allReadCategories = new ArrayList<>();

        // DELETE
        CategoryRepository.startTransaction();
        final String uuid = changedCategory.getUuid();
        categoryRepository.delete(changedCategory);
        //assertThrows(new NoResultException(), categoryRepository.findByUUID(uuid));
        CategoryRepository.commitTransaction();
    }

    @Test
    public void cardToDeckCrudTest() {

    }

    @Test
    public void cardToCategoryCrudTest() {

    }


    /*
     * Hilfsfunktionen um einen Test-Datensatz an Instanzen zu haben
     */
    public List<Card> exampleCards() {
        List<Card> exampleCards = new ArrayList<>();
        Collections.addAll(exampleCards,
                new AudioCard(new byte[]{4}, "Audio 1", "Frage 1", "Antwort 1", false, false),
                new AudioCard(new byte[]{10}, "Audio 2", "Frage 2", "Antwort 2", true, false),
                new AudioCard(new byte[]{100}, "Audio 3", "Frage 3", "Antwort 3", false, true),
                new AudioCard(new byte[]{0}, "Audio 4", "Frage 4", "Antwort 4", true, true),
                new AudioCard(new byte[]{1}, "Audio 5", "Frage 5", "Antwort 5", false, false),
                new ImageDescriptionCard("Bildbeschreibung 1?", new ImageDescriptionCardAnswer[]{}, "Titel der Karte", "/path/to/image1.png", false),
                new ImageDescriptionCard("Bildbeschreibung 2?", new ImageDescriptionCardAnswer[]{}, "Titel der Karte", "/path/to/image2.png", true),
                new ImageDescriptionCard("Bildbeschreibung 3?", new ImageDescriptionCardAnswer[]{
                        new ImageDescriptionCardAnswer("Beschreibung 3", 0, 1)
                }, "Titel der Karte", "/path/to/image3.png", false),
                new ImageDescriptionCard("Bildbeschreibung 4?", new ImageDescriptionCardAnswer[]{
                        new ImageDescriptionCardAnswer("Beschreibung 4", 1, 2),
                        new ImageDescriptionCardAnswer("andere Beschreibung", 6, 1)
                }, "Titel der Karte", "/path/to/image4.png", true),
                new ImageDescriptionCard("Bildbeschreibung 5?", new ImageDescriptionCardAnswer[]{
                        new ImageDescriptionCardAnswer("Beschreibung", 1, 3)
                }, "Titel der Karte", "/path/to/image5.png", false),
                new ImageTestCard("Image 1", "Antwort 1", "/path/to/image1.png", "Titel 1",false, false),
                new ImageTestCard("Image 2", "Antwort 2", "/path/to/image2.png", "Titel 2",true, false),
                new ImageTestCard("Image 3", "Antwort 3", "/path/to/image3.png", "Titel 3",false, true),
                new ImageTestCard("Image 4", "Antwort 4", "/path/to/image4.png", "Titel 4",true, true),
                new ImageTestCard("Image 5", "Antwort 5", "/path/to/image5.png", "Titel 5",false, false),
                new MultipleChoiceCard("Multi 1", new String[]{"Antwort A", "Antwort B", "Antwort C", "Antwort D"}, new int[]{0,3}, "Titel 1", false),
                new MultipleChoiceCard("Multi 2", new String[]{"Antwort A", "Antwort B", "Antwort C",            }, new int[]{1,2}, "Titel 2", true),
                new MultipleChoiceCard("Multi 3!", new String[]{"Eine ungewöhnlich lange Antwort A", "Antwort [B]"}, new int[]{1}, "Titel 3", false),
                new MultipleChoiceCard("Multi 2^2", new String[]{"SQL als Antwort...", "", "Antwort'); DROP TABLE Card"}, new int[]{1}, "Titel 4", true),
                new MultipleChoiceCard("Multi 0b0101", new String[]{"Antwort A", "Antwort B", "Antwort C", "Antwort D"}, new int[]{2,0}, "Titel 5", false),
                new TextCard("Textfrage", "Antwort", "Titel", false),
                new TextCard("Textfrage 2", "Antwort auf 2", "Titel 2", false),
                new TextCard("Textfrage 3", "Antwort auf 3", "Titel 3", false),
                new TextCard("Textfrage 4", "Antwort auf 4", "Titel 4", false),
                new TextCard("Textfrage 5", "Antwort auf 5", "Titel 5", false),
                new TrueFalseCard("WahrFalsch 1", false, "Titel 1", false),
                new TrueFalseCard("WahrFalsch 2", true, "Titel 2", false),
                new TrueFalseCard("WahrFalsch 3", false, "Titel 3", true),
                new TrueFalseCard("WahrFalsch 4", true, "Titel 4", true),
                new TrueFalseCard("WahrFalsch 5", false, "Titel 5", false)
            );

        return exampleCards;
    }

    private List<StudySystem> exampleStudySystem() {
        List<StudySystem> exampleStudySystems = new ArrayList<>();
        Collections.addAll(exampleStudySystems,
                new LeitnerSystem("Leitner 1", StudySystem.CardOrder.RANDOM, false),
                new LeitnerSystem("Leitner 2", StudySystem.CardOrder.ALPHABETICAL, true),
                new LeitnerSystem("Leitner 3", StudySystem.CardOrder.REVERSED_ALPHABETICAL, false),
                new LeitnerSystem("Leitner 4", StudySystem.CardOrder.RANDOM, true),
                new LeitnerSystem("Leitner 5", StudySystem.CardOrder.ALPHABETICAL, false),
                new TimingSystem("Timing 1", StudySystem.CardOrder.RANDOM, false, 10),
                new TimingSystem("Timing 2", StudySystem.CardOrder.ALPHABETICAL, true, 20),
                new TimingSystem("Timing 3", StudySystem.CardOrder.REVERSED_ALPHABETICAL, false, 1),
                new TimingSystem("Timing 4", StudySystem.CardOrder.RANDOM, true, 100),
                new TimingSystem("Timing 5", StudySystem.CardOrder.ALPHABETICAL, false, 5),
                new VoteSystem("Vote 1", StudySystem.CardOrder.RANDOM, false),
                new VoteSystem("Vote 2", StudySystem.CardOrder.ALPHABETICAL, true),
                new VoteSystem("Vote 3", StudySystem.CardOrder.REVERSED_ALPHABETICAL, false),
                new VoteSystem("Vote 4", StudySystem.CardOrder.RANDOM, true),
                new VoteSystem("Vote 5", StudySystem.CardOrder.ALPHABETICAL, false)
            );

        return exampleStudySystems;
    }

//    private List<Deck> exampleDecks() {
//        //TODO Test-Decks erstellen
//        List<Deck> exampleDecks = new ArrayList<>();
//        Collections.addAll(exampleDecks,
//                new Deck()
//            );
//        return null;
//    }

    public List<Category> exampleCategories() {
        //TODO Test-Kategorien erstellen
        List<Category> exampleCategories = new ArrayList<>();
        Collections.addAll(exampleCategories,
                new Category("Kategorie A"),
                new Category("Kategorie B"),
                new Category("Kategorie C"),
                new Category("Kategorie D"),
                new Category("Kategorie E")
            );
        return exampleCategories;
    }
}
