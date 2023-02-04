package com.swp.Logic.CategoryLogicTest;

import com.gumse.gui.Locale;
import com.gumse.tools.Output;
import com.gumse.tools.Toolbox;
import com.swp.Controller.ControllerThreadPool;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardOverview;
import com.swp.DataModel.CardToCategory;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.DataModel.CardTypes.TrueFalseCard;
import com.swp.DataModel.Category;
import com.swp.DataModel.Language.German;
import com.swp.DataModel.StudySystem.BoxToCard;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.DataModel.StudySystem.TimingSystem;
import com.swp.GUI.Extras.ListOrder;
import com.swp.Logic.CardLogic;
import com.swp.Logic.CategoryLogic;
import com.swp.Persistence.CardRepository;
import com.swp.Persistence.CardToCategoryRepository;
import com.swp.Persistence.CategoryRepository;
import com.swp.Persistence.PersistenceManager;
import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.joor.Reflect.on;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

public class FilterForCategoryTest {

    private CardRepository cardRepMock;
    private CategoryRepository categoryRepMock;
    private CardToCategoryRepository cardToCategoryMock;
    private CategoryLogic categoryLogic = CategoryLogic.getInstance();


    /**
     * BeforeAll wird synchronizedTasks aufgerufen und die PU initialisiert für die Tests,
     * sowie die Language Variable gesetzt.
     */
    @BeforeAll
    public static void before() {
        PersistenceManager.init("KarteikartenDBTest");
        German.getInstance().activate();
        ControllerThreadPool.getInstance().synchronizedTasks(true);
    }

    /**
     * Before-Each Tests Methode.
     * Die Repos werden gemockt und in der Logik als gemockt gesetzt.
     */

    @BeforeEach
    public void beforeEach() {
        cardRepMock = mock(CardRepository.class);
        categoryRepMock = mock(CategoryRepository.class);
        cardToCategoryMock = mock(CardToCategoryRepository.class);
        on(categoryLogic).set("cardRepository", cardRepMock);
        on(categoryLogic).set("categoryRepository", categoryRepMock);
        on(categoryLogic).set("cardToCategoryRepository", cardToCategoryMock);
    }

    /**
     * Testet die Exception wenn ein null String übergeben wird
     * Methode: getCardsInCategory null
     */
    @Test
    public void testExceptionIfCategoryNameNull() {
        final String expected = "nonnull";
        final IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> categoryLogic.getCardsInCategory((String) null));
        assertEquals(expected, exception.getMessage());
    }

    /**
     * Testet die Exception wenn ein leerer String übergeben wird
     * Methode : getCardsInCategory
     */
    @Test
    public void testExceptionIfCategoryNameEmpty() {
        final String expected = "nonempty";
        final IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> categoryLogic.getCardsInCategory(""));
        assertEquals(expected, exception.getMessage());
    }

    /**
     * Testet die allgemeine Übergabe, wenn die Methode getCardsInCategory aufgerufen wird,
     */
    @Test
    public void testListOfCardsForCategory() {
        final List<CardOverview> expected = Arrays.asList(new CardOverview(), new CardOverview());
        when(cardRepMock.getCardsByCategory(anyString())).thenReturn(expected);
        List<CardOverview> actual = categoryLogic.getCardsInCategory("test");
        assertEquals(expected, actual);
    }

    /**
     * Testet die Exception wenn ein null String übergeben wird
     * Methode: getCardsInCategory null Order
     */
    @Test
    public void testExceptionIfCategoryOrderNameNull() {
        final String expected = "nonnull";
        final IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> categoryLogic.getCardsInCategory((String) null, ListOrder.Order.DATE, false));
        assertEquals(expected, exception.getMessage());
    }

    /**
     * Testet die Exception wenn ein leerer String übergeben wird
     * Methode : getCardsInCategory Order
     */
    @Test
    public void testExceptionIfCategoryOrderNameEmpty() {
        final String expected = "nonempty";
        final IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> categoryLogic.getCardsInCategory("", ListOrder.Order.DATE, false));
        assertEquals(expected, exception.getMessage());
    }

    /**
     * Testet die Rückgabe einer Liste von Karten, Order by Name, reverse Order true
     * Methode: getCardsInCategory Order Alphabetical, ASC
     */
    @Test
    public void testListOfCardsForInCategoryAlphabeticalAsc() {
        final List<CardOverview> expected = Arrays.asList(new CardOverview(), new CardOverview());
        when(cardRepMock.getCardsByCategory("test", "co.titelToShow", "asc")).thenReturn(expected);
        List<CardOverview> actual = categoryLogic.getCardsInCategory("test", ListOrder.Order.ALPHABETICAL, false);
        assertEquals(expected, actual);
    }

    /**
     * Testet die Rückgabe einer Liste von Karten, Order by Name, reverse Order true
     * Methode: getCardsInCategory Order Alphabetical, DESC
     */
    @Test
    public void testListOfCardsForInCategoryAlphabeticalDesc() {
        final List<CardOverview> expected = Arrays.asList(new CardOverview(), new CardOverview());
        when(cardRepMock.getCardsByCategory("test", "co.titelToShow", "desc")).thenReturn(expected);
        List<CardOverview> actual = categoryLogic.getCardsInCategory("test", ListOrder.Order.ALPHABETICAL, true);
        assertEquals(expected, actual);
    }

    /**
     * Testet die Rückgabe einer Liste von Karten, Order by Name, reverse Order true
     * Methode: getCardsInCategory Order Date, ASC
     */
    @Test
    public void testListOfCardsForInCategoryDateAsc() {
        final List<CardOverview> expected = Arrays.asList(new CardOverview(), new CardOverview());
        when(cardRepMock.getCardsByCategory("test", "co.cardCreated", "asc")).thenReturn(expected);
        List<CardOverview> actual = categoryLogic.getCardsInCategory("test", ListOrder.Order.DATE, false);
        assertEquals(expected, actual);
    }

    /**
     * Testet die Rückgabe einer Liste von Karten, Order by Name, reverse Order true
     * Methode: getCardsInCategory Order Date, DESC
     */
    @Test
    public void testListOfCardsForInCategoryDateDesc() {
        final List<CardOverview> expected = Arrays.asList(new CardOverview(), new CardOverview());
        when(cardRepMock.getCardsByCategory("test", "co.cardCreated", "desc")).thenReturn(expected);
        List<CardOverview> actual = categoryLogic.getCardsInCategory("test", ListOrder.Order.DATE, true);
        assertEquals(expected, actual);
    }

    /**
     * Testet die Rückgabe einer Liste von Karten, Order by Name, reverse Order true
     * Methode: getCardsInCategory Order NumDecks, ASC
     */
    @Test
    public void testListOfCardsForInCategoryNumDecksAsc() {
        final List<CardOverview> expected = Arrays.asList(new CardOverview(), new CardOverview());
        when(cardRepMock.getCardsByCategory("test", "co.countDecks", "asc")).thenReturn(expected);
        List<CardOverview> actual = categoryLogic.getCardsInCategory("test", ListOrder.Order.NUM_DECKS, false);
        assertEquals(expected, actual);
    }

    /**
     * Testet die Rückgabe einer Liste von Karten, Order by Name, reverse Order true
     * Methode: getCardsInCategory Order NumDecks, DESC
     */
    @Test
    public void testListOfCardsForInCategoryNumDecksDesc() {
        final List<CardOverview> expected = Arrays.asList(new CardOverview(), new CardOverview());
        when(cardRepMock.getCardsByCategory("test", "co.countDecks", "desc")).thenReturn(expected);
        List<CardOverview> actual = categoryLogic.getCardsInCategory("test", ListOrder.Order.NUM_DECKS, true);
        assertEquals(expected, actual);
    }

    /**
     * Testet die Exception wenn null übergeben wird
     * Methode: getCategoriesBySearchterms
     */
    @Test
    public void testExceptionIfSearchTermNull(){
        final String expected = "nonnull";
        final IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, ()->categoryLogic.getCategoriesBySearchterms(null));
        assertEquals(expected,exception.getMessage());
    }

    /**
     * Testet die Exception wenn ein leerer String übergeben wird
     * Methode: getCategoriesBySearchterms
     */
    @Test
    public void testExceptionIfSearchTermsEmpty(){
        final String expected = "nonempty";
        final IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, ()->categoryLogic.getCategoriesBySearchterms(""));
        assertEquals(expected,exception.getMessage());
    }

    /**
     * Testet die Rückgabe einer Liste von Karten
     * Methode : getCategoriesBySearchterms 
     */
    @Test
    public void testListOfCardsForSearchterms(){
        final List<Category> expected = Arrays.asList(new Category(), new Category());
        when(categoryRepMock.findCategoriesContaining(anyString())).thenReturn(expected);
        List<Category> actual = categoryLogic.getCategoriesBySearchterms("test");
        assertEquals(expected,actual);
    }

    /**
     * Testet die removeCardsFromCategory null Category Exception
     */
    @Test
    public void removeCardsFromCategoryTestNull(){
        Category category = new Category();
        List<CardOverview> cardsviews = new ArrayList<>();
        Card card = null;
        List<Card> cards = Arrays.asList(card);
        when(cardRepMock.getAllCardsForCardOverview(cardsviews)).thenReturn(cards);
        Exception exception = assertThrows(IllegalStateException.class,() -> {
            categoryLogic.removeCardsFromCategory(cardsviews,category);
        });
        String expected = exception.getMessage();
        assertEquals(expected,"cardnullerror");
    }

    /**
     * Testet die removeCardsFromCategory null Category Exception
     */
    @Test
    public void removeCardsFromCategoryNullTest(){
        Category category = null;
        List<CardOverview> cardsviews = new ArrayList<>();
        Exception exception = assertThrows(IllegalStateException.class,() -> {
            categoryLogic.removeCardsFromCategory(cardsviews,category);
        });
        String expected = exception.getMessage();
        assertEquals(expected,"categorynullerror");
    }

    /**
     * Testet das Löschen einer CardToCategory Instanz
     */
    @Test
    public void removeCardsFromCategoryTest(){
        Category category = new Category();
        List<CardOverview> cardsviews = new ArrayList<>();
        Card card = new TrueFalseCard();
        List<Card> cards = Arrays.asList(card);
        CardToCategory cardToCategory = new CardToCategory();
        when(cardToCategoryMock.getSpecific(card,category)).thenReturn(cardToCategory);
        when(cardRepMock.getAllCardsForCardOverview(cardsviews)).thenReturn(cards);
        categoryLogic.removeCardsFromCategory(cardsviews,category);
        verify(cardToCategoryMock,times(1)).delete(cardToCategory);
    }

}