package com.swp.Controller.StudySystemControllerTest;


import com.gumse.gui.Locale;
import com.gumse.tools.Output;
import com.gumse.tools.Toolbox;
import com.swp.Controller.DataCallback;
import com.swp.Controller.SingleDataCallback;
import com.swp.Controller.StudySystemController;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardOverview;
import com.swp.DataModel.CardTypes.MultipleChoiceCard;
import com.swp.DataModel.Category;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.Logic.StudySystemLogic;
import jakarta.persistence.NoResultException;
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

    private StudySystemLogic studySystemLogic = StudySystemLogic.getInstance();
    private StudySystemController studySystemController = StudySystemController.getInstance();
    private Locale locale = new Locale("German", "de");
    private int i;
    @BeforeEach
    public void beforeEach(){
        studySystemLogic = mock(StudySystemLogic.class);
        on(studySystemController).set("studySystemLogic",studySystemLogic);
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
    public void getAllCardsInStudySystemTestEmptySet(){

        List<CardOverview> list = new ArrayList<>();
        when(studySystemLogic.getAllCardsInStudySystem(any(StudySystem.class))).thenReturn(list);
        final String expected = "Es gibt keine Karten zu diesem StudySystem";
        final String[] actual = new String[1];
        StudySystem studySystem = new StudySystem() {
        };

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
        assertEquals(expected,actual[0]);


    }

    @Test
    public void getAllCardsInStudySystemTestWithList(){
        final List<CardOverview> list = Arrays.asList(new CardOverview(), new CardOverview());
        when(studySystemLogic.getAllCardsInStudySystem(any(StudySystem.class))).thenReturn(list);
        final List<CardOverview> expected = list;
        final List<CardOverview>[] actual = new List[1];
        StudySystem studySystem = new StudySystem() {
        };
        studySystemController.getAllCardsInStudySystem(studySystem,new DataCallback<CardOverview>() {
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
    public void getAllCardsInStudySystemTestException(){
        when(studySystemLogic.getAllCardsInStudySystem(any(StudySystem.class))).thenThrow(RuntimeException.class);
        final String expected = "Ein Fehler ist aufgetreten";
        final String[] actual = new String[1];
        StudySystem studySystem = new StudySystem() {
        };
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
        assertEquals(expected,actual[0]);

    }

    @Test
    public void deleteStudySystemTestNull(){
        StudySystem studySystem = new StudySystem() {
        };
        doThrow(new IllegalStateException("StudySystem existiert nicht")).when(studySystemLogic).deleteStudySystem(studySystem);
        String expected = "StudySystem existiert nicht";
        final String[] actual = new String[1];
        studySystemController.deleteStudySystem(studySystem, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean  data) {

            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

        });
        assertEquals(expected, actual[0]);
    }

    @Test
    public void deleteStudySystemTestException(){
        StudySystem studySystem = new StudySystem() {
        };
        doThrow(new RuntimeException()).when(studySystemLogic).deleteStudySystem(studySystem);
        String expected = "Beim Löschen der StudySystem ist ein Fehler aufgetreten";
        final String[] actual = new String[1];
        studySystemController.deleteStudySystem(studySystem, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean  data) {

            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

        });
        assertEquals(expected, actual[0]);
    }

    @Test
    public void deleteStudySystemTest(){
        StudySystem studySystem = new StudySystem() {
        };
        doNothing().when(studySystemLogic).deleteStudySystem(studySystem);

        studySystemController.deleteStudySystem(studySystem, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean  data) {
            }

            @Override
            public void onFailure(String msg) {
            }
        });
    }

    @Test
    public void getStudySystemsTestNull(){
        List<StudySystem> list = new ArrayList<>();
        when(studySystemLogic.getStudySystems()).thenReturn(list);
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

        assertEquals(expected,actual[0]);
    }

    @Test
    public void getStudySystemsTestException(){
        when(studySystemLogic.getStudySystems()).thenThrow(RuntimeException.class);
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
        assertEquals(expected,actual[0]);
    }

    @Test
    public void getStudySystemsTest(){
        final List<StudySystem> list = Arrays.asList(new StudySystem() {},new StudySystem() {});
        when(studySystemLogic.getStudySystems()).thenReturn(list);
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
    public void getStudySystemBySearchTermsNull(){
        List<StudySystem> list = new ArrayList<>();
        when(studySystemLogic.getStudySystemsBySearchterm(any(String.class))).thenReturn(list);
        final String expected = "Es gibt keine studySystems zu diesem Suchbegriff";
        final String[] actual = new String[1];
        studySystemController.getStudySystemBySearchTerms("Test",new DataCallback<StudySystem>() {
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

        assertEquals(expected,actual[0]);
    }

    @Test
    public void getStudySystemBySearchTermsException(){
        when(studySystemLogic.getStudySystemsBySearchterm(any(String.class))).thenThrow(IllegalArgumentException.class);
        final String expected = "Ein Fehler ist aufgetreten";
        final String[] actual = new String[1];
        studySystemController.getStudySystemBySearchTerms("Test",new DataCallback<StudySystem>() {
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
        assertEquals(expected,actual[0]);
    }
    @Test
    public void getStudySystemBySearchTermsTest(){
        final List<StudySystem> list = Arrays.asList(new StudySystem() {},new StudySystem() {});
        when(studySystemLogic.getStudySystemsBySearchterm(any(String.class))).thenReturn(list);
        final List<StudySystem> expected = list;
        final List<StudySystem>[] actual = new List[1];
        studySystemController.getStudySystemBySearchTerms("Test",new DataCallback<StudySystem>() {
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
    public void getStudySystemByUUIDNull(){
        when(studySystemLogic.getStudySystemByUUID(any(String.class))).thenThrow(new IllegalArgumentException("UUID darf nicht null sein"));
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
        assertEquals(expected,actual[0]);
    }

    @Test
    public void getStudySystemByUUIDTestNoResultException(){
        when(studySystemLogic.getStudySystemByUUID(any(String.class))).thenThrow(new NoResultException("Es konnte kein studySystem zur UUID gefunden werden"));
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
        assertEquals(expected,actual[0]);
    }
    @Test
    public void getStudySystemByUUIDException(){
        when(studySystemLogic.getStudySystemByUUID(any(String.class))).thenThrow(new RuntimeException("Beim Abrufen des studySystems ist ein Fehler aufgetreten"));
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
        assertEquals(expected,actual[0]);
    }

    @Test
    public void getStudySystemByUUIDTest(){
        final StudySystem[] studySystem = {new StudySystem(){}};
        when(studySystemLogic.getStudySystemByUUID(any(String.class))).thenReturn(studySystem[0]);
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
    public void getNextCardTestException(){
        when(studySystemLogic.getNextCard(any(StudySystem.class))).thenThrow(new RuntimeException("Ein Fehler ist aufgetreten"));
        final String expected = "Ein Fehler ist aufgetreten";
        final String[] actual = new String[1];
        StudySystem studySystem = new StudySystem() {};
        studySystemController.getNextCard(studySystem,new SingleDataCallback<Card>() {
            @Override
            public void onSuccess(Card card) {
            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }


        });
        assertEquals(expected,actual[0]);
    }

    @Test
    public void getNextCardTest(){
        final Card[] card = {new MultipleChoiceCard()};
        when(studySystemLogic.getNextCard(any(StudySystem.class))).thenReturn(card[0]);
        final Card[] card1 = new Card[1];
        studySystemController.getNextCard(new StudySystem() {
        }, new SingleDataCallback<Card>() {
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

    //

    @Test
    public void getProgressTestException(){
        when(studySystemLogic.getProgress(any(StudySystem.class))).thenThrow(new RuntimeException("Ein Fehler ist aufgetreten"));
        final String expected = "Ein Fehler ist aufgetreten";
        final String[] actual = new String[1];
        StudySystem studySystem = new StudySystem() {};
        studySystemController.getProgress(studySystem,new SingleDataCallback<Float>() {
            @Override
            public void onSuccess(Float s) {
            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }


        });
        assertEquals(expected,actual[0]);
    }

    @Test
    public void getProgressTest(){
        final float[] f = {0};
        when(studySystemLogic.getProgress(any(StudySystem.class))).thenReturn(f[0]);
        final float[] f1 = new float[1];
        studySystemController.getProgress(new StudySystem() {
        }, new SingleDataCallback<Float>() {
            @Override
            public void onSuccess(Float data) {
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
    public void finishTestAndGetResultException(){
        when(studySystemLogic.finishTestAndGetResult(any(StudySystem.class))).thenThrow(new RuntimeException("Ein Fehler ist aufgetreten"));
        final String expected = "Ein Fehler ist aufgetreten";
        final String[] actual = new String[1];
        StudySystem studySystem = new StudySystem() {};
        studySystemController.finishTestAndGetResult(studySystem,new SingleDataCallback<Integer>() {
            @Override
            public void onSuccess(Integer s) {
            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }


        });
        assertEquals(expected,actual[0]);
    }

    @Test
    public void finishTestAndGetResultTest(){
        final int[] f = {0};
        when(studySystemLogic.finishTestAndGetResult(any(StudySystem.class))).thenReturn(f[0]);
        final int[] f1 = new int[1];
        studySystemController.finishTestAndGetResult(new StudySystem() {
        }, new SingleDataCallback<Integer>() {
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
    public void moveCardToSpecificBoxTestNull(){
        StudySystem studySystem = new StudySystem() {
        };
        Card card = new MultipleChoiceCard();

        doThrow(new IllegalStateException("Null Value Gegeben")).when(studySystemLogic).moveCardToBox(card,0,studySystem);
        String expected = "Null Value Gegeben";
        final String[] actual = new String[1];
        studySystemController.moveCardToSpecificBox(card,0,studySystem, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean  data) {

            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

        });
        assertEquals(expected, actual[0]);
    }

    @Test
    public void moveCardToSpecificBoxTestException(){
        StudySystem studySystem = new StudySystem() {
        };
        Card card = new MultipleChoiceCard();

        doThrow(new RuntimeException("Ein Fehler aufgetreten")).when(studySystemLogic).moveCardToBox(card,0,studySystem);
        String expected = "Ein Fehler aufgetreten";
        final String[] actual = new String[1];
        studySystemController.moveCardToSpecificBox(card,0,studySystem, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean  data) {

            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

        });
        assertEquals(expected, actual[0]);
    }

    @Test
    public void moveCardToSpecificBoxTest(){
        StudySystem studySystem = new StudySystem() {
        };
        Card card = new MultipleChoiceCard();
        doNothing().when(studySystemLogic).moveCardToBox(card,0,studySystem);

        studySystemController.moveCardToSpecificBox(card,0,studySystem, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean  data) {
            }

            @Override
            public void onFailure(String msg) {
            }
        });
    }

    //

    @Test
    public void moveAllCardsForDeckToFirstBoxTestNull(){
        StudySystem studySystem = new StudySystem() {
        };
        List<Card> card = new ArrayList<>();

        doThrow(new IllegalStateException()).when(studySystemLogic).moveAllCardsForDeckToFirstBox(card,studySystem);
        String expected = "Ein Fehler ist aufgetreten";
        final String[] actual = new String[1];
        studySystemController.moveAllCardsForDeckToFirstBox(card,studySystem, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean  data) {

            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

        });
        assertEquals(expected, actual[0]);
    }

    @Test
    public void moveAllCardsForDeckToFirstBoxTestException(){
        StudySystem studySystem = new StudySystem() {
        };
        List<Card> card = new ArrayList<>();

        doThrow(new RuntimeException()).when(studySystemLogic).moveAllCardsForDeckToFirstBox(card,studySystem);
        String expected = "Ein Fehler ist aufgetreten";
        final String[] actual = new String[1];
        studySystemController.moveAllCardsForDeckToFirstBox(card,studySystem, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean  data) {

            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

        });
        assertEquals(expected, actual[0]);
    }

    @Test
    public void moveAllCardsForDeckToFirstBoxTest(){
        StudySystem studySystem = new StudySystem() {
        };
        List<Card> card = new ArrayList<>();
        when(studySystemLogic.moveAllCardsForDeckToFirstBox(card,studySystem)).thenReturn(new ArrayList<>());

        studySystemController.moveAllCardsForDeckToFirstBox(card,studySystem, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean  data) {
            }

            @Override
            public void onFailure(String msg) {
            }
        });
    }

    //

    @Test
    public void giveAnswerTestException(){
        StudySystem studySystem = new StudySystem() {};
        doThrow(new RuntimeException("Ein Fehler ist aufgetreten")).when(studySystemLogic).giveAnswer(studySystem,true);
        String expected = "Ein Fehler ist aufgetreten";
        final String[] actual = new String[1];
        studySystemController.giveAnswer(studySystem,true, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean  data) {

            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

        });
        assertEquals(expected, actual[0]);
    }

    @Test
    public void giveAnswerTest(){
        StudySystem studySystem = new StudySystem() {
        };
        doNothing().when(studySystemLogic).giveAnswer(studySystem,true);

        studySystemController.giveAnswer(studySystem,true, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean  data) {
            }

            @Override
            public void onFailure(String msg) {
            }
        });
    }

    //

    @Test
    public void giveRatingTestException(){
        StudySystem studySystem = new StudySystem() {};
        doThrow(new RuntimeException("Ein Fehler ist aufgetreten")).when(studySystemLogic).giveRating(studySystem,2);
        String expected = "Ein Fehler ist aufgetreten";
        final String[] actual = new String[1];
        studySystemController.giveRating(studySystem,2, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean  data) {

            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

        });
        assertEquals(expected, actual[0]);
    }

    @Test
    public void giveRatingTest(){
        StudySystem studySystem = new StudySystem() {
        };
        doNothing().when(studySystemLogic).giveRating(studySystem,2);

        studySystemController.giveRating(studySystem,2, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean  data) {
            }

            @Override
            public void onFailure(String msg) {
            }
        });
    }

    //

    @Test
    public void giveTimeTestException(){
        StudySystem studySystem = new StudySystem() {};
        doThrow(new RuntimeException("Ein Fehler ist aufgetreten")).when(studySystemLogic).giveTime(studySystem,2);
        String expected = "Ein Fehler ist aufgetreten";
        final String[] actual = new String[1];
        studySystemController.giveTime(studySystem,2, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean  data) {

            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

        });
        assertEquals(expected, actual[0]);
    }

    @Test
    public void giveTimeTest(){
        StudySystem studySystem = new StudySystem() {
        };
        doNothing().when(studySystemLogic).giveTime(studySystem,2);

        studySystemController.giveTime(studySystem,2, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean  data) {
            }

            @Override
            public void onFailure(String msg) {
            }
        });
    }

    //

    @Test
    public void numCardsInDeckTestException(){
        when(studySystemLogic.numCardsInDeck(any(StudySystem.class))).thenThrow(new RuntimeException("Ein Fehler ist aufgetreten"));
        final String expected = "Ein Fehler ist aufgetreten";
        final String[] actual = new String[1];
        StudySystem studySystem = new StudySystem() {};
        studySystemController.numCardsInDeck(studySystem,new SingleDataCallback<Integer>() {
            @Override
            public void onSuccess(Integer s) {
            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }


        });
        assertEquals(expected,actual[0]);
    }

    @Test
    public void numCardsInDeckTest(){
        final int[] f = {0};
        when(studySystemLogic.numCardsInDeck(any(StudySystem.class))).thenReturn(f[0]);
        final int[] f1 = new int[1];
        studySystemController.numCardsInDeck(new StudySystem() {
        }, new SingleDataCallback<Integer>() {
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
    public void removeCardsFromStudySystemTestNull(){
        StudySystem studySystem = new StudySystem() {
        };
        List<CardOverview> list = new ArrayList<>();
        doThrow(new IllegalStateException("StudySystem existiert nicht")).when(studySystemLogic).removeCardsFromStudySystem(list,studySystem);
        String expected = "Null Variable gegeben";
        final String[] actual = new String[1];
        studySystemController.removeCardsFromStudySystem(list,studySystem, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean  data) {

            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

        });
        assertEquals(expected, actual[0]);
    }

    @Test
    public void removeCardsFromStudySystemTestException(){
        StudySystem studySystem = new StudySystem() {
        };
        List<CardOverview> list = new ArrayList<>();
        doThrow(new RuntimeException()).when(studySystemLogic).removeCardsFromStudySystem(list,studySystem);
        String expected = "Ein Fehler ist aufgetreten";
        final String[] actual = new String[1];
        studySystemController.removeCardsFromStudySystem(list,studySystem, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean  data) {

            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

        });
        assertEquals(expected, actual[0]);
    }

    @Test
    public void removeCardsFromStudySystemTest(){
        StudySystem studySystem = new StudySystem() {
        };
        List<CardOverview> list = new ArrayList<>();
        doNothing().when(studySystemLogic).removeCardsFromStudySystem(list,studySystem);

        studySystemController.removeCardsFromStudySystem(list,studySystem, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean  data) {
            }

            @Override
            public void onFailure(String msg) {
            }
        });
    }

    //

    @Test
    public void deleteDeckTestException(){
        StudySystem[] studySystem = {new StudySystem() {
        }};

        doThrow(new RuntimeException()).when(studySystemLogic).deleteStudySystem(studySystem);
        String expected = "Ein Fehler ist aufgetreten";
        final String[] actual = new String[1];
        studySystemController.deleteDecks(studySystem, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean  data) {

            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

        });
        assertEquals(expected, actual[0]);
    }

    @Test
    public void deleteDeckTest(){
        StudySystem[] studySystem = {new StudySystem() {
        }};

        doNothing().when(studySystemLogic).deleteStudySystem(studySystem);

        studySystemController.deleteDecks(studySystem, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean  data) {
            }

            @Override
            public void onFailure(String msg) {
            }
        });
    }

    //


    @Test
    public void addCardsToStudySystemTestException(){
        StudySystem studySystem = new StudySystem() {
        };
        List<CardOverview> list = new ArrayList<>();
        doThrow(new RuntimeException()).when(studySystemLogic).addCardsToDeck(list,studySystem);
        String expected = "Ein Fehler ist aufgetreten";
        final String[] actual = new String[1];
        studySystemController.addCardsToStudySystem(list,studySystem, new SingleDataCallback<String>() {
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
    public void addCardsToStudySystemTest(){
        StudySystem studySystem = new StudySystem() {
        };
        List<CardOverview> list = new ArrayList<>();
        when(studySystemLogic.addCardsToDeck(list,studySystem)).thenReturn(new ArrayList<Card>());

        studySystemController.addCardsToStudySystem(list,studySystem, new SingleDataCallback<String>() {
            @Override
            public void onSuccess(String  data) {
            }

            @Override
            public void onFailure(String msg) {
            }
        });
    }

    //

    @Test
    public void updateDeckDataTestException(){
        StudySystem studySystem = new StudySystem() {
        };
        StudySystem studySystem2 = new StudySystem() {
        };
        doThrow(new RuntimeException()).when(studySystemLogic).updateStudySystemData(studySystem2,studySystem,true);
        String expected = "Ein Fehler ist aufgetreten";
        final String[] actual = new String[1];
        studySystemController.updateDeckData(studySystem2,studySystem,true, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean  data) {

            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

        });
        assertEquals(expected, actual[0]);
    }

    @Test
    public void updateDeckDataTest(){
        StudySystem studySystem = new StudySystem() {
        };
        StudySystem studySystem2 = new StudySystem() {
        };
        doNothing().when(studySystemLogic).updateStudySystemData(studySystem2,studySystem,true);

        studySystemController.updateDeckData(studySystem2,studySystem,true, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean  data) {
            }

            @Override
            public void onFailure(String msg) {
            }
        });
    }

    //

    @Test
    public void addStudySystemTypeAndUpdateTestException(){
        StudySystem.StudySystemType studySystem = StudySystem.StudySystemType.TIMING;
        doThrow(new RuntimeException()).when(studySystemLogic).addStudySystemTypeAndUpdate(studySystem);
        String expected = "Ein Fehler ist aufgetreten";
        final String[] actual = new String[1];
        studySystemController.addStudySystemTypeAndUpdate(studySystem, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean  data) {

            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

        });
        assertEquals(expected, actual[0]);
    }

    @Test
    public void addStudySystemTypeAndUpdateTest(){
        StudySystem.StudySystemType studySystem = StudySystem.StudySystemType.TIMING;
        doNothing().when(studySystemLogic).addStudySystemTypeAndUpdate(studySystem);

        studySystemController.addStudySystemTypeAndUpdate(studySystem, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean  data) {
            }

            @Override
            public void onFailure(String msg) {
            }
        });
    }

    //

    @Test
    public void createBoxToCardForCategoryTestException(){
        StudySystem studySystem = new StudySystem() {
        };
        Category category = new Category();
        doThrow(new RuntimeException()).when(studySystemLogic).createBoxToCardForCategory(category,studySystem);
        String expected = "Ein Fehler ist aufgetreten";
        final String[] actual = new String[1];
        studySystemController.createBoxToCardForCategory(category,studySystem, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean  data) {

            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

        });
        assertEquals(expected, actual[0]);
    }

    @Test
    public void createBoxToCardForCategoryTest(){
        StudySystem studySystem = new StudySystem() {
        };
        Category category = new Category();
        doNothing().when(studySystemLogic).createBoxToCardForCategory(category,studySystem);

        studySystemController.createBoxToCardForCategory(category,studySystem, new SingleDataCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean  data) {
            }

            @Override
            public void onFailure(String msg) {
            }
        });
    }


















}
