package com.swp.ThreeNonTrivialMethods;

import com.gumse.gui.Locale;
import com.gumse.tools.Output;
import com.gumse.tools.Toolbox;
import com.swp.Controller.*;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardOverview;
import com.swp.DataModel.CardToCategory;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.DataModel.CardTypes.TrueFalseCard;
import com.swp.DataModel.Category;
import com.swp.DataModel.Language.German;
import com.swp.DataModel.StudySystem.*;
import com.swp.GUI.Category.CategoryOverviewPage;
import com.swp.GUI.Decks.DeckSelectPage;
import com.swp.GUI.PageManager;
import com.swp.Logic.CategoryLogic;
import com.swp.Logic.StudySystemLogic;
import com.swp.Persistence.*;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static org.joor.Reflect.on;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * In dieser Testklasse werden alle Methoden getestet, welche bei dem nicht-trivialen
 * Anwendungsfall `N26 Karteikasten mehrere Kategorien` involviert sind.
 * <p>
 * In der GUI können mehrere Kategorien ausgewählt und ihre Karten einem Karteikasten
 * zugefügt werden.
 *
 * @Author Ole-Niklas Mahlstädt
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AddCategoriesToStudySystemTest {

    /*
    GUI:
        In `CategoryOverviewPage` wird `addtodeckbutton` ausgewählt und damit die Funktion `addToDeck()` aufgerufen.
        `addToDeck()` ruft `CategoryController.getInstance().getCardsInCategories()` mit der Liste von
        ausgewählten Kategorien auf. Die Karten-Übersichten werden über einen `DataCallback<CardOverview>`
        vom Controller zurückgegeben. Danach wechselt man in eine andere Ansicht, in der man das Lernsystem auswählt,
        zu dem man die Karten zufügen will (`DeckSelectPage`). Nach der Auswahl wird in `selectDeck()` die Funktion
        `addCardsToStudySystem()` vom `StudySystemController` aufgerufen.

    Controller:
        Im `CategoryController` wird `categoryLogic.getCardsInCategories()` mit den übergebenen Kategorien aufgerufen.
        Über den DataCallback werden entweder die Daten, oder entsprechende Fehlermeldungen an die GUI zurückgegeben.
        Dem `StudySystemController` wird eine Liste von Karten-Übersichten und ein Lernsystem übergeben.
        Dort werden die Daten an `StudySystemLogic.addCardsToDeck()` weitergegeben.

    Logic:
        In `CategoryLogic` wird `getCardsInCategories()` pro Kategorie in der Liste `getCardsInCategory()` aufgerufen.
        Dabei wird jeweils `cardRepository.getCardsByCategory()` mit der Kategorie aufgerufen.
        Die Listen werden in einer langen Liste gesammelt und an den Controller zurückgegeben.
        (kann Karten mehrfach enthalten)
        In `StudySystemLogic` sind `addCardsToDeck()` und `moveAllCardsForDeckToFirstBox()` involviert.

    Persistence:
        Im `CardRepository` wird durch den Aufruf von `getCardsByCategory()` die Named-Query
        "CardToCategory.allCardsOfCategory" mit der entsprechenden Kategorie aufgerufen.
        Die Query sieht wie folgt aus:
        "SELECT c FROM CardOverview c LEFT JOIN CardToCategory c2c ON c2c.card = c.uUUID WHERE c2c.category = :category"
        Für die Verbindung der Karten zum Lernsystem-Kasten und Updaten der Verbindung werden nur die BaseRepositoy
        Methoden verwendet.
     */
    @Mock
    Logger mockLogger = mock(Logger.class);

    private Card card1, card2, card3;
    private StudySystem study1, study2, study3;
    private BoxToCard boxToCard1, boxToCard2;
    private Category category1, category2, category3;
    private CardToCategory cardToCategory1, cardToCategory2;

    private CardRepository cardRepository;
    private StudySystemRepository studySystemRepository;
    private CardToBoxRepository cardToBoxRepository;
    private CategoryRepository categoryRepository;
    private CardToCategoryRepository cardToCategoryRepository;

    private CategoryOverviewPage categoryOverviewPage;
    @InjectMocks
    private CategoryController categoryController;
    @InjectMocks
    private CategoryLogic categoryLogic;

    private DeckSelectPage deckSelectPage;
    @InjectMocks
    private StudySystemController studySystemController;
    @InjectMocks
    private StudySystemLogic studySystemLogic;

    private final Locale locale = new Locale("German", "de");
    int i;

    @BeforeAll
    public static void beforeAll() {
        PersistenceManager.init("KarteikartenDBTest");
    }

    @BeforeEach
    public void setup() {
        Locale.setCurrentLocale(locale);
        String fileContent = Toolbox.loadResourceAsString("locale/de_DE.UTF-8", getClass());
        i = 0;
        fileContent.lines().forEach((String line) -> {
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

        card1 = new TextCard("Frage 1", "Antwort 1", "Titel 1");
        card2 = new TextCard("Frage 2", "Antwort 2", "Titel 2");
        card3 = new TrueFalseCard("Frage 3", false, "Titel 3");

        study1 = new LeitnerSystem("Study 1", StudySystem.CardOrder.ALPHABETICAL);
        study2 = new VoteSystem("Study 2", StudySystem.CardOrder.ALPHABETICAL,1);
        study3 = new TimingSystem("Study 3", StudySystem.CardOrder.RANDOM, 10);

        boxToCard1 = new BoxToCard(card1, study1.getBoxes().get(0));
        boxToCard2 = new BoxToCard(card2, study2.getBoxes().get(0));

        category1 = new Category("Kategorie 1");
        category2 = new Category("Kategorie 2");
        category3 = new Category("Kategorie 3"); // enthält keine Karte

        cardToCategory1 = new CardToCategory(card1, category1);
        cardToCategory2 = new CardToCategory(card2, category2);

        cardRepository = CardRepository.getInstance();
        studySystemRepository = StudySystemRepository.getInstance();
        cardToBoxRepository = CardToBoxRepository.getInstance();
        categoryRepository = CategoryRepository.getInstance();
        categoryController = CategoryController.getInstance();
        categoryLogic = CategoryLogic.getInstance();
        cardToCategoryRepository = CardToCategoryRepository.getInstance();
        studySystemController = StudySystemController.getInstance();
        studySystemLogic = StudySystemLogic.getInstance();

        // delete
        BaseRepository.startTransaction();
        cardToBoxRepository.delete(cardToBoxRepository.getAll());
        cardToCategoryRepository.delete(cardToCategoryRepository.getAll());
        cardRepository.delete(cardRepository.getAll());
        studySystemRepository.delete(studySystemRepository.getAll());
        categoryRepository.delete(categoryRepository.getAll());
        BaseRepository.commitTransaction();

        // create
        CardRepository.startTransaction();
        cardRepository.save(card1);
        cardRepository.save(card2);
        cardRepository.save(card3);
        CardRepository.commitTransaction();

        StudySystemRepository.startTransaction();
        studySystemRepository.save(study1);
        studySystemRepository.save(study2);
        studySystemRepository.save(study3);
        StudySystemRepository.commitTransaction();

        CategoryRepository.startTransaction();
        categoryRepository.save(category1);
        categoryRepository.save(category2);
        categoryRepository.save(category3);
        CategoryRepository.commitTransaction();

        CardToCategoryRepository.startTransaction();
        cardToBoxRepository.save(boxToCard1);
        cardToBoxRepository.save(boxToCard2);
        CardToCategoryRepository.commitTransaction();

        CardToCategoryRepository.startTransaction();
        cardToCategoryRepository.save(cardToCategory1);
        cardToCategoryRepository.save(cardToCategory2);
        CardToCategoryRepository.commitTransaction();
    }

    @BeforeAll
    public static void before()
    {
        PersistenceManager.init("KarteikartenDBTest");
        German.getInstance().activate();
        ControllerThreadPool.getInstance().synchronizedTasks(true);
    }


    @Test
    @Order(5)
    public void addCategoryToStudySystem() {
        // Best-Case, keine Sonderfälle (doppelte Karten, Exceptions, etc.)
        categoryController = CategoryController.getInstance();

        List<Category> selectedCategories = new ArrayList<>();
        selectedCategories.add(category1);
        selectedCategories.add(category2);
        List<CardOverview> cardsInCategories = new ArrayList<>();

        StudySystem selectedStudySystem = study3;

        DataCallback<CardOverview> mockDataCallback = mock(DataCallback.class);

        assertDoesNotThrow(() -> categoryController.getCardsInCategories(selectedCategories,mockDataCallback));
        /* verify(mockDataCallback).callSuccess(anyList().stream().forEach(o -> {
            o = (CardOverview) o;
            cardsInCategories.add((CardOverview) o);
            assertTrue(Objects.equals(((CardOverview) o).getUUUID(), card1.getUuid())
                    || Objects.equals(((CardOverview) o).getUUUID(), card2.getUuid()));
        })); */
        verify(mockDataCallback).callSuccess(argThat(new ArgumentMatcher<List<CardOverview>>() {
            @Override
            public boolean matches(List<CardOverview> overviews) {
                overviews.stream().forEach(o -> {
                    cardsInCategories.add(o);
                    assertTrue(Objects.equals(o.getUUUID(), card1.getUuid())
                            || Objects.equals(o.getUUUID(), card2.getUuid()));
                });
                return true;
            }
        }));

        // Jetzt wäre man in der GUI in der Lernsystem-Auswahl Ansicht.
        SingleDataCallback<String> mockCallback = mock(SingleDataCallback.class);
        assertDoesNotThrow(() -> studySystemController.addCardsToStudySystem(cardsInCategories, selectedStudySystem, mockCallback));
        //verify(mockCallback).callSuccess(anyString());
        for (CardOverview co : cardsInCategories) {
            // Prüfen, dass diese Verbindung zwischen Karten und Lern-Kasten existiert.
            // Wenn nicht, dann würde getSpecific eine `NoResultException` werfen.
            BaseRepository.startTransaction();
            assertDoesNotThrow(() -> cardToBoxRepository.getSpecific(cardRepository.getCardByUUID(co.getUUUID()), selectedStudySystem));
            BaseRepository.commitTransaction();
        }
    }

    @Test
    public void ControllerNoCardsInCategory() {
        List<Category> categories = new ArrayList<>();
        categories.add(category3);

        DataCallback<CardOverview> mockDataCallback = mock(DataCallback.class);
        assertDoesNotThrow(() -> categoryController.getCardsInCategories(categories, mockDataCallback));
        verify(mockDataCallback, times(1))
                .callInfo("Es gibt keine Karten für diese Kategorien.");
    }

    @Test
    public void ControllerEmptyList() {
        List<Category> categories = new ArrayList<>();
        categories.add(category1);
        categories.add(category2);

        DataCallback<CardOverview> mockDataCallback = mock(DataCallback.class);
        CategoryLogic mockCategoryLogic = mock(CategoryLogic.class);
        on(categoryController).set("categoryLogic", mockCategoryLogic);
        // Leere Liste von der Logic
        when(mockCategoryLogic.getCardsInCategories(categories)).thenReturn(new ArrayList<>());

        assertDoesNotThrow(() -> categoryController.getCardsInCategories(categories, mockDataCallback));
        //verify(mockDataCallback, times(1))
        //        .onInfo(anyString());

        SingleDataCallback<String> mockCallback = mock(SingleDataCallback.class);
        assertDoesNotThrow(() -> studySystemController.addCardsToStudySystem(new ArrayList<>(), study2, mockCallback));
        // prüfe, dass keine neue Karten-zu-Box Verbindung hergestellt wurde
        BaseRepository.startTransaction();
        assertEquals(2, cardToBoxRepository.countAll());
        BaseRepository.commitTransaction();
    }

    @Order(0)
    @Test
    public void ControllerDuplicateList() {
        List<Category> categories = new ArrayList<>();
        categories.add(category1);
        categories.add(category1);
        categories.add(category2);

        List<CardOverview> cardsInCategories = new ArrayList<>();

        DataCallback<CardOverview> mockDataCallback = mock(DataCallback.class);
        assertDoesNotThrow(() -> categoryController.getCardsInCategories(categories, mockDataCallback));
        verify(mockDataCallback).callSuccess(argThat(new ArgumentMatcher<List<CardOverview>>() {
            @Override
            public boolean matches(List<CardOverview> overviews) {
               assertEquals(3, overviews.size());
               assertEquals(2,overviews.stream().filter(c -> c.getTitelToShow().equals("Titel 1")).collect(Collectors.toSet()).size());
               assertEquals(1,overviews.stream().filter(c -> c.getTitelToShow().equals("Titel 2")).collect(Collectors.toSet()).size());
               cardsInCategories.addAll(overviews);
               return true;
            }
        }));


        SingleDataCallback<String> mockCallback = mock(SingleDataCallback.class);
        studySystemController.addCardsToStudySystem(cardsInCategories, study3, mockCallback);
        BaseRepository.startTransaction();
        assertEquals(4, cardToBoxRepository.countAll());
        BaseRepository.commitTransaction();
    }

    @Order(0)
    @Test
    public void LogicEmptyOrNullList() {
        assertThrows(IllegalStateException.class, () -> categoryLogic.getCardsInCategories(new ArrayList<>()));
        List<Category> categories = new ArrayList<>();
        categories.add(null);
        assertThrows(IllegalStateException.class, () -> categoryLogic.getCardsInCategories(categories));
        categories.remove(0);
        categories.add(category1);
        categories.add(null);
        assertThrows(IllegalStateException.class, () -> categoryLogic.getCardsInCategories(categories));
    }

    @Test
    public void LogicRepoException() {
        List<Category> categories = new ArrayList<>();
        categories.add(category1);
        categories.add(category2);

        CardRepository mockCardRepository = mock(CardRepository.class);
        on(categoryLogic).set("cardRepository", mockCardRepository);
        when(mockCardRepository.getCardsByCategory(category1)).thenThrow(new RuntimeException("gemockter Datenbankfehler"));
        assertThrows(RuntimeException.class, () -> categoryLogic.getCardsInCategories(categories));
    }

    @Test
    public void PersistenceCategory() {
        CardRepository.startTransaction();
        assertEquals(1, cardRepository.getCardsByCategory(category1).size());
        CardRepository.commitTransaction();
    }

    @Test
    public void PersistenceCategoryNoCards() {
        CardRepository.startTransaction();
        assertDoesNotThrow(() -> cardRepository.getCardsByCategory(category3));
        assertTrue(cardRepository.getCardsByCategory(category3).isEmpty());
        CardRepository.commitTransaction();
    }

    @Test
    public void PersistenceInvalidCategory() {
        CardRepository.startTransaction();
        assertThrows(NullPointerException.class, () -> cardRepository.getCardsByCategory((Category) null));
        CardRepository.commitTransaction();
    }

    @Test
    public void PersistenceNoTransaction() {
        assertThrows(NullPointerException.class, () -> cardRepository.getCardsByCategory("keine Transaktion!"));
        assertThrows(NullPointerException.class, () -> studySystemRepository.addCardToBox(null));
    }

    /**
     * Um final attribute in zu testenden Klassen mit gemockten Objekten neu zu setzen.
     * @author polygenelubricants [<a href="https://stackoverflow.com/a/3301720">stackoverflow</a>]
     * @param field    das zu ändernde Attribut in einer Klasse.
     * @param newValue der neue Wert für dieses Attribut.
     * @throws Exception Fehler beim Ermitteln oder setzen von dem Attribut auftreten.
     */
    private static void setFinalStatic(Field field, Object newValue) throws Exception {
        field.setAccessible(true);

        Field modifiersField = Field.class.getDeclaredField("modifiers");
        modifiersField.setAccessible(true);
        modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);

        field.set(null, newValue);
    }
}
