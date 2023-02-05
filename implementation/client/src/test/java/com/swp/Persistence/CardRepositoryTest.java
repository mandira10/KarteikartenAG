package com.swp.Persistence;

import com.gumse.textures.Texture;
import com.swp.Controller.ControllerThreadPool;
import com.swp.DataModel.*;
import com.swp.DataModel.CardTypes.*;
import com.swp.DataModel.Language.German;
import com.swp.DataModel.StudySystem.BoxToCard;
import com.swp.DataModel.StudySystem.LeitnerSystem;
import com.swp.DataModel.StudySystem.StudySystem;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Ole-Niklas Mahlstädt
 */
public class CardRepositoryTest
{
    // Repositories die getestet werden
    private final CardRepository cardRepository = CardRepository.getInstance();
    @BeforeAll
    public static void before()
    {
        PersistenceManager.init("KarteikartenDBTest");
        German.getInstance().activate();
        ControllerThreadPool.getInstance().synchronizedTasks(true);
    }
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
        allReadCards = new ArrayList<>();
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
        assertEquals(cards.size(), allReadCards.size(), "same length");
        assertTrue(allReadCards.containsAll(cards));

        // DELETE
        CardRepository.startTransaction();
        final String uuid = changedCard.getUuid();
        Card card = cardRepository.getCardByUUID(uuid);
        assertNotNull(card);
        cardRepository.delete(card);
        assertThrows(NoResultException.class, () -> cardRepository.getCardByUUID(uuid));
        CardRepository.commitTransaction();
    }

    @Test
    public void getCardsByStudySystem()
    {
        Card card1 = new TextCard("Frage 1", "Antwort 1", "Titel 1");
        Card card2 = new TextCard("Frage 2", "Antwort 2", "Titel 2");
        StudySystem studySystem = new LeitnerSystem("Name", StudySystem.CardOrder.ALPHABETICAL);
        StudySystem notSavedStudySystem = new LeitnerSystem("Anderes", StudySystem.CardOrder.ALPHABETICAL);
        BoxToCard cardToBox = new BoxToCard(card1, studySystem.getBoxes().get(0));

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
        byte[] img1 = new byte[]{(byte) 1024};
        byte[] img2 = new byte[]{(byte) 4096};
        byte[] img3 = new byte[]{(byte) 8192};
        byte[] img4 = new byte[]{(byte) 16384};
        byte[] img5 = new byte[]{(byte) 32768};
        ImageDescriptionCard imagedesccard3 = new ImageDescriptionCard("Bildbeschreibung 3?", new ArrayList<ImageDescriptionCardAnswer>(), "Titel der Karte", img3);
        imagedesccard3.setAnswers(new ArrayList<ImageDescriptionCardAnswer>() {{ 
            new ImageDescriptionCardAnswer("Beschreibung 3", 0, 1, imagedesccard3);
        }});
    
        ImageDescriptionCard imagedesccard4 = new ImageDescriptionCard("Bildbeschreibung 4?", new ArrayList<ImageDescriptionCardAnswer>(), "Titel der Karte", img4);
        imagedesccard4.setAnswers(new ArrayList<ImageDescriptionCardAnswer>() {{ 
            new ImageDescriptionCardAnswer("Beschreibung 4", 1, 2, imagedesccard4);
            new ImageDescriptionCardAnswer("andere Beschreibung", 6, 1, imagedesccard4);
        }});
    
        ImageDescriptionCard imagedesccard5 = new ImageDescriptionCard("Bildbeschreibung 5?", new ArrayList<ImageDescriptionCardAnswer>(), "Titel der Karte", img5);
        imagedesccard5.setAnswers(new ArrayList<ImageDescriptionCardAnswer>() {{ 
            add(new ImageDescriptionCardAnswer("Beschreibung", 1, 3, imagedesccard5)); 
        }});
            
    
        Collections.addAll(exampleCards,
                new AudioCard(new byte[]{4}, "Audio 1", "Frage 1", "Antwort 1", false),
                new AudioCard(new byte[]{10}, "Audio 2", "Frage 2", "Antwort 2", true),
                new AudioCard(new byte[]{100}, "Audio 3", "Frage 3", "Antwort 3", false),
                new AudioCard(new byte[]{0}, "Audio 4", "Frage 4", "Antwort 4", true),
                new AudioCard(new byte[]{1}, "Audio 5", "Frage 5", "Antwort 5", false),
                new ImageDescriptionCard("Bildbeschreibung 1?", new ArrayList<ImageDescriptionCardAnswer>(), "Titel der Karte", img1),
                new ImageDescriptionCard("Bildbeschreibung 2?", new ArrayList<ImageDescriptionCardAnswer>(), "Titel der Karte", img2),
                imagedesccard3,
                imagedesccard4,
                imagedesccard5,
                new ImageTestCard("Image 1", "Antwort 1", img1, "Titel 1",false),
                new ImageTestCard("Image 2", "Antwort 2", img2, "Titel 2",true),
                new ImageTestCard("Image 3", "Antwort 3", img3, "Titel 3",false),
                new ImageTestCard("Image 4", "Antwort 4",img4, "Titel 4",true),
                new ImageTestCard("Image 5", "Antwort 5",img5, "Titel 5",false),
                new MultipleChoiceCard("Multi 1", new String[]{"Antwort A", "Antwort B", "Antwort C", "Antwort D"}, new int[]{0,3}, "Titel 1"),
                new MultipleChoiceCard("Multi 2", new String[]{"Antwort A", "Antwort B", "Antwort C",            }, new int[]{1,2}, "Titel 2"),
                new MultipleChoiceCard("Multi 3!", new String[]{"Eine ungewöhnlich lange Antwort A", "Antwort [B]"}, new int[]{1}, "Titel 3"),
                new MultipleChoiceCard("Multi 2^2", new String[]{"SQL als Antwort...", "", "Antwort'); DROP TABLE Card"}, new int[]{1}, "Titel 4"),
                new MultipleChoiceCard("Multi 0b0101", new String[]{"Antwort A", "Antwort B", "Antwort C", "Antwort D"}, new int[]{2,0}, "Titel 5"),
                new TextCard("Textfrage", "Antwort", "Titel"),
                new TextCard("Textfrage 2", "Antwort auf 2", "Titel 2"),
                new TextCard("Textfrage 3", "Antwort auf 3", "Titel 3"),
                new TextCard("Textfrage 4", "Antwort auf 4", "Titel 4"),
                new TextCard("Textfrage 5", "Antwort auf 5", "Titel 5"),
                new TrueFalseCard("WahrFalsch 1", false, "Titel 1"),
                new TrueFalseCard("WahrFalsch 2", true, "Titel 2"),
                new TrueFalseCard("WahrFalsch 3", false, "Titel 3"),
                new TrueFalseCard("WahrFalsch 4", true, "Titel 4"),
                new TrueFalseCard("WahrFalsch 5", false, "Titel 5")
        );
        return exampleCards;
    }
}
