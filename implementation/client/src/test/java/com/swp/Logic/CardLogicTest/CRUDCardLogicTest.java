package com.swp.Logic.CardLogicTest;

import com.gumse.gui.Locale;
import com.gumse.tools.Output;
import com.gumse.tools.Toolbox;
import com.swp.Controller.ControllerThreadPool;
import com.swp.DataModel.*;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.DataModel.CardTypes.TrueFalseCard;
import com.swp.DataModel.Language.German;
import com.swp.DataModel.StudySystem.BoxToCard;
import com.swp.Logic.CardLogic;
import com.swp.Persistence.*;
import jakarta.transaction.Transactional;
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
 * Klasse, die die normalen Funktionen für eine Karte testet.
 */
public class CRUDCardLogicTest {


    private CardRepository cardRepMock;
    private CardToCategoryRepository cardToCategoryRepMock;
    private CardToTagRepository cardToTagRepMock;
    private CardToBoxRepository cardToBoxRepMock;

    private CardLogic cardLogic = CardLogic.getInstance();

    private Locale locale = new Locale("German", "de");
    private int i;
    @BeforeAll
    public static void before()
    {
        PersistenceManager.init("KarteikartenDBTest");
        German.getInstance().activate();
        ControllerThreadPool.getInstance().synchronizedTasks(true);
    }

    @BeforeEach
    public void beforeEach(){
        cardRepMock = mock(CardRepository.class);
        cardToTagRepMock = mock(CardToTagRepository.class);
        cardToBoxRepMock = mock(CardToBoxRepository.class);
        cardToCategoryRepMock = mock(CardToCategoryRepository.class);
        on(cardLogic).set("cardRepository",cardRepMock);
    }

    @Test
    public void testExceptionIfCardToDeleteIsNull() {
        final String expected = "Karte existiert nicht";
        final IllegalStateException exception =
                assertThrows(IllegalStateException.class, () -> cardLogic.deleteCard(null));
        assertEquals(expected, exception.getMessage());
    }

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

    @Test
    public void testExceptionIfCardToUpdateIsNull() {
        final String expected = "Karte existiert nicht";
        final IllegalStateException exception =
                assertThrows(IllegalStateException.class, () -> cardLogic.updateCardData(null,true));
        assertEquals(expected, exception.getMessage());
    }
    @Test
    public void testUpdateCardData(){
        Card card1  = new TextCard("Testfrage","Testantwort","Testtitel");
        when(cardRepMock.update(card1)).thenReturn(card1);
        when(cardRepMock.save(card1)).thenReturn(card1);
        cardLogic.updateCardData(card1,true);
        cardLogic.updateCardData(card1,false);
    }

    @Test
    public void testGetCardByUUIDNullException(){
        final String expected = "UUID darf nicht null sein!";
        final IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> cardLogic.getCardByUUID(null));
        assertEquals(expected, exception.getMessage());
    }

    @Test
    public void testGetCardByUUIDEmptyException(){
        final String expected = "UUID darf nicht leer sein!";
        final IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> cardLogic.getCardByUUID(""));
        assertEquals(expected, exception.getMessage());
    }

    @Test
    public void testGetCardByUUID(){
        Card card = new TrueFalseCard();
        when(cardRepMock.getCardByUUID(any(String.class))).thenReturn(card);
        Card card1 = cardLogic.getCardByUUID("Test");
        assertEquals(card,card1);
    }

    @Test
    public void testGetCardOverview(){
        final List<CardOverview> expected = Arrays.asList(new CardOverview(), new CardOverview());
        when(cardRepMock.getCardOverview(any(Integer.class),any(Integer.class))).thenReturn(expected);
        List<CardOverview> actual = cardLogic.getCardOverview(1,9);
        assertEquals(expected,actual);
    }




}
