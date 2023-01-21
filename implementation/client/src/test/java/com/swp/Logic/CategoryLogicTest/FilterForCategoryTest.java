package com.swp.Logic.CategoryLogicTest;

import com.swp.DataModel.Card;
import com.swp.DataModel.CardOverview;
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
        final List<CardOverview> expected = Arrays.asList(new CardOverview(), new CardOverview());
        when(cardRepMock.getCardsByCategory(categoryRepMock.find(anyString()))).thenReturn(expected);
        List<CardOverview> actual = categoryLogic.getCardsInCategory("test");
        assertEquals(expected,actual);
    }

}
