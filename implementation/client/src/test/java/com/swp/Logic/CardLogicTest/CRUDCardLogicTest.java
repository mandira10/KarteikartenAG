package com.swp.Logic.CardLogicTest;

import com.swp.Controller.ControllerThreadPool;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardOverview;
import com.swp.DataModel.CardToCategory;
import com.swp.DataModel.CardToTag;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.DataModel.CardTypes.TrueFalseCard;
import com.swp.DataModel.Language.German;
import com.swp.DataModel.StudySystem.BoxToCard;
import com.swp.GUI.Extras.ListOrder;
import com.swp.Logic.CardLogic;
import com.swp.Persistence.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import static org.joor.Reflect.on;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Klasse, die die normalen Funktionen für eine Karte testet, mithilfe von Komponententests.
 */
public class CRUDCardLogicTest {


    private CardRepository cardRepMock;
    private CardToCategoryRepository cardToCategoryRepMock;
    private CardToTagRepository cardToTagRepMock;
    private CardToBoxRepository cardToBoxRepMock;

    private final CardLogic cardLogic = CardLogic.getInstance();

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
        cardToTagRepMock = mock(CardToTagRepository.class);
        cardToBoxRepMock = mock(CardToBoxRepository.class);
        cardToCategoryRepMock = mock(CardToCategoryRepository.class);
        on(cardLogic).set("cardRepository",cardRepMock);
    }

    /**
     * Testet die delete Funktion, wenn null übergangen wird
     */
    @Test
    public void testExceptionIfCardToDeleteIsNull() {
        final String expected = "cardnullerror";
        final IllegalStateException exception =
                assertThrows(IllegalStateException.class, () -> cardLogic.deleteCard(null));
        assertEquals(expected, exception.getMessage());
    }

    /**
     * Testet das Löschen einer einzelnen Karte
     */
    @Test
    public void testDeleteCardFunction(){
        on(cardLogic).set("cardToBoxRepository",cardToBoxRepMock);
        on(cardLogic).set("cardToTagRepository",cardToTagRepMock);
        on(cardLogic).set("cardToCategoryRepository",cardToCategoryRepMock);
        Card card1  = new TextCard("Testfrage","Testantwort","Testtitel");
        when(cardToBoxRepMock.getAllB2CForCard(card1)).thenReturn(new ArrayList<>());
        doNothing().when(cardToBoxRepMock).delete(any(BoxToCard.class));
        when(cardToTagRepMock.getAllC2TForCard(card1)).thenReturn(new ArrayList<>());
        doNothing().when(cardToTagRepMock).delete(any(CardToTag.class));
        when(cardToCategoryRepMock.getAllC2CForCard(card1)).thenReturn(new ArrayList<>());
        doNothing().when(cardToCategoryRepMock).delete(any(CardToCategory.class));
        doNothing().when(cardRepMock).delete(card1);
        cardLogic.deleteCard(card1);
    }

    /**
     * Testet das Löschen mehrerer Karten
     */
    @Test
    public void testDeleteFunctionForManyCards(){
        Card c1 = new TextCard();
        List<CardOverview> cards = Arrays.asList(new CardOverview(UUID.randomUUID().toString()),new CardOverview(UUID.randomUUID().toString()));
        when(cardRepMock.getCardByUUID(any(String.class))).thenReturn(c1);
        on(cardLogic).set("cardToBoxRepository",cardToBoxRepMock);
        on(cardLogic).set("cardToTagRepository",cardToTagRepMock);
        on(cardLogic).set("cardToCategoryRepository",cardToCategoryRepMock);
        Card card1  = new TextCard("Testfrage","Testantwort","Testtitel");
        when(cardToBoxRepMock.getAllB2CForCard(card1)).thenReturn(new ArrayList<>());
        doNothing().when(cardToBoxRepMock).delete(any(BoxToCard.class));
        when(cardToTagRepMock.getAllC2TForCard(card1)).thenReturn(new ArrayList<>());
        doNothing().when(cardToTagRepMock).delete(any(CardToTag.class));
        when(cardToCategoryRepMock.getAllC2CForCard(card1)).thenReturn(new ArrayList<>());
        doNothing().when(cardToCategoryRepMock).delete(any(CardToCategory.class));
        doNothing().when(cardRepMock).delete(c1);
        cardLogic.deleteCards(cards);
    }

    /**
     * Testet die Update Methode, wenn null übergeben wird
     */
    @Test
    public void testExceptionIfCardToUpdateIsNull() {
        final String expected = "cardnullerror";
        final IllegalStateException exception =
                assertThrows(IllegalStateException.class, () -> cardLogic.updateCardData(null,true));
        assertEquals(expected, exception.getMessage());
    }

    /**
     * Testet die Update Methode von CardData
     */
    @Test
    public void testUpdateCardData(){
        Card card1  = new TextCard("Testfrage","Testantwort","Testtitel");
        when(cardRepMock.update(card1)).thenReturn(card1);
        when(cardRepMock.save(card1)).thenReturn(card1);
        cardLogic.updateCardData(card1,true);
        cardLogic.updateCardData(card1,false);
    }

    /**
     * Testet die Rückgabe einer Karte, wenn null übergeben wird
     * Methode: getCardByUUID
     */
    @Test
    public void testGetCardByUUIDNullException(){
        final String expected = "nonnull";
        final IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> cardLogic.getCardByUUID(null));
        assertEquals(expected, exception.getMessage());
    }

    /**
     * Testet die Rückgabe einer Karte, wenn der String empty ist
     * Methode: getCardByUUID
     */
    @Test
    public void testGetCardByUUIDEmptyException(){
        final String expected = "nonempty";
        final IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> cardLogic.getCardByUUID(""));
        assertEquals(expected, exception.getMessage());
    }

    /**
     * Testet die Rückgabe einer Karte
     * Methode: getCardByUUID
     */
    @Test
    public void testGetCardByUUID(){
        Card card = new TrueFalseCard();
        when(cardRepMock.getCardByUUID(any(String.class))).thenReturn(card);
        Card card1 = cardLogic.getCardByUUID("Test");
        assertEquals(card,card1);
    }

    /**
     * Testet die Rückgabe einer Liste von Karten, 
     * Methode: getCardOverview 
     */
    @Test
    public void testGetCardOverview(){
        final List<CardOverview> expected = Arrays.asList(new CardOverview(), new CardOverview());
        when(cardRepMock.getCardOverview(any(Integer.class),any(Integer.class))).thenReturn(expected);
        List<CardOverview> actual = cardLogic.getCardOverview(1,9);
        assertEquals(expected,actual);
    }

    /**
     * Testet die Rückgabe einer Liste von Karten, Order by Name, reverse Order true
     * Methode: getCardOverview Order Alphabetical, ASC
     */
    @Test
    public void testListOfCardsForSearchtermAlphabeticalAsc(){
        final List<CardOverview> expected = Arrays.asList(new CardOverview(), new CardOverview());
        when(cardRepMock.getCardOverview(1,3, "LOWER(c.titelToShow)", "asc")).thenReturn(expected);
        List<CardOverview> actual = cardLogic.getCardOverview(1,3, ListOrder.Order.ALPHABETICAL,false);
        assertEquals(expected,actual);
    }

    /**
     * Testet die Rückgabe einer Liste von Karten, Order by Name, reverse Order true
     * Methode: getCardOverview Order Alphabetical, DESC
     */
    @Test
    public void testListOfCardsForSearchtermAlphabeticalDesc(){
        final List<CardOverview> expected = Arrays.asList(new CardOverview(), new CardOverview());
        when(cardRepMock.getCardOverview(1,3, "LOWER(c.titelToShow)", "desc")).thenReturn(expected);
        List<CardOverview> actual = cardLogic.getCardOverview(1,3, ListOrder.Order.ALPHABETICAL,true);
        assertEquals(expected,actual);
    }

    /**
     * Testet die Rückgabe einer Liste von Karten, Order by Name, reverse Order true
     * Methode: getCardOverview Order Date, ASC
     */
    @Test
    public void testListOfCardsForSearchtermDateAsc(){
        final List<CardOverview> expected = Arrays.asList(new CardOverview(), new CardOverview());
        when(cardRepMock.getCardOverview(1,3, "c.cardCreated", "asc")).thenReturn(expected);
        List<CardOverview> actual = cardLogic.getCardOverview(1,3, ListOrder.Order.DATE,false);
        assertEquals(expected,actual);
    }

    /**
     * Testet die Rückgabe einer Liste von Karten, Order by Name, reverse Order true
     * Methode: getCardOverview Order Date, DESC
     */
    @Test
    public void testListOfCardsForSearchtermDateDesc(){
        final List<CardOverview> expected = Arrays.asList(new CardOverview(), new CardOverview());
        when(cardRepMock.getCardOverview(1,3, "c.cardCreated", "desc")).thenReturn(expected);
        List<CardOverview> actual = cardLogic.getCardOverview(1,3, ListOrder.Order.DATE,true);
        assertEquals(expected,actual);
    }

    /**
     * Testet die Rückgabe einer Liste von Karten, Order by Name, reverse Order true
     * Methode: getCardOverview Order NumDecks, ASC
     */
    @Test
    public void testListOfCardsForSearchtermNumDecksAsc(){
        final List<CardOverview> expected = Arrays.asList(new CardOverview(), new CardOverview());
        when(cardRepMock.getCardOverview(1,3, "c.countDecks", "asc")).thenReturn(expected);
        List<CardOverview> actual = cardLogic.getCardOverview(1,3, ListOrder.Order.NUM_DECKS,false);
        assertEquals(expected,actual);
    }

    /**
     * Testet die Rückgabe einer Liste von Karten, Order by Name, reverse Order true
     * Methode: getCardOverview Order NumDecks, DESC
     */
    @Test
    public void testListOfCardsForSearchtermNumDecksDesc(){
        final List<CardOverview> expected = Arrays.asList(new CardOverview(), new CardOverview());
        when(cardRepMock.getCardOverview(1,3, "c.countDecks", "desc")).thenReturn(expected);
        List<CardOverview> actual = cardLogic.getCardOverview(1,3, ListOrder.Order.NUM_DECKS,true);
        assertEquals(expected,actual);
    }




}
