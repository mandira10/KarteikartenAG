package com.swp.Persistence;

import com.gumse.gui.Locale;
import com.gumse.textures.Texture;
import com.gumse.tools.Output;
import com.gumse.tools.Toolbox;
import com.swp.Controller.ControllerThreadPool;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardToCategory;
import com.swp.DataModel.CardTypes.*;
import com.swp.DataModel.Category;
import com.swp.DataModel.StudySystem.BoxToCard;
import com.swp.DataModel.StudySystem.LeitnerSystem;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.DataModel.StudySystem.StudySystemBox;
import com.swp.DataModel.StudySystem.TimingSystem;
import com.swp.DataModel.StudySystem.VoteSystem;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.joor.Reflect.on;
import static org.junit.jupiter.api.Assertions.*;

public class CrudTest {
    // Repositories die getestet werden
    private final CardRepository cardRepository = CardRepository.getInstance();
    private final StudySystemRepository studySystemRepository = StudySystemRepository.getInstance();
    private final CategoryRepository categoryRepository = CategoryRepository.getInstance();
    private final CardToBoxRepository cardToBoxRepository = CardToBoxRepository.getInstance();
    private final CardToCategoryRepository cardToCategoryRepository = CardToCategoryRepository.getInstance();
    private Locale locale = new Locale("German", "de");
    private int i;

    @BeforeAll
    public static void before(){
        ControllerThreadPool.getInstance().synchronizedTasks(true);
    }
    @BeforeEach
    void setup(){
        CardRepository.startTransaction();
        cardRepository.delete(cardRepository.getAll());
        CardRepository.commitTransaction();

        CategoryRepository.startTransaction();
        categoryRepository.delete(categoryRepository.getAll());
        CategoryRepository.commitTransaction();

        CardToCategoryRepository.startTransaction();
        cardToCategoryRepository.delete(cardToCategoryRepository.getAll());
        CardToCategoryRepository.commitTransaction();

        CardToBoxRepository.startTransaction();
        cardToBoxRepository.delete(cardToBoxRepository.getAll());
        CardToBoxRepository.commitTransaction();

        Locale.setCurrentLocale(locale);
        String filecontent = Toolbox.loadResourceAsString("locale/de_DE.UTF-8", getClass());

        i = 0;
        filecontent.lines().forEach((String line) -> {
            i++;
            if(line.replaceAll("\\s","").isEmpty() || line.charAt(0) == '#')
                return;

            String[] args = line.split("= ");
            if(args.length < 1)
                Output.error("Locale resource for language " + locale.getLanguage() + " is missing a definition at line " + i);
            String id = args[0].replaceAll("\\s","");
            String value = args[1];
            locale.setString(id, value);
        });

    }

    @Test
    public void cardCrudTest() {
        List<Card> cards = exampleCards();

        // CREATE
        for (final Card card : cards) {
            //System.out.println(card.toString());
            CardRepository.startTransaction();
            cardRepository.save(card);
            CardRepository.commitTransaction();
        }

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
        assertEquals(cards.size(), allReadCards.size(), "same length");
        assertTrue(allReadCards.containsAll(cards));
        allReadCards = new ArrayList<>();

        // DELETE
        CardRepository.startTransaction();
        final String uuid = changedCard.getUuid();
        Card card = cardRepository.getCardByUUID(uuid);
        assertNotNull(card);
        cardRepository.delete(card);
        assertThrows(NoResultException.class, () -> cardRepository.getCardByUUID(uuid));
        CardRepository.commitTransaction();

        // Refresh
        CardRepository.startTransaction();
        Card attachedCard = cardRepository.getAll().get(0);
        String savedQuestion = attachedCard.getQuestion();
        attachedCard.setQuestion("andere Frage");
        cardRepository.refresh(attachedCard);
        assertEquals(attachedCard.getQuestion(), savedQuestion);
        CardRepository.commitTransaction();
    }

    @Test
    public void CRUDwithoutTransaction() {
        Texture loadTex = new Texture();
        loadTex.loadFile("/path/to/image3.png", getClass());
        Card exampleCard = new ImageDescriptionCard("Bildbeschreibung 3?", new ImageDescriptionCardAnswer[]{
                new ImageDescriptionCardAnswer("Beschreibung 3", 0, 1)
        }, "Titel der Karte",  new byte[loadTex.getData().remaining()]);

        on(cardRepository).set("entityManager", null);
        assertThrows(IllegalStateException.class, cardRepository::getAll);
        assertThrows(IllegalStateException.class, cardRepository::countAll);
        assertThrows(IllegalStateException.class, () -> cardRepository.save(exampleCard));
        assertThrows(IllegalStateException.class, () -> cardRepository.save(exampleCards()));
        exampleCard.setQuestion("neue Frage...");
        assertThrows(IllegalStateException.class, () -> cardRepository.update(exampleCard));
        assertThrows(IllegalStateException.class, () -> cardRepository.delete(exampleCard));
        assertThrows(IllegalStateException.class, () -> cardRepository.delete(exampleCards()));
        assertThrows(IllegalStateException.class, () -> cardRepository.refresh(exampleCard));
    }

    @Test
    public void accessWithoutTransaction() {
        on(cardRepository).set("entityManager", null);
        assertThrows(IllegalStateException.class, cardRepository::getAll);
        on(cardRepository).set("entityManager", PersistenceManager.emFactory.createEntityManager());
        assertThrows(IllegalStateException.class, cardRepository::getAll);
        on(cardRepository).set("entityManager", null);
    }

    @Test
    public void commitWithoutTransaction() {
        on(cardRepository).set("entityManager", null);
        assertThrows(IllegalStateException.class, CardRepository::commitTransaction);
        on(cardRepository).set("entityManager", PersistenceManager.emFactory.createEntityManager());
        assertThrows(IllegalStateException.class, CardRepository::commitTransaction);
        on(cardRepository).set("entityManager", null);
    }

    @Test
    public void rollbackWithoutTransaction() {
        on(cardRepository).set("entityManager", null);
        assertThrows(IllegalStateException.class, CardRepository::rollbackTransaction);
        //on(cardRepository).set("entityManager", PersistenceManager.emFactory.createEntityManager());
        //assertThrows(IllegalStateException.class, CardRepository::rollbackTransaction);
        //on(cardRepository).set("entityManager", null);
    }

    @Test
    public void isTransactionActive() {
        on(cardRepository).set("entityManager", null);
        assertFalse(CardRepository.isTransactionActive());
        on(cardRepository).set("entityManager",PersistenceManager.emFactory.createEntityManager());
        assertFalse(CardRepository.isTransactionActive());
        on(cardRepository).set("entityManager", null);
        //on(cardRepository).set("entityManager",PersistenceManager.emFactory.createEntityManager().getTransaction());
        //assertFalse(CardRepository.isTransactionActive());
        //on(cardRepository).set("entityManager",PersistenceManager.emFactory.createEntityManager().getTransaction().begin());
        //assertTrue(CardRepository.isTransactionActive());
    }

    @Test
    public void baseRepositoryGetter() {
        assertNull(BaseRepository.getEntityManager());
        assertThrows(NullPointerException.class, () -> cardRepository.findCardsByTag("test"));
        assertNull(BaseRepository.getCriteriaBuilder());

        BaseRepository.startTransaction();
        assertNotNull(BaseRepository.getEntityManager());
        assertNotNull(BaseRepository.getCriteriaBuilder());
        BaseRepository.commitTransaction();
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
        assertEquals(categories.size(), allReadCategories.size(), "same length");
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
        assertEquals(categories.size(), allReadCategories.size(), "same length");
        assertTrue(allReadCategories.containsAll(categories));
        allReadCategories = new ArrayList<>();

        // DELETE
        CategoryRepository.startTransaction();
        final String uuid = changedCategory.getUuid();
        var cat = categoryRepository.findByUUID(uuid);
        assertNotNull(cat, "changed category not found");
        categoryRepository.delete(cat);
        assertThrows(NoResultException.class, () -> categoryRepository.findByUUID(uuid));
        CategoryRepository.commitTransaction();
    }


    @Test
    public void cardToCategoryCrudTest() {

        // Daten
        Card cardA = new MultipleChoiceCard("Multi 1", new String[]{"Antwort A", "Antwort B", "Antwort C", "Antwort D"}, new int[]{0,3}, "Titel 1");
        Card cardB = new TextCard("Textfrage", "Antwort", "Titel");
        Category categoryA = new Category("Kategorie A");
        Category categoryB = new Category("Kategorie B");
        CardToCategory aa = new CardToCategory(cardA, categoryA);
        CardToCategory ab = new CardToCategory(cardA, categoryB);
        CardToCategory bb = new CardToCategory(cardB, categoryB);
        List<CardToCategory> allC2Cs = new ArrayList<>();
        Collections.addAll(allC2Cs, aa,ab,bb);

        // Create
        List<CardToCategory> persistedC2Cs = new ArrayList<>();
        CardToCategoryRepository.startTransaction();
        assertEquals(0, cardToCategoryRepository.countAll());
        for (CardToCategory c2c : allC2Cs) {
            persistedC2Cs.add(cardToCategoryRepository.save(c2c));
        }
        assertEquals(allC2Cs.size(), cardToCategoryRepository.countAll());
        CardToCategoryRepository.commitTransaction();

        // Read
        CardToCategoryRepository.startTransaction();
        List<CardToCategory> readC2Cs = cardToCategoryRepository.getAll();
        CardToCategoryRepository.commitTransaction();
        assertEquals(allC2Cs.size(), readC2Cs.size());
        //assertTrue(readC2Cs.containsAll(persistedC2Cs));

        // Update (CardToCategory hat keine setter. Card und Category sind final)

        // Delete
        CardToCategory deletedC2C = persistedC2Cs.get(0);
        CardToCategoryRepository.startTransaction();
        deletedC2C = cardToCategoryRepository.getSpecific(deletedC2C.getCard(), deletedC2C.getCategory());
        cardToCategoryRepository.delete(deletedC2C);
        //assertEquals(allC2Cs.size()-1, cardToCategoryRepository.countAll());
        CardToCategoryRepository.commitTransaction();

    }

    @Test
    public void studySystemCrudTest() {
        List<StudySystem> studySystems = exampleStudySystems();

        // CREATE
        StudySystemRepository.startTransaction();
        for (final StudySystem studySystem : studySystems) {
            studySystemRepository.save(studySystem);
        }
        StudySystemRepository.commitTransaction();

        // READ
        List<StudySystem> allReadStudySystems = new ArrayList<>();
        StudySystemRepository.startTransaction();
        for (final StudySystem studySystem : studySystems) {
            final StudySystem readStudySystem = studySystemRepository.getStudySystemByUUID(studySystem.getUuid());
            assertEquals(studySystem, readStudySystem);
            allReadStudySystems.add(readStudySystem);
        }
        StudySystemRepository.commitTransaction();
        assertEquals(studySystems.size(), allReadStudySystems.size(), "same length");
        assertTrue(allReadStudySystems.containsAll(studySystems));
        allReadStudySystems = new ArrayList<>();

        // UPDATE
        StudySystem changedStudySystem = studySystems.get(0);
        changedStudySystem.setName("Neuer Name");
        studySystems.set(0, changedStudySystem);
        StudySystemRepository.startTransaction();
        studySystemRepository.update(changedStudySystem);
        for (final StudySystem studySystem : studySystems) {
            final StudySystem readStudySystem = studySystemRepository.getStudySystemByUUID(studySystem.getUuid());
            assertEquals(studySystem, readStudySystem);
            allReadStudySystems.add(readStudySystem);
        }

        StudySystemRepository.commitTransaction();
        assertEquals(studySystems.size(), allReadStudySystems.size(), "same length");
        assertTrue(allReadStudySystems.containsAll(studySystems));
        allReadStudySystems = new ArrayList<>();
        
        // DELETE
        StudySystemRepository.startTransaction();
        final String uuid = changedStudySystem.getUuid();
        var ss = studySystemRepository.getStudySystemByUUID(uuid);
        assertNotNull(ss, "changed study system not found");
        studySystemRepository.delete(ss);
        assertThrows(NoResultException.class, () -> studySystemRepository.getStudySystemByUUID(uuid));
        StudySystemRepository.commitTransaction();


    }

    @Test
    public void studySystemBoxCrudTest() {
        
    }

    @Test
    public void cardToBoxCrudTest() {
    
        // Daten
        Card cardA = new MultipleChoiceCard("Multi 1", new String[]{"Antwort A", "Antwort B", "Antwort C", "Antwort D"}, new int[]{0,3}, "Titel 1");
        Card cardB = new TextCard("Textfrage", "Antwort", "Titel");
        StudySystemBox boxA = new StudySystemBox();
        StudySystemBox boxB = new StudySystemBox();
        BoxToCard aa = new BoxToCard(cardA, boxA);
        BoxToCard ab = new BoxToCard(cardA, boxB);
        BoxToCard bb = new BoxToCard(cardB, boxB);
        List<BoxToCard> allB2Cs = new ArrayList<>();
        Collections.addAll(allB2Cs, aa,ab,bb);

        // Create
        List<BoxToCard> persistedB2Cs = new ArrayList<>();
        CardToBoxRepository.startTransaction();
        assertEquals(0, cardToBoxRepository.countAll());
        for (BoxToCard b2c : allB2Cs) {
            persistedB2Cs.add(cardToBoxRepository.save(b2c));
        }assertEquals(allB2Cs.size(), cardToBoxRepository.countAll());
        CardToBoxRepository.commitTransaction();

        // Read
        CardToBoxRepository.startTransaction();
        List<BoxToCard> readB2Cs = cardToBoxRepository.getAll();
        CardToBoxRepository.commitTransaction();
        assertEquals(allB2Cs.size(), readB2Cs.size());
        //assertTrue(readB2Cs.containsAll(persistedB2Cs));

        // Update (CardToBox hat keine setter. Card und Box sind final)

        // Delete
        BoxToCard deletedB2C = persistedB2Cs.get(0);
        CardToBoxRepository.startTransaction();
        //deletedB2C = cardToBoxRepository.getSpecific(deletedB2C.getCard(), deletedB2C.getStudySystemBox());
        cardToBoxRepository.delete(deletedB2C);
        //assertEquals(allB2Cs.size()-1, cardToBoxRepository.countAll());
        CardToBoxRepository.commitTransaction();


        

        }



    @Test
    public void saveAndDeleteList() {
        CategoryRepository.startTransaction();

        assertDoesNotThrow(() -> {
            List<Category> savedCategories;
            savedCategories = categoryRepository.save(exampleCategories());
            List<Category> readCategories;
            readCategories = categoryRepository.getAll();
            assertTrue(readCategories.containsAll(savedCategories));
        });
        CategoryRepository.commitTransaction();
    }

    /*
     * Hilfsfunktionen um einen Test-Datensatz an Instanzen zu haben
     */
    public List<Card> exampleCards() {
        List<Card> exampleCards = new ArrayList<>();
        Texture images = new Texture();
        images.loadFile("/path/to/image1.png", getClass());
        byte[] img1 = new byte[images.getData().remaining()];
        images.loadFile("/path/to/image2.png", getClass());
        byte[] img2 = new byte[images.getData().remaining()];
        images.loadFile("/path/to/image3.png", getClass());
        byte[] img3 = new byte[images.getData().remaining()];
        images.loadFile("/path/to/image4.png", getClass());
        byte[] img4 = new byte[images.getData().remaining()];
        images.loadFile("/path/to/image5.png", getClass());
        byte[] img5 = new byte[images.getData().remaining()];
        Collections.addAll(exampleCards,
                new AudioCard(new byte[]{4}, "Audio 1", "Frage 1", "Antwort 1", false),
                new AudioCard(new byte[]{10}, "Audio 2", "Frage 2", "Antwort 2", true),
                new AudioCard(new byte[]{100}, "Audio 3", "Frage 3", "Antwort 3", false),
                new AudioCard(new byte[]{0}, "Audio 4", "Frage 4", "Antwort 4", true),
                new AudioCard(new byte[]{1}, "Audio 5", "Frage 5", "Antwort 5", false),
                new ImageDescriptionCard("Bildbeschreibung 1?", new ImageDescriptionCardAnswer[]{}, "Titel der Karte", img1),
                new ImageDescriptionCard("Bildbeschreibung 2?", new ImageDescriptionCardAnswer[]{}, "Titel der Karte", img2),
                new ImageDescriptionCard("Bildbeschreibung 3?", new ImageDescriptionCardAnswer[]{
                        new ImageDescriptionCardAnswer("Beschreibung 3", 0, 1)
                }, "Titel der Karte", img3),
                new ImageDescriptionCard("Bildbeschreibung 4?", new ImageDescriptionCardAnswer[]{
                        new ImageDescriptionCardAnswer("Beschreibung 4", 1, 2),
                        new ImageDescriptionCardAnswer("andere Beschreibung", 6, 1)
                }, "Titel der Karte", img4),
                new ImageDescriptionCard("Bildbeschreibung 5?", new ImageDescriptionCardAnswer[]{
                        new ImageDescriptionCardAnswer("Beschreibung", 1, 3)
                }, "Titel der Karte", img5),
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




    private List<StudySystem> exampleStudySystems() {
        List<StudySystem> exampleStudySystems = new ArrayList<>();
        Collections.addAll(exampleStudySystems,
                new LeitnerSystem("Leitner 1", StudySystem.CardOrder.RANDOM),
                new LeitnerSystem("Leitner 2", StudySystem.CardOrder.ALPHABETICAL),
                new LeitnerSystem("Leitner 3", StudySystem.CardOrder.REVERSED_ALPHABETICAL),
                new LeitnerSystem("Leitner 4", StudySystem.CardOrder.RANDOM),
                new LeitnerSystem("Leitner 5", StudySystem.CardOrder.ALPHABETICAL),
                new TimingSystem("Timing 1", StudySystem.CardOrder.RANDOM, 10),
                new TimingSystem("Timing 2", StudySystem.CardOrder.ALPHABETICAL, 20),
                new TimingSystem("Timing 3", StudySystem.CardOrder.REVERSED_ALPHABETICAL, 1),
                new TimingSystem("Timing 4", StudySystem.CardOrder.RANDOM, 100),
                new TimingSystem("Timing 5", StudySystem.CardOrder.ALPHABETICAL, 5),
                new VoteSystem("Vote 1", StudySystem.CardOrder.RANDOM),
                new VoteSystem("Vote 2", StudySystem.CardOrder.ALPHABETICAL),
                new VoteSystem("Vote 3", StudySystem.CardOrder.REVERSED_ALPHABETICAL),
                new VoteSystem("Vote 4", StudySystem.CardOrder.RANDOM),
                new VoteSystem("Vote 5", StudySystem.CardOrder.ALPHABETICAL)
        );

        return exampleStudySystems;
    }

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

//    public List<StudySystemBox> exampleBoxes() {
//        List<StudySystemBox> exampleBoxes = new ArrayList<>();
//        Collections.addAll(exampleBoxes,
//                new StudySystemBox(1),
//                new StudySystemBox("Box 2"),
//                new StudySystemBox("Box 3"),
//                new StudySystemBox("Box 4"),
//                new StudySystemBox("Box 5")
//        );
//        return exampleBoxes;
//    }



}
