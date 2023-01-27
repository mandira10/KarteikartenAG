package com.swp.Logic.StudySystemLogicTest;

import com.gumse.gui.Locale;
import com.gumse.tools.Output;
import com.gumse.tools.Toolbox;
import com.swp.Controller.DataCallback;
import com.swp.Controller.SingleDataCallback;
import com.swp.Controller.StudySystemController;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardOverview;
import com.swp.DataModel.CardTypes.MultipleChoiceCard;
import com.swp.DataModel.CardTypes.TrueFalseCard;
import com.swp.DataModel.Category;
import com.swp.DataModel.StudySystem.BoxToCard;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.DataModel.StudySystem.StudySystemBox;
import com.swp.DataModel.StudySystem.TimingSystem;
import com.swp.Logic.StudySystemLogic;
import com.swp.Persistence.CardRepository;
import com.swp.Persistence.CardToBoxRepository;
import com.swp.Persistence.StudySystemRepository;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.joor.Reflect.on;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


/**
 * Testet die Logik Funktionen
 */
public class StudySystemLogicTest {

    private StudySystemLogic studySystemLogic = StudySystemLogic.getInstance();
    private StudySystemRepository studySystemRepository = StudySystemRepository.getInstance();
    private CardRepository cardRepository = CardRepository.getInstance();
    private CardToBoxRepository cardToBoxRepository = CardToBoxRepository.getInstance();

    private Locale locale = new Locale("German", "de");
    private int i;


    @BeforeEach
    public void beforeEach(){

        studySystemRepository = mock(StudySystemRepository.class);
        cardRepository = mock(CardRepository.class);
        cardToBoxRepository = mock(CardToBoxRepository.class);
        on(studySystemLogic).set("cardToBoxRepository",cardToBoxRepository);
        on(studySystemLogic).set("cardRepository",cardRepository);
        on(studySystemLogic).set("studySystemRepository",studySystemRepository);
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
    public void getAllCardsInStudySystemTest(){
        List<CardOverview> list = new ArrayList<>();
        when(cardRepository.findCardsByStudySystem(any(StudySystem.class))).thenReturn(list);
        StudySystem studySystem = new StudySystem() {};

        assertEquals(list, studySystemLogic.getAllCardsInStudySystem(studySystem));
    }


    @Test
    public void getProgressTest(){
        StudySystem studySystem2 = new StudySystem() {};
        studySystem2.setProgress(5);

        assertEquals(5,studySystemLogic.getProgress(studySystem2));

    }

    @Test
    public void getStudySystemsTest() {
        List<StudySystem> list = new ArrayList<>();
        when(studySystemRepository.getStudySystems()).thenReturn(list);

        assertEquals(list, studySystemLogic.getStudySystems());
    }

    @Test
    public void getStudySystemsBySearchtermTest(){
        List<StudySystem> list = new ArrayList<>();
        when(studySystemRepository.findStudySystemsContaining(anyString())).thenReturn(list);

        assertEquals(list,studySystemLogic.getStudySystemsBySearchterm("Test"));
    }


    @Test
    public void getCardsForCardOverviewTest(){
        List<Card> list = new ArrayList<>();
        when(cardRepository.getAllCardsForCardOverview(anyList())).thenReturn(list);
        List<CardOverview> l = new ArrayList<>();


        assertEquals(list,studySystemLogic.getCardsForCardOverview(l));

    }

    @Test
    public void getStudySystemByUUIDTest(){
        StudySystem studySystem = new StudySystem() {};
        when(studySystemRepository.getStudySystemByUUID(anyString())).thenReturn(studySystem);

        assertEquals(studySystem,studySystemLogic.getStudySystemByUUID("Test"));

    }


    @Test
    public void getBoxToCardTest(){
        BoxToCard boxToCard = new BoxToCard();
        when(cardToBoxRepository.getSpecific(any(Card.class),any(StudySystem.class))).thenReturn(boxToCard);
        Card card = new TrueFalseCard();
        StudySystem studySystem = new StudySystem(){};


        assertEquals(boxToCard,studySystemLogic.getBoxToCard(card,studySystem));
    }

    @Test
    public void moveCardToBox(){
        Card card = new TrueFalseCard();
        StudySystem studySystem = new StudySystem() {
        };
        StudySystemBox studySystemBox = new StudySystemBox();
        StudySystemBox studySystemBox2 = new StudySystemBox();
        StudySystemBox studySystemBox3 = new StudySystemBox();
        List<StudySystemBox> boxlist = new ArrayList<>();
        boxlist.add(studySystemBox);
        boxlist.add(studySystemBox2);
        boxlist.add(studySystemBox3);
        studySystem.setBoxes(boxlist);
        BoxToCard boxToCard = new BoxToCard(card,studySystemBox,0);
        when(cardToBoxRepository.getSpecific(card,studySystem)).thenReturn(boxToCard);
        studySystemLogic.moveCardToBox(card,2,studySystem);

        assertEquals(2,boxToCard.getBoxNumber());
        assertEquals(card,boxToCard.getCard());
        assertEquals(boxToCard.getStudySystemBox(),studySystemBox3);

    }

    @Test
    public void updateStudySystemTestNull(){
        StudySystem studySystem = new StudySystem() {
        };
        StudySystem leer = null;
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            studySystemLogic.updateStudySystemData(studySystem,leer,true);
        });

        String expectedMessage = "New Study System can't be null";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));

    }

    @Test
    public void updateStudySystemTestNeu(){
       /* StudySystem oldStudySystem = new StudySystem() {
        };
        StudySystem newStudySystem = new StudySystem() {
        };

       doNothing().when(studySystemRepository).save(newStudySystem);
       studySystemLogic.updateStudySystemData(oldStudySystem,newStudySystem,true);

        */
    }


    @Test
    public void giveAnswerTestTiming(){
        StudySystem studySystem = new StudySystem() {
        };
        studySystem.setType(StudySystem.StudySystemType.TIMING);
        studySystem.setTrueAnswerCount(0);
        studySystemLogic.giveAnswer(studySystem,true);
        assertEquals(studySystem.getTrueAnswerCount(),1);
    }

    @Test
    public void giveAnswerTestVote(){
        StudySystem studySystem = new StudySystem() {
        };
        studySystem.setType(StudySystem.StudySystemType.VOTE);
        studySystem.setTrueAnswerCount(0);
        studySystemLogic.giveAnswer(studySystem,true);
        assertEquals(studySystem.getTrueAnswerCount(),1);
    }

    @Test
    public void giveAnswerTestCustom(){
        StudySystem studySystem = new StudySystem() {
        };
        studySystem.setType(StudySystem.StudySystemType.CUSTOM);
        studySystem.setTrueAnswerCount(0);
        studySystemLogic.giveAnswer(studySystem,true);
        assertEquals(studySystem.getTrueAnswerCount(),1);
    }

    @Test
    public void giveAnswerTestLeitner(){
        List<Card> list = new ArrayList<>();
        Card card = new TrueFalseCard();
        Card card2= new TrueFalseCard();
        Card card3 = new TrueFalseCard();
        Card card4 = new TrueFalseCard();
        list.add(card);
        list.add(card2);
        list.add(card3);
        list.add(card4);
        StudySystem studySystem = new StudySystem() {
        };
        studySystem.setType(StudySystem.StudySystemType.TIMING);
        when(cardRepository.getAllCardsForTimingSystem(any(StudySystem.class))).thenReturn(list);
        studySystemLogic.getNextCard(studySystem);
        StudySystemBox studySystemBox = new StudySystemBox();
        BoxToCard boxToCard = new BoxToCard(card,studySystemBox,0);
        when(cardToBoxRepository.getSpecific(card,studySystem)).thenReturn(boxToCard);
        int boxNumber = boxToCard.getBoxNumber();
        studySystemLogic.giveAnswer(studySystem,true);

        assertEquals(boxNumber,boxToCard.getBoxNumber());


    }

































    }
