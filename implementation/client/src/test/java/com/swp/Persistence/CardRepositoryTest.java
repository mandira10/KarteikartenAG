package com.swp.Persistence;

import com.swp.DataModel.*;
import com.swp.DataModel.CardTypes.*;
import com.swp.DataModel.StudySystem.BoxToCard;
import com.swp.DataModel.StudySystem.LeitnerSystem;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.DataModel.StudySystem.StudySystemBox;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;


public class CardRepositoryTest {
    // Repositories die getestet werden
    private final CardRepository cardRepository = CardRepository.getInstance();

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
        assertEquals(cards.size(), allReadCards.size(), "same length");
        assertTrue(allReadCards.containsAll(cards));

        // UPDATE
        //TODO

        // DELETE
        //TODO

    }

    @Test
    public void getCardsByStudySystem() {
        Card card1 = new TextCard("Frage 1", "Antwort 1", "Titel 1");
        Card card2 = new TextCard("Frage 2", "Antwort 2", "Titel 2");
        StudySystem studySystem = new LeitnerSystem("Name", StudySystem.CardOrder.ALPHABETICAL);
        StudySystem notSavedStudySystem = new LeitnerSystem("Anderes", StudySystem.CardOrder.ALPHABETICAL);
        BoxToCard cardToBox = new BoxToCard(card1, studySystem.getBoxes().get(0),0);

        CardRepository.startTransaction();
        cardRepository.save(card1);
        cardRepository.save(card2);
        CardRepository.commitTransaction();

        StudySystemRepository.startTransaction();
        StudySystemRepository studySystemRepository = StudySystemRepository.getInstance();
        studySystemRepository.save(studySystem);
        StudySystemRepository.commitTransaction();

        CardToBoxRepository.startTransaction();
        CardToBoxRepository cardToBoxRepository = CardToBoxRepository.getInstance();
        cardToBoxRepository.save(cardToBox);
        CardToBoxRepository.commitTransaction();

        CardToBoxRepository.startTransaction();
        assertEquals(1, cardRepository.findCardsByStudySystem(studySystem).size());
        assertEquals(card1.getUuid(), cardRepository.findCardsByStudySystem(studySystem).get(0).getUUUID());
        assertEquals(0, cardRepository.findCardsByStudySystem(notSavedStudySystem).size());
        CardToBoxRepository.commitTransaction();
    }

    @Test
    public void getCardsByTag() {
        Card card1 = new TextCard("Frage 1", "Antwort 1", "Titel 1");
        Tag tag1 = new Tag("Tag_1");
        Tag tag2 = new Tag("Tag_2"); // not persisted
        CardToTag cardToTag = new CardToTag(card1, tag1);
        CardToTag notSavedC2T = new CardToTag(card1, tag2);

        CardRepository.startTransaction();
        cardRepository.save(card1);
        CardRepository.commitTransaction();

        TagRepository.startTransaction();
        TagRepository tagRepository = TagRepository.getInstance();
        tagRepository.save(tag1);
        tagRepository.save(tag2);
        TagRepository.commitTransaction();

        CardToTagRepository.startTransaction();
        CardToTagRepository cardToTagRepository = CardToTagRepository.getInstance();
        cardToTagRepository.save(cardToTag);
        CardToTagRepository.commitTransaction();

        CardRepository.startTransaction();
        assertEquals(1, cardRepository.findCardsByTag(tag1).size());
        assertEquals(1, cardRepository.findCardsByTag(tag1.getVal()).size());
        assertEquals(card1.getUuid(), cardRepository.findCardsByTag(tag1).get(0).getUUUID());
        assertEquals(0, cardRepository.findCardsByTag(tag2).size());
        assertEquals(0, cardRepository.findCardsByTag(tag2.getVal()).size());
        CardRepository.commitTransaction();
    }

    @Test
    public void getCardsByCategory() {
        Card card1 = new TextCard("Frage 1", "Antwort 1", "Titel 1");
        Category cat1 = new Category("Kategorie A");
        Category cat2 = new Category("Kategorie B"); // not persisted
        CardToCategory cardToCategory = new CardToCategory(card1, cat1);
        CardToCategory notSavedC2C = new CardToCategory(card1, cat2);

        CardRepository.startTransaction();
        cardRepository.save(card1);
        CardRepository.commitTransaction();

        CategoryRepository.startTransaction();
        CategoryRepository categoryRepository = CategoryRepository.getInstance();
        categoryRepository.save(cat1);
        categoryRepository.save(cat2);
        CategoryRepository.commitTransaction();

        CardToCategoryRepository.startTransaction();
        CardToCategoryRepository cardToCategoryRepository = CardToCategoryRepository.getInstance();
        cardToCategoryRepository.save(cardToCategory);
        CardToCategoryRepository.commitTransaction();

        CardRepository.startTransaction();
        assertEquals(1, cardRepository.getCardsByCategory(cat1).size());
        assertEquals(1, cardRepository.getCardsByCategory(cat1.getName()).size());
        assertEquals(card1.getUuid(), cardRepository.getCardsByCategory(cat1).get(0).getUUUID());
        assertEquals(0, cardRepository.getCardsByCategory(cat2).size());
        assertEquals(0, cardRepository.getCardsByCategory(cat2.getName()).size());
        CardRepository.commitTransaction();
    }

    @Test
    public void getCardsByUUID() {
        Card card1 = new TextCard("Frage 1", "Antwort 1", "Titel 1");
        Card card2 = new TextCard("Frage 2", "Antwort 2", "Titel 2");

        CardRepository.startTransaction();
        cardRepository.save(card1);
        CardRepository.commitTransaction();

        CardRepository.startTransaction();
        assertEquals(card1, cardRepository.getCardByUUID(card1.getUuid()));
        assertThrows(NoResultException.class, () -> cardRepository.getCardByUUID(card2.getUuid()));
        CardRepository.commitTransaction();
    }

    // Hilfsfunktionen um einen Test-Datensatz an Instanzen zu haben
    public List<Card> exampleCards() {
        List<Card> exampleCards = new ArrayList<>();
        //TODO Karten mit Inhalt füllen
        Collections.addAll(exampleCards,
                new AudioCard(),
                new AudioCard(),
                new AudioCard(),
                new AudioCard(),
                new AudioCard(),
                new ImageDescriptionCard("Sind das komische Fragen?", new ImageDescriptionCardAnswer[]{}, "Titel der Karte", "/path/to/image.png"),
                new ImageDescriptionCard(),
                new ImageDescriptionCard(),
                new ImageDescriptionCard(),
                new ImageDescriptionCard(),
                new ImageTestCard(),
                new ImageTestCard(),
                new ImageTestCard(),
                new ImageTestCard(),
                new ImageTestCard(),
                new MultipleChoiceCard("Frage 1", new String[]{"Antwort A", "Antwort B", "Antwort C", "Antwort D"}, new int[]{0,3}, "Titel 1"),
                new MultipleChoiceCard("Frage 2", new String[]{"Antwort A", "Antwort B", "Antwort C",            }, new int[]{1,2}, "Titel 2"),
                new MultipleChoiceCard("Frage 3!", new String[]{"Eine ungewöhnlich lange Antwort A", "Antwort [B]"}, new int[]{1}, "Titel 3"),
                new MultipleChoiceCard("Frage 2^2", new String[]{"SQL als Antwort...", "", "Antwort'); DROP TABLE Card"}, new int[]{1}, "Titel 4"),
                new MultipleChoiceCard("Frage 0b0101", new String[]{"Antwort A", "Antwort B", "Antwort C", "Antwort D"}, new int[]{2,0}, "Titel 5"),
                new TextCard("Frage", "Antwort", "Titel"),
                new TextCard("Frage 2", "Antwort auf die Frage", "Titel XY"),
                new TextCard(),
                new TextCard(),
                new TextCard(),
                new TrueFalseCard(),
                new TrueFalseCard(),
                new TrueFalseCard(),
                new TrueFalseCard(),
                new TrueFalseCard()
        );

        return exampleCards;
    }
}
