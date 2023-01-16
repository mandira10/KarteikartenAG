package com.swp.Logic.CategoryLogicTest;

import com.swp.DataModel.Card;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.Logic.CardLogic;
import com.swp.Logic.CategoryLogic;
import com.swp.Persistence.CardRepository;
import com.swp.Persistence.CategoryRepository;
import org.junit.Before;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

import static org.joor.Reflect.on;
import static org.junit.Assert.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FilterForCategoryTest {

    private CardRepository cardRepMock;
    private CategoryRepository categoryRepMock;
    private CardLogic cardLogic = CardLogic.getInstance();
    private CategoryLogic categoryLogic = CategoryLogic.getInstance();

    @Before
    public void beforeEach(){
        cardRepMock = mock(CardRepository.class);
        categoryRepMock = mock(CategoryRepository.class);
        on(categoryLogic).set("cardRepository",cardRepMock);
        on(categoryLogic).set("categoryRepository",categoryRepMock);
    }

    @Test
    public void testExceptionIfCategoryNameNull(){
        final String expected = "Kategorie darf nicht null sein!";
        final IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, ()->categoryLogic.getCardsInCategory((String) null));
        assertEquals(expected,exception.getMessage());
    }

    @Test
    public void testExceptionIfCategoryNameEmpty(){
        final String expected = "Kategorie darf nicht leer sein!";
        final IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, ()->categoryLogic.getCardsInCategory(""));
        assertEquals(expected,exception.getMessage());
    }

    @Test
    public void testListOfCardsForCategory(){
        Card card1  = new TextCard("Testfrage","Testantwort","Testtitel",true);
        Card card2  = new TextCard("Testfrage1","Testantwort1","Testtitel1",true);
        Card card3  = new TextCard("Testfrage2","Testantwort2","Testtitel2",true);
        final List<Card> expected = Arrays.asList(new Card[]{card1,card2,card3});
        when(cardRepMock.getCardsByCategory(categoryRepMock.find(anyString()))).thenReturn(expected);
        List<Card> actual = categoryLogic.getCardsInCategory("test");
        assertEquals(expected,actual);
    }

}
