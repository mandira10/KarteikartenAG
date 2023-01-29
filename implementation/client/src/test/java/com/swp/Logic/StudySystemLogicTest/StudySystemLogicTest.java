package com.swp.Logic.StudySystemLogicTest;

import com.gumse.gui.Locale;
import com.gumse.tools.Output;
import com.gumse.tools.Toolbox;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardOverview;
import com.swp.DataModel.CardTypes.TrueFalseCard;
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

import java.sql.Timestamp;
import java.util.ArrayList;
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
    private StudySystemRepository studySystemRepository;
    private CardRepository cardRepository;
    private CardToBoxRepository cardToBoxRepository;
    private List<Card> testingBoxMockCards ;



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
//        studySystemLogic.giveAnswer(studySystem,true);
 //       assertEquals(studySystem.getTrueAnswerCount(),1);
        //TODO
    }

    @Test
    public void giveAnswerTestVote(){
        StudySystem studySystem = new StudySystem() {
        };
        studySystem.setType(StudySystem.StudySystemType.VOTE);
        studySystem.setTrueAnswerCount(0);
//        studySystemLogic.giveAnswer(studySystem,true);
   //     assertEquals(studySystem.getTrueAnswerCount(),1);
        //TODO
    }

    @Test
    public void giveAnswerTestCustom(){
        StudySystem studySystem = new StudySystem() {
        };
        studySystem.setType(StudySystem.StudySystemType.CUSTOM);
        studySystem.setTrueAnswerCount(0);
    //    studySystemLogic.giveAnswer(studySystem,true);
   //     assertEquals(studySystem.getTrueAnswerCount(),1);
        //TODO
    }




    @Test
    public void giveAnswerTestLeitnerTrueBoxZero(){
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
        studySystem.setType(StudySystem.StudySystemType.LEITNER);
        testingBoxMockCards = list;
        on(studySystemLogic).set("testingBoxCards",testingBoxMockCards);
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
        int boxNumber = boxToCard.getBoxNumber();
        int date = boxToCard.getLearnedNextAt().getDate();
        studySystemLogic.giveAnswer(studySystem,true);

        //TODO

     //   assertNotEquals(boxNumber,boxToCard.getBoxNumber());
//        assertNotEquals(date,boxToCard.getLearnedNextAt().getDate());



    }

    @Test
    public void giveAnswerTestLeitnerTrueBoxFour(){
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
        studySystem.setType(StudySystem.StudySystemType.LEITNER);
        testingBoxMockCards = list;
        on(studySystemLogic).set("testingBoxCards",testingBoxMockCards);
        StudySystemBox studySystemBox = new StudySystemBox();
        StudySystemBox studySystemBox2 = new StudySystemBox();
        StudySystemBox studySystemBox3 = new StudySystemBox();
        StudySystemBox studySystemBox4 = new StudySystemBox();
        List<StudySystemBox> boxlist = new ArrayList<>();
        boxlist.add(studySystemBox);
        boxlist.add(studySystemBox2);
        boxlist.add(studySystemBox3);
        boxlist.add(studySystemBox4);
        studySystem.setBoxes(boxlist);
        BoxToCard boxToCard = new BoxToCard(card,studySystemBox4,4);
        when(cardToBoxRepository.getSpecific(card,studySystem)).thenReturn(boxToCard);
        int boxNumber = boxToCard.getBoxNumber();
        Timestamp timestamp = boxToCard.getLearnedNextAt();
        studySystemLogic.giveAnswer(studySystem,true);

        assertEquals(boxNumber,boxToCard.getBoxNumber());
        assertNotEquals(timestamp,boxToCard.getLearnedNextAt());



    }

    @Test
    public void giveAnswerTestLeitnerFalseBoxZero(){
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
        studySystem.setType(StudySystem.StudySystemType.LEITNER);
        testingBoxMockCards = list;
        on(studySystemLogic).set("testingBoxCards",testingBoxMockCards);
        StudySystemBox studySystemBox = new StudySystemBox();
        StudySystemBox studySystemBox2 = new StudySystemBox();
        StudySystemBox studySystemBox3 = new StudySystemBox();
        StudySystemBox studySystemBox4 = new StudySystemBox();
        List<StudySystemBox> boxlist = new ArrayList<>();
        boxlist.add(studySystemBox);
        boxlist.add(studySystemBox2);
        boxlist.add(studySystemBox3);
        boxlist.add(studySystemBox4);
        studySystem.setBoxes(boxlist);
        BoxToCard boxToCard = new BoxToCard(card,studySystemBox,0);
        when(cardToBoxRepository.getSpecific(card,studySystem)).thenReturn(boxToCard);
        int boxNumber = boxToCard.getBoxNumber();
        int date = boxToCard.getLearnedNextAt().getDate();
        studySystemLogic.giveAnswer(studySystem,false);

        assertEquals(boxNumber,boxToCard.getBoxNumber());
        assertEquals(date,boxToCard.getLearnedNextAt().getDate());

    }

    @Test
    public void giveAnswerTestLeitnerFalseBox4(){
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
        studySystem.setType(StudySystem.StudySystemType.LEITNER);
        testingBoxMockCards = list;
        on(studySystemLogic).set("testingBoxCards",testingBoxMockCards);
        StudySystemBox studySystemBox = new StudySystemBox();
        StudySystemBox studySystemBox2 = new StudySystemBox();
        StudySystemBox studySystemBox3 = new StudySystemBox();
        StudySystemBox studySystemBox4 = new StudySystemBox();
        List<StudySystemBox> boxlist = new ArrayList<>();
        boxlist.add(studySystemBox);
        boxlist.add(studySystemBox2);
        boxlist.add(studySystemBox3);
        boxlist.add(studySystemBox4);
        studySystem.setBoxes(boxlist);
        BoxToCard boxToCard = new BoxToCard(card,studySystemBox4,4);
        when(cardToBoxRepository.getSpecific(card,studySystem)).thenReturn(boxToCard);
        int boxNumber = boxToCard.getBoxNumber();
        Timestamp timestamp = boxToCard.getLearnedNextAt();
        studySystemLogic.giveAnswer(studySystem,false);

        assertNotEquals(boxNumber,boxToCard.getBoxNumber());
        //assertNotEquals(timestamp,boxToCard.getLearnedNextAt());

    }



    @Test
    public void getNextCardTestCustomTiming(){
        StudySystem studySystem = new StudySystem() {
        };

        studySystem.setType(StudySystem.StudySystemType.TIMING);
        List<Card> list = new ArrayList<>();
        Card card = new TrueFalseCard();
        Card card2= new TrueFalseCard();
        Card card3 = new TrueFalseCard();
        Card card4 = new TrueFalseCard();
        list.add(card);
        list.add(card2);
        list.add(card3);
        list.add(card4);
        testingBoxMockCards = list;
        on(studySystemLogic).set("testingBoxCards",testingBoxMockCards);
        Card test = studySystemLogic.getNextCard(studySystem);
        assertEquals(test,card);


    }

    @Test
    public void getNextCardTestLeitner(){
        StudySystem studySystem = new StudySystem() {
        };

        studySystem.setType(StudySystem.StudySystemType.LEITNER);
        List<Card> list = new ArrayList<>();
        Card card = new TrueFalseCard();
        Card card2= new TrueFalseCard();
        Card card3 = new TrueFalseCard();
        Card card4 = new TrueFalseCard();
        list.add(card);
        list.add(card2);
        list.add(card3);
        list.add(card4);
        testingBoxMockCards = list;
        on(studySystemLogic).set("testingBoxCards",testingBoxMockCards);
        Card test = studySystemLogic.getNextCard(studySystem);
        assertEquals(test,card);

    }

    @Test
    public void getNextCardTestVote(){
        StudySystem studySystem = new StudySystem() {
        };

        studySystem.setType(StudySystem.StudySystemType.VOTE);
        List<Card> list = new ArrayList<>();
        Card card = new TrueFalseCard();
        Card card2= new TrueFalseCard();
        Card card3 = new TrueFalseCard();
        Card card4 = new TrueFalseCard();
        list.add(card);
        list.add(card2);
        list.add(card3);
        list.add(card4);
        testingBoxMockCards = list;
        on(studySystemLogic).set("testingBoxCards",testingBoxMockCards);
        Card test = studySystemLogic.getNextCard(studySystem);
        assertEquals(test,card);

    }

    @Test
    public void giveRatingTest(){
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
        studySystem.setType(StudySystem.StudySystemType.VOTE);
        testingBoxMockCards = list;
        on(studySystemLogic).set("testingBoxCards",testingBoxMockCards);
        StudySystemBox studySystemBox = new StudySystemBox();
        StudySystemBox studySystemBox2 = new StudySystemBox();
        StudySystemBox studySystemBox3 = new StudySystemBox();
        List<StudySystemBox> boxlist = new ArrayList<>();
        boxlist.add(studySystemBox);
        boxlist.add(studySystemBox2);
        boxlist.add(studySystemBox3);
        studySystem.setBoxes(boxlist);
        BoxToCard boxToCard = new BoxToCard(card,studySystemBox,0);
       int test = boxToCard.getRating();
        when(cardToBoxRepository.getSpecific(any(Card.class),any(StudySystem.class))).thenReturn(boxToCard);
        studySystemLogic.giveRating(studySystem, card, 5);
        int value = boxToCard.getRating();
        //TODO
 //       assertNotEquals(test,value);
    }


    @Test
    public void giveTimeTest(){
        StudySystem timingSystem = new TimingSystem();
        timingSystem.setTrueAnswerCount(5);
        int before = timingSystem.getTrueAnswerCount();
        studySystemLogic.giveTime(timingSystem, 10);
        int after = timingSystem.getTrueAnswerCount();
        assertNotEquals(before,after);
    }



    @Test
    public void finishTestAndGetResultTestEmpty(){
        StudySystem studySystem = new TimingSystem();
        testingBoxMockCards = new ArrayList<>();
        on(studySystemLogic).set("testingBoxCards",testingBoxMockCards);
        //TODO
//        Exception exception = assertThrows(NoResultException.class, () -> {
//            studySystemLogic.finishTestAndGetResult(studySystem);
//        });
       // String expectedMessage = "Es wurden keine Karten gelernt";
        //String actualMessage = exception.getMessage();
        //assertEquals(expectedMessage,actualMessage);
    }


    @Test
    public void finishTestAndGetResultTestFirst(){
        StudySystem studySystem = new TimingSystem();
        testingBoxMockCards = new ArrayList<>();
        Card card = new TrueFalseCard();
        Card card2 = new TrueFalseCard();
        testingBoxMockCards.add(card);
        testingBoxMockCards.add(card2);
        studySystem.setQuestionCount(testingBoxMockCards.size()-1);
        on(studySystemLogic).set("testingBoxCards",testingBoxMockCards);
        studySystem.setTrueAnswerCount(2);
        int expected = 100;
        double progress = 1;
        when(studySystemRepository.update(any(StudySystem.class))).thenReturn(studySystem);
        int actual = studySystemLogic.finishTestAndGetResult(studySystem);
       // assertEquals(expected,actual);
       // assertEquals(progress,studySystem.getProgress());
       // assertEquals(0,studySystem.getQuestionCount());
        //assertEquals(0,studySystem.getTrueAnswerCount());
        //TODO
    }

    @Test
    public void finishTestAndGetResultTestSecond(){
        StudySystem studySystem = new TimingSystem();
        testingBoxMockCards = new ArrayList<>();
        Card card = new TrueFalseCard();
        Card card2 = new TrueFalseCard();
        Card card3 = new TrueFalseCard();
        testingBoxMockCards.add(card);
        testingBoxMockCards.add(card2);
        testingBoxMockCards.add(card3);
        studySystem.setQuestionCount(1);
        on(studySystemLogic).set("testingBoxCards",testingBoxMockCards);
        studySystem.setTrueAnswerCount(1);
        int expected = 33;
        double expected2 = 0.33;
        when(studySystemRepository.update(any(StudySystem.class))).thenReturn(studySystem);
        int actual = studySystemLogic.finishTestAndGetResult(studySystem);
        //TODO
       // assertEquals(expected,actual);
       // assertEquals(expected2,studySystem.getProgress());

    }

    @Test
    public void deleteStudySystemTestNull(){
        StudySystem studySystem = null;
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            studySystemLogic.deleteStudySystem(studySystem);
        });
        String expected = "Karte existiert nicht";
        String actual = exception.getMessage();
        assertEquals(expected,actual);
    }


    @Test
    public void moveAllCardsForDeckToFirstBoxTestAlreadyExist(){
        List<Card> list = new ArrayList<>();
        Card card = new TrueFalseCard();
        list.add(card);
        StudySystem studySystem = new TimingSystem();
        when(cardRepository.findCardByStudySystem(any(StudySystem.class),any(Card.class))).thenReturn(card);
        List<Card> check = studySystemLogic.moveAllCardsForDeckToFirstBox(list,studySystem);
        assertEquals(list,check);
    }








































    }
