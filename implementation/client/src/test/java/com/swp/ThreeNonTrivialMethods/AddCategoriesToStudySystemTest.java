package com.swp.ThreeNonTrivialMethods;

import com.gumse.gui.Locale;
import com.gumse.gui.Primitives.RenderGUI;
import com.swp.Controller.CategoryController;
import com.swp.Controller.DataCallback;
import com.swp.DataModel.CardOverview;
import com.swp.DataModel.Category;
import com.swp.GUI.Cards.CardOverviewPage;
import com.swp.GUI.Category.CategoryOverviewPage;
import com.swp.GUI.PageManager;
import com.swp.Logic.CategoryLogic;
import com.swp.Persistence.CategoryRepository;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
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
        vom Controller zurückgegeben.

    Controller:
        Im `CategoryController` wird `categoryLogic.getCardsInCategories()` mit den übergebenen Kategorien aufgerufen.
        Über den DataCallback werden entweder die Daten, oder entsprechende Fehlermeldungen an die GUI zurückgegeben.

    Logic:
        In `CategoryLogic` wird `getCardsInCategories()` pro Kategorie in der Liste `getCardsInCategory()` aufgerufen.
        Dabei wird jeweils `cardRepository.getCardsByCategory()` mit der Kategorie aufgerufen.
        Die Listen werden in einer langen Liste gesammelt und an den Controller zurückgegeben.
        (kann Karten mehrfach enthalten)

    Persistence:
        Im `CardRepository` wird durch den Aufruf von `getCardsByCategory()` die Named-Query
        "CardToCategory.allCardsOfCategory" mit der entsprechenden Kategorie aufgerufen.
        Die Query sieht wie folgt aus:
        "SELECT c FROM CardOverview c LEFT JOIN CardToCategory c2c ON c2c.card = c.uUUID WHERE c2c.category = :category"
     */

    @Mock
    Logger mockLogger = mock(Logger.class);
    DataCallback<CardOverview> mockDataCallback = mock(DataCallback.class);
    CategoryLogic mockCategoryLogic = mock(CategoryLogic.class);

    @InjectMocks
    CardOverviewPage cardOverviewPage;
    CategoryController categoryController;
    CategoryLogic categoryLogic;
    CategoryRepository categoryRepository;

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
        verify(mockDataCallback, times(1))
                .onFailure(any(String.class));
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
        verify(mockDataCallback, times(1))
                .onInfo(anyString());
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
