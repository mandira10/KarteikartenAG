package com.swp.Logic.CardLogicTest;

import com.swp.DataModel.Card;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.Logic.CardLogic;
import com.swp.Persistence.CardRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.joor.Reflect.on;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

/**
 * Klasse, die die normalen Funktionen für eine Karte testet.
 */
public class updateSaveDeleteCardTest {


    private CardRepository cardRepMock;
    private CardLogic cardLogic = CardLogic.getInstance();


    @BeforeEach
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

    //TODO
//    @Test
//    public void testDeleteFunctionForManyCards(){
//        List<CardOverview> cards = Arrays.asList(new CardOverview(),new CardOverview());
//        doNothing().when(cardRepMock).delete(any(CardOverview.class));
//        cardLogic.deleteCards(cards);
//    }

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
