package com.swp.Logic.CategoryLogicTest;

import com.gumse.gui.Locale;
import com.gumse.tools.Output;
import com.gumse.tools.Toolbox;
import com.swp.DataModel.CardOverview;
import com.swp.Logic.CardLogic;
import com.swp.Logic.CategoryLogic;
import com.swp.Persistence.CardRepository;
import com.swp.Persistence.CategoryRepository;
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

public class FilterForCategoryTest {

    private CardRepository cardRepMock;
    private CategoryRepository categoryRepMock;
    private CategoryLogic categoryLogic = CategoryLogic.getInstance();
    private Locale locale = new Locale("German", "de");
    private int i;
    @BeforeEach
    public void beforeEach(){
        cardRepMock = mock(CardRepository.class);
        categoryRepMock = mock(CategoryRepository.class);
        on(categoryLogic).set("cardRepository",cardRepMock);
        on(categoryLogic).set("categoryRepository",categoryRepMock);
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
        when(cardRepMock.getCardsByCategory(anyString())).thenReturn(expected);
        List<CardOverview> actual = categoryLogic.getCardsInCategory("test");
        assertEquals(expected,actual);
    }

}
