package com.swp.Logic.CardLogicTest;


import com.swp.Controller.ControllerThreadPool;
import com.swp.DataModel.CardOverview;
import com.swp.DataModel.Language.German;
import com.swp.GUI.Extras.ListOrder;
import com.swp.Logic.CardLogic;
import com.swp.Persistence.CardRepository;
import com.swp.Persistence.PersistenceManager;
import com.swp.Persistence.TagRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.joor.Reflect.on;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

/**
 * Testklasse, um die Filterfunktionen in CardLogic zu testen sowie weitere Funktionen der Logik.
 *
 * @author Nadja Cordes
 *
 */

public class FilterSearchTermsLogicTagTest {

    /**
     * Zu testende Logik
     */
    private final CardLogic cardLogic = CardLogic.getInstance();
    /**
     * Gemockte Repos
     */
    private CardRepository cardRepMock;
    private TagRepository tagRepMock;
    
    /**
     * BeforeAll wird synchronizedTasks aufgerufen und die PU initialisiert für die Tests,
     * sowie die Language Variable gesetzt.
     */
    @BeforeAll
    public static void before()
    {
        PersistenceManager.init("KarteikartenDBTest");
        German.getInstance().activate();
        ControllerThreadPool.getInstance().synchronizedTasks(true);
    }
    /**
     * Before-Each Tests Methode.
     * Die Repos werden gemockt und in der Logik als gemockt gesetzt.
     */
    @BeforeEach
    public void beforeEach(){
        cardRepMock = mock(CardRepository.class);
        tagRepMock = mock(TagRepository.class);
        on(cardLogic).set("cardRepository",cardRepMock);

    }

    /**
     * Testet die Exception wenn ein null String übergeben wird
     * Methode: getCardsByTag
     */
    @Test
    public void testExceptionIfTagStringNull(){
        final String expected = "nonnull";
        final IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, ()->cardLogic.getCardsByTag(null));
                assertEquals(expected,exception.getMessage());
    }

    /**
     * Testet die Exception wenn ein null String übergeben wird
     * Methode: getCardsByTag Order
     */
    @Test
    public void testExceptionIfTagStringNullOrder(){
        final String expected = "nonnull";
        final IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, ()->cardLogic.getCardsByTag(null, ListOrder.Order.DATE,true));
        assertEquals(expected,exception.getMessage());
    }

    /**
     * Testet die Exception wenn ein leerer String übergeben wird
     * Methode : getCardsByTag
     */
    @Test
    public void testExceptionIfTagEmpty(){
        final String expected = "nonempty";
        final IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, ()->cardLogic.getCardsByTag(""));
        assertEquals(expected,exception.getMessage());
    }


    /**
     * Testet die Exception wenn ein leerer String übergeben wird
     * Methode : getCardsByTag Order
     */
    @Test
    public void testExceptionIfTagEmptyOrder(){
        final String expected = "nonempty";
        final IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, ()->cardLogic.getCardsByTag("", ListOrder.Order.DATE,true));
        assertEquals(expected,exception.getMessage());
    }

    /**
     * Testet die Rückgabe einer Liste von Karten
     * Methode : getCardsByTag 
     */
    @Test
    public void testListOfCardsForTags(){
        final List<CardOverview> expected = Arrays.asList(new CardOverview(), new CardOverview());
        when(cardRepMock.findCardsByTag("test")).thenReturn(expected);
        List<CardOverview> actual = cardLogic.getCardsByTag("test");
        assertEquals(expected,actual);
    }

    /**
     * Testet die Rückgabe einer Liste von Karten, Order by Name, reverse Order true
     * Methode : getCardsByTag Order Alphabetical, ASC
     */
    @Test
    public void testListOfCardsForTagsAlphabeticalAsc(){
        final List<CardOverview> expected = Arrays.asList(new CardOverview(), new CardOverview());
        when(cardRepMock.findCardsByTag("test", "LOWER(co.titelToShow)", "desc")).thenReturn(expected);
        List<CardOverview> actual = cardLogic.getCardsByTag("test", ListOrder.Order.ALPHABETICAL,true);
        assertEquals(expected,actual);
    }

    /**
     * Testet die Rückgabe einer Liste von Karten, Order by Name, reverse Order true
     * Methode: getCardsByTag Order Alphabetical, DESC
     */
    @Test
    public void testListOfCardsForTagsAlphabeticalDesc(){
        final List<CardOverview> expected = Arrays.asList(new CardOverview(), new CardOverview());
        when(cardRepMock.findCardsByTag("test", "LOWER(co.titelToShow)", "asc")).thenReturn(expected);
        List<CardOverview> actual = cardLogic.getCardsByTag("test", ListOrder.Order.ALPHABETICAL,false);
        assertEquals(expected,actual);
    }
    
    /**
     * Testet die Rückgabe einer Liste von Karten, Order by Name, reverse Order true
     * Methode : getCardsByTag Order Date, ASC
     */
    @Test
    public void testListOfCardsForTagsDateAsc(){
        final List<CardOverview> expected = Arrays.asList(new CardOverview(), new CardOverview());
        when(cardRepMock.findCardsByTag("test", "co.cardCreated", "asc")).thenReturn(expected);
        List<CardOverview> actual = cardLogic.getCardsByTag("test", ListOrder.Order.DATE,false);
        assertEquals(expected,actual);
    }

    /**
     * Testet die Rückgabe einer Liste von Karten, Order by Name, reverse Order true
     * Methode : getCardsByTag Order Date, DESC
     */
    @Test
    public void testListOfCardsForTagsDateDesc(){
        final List<CardOverview> expected = Arrays.asList(new CardOverview(), new CardOverview());
        when(cardRepMock.findCardsByTag("test", "co.cardCreated", "desc")).thenReturn(expected);
        List<CardOverview> actual = cardLogic.getCardsByTag("test", ListOrder.Order.DATE,true);
        assertEquals(expected,actual);
    }

    /**
     * Testet die Rückgabe einer Liste von Karten, Order by Name, reverse Order true
     * Methode : getCardsByTag Order NumDecks, ASC
     */
    @Test
    public void testListOfCardsForTagsNumDecksAsc(){
        final List<CardOverview> expected = Arrays.asList(new CardOverview(), new CardOverview());
        when(cardRepMock.findCardsByTag("test", "co.countDecks", "asc")).thenReturn(expected);
        List<CardOverview> actual = cardLogic.getCardsByTag("test", ListOrder.Order.NUM_DECKS,false);
        assertEquals(expected,actual);
    }

    /**
     * Testet die Rückgabe einer Liste von Karten, Order by Name, reverse Order true
     * Methode : getCardsByTag Order NumDecks, DESC
     */
    @Test
    public void testListOfCardsForTagsNumDecksDesc(){
        final List<CardOverview> expected = Arrays.asList(new CardOverview(), new CardOverview());
        when(cardRepMock.findCardsByTag("test", "co.countDecks", "desc")).thenReturn(expected);
        List<CardOverview> actual = cardLogic.getCardsByTag("test", ListOrder.Order.NUM_DECKS,true);
        assertEquals(expected,actual);
    }

    /**
     * Testet die Exception wenn null übergeben wird
     * Methode: getCardsBySearchterms
     */
    @Test
    public void testExceptionIfSearchTermNull(){
        final String expected = "nonnull";
        final IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, ()->cardLogic.getCardsBySearchterms(null));
        assertEquals(expected,exception.getMessage());
    }
    
    /**
     * Testet die Exception wenn ein leerer String übergeben wird
     * Methode: getCardsBySearchterms
     */
    @Test
    public void testExceptionIfSearchTermsEmpty(){
        final String expected = "nonempty";
        final IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, ()->cardLogic.getCardsBySearchterms(""));
        assertEquals(expected,exception.getMessage());
    }

    /**
     * Testet die Rückgabe einer Liste von Karten
     * Methode : getCardsBySearchterms 
     */
    @Test
    public void testListOfCardsForSearchterms(){
        final List<CardOverview> expected = Arrays.asList(new CardOverview(), new CardOverview());
        when(cardRepMock.findCardsContaining(anyString())).thenReturn(expected);
        List<CardOverview> actual = cardLogic.getCardsBySearchterms("test");
        assertEquals(expected,actual);
    }


    /**
     * Testet die Exception wenn ein null String übergeben wird
     * Methode : getCardsBySearchterms Order
     */
    @Test
    public void testExceptionIfSearchtermStringNullOrder(){
        final String expected = "nonnull";
        final IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, ()->cardLogic.getCardsBySearchterms(null, ListOrder.Order.DATE,true));
        assertEquals(expected,exception.getMessage());
    }

    /**
     * Testet die Exception wenn ein leerer String übergeben wird
     * Methode : getCardsBySearchterms Order
     */
    @Test
    public void testExceptionIfSearchtermEmptyOrder(){
        final String expected = "nonempty";
        final IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, ()->cardLogic.getCardsBySearchterms("", ListOrder.Order.DATE,true));
        assertEquals(expected,exception.getMessage());
    }
    

    /**
     * Testet die Rückgabe einer Liste von Karten, Order by Name, reverse Order true
     * Methode: getCardsBySearchterms Order Alphabetical, ASC
     */
    @Test
    public void testListOfCardsForSearchtermAlphabeticalAsc(){
        final List<CardOverview> expected = Arrays.asList(new CardOverview(), new CardOverview());
        when(cardRepMock.findCardsContaining("test", "LOWER(co.titelToShow)", "asc")).thenReturn(expected);
        List<CardOverview> actual = cardLogic.getCardsBySearchterms("test", ListOrder.Order.ALPHABETICAL,false);
        assertEquals(expected,actual);
    }

    /**
     * Testet die Rückgabe einer Liste von Karten, Order by Name, reverse Order true
     * Methode: getCardsBySearchterms Order Alphabetical, DESC
     */
    @Test
    public void testListOfCardsForSearchtermAlphabeticalDesc(){
        final List<CardOverview> expected = Arrays.asList(new CardOverview(), new CardOverview());
        when(cardRepMock.findCardsContaining("test", "LOWER(co.titelToShow)", "desc")).thenReturn(expected);
        List<CardOverview> actual = cardLogic.getCardsBySearchterms("test", ListOrder.Order.ALPHABETICAL,true);
        assertEquals(expected,actual);
    }

    /**
     * Testet die Rückgabe einer Liste von Karten, Order by Name, reverse Order true
     * Methode: getCardsBySearchterms Order Date, ASC
     */
    @Test
    public void testListOfCardsForSearchtermDateAsc(){
        final List<CardOverview> expected = Arrays.asList(new CardOverview(), new CardOverview());
        when(cardRepMock.findCardsContaining("test", "co.cardCreated", "asc")).thenReturn(expected);
        List<CardOverview> actual = cardLogic.getCardsBySearchterms("test", ListOrder.Order.DATE,false);
        assertEquals(expected,actual);
    }

    /**
     * Testet die Rückgabe einer Liste von Karten, Order by Name, reverse Order true
     * Methode: getCardsBySearchterms Order Date, DESC
     */
    @Test
    public void testListOfCardsForSearchtermDateDesc(){
        final List<CardOverview> expected = Arrays.asList(new CardOverview(), new CardOverview());
        when(cardRepMock.findCardsContaining("test", "co.cardCreated", "desc")).thenReturn(expected);
        List<CardOverview> actual = cardLogic.getCardsBySearchterms("test", ListOrder.Order.DATE,true);
        assertEquals(expected,actual);
    }

    /**
     * Testet die Rückgabe einer Liste von Karten, Order by Name, reverse Order true
     * Methode: getCardsBySearchterms Order NumDecks, ASC
     */
    @Test
    public void testListOfCardsForSearchtermNumDecksAsc(){
        final List<CardOverview> expected = Arrays.asList(new CardOverview(), new CardOverview());
        when(cardRepMock.findCardsContaining("test", "co.countDecks", "asc")).thenReturn(expected);
        List<CardOverview> actual = cardLogic.getCardsBySearchterms("test", ListOrder.Order.NUM_DECKS,false);
        assertEquals(expected,actual);
    }

    /**
     * Testet die Rückgabe einer Liste von Karten, Order by Name, reverse Order true
     * Methode: getCardsBySearchterms Order NumDecks, DESC
     */
    @Test
    public void testListOfCardsForSearchtermNumDecksDesc(){
        final List<CardOverview> expected = Arrays.asList(new CardOverview(), new CardOverview());
        when(cardRepMock.findCardsContaining("test", "co.countDecks", "desc")).thenReturn(expected);
        List<CardOverview> actual = cardLogic.getCardsBySearchterms("test", ListOrder.Order.NUM_DECKS,true);
        assertEquals(expected,actual);
    }

}
