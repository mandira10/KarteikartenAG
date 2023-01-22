package com.swp.Logic.CategoryLogicTest;

import com.swp.DataModel.Card;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.DataModel.Category;
import com.swp.Logic.CardLogic;
import com.swp.Logic.CategoryLogic;
import com.swp.Persistence.CardRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.joor.Reflect.on;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mock;

public class updateSaveDeleteCategoryTest {

    private CardRepository cardRepMock;
    private CardLogic cardLogic = CardLogic.getInstance();
    private CategoryLogic categoryLogic = CategoryLogic.getInstance();


    public void beforeEach(){

    }


    public void testExceptionIfCardToDeleteIsNull() {
        final String expected = "Karte existiert nicht";
        final IllegalStateException exception =
                assertThrows(IllegalStateException.class, () -> cardLogic.deleteCard(null));
        assertEquals(expected, exception.getMessage());
    }


    public void testDeleteCardFunction(){
        Card card1  = new TextCard("Testfrage","Testantwort","Testtitel",true);
        doNothing().when(cardRepMock).delete(card1);
        cardLogic.deleteCard(card1);
    }


    public void testDeleteFunctionForManyCards(){
        Card card1  = new TextCard("Testfrage","Testantwort","Testtitel",true);
        Card card2  = new TextCard("Testfrage1","Testantwort1","Testtitel1",true);
        Card card3  = new TextCard("Testfrage2","Testantwort2","Testtitel2",true);
        List<Card> cards = Arrays.asList(new Card[]{card1,card2,card3});
        doNothing().when(cardRepMock).delete(card1);
        cardLogic.deleteCards(cards);
    }


    public void testExceptionIfCardToUpdateIsNull() {
        final String expected = "Karte existiert nicht";
        final IllegalStateException exception =
                assertThrows(IllegalStateException.class, () -> cardLogic.updateCardData(null,true));
        assertEquals(expected, exception.getMessage());
    }



    public void testGetCardByUUID(){
        //TODO
    }

    public void testGetCardsToShow(){
        //TODO
    }
}
