package com.swp.Logic.CardLogicTest;

import com.swp.DataModel.Card;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.Logic.CardLogic;
import com.swp.Persistence.CardRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.joor.Reflect.on;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

/**
 * Class testing the normal functionalities of any card.
 */
public class updateSaveDeleteTests {


    private CardRepository cardRepMock;
    private CardLogic cardLogic = CardLogic.getInstance();


    @Before
    public void beforeEach(){
        cardRepMock = mock(CardRepository.class);
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
        Card card1  = new TextCard("Testfrage","Testantwort","Testtitel",true);
        doNothing().when(cardRepMock).delete(card1);
        cardLogic.deleteCard(card1);
    }

    @Test
    public void testDeleteFunctionForManyCards(){
        Card card1  = new TextCard("Testfrage","Testantwort","Testtitel",true);
        Card card2  = new TextCard("Testfrage1","Testantwort1","Testtitel1",true);
        Card card3  = new TextCard("Testfrage2","Testantwort2","Testtitel2",true);
        List<Card> cards = Arrays.asList(new Card[]{card1,card2,card3});
        doNothing().when(cardRepMock).delete(card1);
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
        Card card1  = new TextCard("Testfrage","Testantwort","Testtitel",true);
        doNothing().when(cardRepMock).update(card1);
        doNothing().when(cardRepMock).save(card1);
        cardLogic.updateCardData(card1,true);
        cardLogic.updateCardData(card1,false);
    }


    public void testGetCardByUUID(){
        //TODO
    }

    public void testGetCardsToShow(){
        //TODO
    }


}
