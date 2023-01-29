package com.swp.ThreeNonTrivialMethods;

import com.gumse.gui.Locale;
import com.gumse.gui.Primitives.RenderGUI;
import com.swp.Controller.CardController;
import com.swp.Controller.CategoryController;
import com.swp.Controller.DataCallback;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardOverview;
import com.swp.DataModel.CardToCategory;
import com.swp.DataModel.CardTypes.MultipleChoiceCard;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.DataModel.CardTypes.TrueFalseCard;
import com.swp.DataModel.Category;
import com.swp.DataModel.StudySystem.BoxToCard;
import com.swp.DataModel.StudySystem.LeitnerSystem;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.DataModel.StudySystem.VoteSystem;
import com.swp.GUI.Cards.CardOverviewPage;
import com.swp.GUI.Category.CategoryOverviewPage;
import com.swp.GUI.PageManager;
import com.swp.Logic.CategoryLogic;
import com.swp.Persistence.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.slf4j.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.*;

import static org.joor.Reflect.on;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.*;

/**
 * In dieser Testklasse werden alle Methoden getestet, welche bei dem nicht-trivialen
 * Anwendungsfall `N26 Karteikasten mehrere Kategorien` involviert sind.
 * <p>
 * In der GUI können mehrere Kategorien ausgewählt und ihre Karten einem Karteikasten
 * zugefügt werden.
 */
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

    private Card card1, card2, card3;
    private StudySystem study1, study2;
    private BoxToCard boxToCard1, boxToCard2;
    private Category category1, category2, category3;
    private CardToCategory cardToCategory1, cardToCategory2;

    private CardRepository cardRepository;
    private StudySystemRepository studySystemRepository;
    private CardToBoxRepository cardToBoxRepository;
    private CategoryRepository categoryRepository;
    private CardToCategoryRepository cardToCategoryRepository;

    private CategoryOverviewPage categoryOverviewPage;
    private CategoryController categoryController;
    private CategoryLogic categoryLogic;


    @BeforeEach
    public void setup() {
        card1 = new TextCard("Frage 1", "Antwort 1", "Titel 1");
        card2 = new TextCard("Frage 2", "Antwort 2", "Titel 2");
        card3 = new TrueFalseCard("Frage 3", false, "Titel 3");

        study1 = new LeitnerSystem("Study 1", StudySystem.CardOrder.ALPHABETICAL);
        study2 = new VoteSystem("Study 2", StudySystem.CardOrder.ALPHABETICAL);

        boxToCard1 = new BoxToCard(card1, study1.getBoxes().get(0), 0);
        boxToCard2 = new BoxToCard(card2, study2.getBoxes().get(0), 0);

        category1 = new Category("Kategorie 1");
        category2 = new Category("Kategorie 2");
        category3 = new Category("Kategorie 3"); // enthält keine Karte

        cardToCategory1 = new CardToCategory(card1, category1);
        cardToCategory2 = new CardToCategory(card2, category2);

        cardRepository = CardRepository.getInstance();
        studySystemRepository = StudySystemRepository.getInstance();
        cardToBoxRepository = CardToBoxRepository.getInstance();
        categoryRepository = CategoryRepository.getInstance();
        cardToCategoryRepository = CardToCategoryRepository.getInstance();

        CardRepository.startTransaction();
        cardRepository.save(card1);
        cardRepository.save(card2);
        cardRepository.save(card3);
        CardRepository.commitTransaction();

        StudySystemRepository.startTransaction();
        studySystemRepository.save(study1);
        studySystemRepository.save(study2);
        StudySystemRepository.commitTransaction();

        CardToCategoryRepository.startTransaction();
        cardToBoxRepository.save(boxToCard1);
        cardToBoxRepository.save(boxToCard2);
        CardToCategoryRepository.commitTransaction();

        CategoryRepository.startTransaction();
        categoryRepository.save(category1);
        categoryRepository.save(category2);
        categoryRepository.save(category3);
        CategoryRepository.commitTransaction();

        CardToCategoryRepository.startTransaction();
        cardToCategoryRepository.save(cardToCategory1);
        cardToCategoryRepository.save(cardToCategory2);
        CardToCategoryRepository.commitTransaction();
    }

    @Test
    @Disabled
    public void addCategoryToStudySystem() {
        // Best-Case, keine Sonderfälle (doppelte Karten, Exceptions, etc.)
        categoryController = CategoryController.getInstance();

        List<Category> selectedCategories = new ArrayList<>();
        selectedCategories.add(category1);
        selectedCategories.add(category2);

        DataCallback<CardOverview> mockDataCallback = mock(DataCallback.class);

        assertDoesNotThrow(() -> categoryController.getCardsInCategories(selectedCategories,mockDataCallback));
        verify(mockDataCallback).callSuccess(argThat(new ArgumentMatcher<List<CardOverview>>() {
            @Override
            public boolean matches(List<CardOverview> overviews) {
                overviews.stream().map(o -> o.getUUUID()).allMatch(id -> {
                    return Objects.equals(card1.getUuid(), id) || Objects.equals(card2.getUuid(), id);
                });
                return false;
            }
        }));

        // Jetzt wäre man in der GUI in der Lernsystem-Auswahl Ansicht.
    }



    @Mock
    Logger mockLogger = mock(Logger.class);
    DataCallback<CardOverview> mockDataCallback = mock(DataCallback.class);
    CategoryLogic mockCategoryLogic = mock(CategoryLogic.class);

    @InjectMocks
    CardOverviewPage cardOverviewPage;
    //CategoryController categoryController;
    //CategoryLogic categoryLogic;
    //CategoryRepository categoryRepository;

    @Test
    @Disabled
    public void GUIaddToDeckEmptyList() {
        CategoryController mockCategoryController = mock(CategoryController.class);
        when(CategoryController.getInstance()).thenReturn(mockCategoryController); // `when()` muss mit einem Mock gemacht werden
        CategoryOverviewPage categoryOverviewPage = new CategoryOverviewPage();
        PageManager mockPageManager = mock(PageManager.class);
        on(categoryOverviewPage).set("PageManager", mockPageManager);

        // selected Categories
        List<Category> selectedCategories = new ArrayList<>();
        on(categoryOverviewPage).set("pCategoryList", selectedCategories);

        //categoryOverviewPage.onClick((RenderGUI gui) -> categoryOverviewPage.addToDeck());
        assertDoesNotThrow(categoryOverviewPage::addToDeck);
        verify(mockCategoryController, times(1))
                .getCardsInCategories(selectedCategories, argThat(Objects::nonNull));
        doNothing().when(mockCategoryController).getCardsInCategories(selectedCategories, any(DataCallback.class));
        //verify(mockPageManager, times(1))
        //        .viewPage(PageManager.PAGES.CATEGORY_OVERVIEW);
    }

    @Test
    @Disabled
    public void ControllerLogicException() throws Exception {
        categoryController = CategoryController.getInstance();

        List<Category> categories = new ArrayList<>();
        categories.add(new Category("Test"));

        // Exception von der Logic (aktuell kann sowas von der Logic nicht ausgelöst werden)
        /*
        when(mockCategoryLogic.getCardsInCategories(categories)).thenThrow(Exception.class);
        verify(mockLog, times(1))
                .error(contains("Beim Suchen nach Karten mit Kategorien ist ein Fehler"));
        verify(mockDataCallback, times(1))
                .onFailure(Locale.getCurrentLocale().getString("getcardsincategoryerror"));
        categoryController.getCardsInCategories(categories, mockDataCallback);
         */

        // IllegalStateException von der Logic
        when(mockCategoryLogic.getCardsInCategories(categories)).thenThrow(IllegalStateException.class);
        assertDoesNotThrow(() -> categoryController.getCardsInCategories(categories, mockDataCallback));
        verify(mockLogger, times(1))
                .error(contains("Der übergebene Wert war leer"));
        //verify(mockDataCallback, times(1))
        //        .onFailure(any(String.class));
    }

    @Test
    @Disabled
    public void ControllerEmptyList() {
        CategoryLogic mockCategoryLogic = mock(CategoryLogic.class);
        DataCallback<CardOverview> mockDataCallback = mock(DataCallback.class);
        Logger mockLog = mock(Logger.class);
        Locale mockLocale = mock(Locale.class);

        CategoryController categoryController = CategoryController.getInstance();
        on(categoryController).set("categoryLogic", mockCategoryLogic);

        List<Category> categories = new ArrayList<>();

        // Leere Liste von der Logic
        //when(mockLocale.getCurrentLocale()).thenReturn(new Locale("deutsch","de_DE")); // kann keine statischen Methoden mocken.
        when(mockCategoryLogic.getCardsInCategories(categories)).thenReturn(new ArrayList<>()); // NullPointerException, weil Locale.getCurrentLocale() null zurückgibt (für DataCallback.onInfo Text)

        categoryController.getCardsInCategories(categories, mockDataCallback);
        //verify(mockDataCallback, times(1))
        //        .onInfo(anyString());
    }

    @Test
    @Disabled
    public void ControllerList() {
    }

    @Test
    @Disabled
    public void ControllerDuplicateList() {
    }

    @Test
    @Disabled
    public void LogicEmptyList() {
    }

    @Test
    @Disabled
    public void LogicList() {
    }

    @Test
    @Disabled
    public void LogicRepoException() {
    }

    @Test
    @Disabled
    public void PersistenceEmptyList() {
    }

    @Test
    @Disabled
    public void PersistenceList() {
    }

    @Test
    @Disabled
    public void PersistenceInvalidList() {
    }

    @Test
    @Disabled
    public void PersistenceNoTransaction() {
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
