package com.swp.Logic.CardLogicTest;


import com.gumse.gui.Locale;
import com.gumse.tools.Output;
import com.gumse.tools.Toolbox;
import com.swp.DataModel.CardOverview;
import com.swp.DataModel.Tag;
import com.swp.Logic.CardLogic;
import com.swp.Persistence.CardRepository;
import com.swp.Persistence.TagRepository;
import jakarta.transaction.Transactional;
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
 * Testklasse, um die Filterfunktionen in CardLogic zu testen.
 *
 */

public class FilterSearchTermsTagTest {

    private CardRepository cardRepMock;
    private TagRepository tagRepMock;
    private CardLogic cardLogic = CardLogic.getInstance();

    private Locale locale = new Locale("German", "de");
    private int i;

    @BeforeEach
    public void beforeEach(){
        cardRepMock = mock(CardRepository.class);
        tagRepMock = mock(TagRepository.class);
        on(cardLogic).set("cardRepository",cardRepMock);

        Locale.setCurrentLocale(locale);
        String filecontent = Toolbox.loadResourceAsString("locale/de_DE.UTF-8", getClass());

        i = 0;
        filecontent.lines().forEach((String line) -> {
            i++;
            if(line.replaceAll("\\s","").isEmpty() || line.charAt(0) == '#')
                return;

            String[] args = line.split("= ");
            if(args.length < 1)
                Output.error("Locale resource for language " + locale.getLanguage() + " is missing a definition at line " + i);
            String id = args[0].replaceAll("\\s","");
            String value = args[1];
            locale.setString(id, value);
        });
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
        final List<CardOverview> expected = Arrays.asList(new CardOverview(), new CardOverview());
        when(cardRepMock.findCardsByTag(anyString())).thenReturn(expected);
        List<CardOverview> actual = cardLogic.getCardsByTag("test");
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
        final List<CardOverview> expected = Arrays.asList(new CardOverview(), new CardOverview());
        when(cardRepMock.findCardsContaining(anyString())).thenReturn(expected);
        List<CardOverview> actual = cardLogic.getCardsBySearchterms("test");
        assertEquals(expected,actual);
    }


}
