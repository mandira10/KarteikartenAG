package com.swp.Controller.StudySystemControllerTest;


import com.gumse.gui.Locale;
import com.swp.Controller.ControllerThreadPool;
import com.swp.Controller.DataCallback;
import com.swp.Controller.SingleDataCallback;
import com.swp.Controller.StudySystemController;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardOverview;
import com.swp.DataModel.CardTypes.MultipleChoiceCard;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.DataModel.CardTypes.TrueFalseCard;
import com.swp.DataModel.Language.German;
import com.swp.DataModel.StudySystem.LeitnerSystem;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.Logic.StudySystemLogic;
import com.swp.Persistence.PersistenceManager;

import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentMatcher;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.joor.Reflect.on;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

/**
 * Testet die Controller Funktionen
 */
public class StudySystemControllerTests {

    private StudySystemLogic studySystemMockLogic;
    private final StudySystemController studySystemController = StudySystemController.getInstance();
    private SingleDataCallback<Boolean> bolMockSingleDataCallback;
    private  DataCallback<CardOverview> cardMockDataCallback;
    private DataCallback<StudySystem> studySMockDataCallback;
    private SingleDataCallback<StudySystem> studySMockSingleDataCallback;
    private SingleDataCallback<Card> cardMockSingleDataCallback;
    SingleDataCallback<Integer> intMockSingleDataCallback;


    @BeforeEach
    public void beforeEach() 
    {
        studySystemMockLogic = mock(StudySystemLogic.class);
        bolMockSingleDataCallback = mock(SingleDataCallback.class);
        cardMockDataCallback = mock(DataCallback.class);
        studySMockDataCallback = mock(DataCallback.class);
        studySMockSingleDataCallback = mock(SingleDataCallback.class);
        cardMockSingleDataCallback = mock(SingleDataCallback.class);
        intMockSingleDataCallback = mock(SingleDataCallback.class);

        on(studySystemController).set("studySystemLogic", studySystemMockLogic);
    }

    @BeforeAll
    public static void before() 
    {
        PersistenceManager.init("KarteikartenDBTest");
        ControllerThreadPool.getInstance().synchronizedTasks(true);
        German.getInstance().activate();
    }


    @Test
    public void getAllCardsInStudySystemTestEmptySet() {
        List<CardOverview> list = new ArrayList<>();
        when(studySystemMockLogic.getAllCardsInStudySystem(any(StudySystem.class))).thenReturn(list);
        StudySystem studySystem = new LeitnerSystem();
        assertDoesNotThrow(() -> studySystemController.getAllCardsInStudySystem(studySystem, cardMockDataCallback));
    }

    @Test
    public void getAllCardsInStudySystemTestWithList() {
        final List<CardOverview> list = Arrays.asList(new CardOverview(), new CardOverview());
        when(studySystemMockLogic.getAllCardsInStudySystem(any(StudySystem.class))).thenReturn(list);
        StudySystem studySystem = new LeitnerSystem();
        assertDoesNotThrow(() -> studySystemController.getAllCardsInStudySystem(studySystem,cardMockDataCallback));
        verify(cardMockDataCallback).callSuccess(argThat(overviews -> {
            assertEquals(overviews,list);
            return true;
        }));
    }

    @Test
    public void getAllCardsInStudySystemTestException() {
        when(studySystemMockLogic.getAllCardsInStudySystem(any(StudySystem.class))).thenThrow(new RuntimeException("Test"));
        StudySystem studySystem = new LeitnerSystem();

        assertDoesNotThrow(() -> studySystemController.getAllCardsInStudySystem(studySystem, cardMockDataCallback));
        verify(cardMockDataCallback, times(1))
                .callFailure(Locale.getCurrentLocale().getString("getallcardsinstudysystemerror"));

    }

    @Test
    public void deleteStudySystemTestNull() {
        StudySystem studySystem = new LeitnerSystem();

        doThrow(new IllegalStateException("studysystemnullerror")).when(studySystemMockLogic).deleteStudySystem(studySystem);
        assertDoesNotThrow(() -> studySystemController.deleteStudySystem(studySystem, bolMockSingleDataCallback));
            verify(bolMockSingleDataCallback, times(1))
                    .callFailure(Locale.getCurrentLocale().getString("studysystemnullerror"));
    }

    @Test
    public void deleteStudySystemTestException() {
        StudySystem studySystem = new LeitnerSystem();
        doThrow(new RuntimeException("Test")).when(studySystemMockLogic).deleteStudySystem(studySystem);
        assertDoesNotThrow(() ->studySystemController.deleteStudySystem(studySystem, bolMockSingleDataCallback));
                verify(bolMockSingleDataCallback, times(1))
                        .callFailure("Beim Löschen des Decks ist ein Fehler aufgetreten.");
    }

    @Test
    public void deleteStudySystemTest() {
        StudySystem studySystem = new LeitnerSystem();
        doNothing().when(studySystemMockLogic).deleteStudySystem(studySystem);
        assertDoesNotThrow(() -> studySystemController.deleteStudySystem(studySystem, bolMockSingleDataCallback));
        verify(bolMockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Boolean>() {
            @Override
            public boolean matches(Boolean bol) {
                assertTrue(bol);
                return true;
            }
        }));
    }

    @Test
    public void getStudySystemsTestNull() {
        List<StudySystem> list = new ArrayList<>();
        when(studySystemMockLogic.getStudySystems()).thenReturn(list);
        assertDoesNotThrow(() -> studySystemController.getStudySystems(studySMockDataCallback));
        verify(studySystemMockLogic).getStudySystems();
    }

    @Test
    public void getStudySystemsTestException() {
        doThrow(new RuntimeException("Test")).when(studySystemMockLogic).getStudySystems();
        assertDoesNotThrow(() ->  studySystemController.getStudySystems(studySMockDataCallback));
        verify(studySMockDataCallback, times(1))
                .callFailure(Locale.getCurrentLocale().getString("getstudysystemserror"));
    }

    @Test
    public void getStudySystemsTest() {
        final List<StudySystem> list = Arrays.asList(new LeitnerSystem(), new LeitnerSystem());
        when(studySystemMockLogic.getStudySystems()).thenReturn(list);
        final List<StudySystem> expected = list;
        assertDoesNotThrow(() -> studySystemController.getStudySystems(studySMockDataCallback));
        verify(studySMockDataCallback).callSuccess(argThat(new ArgumentMatcher<List<StudySystem>>() {
            @Override
            public boolean matches(List<StudySystem> overviews) {
                assertEquals(overviews,list);
                return true;
            }
        }));

    }

    @Test
    public void getStudySystemBySearchTermsNull() {
        List<StudySystem> list = new ArrayList<>();
        when(studySystemMockLogic.getStudySystemsBySearchterm(anyString())).thenReturn(list);
        assertDoesNotThrow(() ->studySystemController.getStudySystemBySearchTerms("Test", studySMockDataCallback));
        verify(studySMockDataCallback, times(1))
                .callInfo("Es gibt keine Decks zu diesem Suchbegriff.");

    }

    @Test
    public void getStudySystemBySearchTermsException() {
        when(studySystemMockLogic.getStudySystemsBySearchterm(any(String.class))).thenThrow(new RuntimeException("Test"));
        assertDoesNotThrow(() -> studySystemController.getStudySystemBySearchTerms("Test", studySMockDataCallback));
            verify(studySMockDataCallback, times(1))
                    .callFailure("Beim Abrufen der Decks für den Suchgriff ist ein Fehler aufgetreten.");
    }

    @Test
    public void getStudySystemBySearchTermsTest() {
        final List<StudySystem> list = Arrays.asList(new LeitnerSystem(), new LeitnerSystem());
        when(studySystemMockLogic.getStudySystemsBySearchterm(any(String.class))).thenReturn(list);
         assertDoesNotThrow(() -> studySystemController.getStudySystemBySearchTerms("Test", studySMockDataCallback));
            verify(studySMockDataCallback).callSuccess(argThat(new ArgumentMatcher<List<StudySystem>>() {
                @Override
                public boolean matches(List<StudySystem> overviews) {
                    assertEquals(overviews,list);
                    return true;
                }
            }));
    }


    @Test
    public void getStudySystemByUUIDNull() {
        when(studySystemMockLogic.getStudySystemByUUID(any(String.class))).thenThrow(new IllegalStateException("nonnull"));
         assertDoesNotThrow(() -> studySystemController.getStudySystemByUUID("Test", studySMockSingleDataCallback));
            verify(studySMockSingleDataCallback, times(1))
                    .callFailure("ID darf nicht null sein!");
    }

    @Test
    public void getStudySystemByUUIDEmpty(){
        when(studySystemMockLogic.getStudySystemByUUID("")).thenThrow(new IllegalArgumentException("nonempty"));
        String uuid = "";
        assertDoesNotThrow(() -> studySystemController.getStudySystemByUUID(uuid, studySMockSingleDataCallback));
        verify(studySMockSingleDataCallback, times(1))
                .callFailure("ID darf nicht leer sein!");
    }

    @Test
    public void getStudySystemByUUIDTestNoResultException() {
        when(studySystemMockLogic.getStudySystemByUUID(any(String.class))).thenThrow(new NoResultException("Es konnte kein studySystem zur UUID gefunden werden"));
        assertDoesNotThrow(() ->studySystemController.getStudySystemByUUID("Test", studySMockSingleDataCallback));
        verify(studySMockSingleDataCallback, times(1))
                .callFailure("Es konnte nichts gefunden werden.");
    }

    @Test
    public void getStudySystemByUUIDException() {
        when(studySystemMockLogic.getStudySystemByUUID(any(String.class))).thenThrow(new RuntimeException("Test"));
        assertDoesNotThrow(() -> studySystemController.getStudySystemByUUID("Test",studySMockSingleDataCallback));
        verify(studySMockSingleDataCallback, times(1))
                .callFailure("Beim Abrufen des Decks ist ein Fehler aufgetreten.");
    }
    @Test
    public void getStudySystemByUUIDTest() {
        final StudySystem studySystem = new LeitnerSystem();
        when(studySystemMockLogic.getStudySystemByUUID("Test")).thenReturn(studySystem);
          assertDoesNotThrow(() -> studySystemController.getStudySystemByUUID("Test", studySMockSingleDataCallback));
            verify(studySMockSingleDataCallback).callSuccess(argThat(studySystem1 -> {
                assertEquals(studySystem, studySystem1);
                return true;
            }));
    }

    @Test
    public void getNextCardTestException() {
        StudySystem studySystem = new LeitnerSystem();
        when(studySystemMockLogic.getNextCard(studySystem)).thenThrow(new RuntimeException("Test"));
        assertDoesNotThrow(() -> studySystemController.getNextCard(studySystem, cardMockSingleDataCallback));
        verify(cardMockSingleDataCallback, times(1))
            .callFailure("Beim Abrufen der nächsten Karte ist ein Fehler aufgetreten.");
}

    @Test
    public void getNextCardTest() {
        final Card card = new MultipleChoiceCard();
        when(studySystemMockLogic.getNextCard(any(StudySystem.class))).thenReturn(card);
        assertDoesNotThrow(() -> studySystemController.getNextCard(new LeitnerSystem(), cardMockSingleDataCallback));
            verify(cardMockSingleDataCallback).callSuccess(argThat(card1 -> {
                assertEquals(card1, card);
                return true;
            }));
    }


    @Test
    public void finishTestAndGetResultException() {
            StudySystem studySystem = new LeitnerSystem();
        when(studySystemMockLogic.finishTestAndGetResult(studySystem)).thenThrow(new RuntimeException("Test"));
        assertDoesNotThrow(() -> studySystemController.finishTestAndGetResult(studySystem, intMockSingleDataCallback));
            verify(intMockSingleDataCallback, times(1))
                    .callFailure("Beim Beenden des Tests ist ein Fehler aufgetreten.");
        }

    @Test
    public void finishTestAndGetResultTest() {
        int f = 10;
        when(studySystemMockLogic.finishTestAndGetResult(any(StudySystem.class))).thenReturn(f);
        assertDoesNotThrow(() ->studySystemController.finishTestAndGetResult(new LeitnerSystem(), intMockSingleDataCallback));
            verify(intMockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Integer>() {
                @Override
                public boolean matches(Integer integer) {
                  assertEquals(f,integer);
                    return true;
                }
            }));
    }


    @Test
    public void moveAllCardsForDeckToFirstBoxTestNull() {
        StudySystem studySystem = new LeitnerSystem();
        List<Card> card = new ArrayList<>();
        when(studySystemMockLogic.moveAllCardsForDeckToFirstBox(card, studySystem)).thenReturn(new ArrayList<>());
         assertDoesNotThrow(() -> studySystemController.moveAllCardsForDeckToFirstBox(card, studySystem,bolMockSingleDataCallback));
         verify(studySystemMockLogic).moveAllCardsForDeckToFirstBox(card,studySystem);
        }

    @Test
    public void moveAllCardsForDeckToFirstBoxTestException() {
        StudySystem studySystem = new LeitnerSystem();
        Card testcard = new TrueFalseCard();
        List<Card> cards = List.of(testcard);
        doThrow(new RuntimeException("Test")).when(studySystemMockLogic).moveAllCardsForDeckToFirstBox(cards, studySystem);
        String expected = "Beim Speichern der Karte in dem Deck ist ein Fehler aufgetreten.";
        assertDoesNotThrow(() -> studySystemController.moveAllCardsForDeckToFirstBox(cards, studySystem, bolMockSingleDataCallback));
        verify(bolMockSingleDataCallback, times(1))
                .callFailure(expected);
    }

    @Test
    public void moveAllCardsForDeckToFirstBoxTest() {
        StudySystem studySystem = new LeitnerSystem();

        List<Card> card = new ArrayList<>();
        when(studySystemMockLogic.moveAllCardsForDeckToFirstBox(card, studySystem)).thenReturn(new ArrayList<>());

        assertDoesNotThrow(() -> studySystemController.moveAllCardsForDeckToFirstBox(card, studySystem,bolMockSingleDataCallback));
            verify(bolMockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Boolean>() {
                @Override
                public boolean matches(Boolean bol) {
                    assertTrue(bol);
                    return true;
                }
            }));
    }



    @Test
    public void giveAnswerTestException() {
        StudySystem studySystem = new LeitnerSystem();
        doThrow(new RuntimeException("Test")).when(studySystemMockLogic).giveAnswer(studySystem, true);
        String expected = "Beim Prüfen der Antwort ist ein Fehler aufgetreten.";
        assertDoesNotThrow(() ->studySystemController.giveAnswer(studySystem, true, bolMockSingleDataCallback));
        verify(bolMockSingleDataCallback, times(1))
                .callFailure(expected);
    }

    @Test
    public void giveAnswerTest() {
        StudySystem studySystem = new LeitnerSystem();

        doNothing().when(studySystemMockLogic).giveAnswer(studySystem, true);

        assertDoesNotThrow(() ->studySystemController.giveAnswer(studySystem, true, bolMockSingleDataCallback));
                verify(bolMockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Boolean>() {
                    @Override
                    public boolean matches(Boolean bol) {
                        assertTrue(bol);
                        return true;
                    }
                }));
    }

    //

    @Test
    public void giveRatingTestException() {
        StudySystem studySystem = new LeitnerSystem();
        Card card = new TrueFalseCard();
        doThrow(new RuntimeException("Test")).when(studySystemMockLogic).giveRating(any(StudySystem.class), any(Card.class), anyInt());
        String expected = "Beim Speichern des Ratings für eine Karte ist ein Fehler aufgetreten.";
        assertDoesNotThrow(() -> studySystemController.giveRating(studySystem, card, 2,bolMockSingleDataCallback));
            verify(bolMockSingleDataCallback, times(1))
                    .callFailure(expected);
    }

    @Test
    public void giveRatingTest() {
        StudySystem studySystem = new LeitnerSystem();
        Card card = new TrueFalseCard();

        doNothing().when(studySystemMockLogic).giveRating(studySystem, card, 2);

        assertDoesNotThrow(() -> studySystemController.giveRating(studySystem, card, 2, bolMockSingleDataCallback));
                verify(bolMockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Boolean>() {
                    @Override
                    public boolean matches(Boolean bol) {
                        assertTrue(bol);
                        return true;
                    }
                }));
    }

    //


    //

    @Test
    public void numCardsInDeckTestException() {
        when(studySystemMockLogic.numCardsInDeck(any(StudySystem.class))).thenThrow(new RuntimeException("Test"));
        final String expected = "Beim Abrufen der Anzahl an Karten pro Deck ist ein Fehler aufgetreten.";
        StudySystem studySystem = new LeitnerSystem();
        assertDoesNotThrow(() -> studySystemController.numCardsInDeck(studySystem, intMockSingleDataCallback));
            verify(intMockSingleDataCallback, times(1))
                    .callFailure(expected);
    }

    @Test
    public void numCardsInDeckTest() {
        int f = 10;
        when(studySystemMockLogic.numCardsInDeck(any(StudySystem.class))).thenReturn(f);
        assertDoesNotThrow(() -> studySystemController.numCardsInDeck(new LeitnerSystem(),intMockSingleDataCallback));
            verify(intMockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Integer>() {
                @Override
                public boolean matches(Integer integer) {
                    assertEquals(f,integer);
                    return true;
                }
            }));
    }

    //

    @Test
    public void removeCardsFromStudySystemTestNull() {
        StudySystem studySystem = new LeitnerSystem();
        List<CardOverview> list = new ArrayList<>();
        doThrow(new IllegalStateException("cardnullerror")).when(studySystemMockLogic).removeCardsFromStudySystem(anyList(), any(StudySystem.class));
        String expected = "Karte existiert nicht";
        final String[] actual = new String[1];
        assertDoesNotThrow(() -> studySystemController.removeCardsFromStudySystem(list, studySystem, bolMockSingleDataCallback));
            verify(bolMockSingleDataCallback, times(1))
                    .callFailure(expected);
    }

    @Test
    public void removeCardsFromStudySystemTestException() {
        StudySystem studySystem = new LeitnerSystem();
        List<CardOverview> list = new ArrayList<>();
        doThrow(new RuntimeException("Test")).when(studySystemMockLogic).removeCardsFromStudySystem(anyList(), any(StudySystem.class));
        String expected = "Beim Entfernen der Karte aus dem Deck ist ein Fehler aufgetreten.";
        assertDoesNotThrow(() -> studySystemController.removeCardsFromStudySystem(list, studySystem, bolMockSingleDataCallback));
        verify(bolMockSingleDataCallback, times(1))
                .callFailure(expected);
    }

    @Test
    public void removeCardsFromStudySystemTest() {
        StudySystem studySystem = new LeitnerSystem();

        List<CardOverview> list = new ArrayList<>();
        doNothing().when(studySystemMockLogic).removeCardsFromStudySystem(list, studySystem);

        assertDoesNotThrow(() -> studySystemController.removeCardsFromStudySystem(list, studySystem, bolMockSingleDataCallback));
            verify(bolMockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Boolean>() {
                @Override
                public boolean matches(Boolean bol) {
                    assertTrue(bol);
                    return true;
                }
            }));
    }

    //

    @Test
    public void deleteDeckTestException() {
        StudySystem[] studySystem = {new LeitnerSystem()};
        doThrow(new RuntimeException("Test")).when(studySystemMockLogic).deleteStudySystem(studySystem);
        String expected = "Beim Löschen der Decks ist ein Fehler aufgetreten.";
        assertDoesNotThrow(() -> studySystemController.deleteDecks(studySystem, bolMockSingleDataCallback));
            verify(bolMockSingleDataCallback, times(1))
                    .callFailure(expected);
    }

    @Test
    public void deleteDeckTest() {
        StudySystem[] studySystem = {new LeitnerSystem()};
        doNothing().when(studySystemMockLogic).deleteStudySystem(studySystem);
        assertDoesNotThrow(() ->studySystemController.deleteDecks(studySystem,bolMockSingleDataCallback));
            verify(bolMockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Boolean>() {
                @Override
                public boolean matches(Boolean bol) {
                    assertTrue(bol);
                    return true;
                }
            }));
    }

    @Test
    public void addCardsToStudySystemTestNotEmpty() {
        StudySystem studySystem = new LeitnerSystem();
        SingleDataCallback<String> stringSMockDataCallback = mock(SingleDataCallback.class);
        List<CardOverview> list = new ArrayList<>();
        list.add(new CardOverview());
        when(studySystemMockLogic.addCardsToDeck(list, studySystem)).thenReturn(new ArrayList<Card>());
        assertDoesNotThrow(() -> studySystemController.addCardsToStudySystem(list, studySystem, stringSMockDataCallback));
        verify(studySystemMockLogic).addCardsToDeck(list, studySystem);
    }




    @Test
    public void addCardsToStudySystemTestException() {
        StudySystem studySystem = new LeitnerSystem();
        List<CardOverview> list = new ArrayList<>();
        SingleDataCallback<String> stringSMockDataCallback = mock(SingleDataCallback.class);
        doThrow(new RuntimeException("Test")).when(studySystemMockLogic).addCardsToDeck(anyList(), any(StudySystem.class));
        String expected = "Beim Hinzufügen von Karten ins Deck ist ein Fehler aufgetreten.";
        assertDoesNotThrow(() -> studySystemController.addCardsToStudySystem(list, studySystem, stringSMockDataCallback));
        verify(stringSMockDataCallback, times(1))
                .callFailure(expected);
    }

    @Test
    public void addCardsToStudySystemTest() {
        StudySystem studySystem = new LeitnerSystem();
        List<CardOverview> list = new ArrayList<>();
        SingleDataCallback<String> stringSMockDataCallback = mock(SingleDataCallback.class);
        when(studySystemMockLogic.addCardsToDeck(list, studySystem)).thenReturn(new ArrayList<Card>());
        assertDoesNotThrow(() -> studySystemController.addCardsToStudySystem(list, studySystem, stringSMockDataCallback));
        verify(studySystemMockLogic).addCardsToDeck(list,studySystem);
    }

    @Test
    public void addCardsToStudySystemTestNonEmptyListString() {
        StudySystem studySystem = new LeitnerSystem();
        List<CardOverview> list = new ArrayList<>();
        List<Card> cards= List.of(new TextCard());
        SingleDataCallback<String> stringSMockDataCallback = mock(SingleDataCallback.class);
        when(studySystemMockLogic.addCardsToDeck(list, studySystem)).thenReturn(cards);
        assertDoesNotThrow(() -> studySystemController.addCardsToStudySystem(list, studySystem, stringSMockDataCallback));
        verify(stringSMockDataCallback).callSuccess(argThat(new ArgumentMatcher<String>() {
            @Override
            public boolean matches(String string) {
                assertEquals(string,Locale.getCurrentLocale().getString("studysystemaddcardsinfo")
                        + cards.stream().map(Card::getTitle).collect(Collectors.joining(",")));
                return true;
            }
        }));

    }


    @Test
    public void updateDeckDataTestException() {
        StudySystem studySystem = new LeitnerSystem();
        StudySystem studySystem2 = new LeitnerSystem();
        doThrow(new RuntimeException("Test")).when(studySystemMockLogic).updateStudySystemData(studySystem,studySystem2, false, false);
        String expected = "Beim Aktualisieren der Deckdaten ist ein Fehler aufgetreten.";
        assertDoesNotThrow(() -> studySystemController.updateStudySystemData(studySystem,studySystem2,false, false, bolMockSingleDataCallback));
        verify(bolMockSingleDataCallback, times(1))
                .callFailure(expected);
    }

    @Test
    public void updateDeckDataTest() {
        StudySystem studySystem = new LeitnerSystem();
        StudySystem studySystem2 = new LeitnerSystem();
        doNothing().when(studySystemMockLogic).updateStudySystemData(studySystem2, studySystem, true, false);
        assertDoesNotThrow(() -> studySystemController.updateStudySystemData(studySystem2, studySystem, true, false, bolMockSingleDataCallback));
            verify(bolMockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Boolean>() {
                @Override
                public boolean matches(Boolean bol) {
                  assertTrue(bol);
                    return true;
                }
            }));
        verify(studySystemMockLogic).updateStudySystemData(studySystem2,studySystem,true,false);
    }

    @Test
    public void resetLearnStatusTest() {
        StudySystem studySystem = new LeitnerSystem();
        assertDoesNotThrow(() -> studySystemController.resetLearnStatus(studySystem, bolMockSingleDataCallback));
        verify(studySystemMockLogic).resetLearnStatus(studySystem);
            verify(bolMockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Boolean>() {
                @Override
                public boolean matches(Boolean bol) {
                    assertTrue(bol);
                    return true;
                }
            }));
    }




}



















