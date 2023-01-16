package com.swp.Logic.CardLogicTest;

import com.swp.DataModel.Card;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.Logic.CardLogic;
import com.swp.Persistence.CardRepository;
import com.swp.Persistence.TagRepository;
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

/**
 * Testklasse, um die Filterfunktionen in CardLogic zu testen.
 *
 */
public class FilterSearchTermsTagTest {

    private CardRepository cardRepMock;
    private TagRepository tagRepMock;
    private CardLogic cardLogic = CardLogic.getInstance();


    @Before
    public void beforeEach(){
        cardRepMock = mock(CardRepository.class);
        tagRepMock = mock(TagRepository.class);
        on(cardLogic).set("cardRepository",cardRepMock);
    }

    @Test
    public void testExceptionIfTagString(){
        final String expected = "Tag darf nicht null sein!";
        final IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, ()->cardLogic.getCardsByTag(null));
                assertEquals(expected,exception.getMessage());
    }

    @Test
    public void testExceptionIfTagEmpty(){
        final String expected = "Tag darf nicht leer sein!";
        final IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, ()->cardLogic.getCardsByTag(""));
        assertEquals(expected,exception.getMessage());
    }

    @Test
    public void testListOfCardsForTags(){
        on(cardLogic).set("tagRepository",tagRepMock);
        Card card1  = new TextCard("Testfrage","Testantwort","Testtitel",true);
        Card card2  = new TextCard("Testfrage1","Testantwort1","Testtitel1",true);
        Card card3  = new TextCard("Testfrage2","Testantwort2","Testtitel2",true);
        final List<Card> expected =Arrays.asList(new Card[]{card1,card2,card3});
        when(cardRepMock.findCardsByTag(tagRepMock.findTag(anyString()))).thenReturn(expected);
        List<Card> actual = cardLogic.getCardsByTag("test");
        assertEquals(expected,actual);
    }

    @Test
    public void testExceptionIfSearchTermNull(){
        final String expected = "Suchbegriff darf nicht null sein!";
        final IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, ()->cardLogic.getCardsBySearchterms(null));
        assertEquals(expected,exception.getMessage());
    }

    @Test
    public void testExceptionIfSearchTermsEmpty(){
        final String expected = "Suchbegriff darf nicht leer sein!";
        final IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, ()->cardLogic.getCardsBySearchterms(""));
        assertEquals(expected,exception.getMessage());
    }

    @Test
    public void testListOfCardsForSearchterms(){
        Card card1  = new TextCard("Testfrage","Testantwort","Testtitel",true);
        Card card2  = new TextCard("Testfrage1","Testantwort1","Testtitel1",true);
        Card card3  = new TextCard("Testfrage2","Testantwort2","Testtitel2",true);
        final List<Card> expected =Arrays.asList(new Card[]{card1,card2,card3});
        when(cardRepMock.findCardsContaining(anyString())).thenReturn(expected);
        List<Card> actual = cardLogic.getCardsBySearchterms("test");
        assertEquals(expected,actual);
    }


}
