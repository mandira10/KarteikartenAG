package com.swp.Logic.StudySystemLogicTest;

import com.gumse.gui.Locale;
import com.gumse.tools.Output;
import com.gumse.tools.Toolbox;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardOverview;
import com.swp.DataModel.CardTypes.TrueFalseCard;
import com.swp.DataModel.StudySystem.*;
import com.swp.Logic.StudySystemLogic;
import com.swp.Persistence.CardRepository;
import com.swp.Persistence.CardToBoxRepository;
import com.swp.Persistence.StudySystemRepository;
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
    public void giveAnswerTestTimingTrue(){
        StudySystem studySystem = new TimingSystem();
        StudySystemBox ssb = new StudySystemBox();
        StudySystemBox ssb2 = new StudySystemBox();
        List<StudySystemBox> listbox = new ArrayList<>();
        listbox.add(ssb);
        listbox.add(ssb2);
        studySystem.setBoxes(listbox);
        List<Card> list = new ArrayList<>();
        Card card = new TrueFalseCard();
        Card card2 = new TrueFalseCard();
        Card card3 = new TrueFalseCard();
        list.add(card);
        list.add(card2);
        list.add(card3);
        testingBoxMockCards = list;
        on(studySystemLogic).set("testingBoxCards",testingBoxMockCards);
        BoxToCard boxToCard = new BoxToCard(card,ssb);
        when(studySystemLogic.getBoxToCard(card,studySystem)).thenReturn(boxToCard);
        int test1 = studySystem.getTrueAnswerCount();
        boxToCard.setStatus(BoxToCard.CardStatus.RELEARN);
        BoxToCard.CardStatus status = boxToCard.getStatus();
        when(cardToBoxRepository.update(any(BoxToCard.class))).thenReturn(boxToCard);
        studySystemLogic.giveAnswer(studySystem,true);
        assertNotEquals(test1,studySystem.getTrueAnswerCount());
        assertEquals(ssb2,boxToCard.getStudySystemBox());
        assertNotEquals(status,boxToCard.getStatus());
    }

    @Test
    public void giveAnswerTestTimingFalse(){
        StudySystem studySystem = new TimingSystem();
        StudySystemBox ssb = new StudySystemBox();
        StudySystemBox ssb2 = new StudySystemBox();
        List<StudySystemBox> listbox = new ArrayList<>();
        listbox.add(ssb);
        listbox.add(ssb2);
        studySystem.setBoxes(listbox);
        List<Card> list = new ArrayList<>();
        Card card = new TrueFalseCard();
        Card card2 = new TrueFalseCard();
        Card card3 = new TrueFalseCard();
        list.add(card);
        list.add(card2);
        list.add(card3);
        testingBoxMockCards = list;
        on(studySystemLogic).set("testingBoxCards",testingBoxMockCards);
        BoxToCard boxToCard = new BoxToCard(card,ssb);
        when(studySystemLogic.getBoxToCard(card,studySystem)).thenReturn(boxToCard);
        int test1 = studySystem.getTrueAnswerCount();
        boxToCard.setStatus(BoxToCard.CardStatus.SKILLED);
        BoxToCard.CardStatus status = boxToCard.getStatus();
        when(cardToBoxRepository.update(any(BoxToCard.class))).thenReturn(boxToCard);
        studySystemLogic.giveAnswer(studySystem,false);
        assertEquals(test1,studySystem.getTrueAnswerCount());
        assertNotEquals(status,boxToCard.getStatus());
        assertEquals(card,testingBoxMockCards.get(testingBoxMockCards.size()-1));
    }

    @Test
    public void giveAnswerTestVoteTrue(){
        StudySystem studySystem = new VoteSystem();
        StudySystemBox ssb = new StudySystemBox();
        StudySystemBox ssb2 = new StudySystemBox();
        List<StudySystemBox> listbox = new ArrayList<>();
        listbox.add(ssb);
        listbox.add(ssb2);
        studySystem.setBoxes(listbox);
        List<Card> list = new ArrayList<>();
        Card card = new TrueFalseCard();
        Card card2 = new TrueFalseCard();
        Card card3 = new TrueFalseCard();
        list.add(card);
        list.add(card2);
        list.add(card3);
        testingBoxMockCards = list;
        on(studySystemLogic).set("testingBoxCards",testingBoxMockCards);
        BoxToCard boxToCard = new BoxToCard(card,ssb);
        when(studySystemLogic.getBoxToCard(card,studySystem)).thenReturn(boxToCard);
        int test1 = studySystem.getTrueAnswerCount();
        boxToCard.setStatus(BoxToCard.CardStatus.RELEARN);
        BoxToCard.CardStatus status = boxToCard.getStatus();
        when(cardToBoxRepository.update(any(BoxToCard.class))).thenReturn(boxToCard);
        studySystemLogic.giveAnswer(studySystem,true);
        assertNotEquals(test1,studySystem.getTrueAnswerCount());
        assertEquals(ssb2,boxToCard.getStudySystemBox());
        assertNotEquals(status,boxToCard.getStatus());
    }

    @Test
    public void giveAnswerTestVoteFalse(){
        StudySystem studySystem = new VoteSystem();
        StudySystemBox ssb = new StudySystemBox();
        StudySystemBox ssb2 = new StudySystemBox();
        List<StudySystemBox> listbox = new ArrayList<>();
        listbox.add(ssb);
        listbox.add(ssb2);
        studySystem.setBoxes(listbox);
        List<Card> list = new ArrayList<>();
        Card card = new TrueFalseCard();
        Card card2 = new TrueFalseCard();
        Card card3 = new TrueFalseCard();
        list.add(card);
        list.add(card2);
        list.add(card3);
        testingBoxMockCards = list;
        on(studySystemLogic).set("testingBoxCards",testingBoxMockCards);
        BoxToCard boxToCard = new BoxToCard(card,ssb);
        when(studySystemLogic.getBoxToCard(card,studySystem)).thenReturn(boxToCard);
        int test1 = studySystem.getTrueAnswerCount();
        boxToCard.setStatus(BoxToCard.CardStatus.SKILLED);
        BoxToCard.CardStatus status = boxToCard.getStatus();
        when(cardToBoxRepository.update(any(BoxToCard.class))).thenReturn(boxToCard);
        studySystemLogic.giveAnswer(studySystem,false);
        assertEquals(test1,studySystem.getTrueAnswerCount());
        assertNotEquals(status,boxToCard.getStatus());
        assertEquals(card,testingBoxMockCards.get(testingBoxMockCards.size()-1));
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
        StudySystem studySystem = new LeitnerSystem();
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
        BoxToCard boxToCard = new BoxToCard(card,studySystemBox);
        when(studySystemLogic.getBoxToCard(card,studySystem)).thenReturn(boxToCard);
        boxToCard.getStudySystemBox().setBoxNumber(0);
        int test1 = studySystem.getTrueAnswerCount();
        long timeold = boxToCard.getLearnedNextAt().getTime();
        boxToCard.setStatus(BoxToCard.CardStatus.SKILLED);
        BoxToCard.CardStatus status = boxToCard.getStatus();
        studySystemLogic.giveAnswer(studySystem,true);
        assertNotEquals(test1,studySystem.getTrueAnswerCount());
        //assertNotEquals(timeold,boxToCard.getLearnedNextAt().getTime());
        assertNotEquals(status,boxToCard.getStatus());

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
        BoxToCard boxToCard = new BoxToCard(card,studySystemBox4);
        boxToCard.getStudySystemBox().setBoxNumber(4);
        when(cardToBoxRepository.getSpecific(card,studySystem)).thenReturn(boxToCard);
        int boxNumber = boxToCard.getStudySystemBox().getBoxNumber();
        Timestamp timestamp = boxToCard.getLearnedNextAt();
        boxToCard.setStatus(BoxToCard.CardStatus.RELEARN);
        BoxToCard.CardStatus status = boxToCard.getStatus();
        studySystemLogic.giveAnswer(studySystem,true);

        assertNotEquals(status,boxToCard.getStatus());
        assertEquals(boxNumber,boxToCard.getStudySystemBox().getBoxNumber());
        //assertNotEquals(timestamp,boxToCard.getLearnedNextAt());



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
        StudySystem studySystem = new LeitnerSystem();
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
        BoxToCard boxToCard = new BoxToCard(card,studySystemBox);
        when(cardToBoxRepository.getSpecific(card,studySystem)).thenReturn(boxToCard);
        BoxToCard.CardStatus status = boxToCard.getStatus();
        studySystemLogic.giveAnswer(studySystem,false);

        assertNotEquals(status,boxToCard.getStatus());
        //Time Check TODO
        assertEquals(card,testingBoxMockCards.get(testingBoxMockCards.size()-1));

    }

    @Test //TODO adapt to new way
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
        StudySystem studySystem = new LeitnerSystem();
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
        BoxToCard boxToCard = new BoxToCard(card,studySystemBox4);
        when(cardToBoxRepository.getSpecific(card,studySystem)).thenReturn(boxToCard);
        Timestamp timestamp = boxToCard.getLearnedNextAt();
        studySystemLogic.giveAnswer(studySystem,false);

        //assertEquals(studySystemBox3,boxToCard.getStudySystemBox());
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
        StudySystem studySystem = new VoteSystem();
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
        BoxToCard boxToCard = new BoxToCard(card,studySystemBox);
        int test = boxToCard.getRating();
        when(studySystemLogic.getBoxToCard(any(Card.class),any(StudySystem.class))).thenReturn(boxToCard);
        studySystemLogic.giveRating(studySystem, card, 5);
        int value = boxToCard.getRating();
        assertNotEquals(test,value);
    }

    @Test
    public void giveRatingTestException(){
        StudySystem studySystem = new TimingSystem();
        Card card = new TrueFalseCard();
        IllegalStateException thrown = assertThrows(IllegalStateException.class, () -> {
            studySystemLogic.giveRating(studySystem,card,5);
        });

        assertEquals("Falsches StudySystem", thrown.getMessage());
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
    public void finishTestAndGetResultTestZeroResult(){
        StudySystem studySystem = new TimingSystem();
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
        studySystem.setTrueAnswerCount(0);
        int result = studySystemLogic.finishTestAndGetResult(studySystem);
        assertEquals(testingBoxMockCards.size(),0);
        assertEquals(result,0);
    }


    @Test
    public void finishTestAndGetResultTestWithResultNotEmptyList(){
        StudySystem studySystem = new TimingSystem();
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
        studySystem.setTrueAnswerCount(1);
        studySystem.setQuestionCount(2);
        int expected = 50;
        int actual = studySystemLogic.finishTestAndGetResult(studySystem);
        assertEquals(expected,actual);
        assertEquals(studySystem.getTrueAnswerCount(),0);
        assertEquals(studySystem.getQuestionCount(),0);
        assertEquals(testingBoxMockCards.size(),0);


    }

    @Test
    public void finishTestAndGetResultTestWithResultEmptyListNotLeiterSystem(){
        StudySystem studySystem = new TimingSystem();
        List<Card> list = new ArrayList<>();
        testingBoxMockCards = list;
        on(studySystemLogic).set("testingBoxCards",testingBoxMockCards);
        studySystem.setTrueAnswerCount(1);
        studySystem.setQuestionCount(2);
        int expected = 50;
        int actual = studySystemLogic.finishTestAndGetResult(studySystem);
        assertEquals(expected,actual);
        assertEquals(studySystem.getTrueAnswerCount(),0);
        assertEquals(studySystem.getQuestionCount(),0);
        //TODO
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
