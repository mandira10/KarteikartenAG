package com.swp.Controller.StudySystemControllerTest;


import com.gumse.gui.Locale;
import com.gumse.tools.Output;
import com.gumse.tools.Toolbox;
import com.swp.Controller.ControllerThreadPool;
import com.swp.Controller.DataCallback;
import com.swp.Controller.SingleDataCallback;
import com.swp.Controller.StudySystemController;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardOverview;
import com.swp.DataModel.CardTypes.MultipleChoiceCard;
import com.swp.DataModel.CardTypes.TrueFalseCard;
import com.swp.DataModel.Category;
import com.swp.DataModel.StudySystem.LeitnerSystem;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.Logic.StudySystemLogic;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.joor.Reflect.on;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testet die Controller Funktionen
 */
public class StudySystemControllerTests {

    private StudySystemLogic studySystemMockLogic;
    private StudySystemController studySystemController = StudySystemController.getInstance();
    private Locale locale = new Locale("German", "de");
    private int i;

    @BeforeEach
    public void beforeEach() {
        studySystemMockLogic = mock(StudySystemLogic.class);
        on(studySystemController).set("studySystemLogic", studySystemMockLogic);
        Locale.setCurrentLocale(locale);
        String filecontent = Toolbox.loadResourceAsString("locale/de_DE.UTF-8", getClass());
        i = 0;
        filecontent.lines().forEach((String line) -> {
            i++;
            if (line.replaceAll("\\s", "").isEmpty() || line.charAt(0) == '#')
                return;

            String[] args = line.split("= ");
            if (args.length < 1)
                Output.error("Locale resource for language " + locale.getLanguage() + " is missing a definition at line " + i);
            String id = args[0].replaceAll("\\s", "");
            String value = args[1];
            locale.setString(id, value);
        });
    }

    @BeforeAll
    public static void before() {
        ControllerThreadPool.getInstance().synchronizedTasks(true);
    }


    @Test
    public void getAllCardsInStudySystemTestEmptySet() {

        List<CardOverview> list = new ArrayList<>();
        when(studySystemMockLogic.getAllCardsInStudySystem(any(StudySystem.class))).thenReturn(list);
        final String expected = "Es gibt keine Karten zu diesem StudySystem";
        final String[] actual = new String[1];
        StudySystem studySystem = new LeitnerSystem();


        studySystemController.getAllCardsInStudySystem(studySystem, new DataCallback<CardOverview>() {
            @Override
            public void onSuccess(List<CardOverview> data) {

            }

            @Override
            public void onFailure(String msg) {

            }

            @Override
            public void onInfo(String msg) {
                actual[0] = msg;
            }
        });
        //Thread.join();
        assertEquals(expected, actual[0]);


    }

    @Test
    public void getAllCardsInStudySystemTestWithList() {
        final List<CardOverview> list = Arrays.asList(new CardOverview(), new CardOverview());
        when(studySystemMockLogic.getAllCardsInStudySystem(any(StudySystem.class))).thenReturn(list);
        final List<CardOverview> expected = list;
        final List<CardOverview>[] actual = new List[1];
        StudySystem studySystem = new LeitnerSystem();

        studySystemController.getAllCardsInStudySystem(studySystem, new DataCallback<CardOverview>() {
            @Override
            public void onSuccess(List<CardOverview> data) {
                actual[0] = data;
            }

            @Override
            public void onFailure(String msg) {
            }

            @Override
            public void onInfo(String msg) {
            }
        });
        assertEquals(expected, actual[0]);
    }

    @Test
    public void getAllCardsInStudySystemTestException() {
        when(studySystemMockLogic.getAllCardsInStudySystem(any(StudySystem.class))).thenThrow(RuntimeException.class);
        final String expected = "Ein Fehler ist aufgetreten";
        final String[] actual = new String[1];
        StudySystem studySystem = new LeitnerSystem();

        studySystemController.getAllCardsInStudySystem(studySystem, new DataCallback<CardOverview>() {
            @Override
            public void onSuccess(List<CardOverview> data) {
            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

            @Override
            public void onInfo(String msg) {
            }
        });
        assertEquals(expected, actual[0]);

    }

    @Test
    public void deleteStudySystemTestNull() {
        StudySystem studySystem = new LeitnerSystem();

        doThrow(new IllegalStateException("StudySystem existiert nicht")).when(studySystemMockLogic).deleteStudySystem(studySystem);
        String expected = "StudySystem existiert nicht";
        final String[] actual = new String[1];
        studySystemController.deleteStudySystem(studySystem, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {

            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

        });
        assertEquals(expected, actual[0]);
    }

    @Test
    public void deleteStudySystemTestException() {
        StudySystem studySystem = new LeitnerSystem();

        doThrow(new RuntimeException()).when(studySystemMockLogic).deleteStudySystem(studySystem);
        String expected = "Beim Löschen der StudySystem ist ein Fehler aufgetreten";
        final String[] actual = new String[1];
        studySystemController.deleteStudySystem(studySystem, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {

            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

        });
        assertEquals(expected, actual[0]);
    }

    @Test
    public void deleteStudySystemTest() {
        StudySystem studySystem = new LeitnerSystem();

        doNothing().when(studySystemMockLogic).deleteStudySystem(studySystem);

        studySystemController.deleteStudySystem(studySystem, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
            }

            @Override
            public void onFailure(String msg) {
            }
        });
    }

    @Test
    public void getStudySystemsTestNull() {
        List<StudySystem> list = new ArrayList<>();
        when(studySystemMockLogic.getStudySystems()).thenReturn(list);
        final String expected = "Es gibt derzeit studySystems";
        final String[] actual = new String[1];
        studySystemController.getStudySystems(new DataCallback<StudySystem>() {
            @Override
            public void onSuccess(List<StudySystem> data) {

            }

            @Override
            public void onFailure(String msg) {

            }

            @Override
            public void onInfo(String msg) {
                actual[0] = msg;
            }
        });

        assertEquals(expected, actual[0]);
    }

    @Test
    public void getStudySystemsTestException() {
        when(studySystemMockLogic.getStudySystems()).thenThrow(RuntimeException.class);
        final String expected = "Ein Fehler ist aufgetreten";
        final String[] actual = new String[1];
        studySystemController.getStudySystems(new DataCallback<StudySystem>() {
            @Override
            public void onSuccess(List<StudySystem> data) {
            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

            @Override
            public void onInfo(String msg) {
            }
        });
        assertEquals(expected, actual[0]);
    }

    @Test
    public void getStudySystemsTest() {
        final List<StudySystem> list = Arrays.asList(new LeitnerSystem(), new LeitnerSystem());
        when(studySystemMockLogic.getStudySystems()).thenReturn(list);
        final List<StudySystem> expected = list;
        final List<StudySystem>[] actual = new List[1];
        studySystemController.getStudySystems(new DataCallback<StudySystem>() {
            @Override
            public void onSuccess(List<StudySystem> data) {
                actual[0] = data;
            }

            @Override
            public void onFailure(String msg) {
            }

            @Override
            public void onInfo(String msg) {
            }
        });
        assertEquals(expected, actual[0]);
    }

    @Test
    public void getStudySystemBySearchTermsNull() {
        List<StudySystem> list = new ArrayList<>();
        when(studySystemMockLogic.getStudySystemsBySearchterm(any(String.class))).thenReturn(list);
        final String expected = "Es gibt keine studySystems zu diesem Suchbegriff";
        final String[] actual = new String[1];
        studySystemController.getStudySystemBySearchTerms("Test", new DataCallback<StudySystem>() {
            @Override
            public void onSuccess(List<StudySystem> data) {

            }

            @Override
            public void onFailure(String msg) {

            }

            @Override
            public void onInfo(String msg) {
                actual[0] = msg;
            }
        });

        assertEquals(expected, actual[0]);
    }

    @Test
    public void getStudySystemBySearchTermsException() {
        when(studySystemMockLogic.getStudySystemsBySearchterm(any(String.class))).thenThrow(IllegalArgumentException.class);
        final String expected = "Ein Fehler ist aufgetreten";
        final String[] actual = new String[1];
        studySystemController.getStudySystemBySearchTerms("Test", new DataCallback<StudySystem>() {
            @Override
            public void onSuccess(List<StudySystem> data) {
            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

            @Override
            public void onInfo(String msg) {
            }
        });
        assertEquals(expected, actual[0]);
    }

    @Test
    public void getStudySystemBySearchTermsTest() {
        final List<StudySystem> list = Arrays.asList(new LeitnerSystem(), new LeitnerSystem());
        when(studySystemMockLogic.getStudySystemsBySearchterm(any(String.class))).thenReturn(list);
        final List<StudySystem> expected = list;
        final List<StudySystem>[] actual = new List[1];
        studySystemController.getStudySystemBySearchTerms("Test", new DataCallback<StudySystem>() {
            @Override
            public void onSuccess(List<StudySystem> data) {
                actual[0] = data;
            }

            @Override
            public void onFailure(String msg) {
            }

            @Override
            public void onInfo(String msg) {
            }
        });
        assertEquals(expected, actual[0]);
    }

    //

    @Test
    public void getStudySystemByUUIDNull() {
        when(studySystemMockLogic.getStudySystemByUUID(any(String.class))).thenThrow(new IllegalArgumentException("UUID darf nicht null sein"));
        final String expected = "UUID darf nicht null sein";
        final String[] actual = new String[1];
        studySystemController.getStudySystemByUUID("Test", new SingleDataCallback<StudySystem>() {
            @Override
            public void onSuccess(StudySystem studySystem) {
            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }


        });
        assertEquals(expected, actual[0]);
    }

    @Test
    public void getStudySystemByUUIDTestNoResultException() {
        when(studySystemMockLogic.getStudySystemByUUID(any(String.class))).thenThrow(new NoResultException("Es konnte kein studySystem zur UUID gefunden werden"));
        final String expected = "Es konnte kein studySystem zur UUID gefunden werden";
        final String[] actual = new String[1];
        studySystemController.getStudySystemByUUID("Test", new SingleDataCallback<StudySystem>() {
            @Override
            public void onSuccess(StudySystem studySystem) {
            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }


        });
        assertEquals(expected, actual[0]);
    }

    @Test
    public void getStudySystemByUUIDException() {
        when(studySystemMockLogic.getStudySystemByUUID(any(String.class))).thenThrow(new RuntimeException("Beim Abrufen des studySystems ist ein Fehler aufgetreten"));
        final String expected = "Beim Abrufen des studySystems ist ein Fehler aufgetreten";
        final String[] actual = new String[1];
        studySystemController.getStudySystemByUUID("Test", new SingleDataCallback<StudySystem>() {
            @Override
            public void onSuccess(StudySystem studySystem) {
            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }


        });
        assertEquals(expected, actual[0]);
    }

    @Test
    public void getStudySystemByUUIDTest() {
        final StudySystem[] studySystem = {new LeitnerSystem()};
        when(studySystemMockLogic.getStudySystemByUUID(any(String.class))).thenReturn(studySystem[0]);
        final StudySystem[] studySystem1 = new StudySystem[1];
        studySystemController.getStudySystemByUUID("Test", new SingleDataCallback<StudySystem>() {
            @Override
            public void onSuccess(StudySystem data) {
                studySystem1[0] = data;
            }

            @Override
            public void onFailure(String msg) {
            }


        });
        assertEquals(studySystem[0], studySystem1[0]);
    }

    @Test
    public void getNextCardTestException() {
        when(studySystemMockLogic.getNextCard(any(StudySystem.class))).thenThrow(new RuntimeException("Ein Fehler ist aufgetreten"));
        final String expected = "Ein Fehler ist aufgetreten";
        final String[] actual = new String[1];
        StudySystem studySystem = new LeitnerSystem();
        ;
        studySystemController.getNextCard(studySystem, new SingleDataCallback<Card>() {
            @Override
            public void onSuccess(Card card) {
            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }


        });
        assertEquals(expected, actual[0]);
    }

    @Test
    public void getNextCardTest() {
        final Card[] card = {new MultipleChoiceCard()};
        when(studySystemMockLogic.getNextCard(any(StudySystem.class))).thenReturn(card[0]);
        final Card[] card1 = new Card[1];
        studySystemController.getNextCard(new LeitnerSystem(), new SingleDataCallback<Card>() {
            @Override
            public void onSuccess(Card data) {
                card1[0] = data;
            }

            @Override
            public void onFailure(String msg) {
            }


        });
        assertEquals(card[0], card1[0]);
    }


    @Test
    public void finishTestAndGetResultException() {
        when(studySystemMockLogic.finishTestAndGetResult(any(StudySystem.class))).thenThrow(new RuntimeException("Ein Fehler ist aufgetreten"));
        final String expected = "Ein Fehler ist aufgetreten";
        final String[] actual = new String[1];
        StudySystem studySystem = new LeitnerSystem();
        ;
        studySystemController.finishTestAndGetResult(studySystem, new SingleDataCallback<Integer>() {
            @Override
            public void onSuccess(Integer s) {
            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }


        });
        assertEquals(expected, actual[0]);
    }

    @Test
    public void finishTestAndGetResultTest() {
        final int[] f = {0};
        when(studySystemMockLogic.finishTestAndGetResult(any(StudySystem.class))).thenReturn(f[0]);
        final int[] f1 = new int[1];
        studySystemController.finishTestAndGetResult(new LeitnerSystem(), new SingleDataCallback<Integer>() {
            @Override
            public void onSuccess(Integer data) {
                f1[0] = data;
            }

            @Override
            public void onFailure(String msg) {
            }


        });
        assertEquals(f[0], f1[0]);
    }


    @Test
    public void moveAllCardsForDeckToFirstBoxTestNull() {
        StudySystem studySystem = new LeitnerSystem();

        List<Card> card = new ArrayList<>();

        doThrow(new IllegalStateException()).when(studySystemMockLogic).moveAllCardsForDeckToFirstBox(card, studySystem);
        String expected = "Null Value Gegeben";
        final String[] actual = new String[1];
        studySystemController.moveAllCardsForDeckToFirstBox(card, studySystem, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {

            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

        });
        assertEquals(expected, actual[0]);
    }

    @Test
    public void moveAllCardsForDeckToFirstBoxTestException() {
        StudySystem studySystem = new LeitnerSystem();

        List<Card> card = new ArrayList<>();

        doThrow(new RuntimeException()).when(studySystemMockLogic).moveAllCardsForDeckToFirstBox(card, studySystem);
        String expected = "Null Value Gegeben";
        final String[] actual = new String[1];
        studySystemController.moveAllCardsForDeckToFirstBox(card, studySystem, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {

            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

        });
        assertEquals(expected, actual[0]);
    }

    @Test
    public void moveAllCardsForDeckToFirstBoxTest() {
        StudySystem studySystem = new LeitnerSystem();

        List<Card> card = new ArrayList<>();
        when(studySystemMockLogic.moveAllCardsForDeckToFirstBox(card, studySystem)).thenReturn(new ArrayList<>());

        studySystemController.moveAllCardsForDeckToFirstBox(card, studySystem, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
            }

            @Override
            public void onFailure(String msg) {
            }
        });
    }

    //

    @Test
    public void giveAnswerTestException() {
        StudySystem studySystem = new LeitnerSystem();
        ;
        doThrow(new RuntimeException("Ein Fehler ist aufgetreten")).when(studySystemMockLogic).giveAnswer(studySystem, true);
        String expected = "Ein Fehler ist aufgetreten";
        final String[] actual = new String[1];
        studySystemController.giveAnswer(studySystem, true, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {

            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

        });
        assertEquals(expected, actual[0]);
    }

    @Test
    public void giveAnswerTest() {
        StudySystem studySystem = new LeitnerSystem();

        doNothing().when(studySystemMockLogic).giveAnswer(studySystem, true);

        studySystemController.giveAnswer(studySystem, true, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
            }

            @Override
            public void onFailure(String msg) {
            }
        });
    }

    //

    @Test
    public void giveRatingTestException() {
        StudySystem studySystem = new LeitnerSystem();
        Card card = new TrueFalseCard();
        doThrow(new RuntimeException("Ein Fehler ist aufgetreten")).when(studySystemMockLogic).giveRating(studySystem, card, 2);
        String expected = "Ein Fehler ist aufgetreten";
        final String[] actual = new String[1];
        studySystemController.giveRating(studySystem, card, 2, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {

            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

        });
        assertEquals(expected, actual[0]);
    }

    @Test
    public void giveRatingTest() {
        StudySystem studySystem = new LeitnerSystem();
        Card card = new TrueFalseCard();

        doNothing().when(studySystemMockLogic).giveRating(studySystem, card, 2);

        studySystemController.giveRating(studySystem, card, 2, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
            }

            @Override
            public void onFailure(String msg) {
            }
        });
    }

    //

    @Test
    public void giveTimeTestException() {
        StudySystem studySystem = new LeitnerSystem();
        ;
        doThrow(new RuntimeException("Ein Fehler ist aufgetreten")).when(studySystemMockLogic).giveTime(studySystem, 2);
        String expected = "Ein Fehler ist aufgetreten";
        final String[] actual = new String[1];
        studySystemController.giveTime(studySystem, 2, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {

            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

        });
        assertEquals(expected, actual[0]);
    }

    @Test
    public void giveTimeTest() {
        StudySystem studySystem = new LeitnerSystem();

        doNothing().when(studySystemMockLogic).giveTime(studySystem, 2);

        studySystemController.giveTime(studySystem, 2, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
            }

            @Override
            public void onFailure(String msg) {
            }
        });
    }

    //

    @Test
    public void numCardsInDeckTestException() {
        when(studySystemMockLogic.numCardsInDeck(any(StudySystem.class))).thenThrow(new RuntimeException("Ein Fehler ist aufgetreten"));
        final String expected = "Ein Fehler ist aufgetreten";
        final String[] actual = new String[1];
        StudySystem studySystem = new LeitnerSystem();
        ;
        studySystemController.numCardsInDeck(studySystem, new SingleDataCallback<Integer>() {
            @Override
            public void onSuccess(Integer s) {
            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }


        });
        assertEquals(expected, actual[0]);
    }

    @Test
    public void numCardsInDeckTest() {
        final int[] f = {0};
        when(studySystemMockLogic.numCardsInDeck(any(StudySystem.class))).thenReturn(f[0]);
        final int[] f1 = new int[1];
        studySystemController.numCardsInDeck(new LeitnerSystem(), new SingleDataCallback<Integer>() {
            @Override
            public void onSuccess(Integer data) {
                f1[0] = data;
            }

            @Override
            public void onFailure(String msg) {
            }


        });
        assertEquals(f[0], f1[0]);
    }

    //

    @Test
    public void removeCardsFromStudySystemTestNull() {
        StudySystem studySystem = new LeitnerSystem();

        List<CardOverview> list = new ArrayList<>();
        doThrow(new IllegalStateException("StudySystem existiert nicht")).when(studySystemMockLogic).removeCardsFromStudySystem(list, studySystem);
        String expected = "Null Variable gegeben";
        final String[] actual = new String[1];
        studySystemController.removeCardsFromStudySystem(list, studySystem, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {

            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

        });
        assertEquals(expected, actual[0]);
    }

    @Test
    public void removeCardsFromStudySystemTestException() {
        StudySystem studySystem = new LeitnerSystem();

        List<CardOverview> list = new ArrayList<>();
        doThrow(new RuntimeException()).when(studySystemMockLogic).removeCardsFromStudySystem(list, studySystem);
        String expected = "Ein Fehler ist aufgetreten";
        final String[] actual = new String[1];
        studySystemController.removeCardsFromStudySystem(list, studySystem, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {

            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

        });
        assertEquals(expected, actual[0]);
    }

    @Test
    public void removeCardsFromStudySystemTest() {
        StudySystem studySystem = new LeitnerSystem();

        List<CardOverview> list = new ArrayList<>();
        doNothing().when(studySystemMockLogic).removeCardsFromStudySystem(list, studySystem);

        studySystemController.removeCardsFromStudySystem(list, studySystem, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
            }

            @Override
            public void onFailure(String msg) {
            }
        });
    }

    //

    @Test
    public void deleteDeckTestException() {
        StudySystem[] studySystem = {new LeitnerSystem()};

        doThrow(new RuntimeException()).when(studySystemMockLogic).deleteStudySystem(studySystem);
        String expected = "Ein Fehler ist aufgetreten";
        final String[] actual = new String[1];
        studySystemController.deleteDecks(studySystem, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {

            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

        });
        assertEquals(expected, actual[0]);
    }

    @Test
    public void deleteDeckTest() {
        StudySystem[] studySystem = {new LeitnerSystem()
        };

        doNothing().when(studySystemMockLogic).deleteStudySystem(studySystem);

        studySystemController.deleteDecks(studySystem, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
            }

            @Override
            public void onFailure(String msg) {
            }
        });
    }

    //


    @Test
    public void addCardsToStudySystemTestException() {
        StudySystem studySystem = new LeitnerSystem();
        ;

        List<CardOverview> list = new ArrayList<>();
        doThrow(new RuntimeException()).when(studySystemMockLogic).addCardsToDeck(list, studySystem);
        String expected = "Ein Fehler ist aufgetreten";
        final String[] actual = new String[1];
        studySystemController.addCardsToStudySystem(list, studySystem, new SingleDataCallback<String>() {
            @Override
            public void onSuccess(String data) {
                //TODO Mert bitte einmal die beiden Nachrichten prüfen :)
            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

        });
        assertEquals(expected, actual[0]);
    }

    @Test
    public void addCardsToStudySystemTest() {
        StudySystem studySystem = new LeitnerSystem();

        List<CardOverview> list = new ArrayList<>();
        when(studySystemMockLogic.addCardsToDeck(list, studySystem)).thenReturn(new ArrayList<Card>());

        studySystemController.addCardsToStudySystem(list, studySystem, new SingleDataCallback<String>() {
            @Override
            public void onSuccess(String data) {
            }

            @Override
            public void onFailure(String msg) {
            }
        });
    }

    //

    @Test
    public void updateDeckDataTestException() {
        StudySystem studySystem = new LeitnerSystem();

        StudySystem studySystem2 = new LeitnerSystem();

        doThrow(new RuntimeException()).when(studySystemMockLogic).updateStudySystemData(studySystem2, studySystem, true);
        String expected = "Ein Fehler ist aufgetreten";
        final String[] actual = new String[1];
        studySystemController.updateDeckData(studySystem2, studySystem, true, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {

            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

        });
        assertEquals(expected, actual[0]);
    }

    @Test
    public void updateDeckDataTest() {
        StudySystem studySystem = new LeitnerSystem();

        StudySystem studySystem2 = new LeitnerSystem();

        doNothing().when(studySystemMockLogic).updateStudySystemData(studySystem2, studySystem, true);

        studySystemController.updateDeckData(studySystem2, studySystem, true, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
            }

            @Override
            public void onFailure(String msg) {
            }
        });
    }

    //

    @Test
    public void addStudySystemTypeAndUpdateTestException() {
        StudySystem.StudySystemType studySystem = StudySystem.StudySystemType.TIMING;
        doThrow(new RuntimeException()).when(studySystemMockLogic).addStudySystemTypeAndUpdate(studySystem);
        String expected = "Ein Fehler ist aufgetreten";
        final String[] actual = new String[1];
        studySystemController.addStudySystemTypeAndUpdate(studySystem, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {

            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

        });
        assertEquals(expected, actual[0]);
    }

    @Test
    public void addStudySystemTypeAndUpdateTest() {
        StudySystem.StudySystemType studySystem = StudySystem.StudySystemType.TIMING;
        doNothing().when(studySystemMockLogic).addStudySystemTypeAndUpdate(studySystem);

        studySystemController.addStudySystemTypeAndUpdate(studySystem, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean data) {
            }

            @Override
            public void onFailure(String msg) {
            }
        });
    }

}



















