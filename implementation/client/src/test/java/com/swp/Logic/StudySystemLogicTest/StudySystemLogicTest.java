package com.swp.Logic.StudySystemLogicTest;

import clojure.lang.IFn;
import com.gumse.gui.Locale;
import com.gumse.tools.Output;
import com.gumse.tools.Toolbox;
import com.swp.Controller.ControllerThreadPool;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardOverview;
import com.swp.DataModel.CardTypes.TrueFalseCard;
import com.swp.DataModel.Language.German;
import com.swp.DataModel.StudySystem.*;
import com.swp.Logic.StudySystemLogic;
import com.swp.Persistence.*;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.BeforeAll;
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
 *
 * @Author Mert As
 */

public class StudySystemLogicTest {

    private final StudySystemLogic studySystemLogic = StudySystemLogic.getInstance();
    private StudySystemRepository studySystemMockRepo;
    
    private StudySystemBoxRepository studySystemBoxMockRepo;
    private CardRepository cardMockRepo;
    private CardToBoxRepository cardToBoxMockRepo;
    private List<Card> testingBoxMockCards ;

    @BeforeAll
    public static void before()
    {
        PersistenceManager.init("KarteikartenDBTest");
        German.getInstance().activate();
        ControllerThreadPool.getInstance().synchronizedTasks(true);
    }


    private final Locale locale = new Locale("German", "de");
    private int i;


    @BeforeEach
    public void beforeEach(){
        studySystemMockRepo = mock(StudySystemRepository.class);
        cardMockRepo = mock(CardRepository.class);
        cardToBoxMockRepo = mock(CardToBoxRepository.class);
        studySystemBoxMockRepo = mock(StudySystemBoxRepository.class);
        on(studySystemLogic).set("cardToBoxRepository",cardToBoxMockRepo);
        on(studySystemLogic).set("cardRepository",cardMockRepo);
        on(studySystemLogic).set("studySystemRepository",studySystemMockRepo);
        on(studySystemLogic).set("studySystemBoxRepository",studySystemBoxMockRepo);
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
        when(cardMockRepo.findCardsByStudySystem(any(StudySystem.class))).thenReturn(list);
        StudySystem studySystem = new TimingSystem();
        assertEquals(list, studySystemLogic.getAllCardsInStudySystem(studySystem));
    }

    @Test
    public void getStudySystemsTest() {
        List<StudySystem> list = new ArrayList<>();
        when(studySystemMockRepo.getStudySystems()).thenReturn(list);
        assertEquals(list, studySystemLogic.getStudySystems());
    }

    @Test
    public void getStudySystemsBySearchtermTest(){
        List<StudySystem> list = new ArrayList<>();
        when(studySystemMockRepo.findStudySystemsContaining(anyString())).thenReturn(list);
        assertEquals(list,studySystemLogic.getStudySystemsBySearchterm("Test"));
    }


    @Test
    public void getCardsForCardOverviewTest(){
        List<Card> list = new ArrayList<>();
        when(cardMockRepo.getAllCardsForCardOverview(anyList())).thenReturn(list);
        List<CardOverview> l = new ArrayList<>();
        assertEquals(list,studySystemLogic.getCardsForCardOverview(l));

    }

    @Test
    public void getStudySystemByUUIDTest(){
        StudySystem studySystem = new TimingSystem();
        when(studySystemMockRepo.getStudySystemByUUID(anyString())).thenReturn(studySystem);
        assertEquals(studySystem,studySystemLogic.getStudySystemByUUID("Test"));

    }


    @Test
    public void getBoxToCardTest(){
        BoxToCard boxToCard = new BoxToCard();
        when(cardToBoxMockRepo.getSpecific(any(Card.class),any(StudySystem.class))).thenReturn(boxToCard);
        Card card = new TrueFalseCard();
        StudySystem studySystem = new TimingSystem();
        assertEquals(boxToCard,studySystemLogic.getBoxToCard(card,studySystem));
    }


    @Test
    public void updateStudySystemTestNull(){
        StudySystem studySystem = new TimingSystem();
        StudySystem leer = null;
        Exception exception = assertThrows(IllegalArgumentException.class, () -> {
            studySystemLogic.updateStudySystemData(studySystem,leer,true, false);
        });
        String expectedMessage = "studysystemnullerror";
        String actualMessage = exception.getMessage();
        assertTrue(actualMessage.contains(expectedMessage));

    }

    @Test
    public void updateStudySystemTestNeu(){
        StudySystem studySystem = new VoteSystem();
        StudySystem studySystem1 = new TimingSystem();
        when(studySystemMockRepo.save(studySystem1)).thenReturn(studySystem1);
        studySystemLogic.updateStudySystemData(studySystem,studySystem1,true, false);
        verify(studySystemMockRepo).save(studySystem1);
    }

    @Test
    public void updateStudySystemTestElseIfFalse(){
        StudySystem oldstudySystem = new VoteSystem();
        StudySystem newstudySystem = new TimingSystem();
        List<CardOverview> cardsToStudySystem = new ArrayList<>();
        List<Card> cards = new ArrayList<>();
        CardOverview cardOverview = new CardOverview();
        CardOverview cardOverview1 = new CardOverview();
        Card card = new TrueFalseCard();
        Card card1 = new TrueFalseCard();
        cardsToStudySystem.add(cardOverview);
        cardsToStudySystem.add(cardOverview1);
        cards.add(card);
        cards.add(card1);
        when(cardMockRepo.findCardsByStudySystem(oldstudySystem)).thenReturn(cardsToStudySystem);
        when(cardMockRepo.getAllCardsForCardOverview(cardsToStudySystem)).thenReturn(cards);
        when(cardMockRepo.findCardByStudySystem(any(StudySystem.class),any(Card.class))).thenThrow(NoResultException.class);
        doNothing().when(cardToBoxMockRepo).delete(anyList());
        when(cardToBoxMockRepo.getAllB2CForStudySystem(any(StudySystem.class))).thenReturn(new ArrayList<BoxToCard>());
        doNothing().when(studySystemMockRepo).delete(any(StudySystem.class));
        when(cardToBoxMockRepo.save(any(BoxToCard.class))).thenReturn(new BoxToCard());
        studySystemLogic.updateStudySystemData(oldstudySystem,newstudySystem,false, false);
        verify(cardToBoxMockRepo,times(2)).save(any(BoxToCard.class));
    }


    @Test
    public void updateStudySystemDataElseTest(){
        StudySystem studySystem = new TimingSystem();
        StudySystem studySystem1 = new TimingSystem();
        studySystemLogic.updateStudySystemData(studySystem,studySystem1,false,false);
        verify(studySystemMockRepo).update(studySystem1);
    }

    @Test
    public void updateStudySystemDataElseTestChangedTrue(){
        StudySystem studySystem = new TimingSystem();
        StudySystem studySystem1 = new TimingSystem();
        List<CardOverview> cardsToStudySystem = new ArrayList<>();
        when(cardMockRepo.findCardsByStudySystem(studySystem)).thenReturn(cardsToStudySystem);
        List<BoxToCard> b2c = new ArrayList<>();
        when(cardToBoxMockRepo.getAllB2CForStudySystem(studySystem)).thenReturn(b2c);
        List<Card> cardlist = new ArrayList<>();
        cardlist.add(new TrueFalseCard());
        when(cardMockRepo.getAllCardsForCardOverview(cardsToStudySystem)).thenReturn(cardlist);
        studySystemLogic.updateStudySystemData(studySystem,studySystem1,false,true);
        verify(studySystemMockRepo).save(studySystem1);
        verify(cardToBoxMockRepo).delete(b2c);
        verify(studySystemMockRepo).delete(studySystem);

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
        when(cardToBoxMockRepo.update(any(BoxToCard.class))).thenReturn(boxToCard);
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
        when(cardToBoxMockRepo.update(any(BoxToCard.class))).thenReturn(boxToCard);
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
        when(cardToBoxMockRepo.update(any(BoxToCard.class))).thenReturn(boxToCard);
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
        when(cardToBoxMockRepo.update(any(BoxToCard.class))).thenReturn(boxToCard);
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
        BoxToCard boxToCard = new BoxToCard(card,studySystem.getBoxes().get(0));
        when(cardToBoxMockRepo.getSpecific(card,studySystem)).thenReturn(boxToCard);
        int test1 = studySystem.getTrueAnswerCount();
        int test2 = boxToCard.getStudySystemBox().getBoxNumber();
        Timestamp timestamp = boxToCard.getLearnedNextAt();
        boxToCard.setStatus(BoxToCard.CardStatus.SKILLED);
        BoxToCard.CardStatus status = boxToCard.getStatus();
        studySystemLogic.giveAnswer(studySystem,true);
        assertNotEquals(test1,studySystem.getTrueAnswerCount());
        assertNotEquals(timestamp,boxToCard.getLearnedNextAt());
        assertNotEquals(status,boxToCard.getStatus());
        assertNotEquals(test2,boxToCard.getStudySystemBox().getBoxNumber());

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
        testingBoxMockCards = list;
        on(studySystemLogic).set("testingBoxCards",testingBoxMockCards);
        StudySystem leitner = new LeitnerSystem();
        BoxToCard boxToCard = new BoxToCard(card,leitner.getBoxes().get(4));
        //boxToCard.getStudySystemBox().setBoxNumber(4); not needed
        when(cardToBoxMockRepo.getSpecific(card,leitner)).thenReturn(boxToCard);
        int boxNumber = boxToCard.getStudySystemBox().getBoxNumber();
        Timestamp timestamp = boxToCard.getLearnedNextAt();
        boxToCard.setStatus(BoxToCard.CardStatus.RELEARN);
        BoxToCard.CardStatus status = boxToCard.getStatus();
        studySystemLogic.giveAnswer(leitner,true);
        assertNotEquals(status,boxToCard.getStatus());
        assertEquals(boxNumber,boxToCard.getStudySystemBox().getBoxNumber());
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
        StudySystem studySystem = new LeitnerSystem();
        testingBoxMockCards = list;
        on(studySystemLogic).set("testingBoxCards",testingBoxMockCards);
        BoxToCard boxToCard = new BoxToCard(card,studySystem.getBoxes().get(0));
        when(cardToBoxMockRepo.getSpecific(card,studySystem)).thenReturn(boxToCard);
        BoxToCard.CardStatus status = boxToCard.getStatus();
        Timestamp timestamp = boxToCard.getLearnedNextAt();
        int test = boxToCard.getStudySystemBox().getBoxNumber();
        studySystemLogic.giveAnswer(studySystem,false);
        assertNotEquals(status,boxToCard.getStatus());
        assertEquals(timestamp,boxToCard.getLearnedNextAt());
        assertEquals(test,boxToCard.getStudySystemBox().getBoxNumber());
        assertEquals(card,testingBoxMockCards.get(testingBoxMockCards.size()-1));

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
        StudySystem studySystem = new LeitnerSystem();
        testingBoxMockCards = list;
        on(studySystemLogic).set("testingBoxCards",testingBoxMockCards);
        BoxToCard boxToCard = new BoxToCard(card,studySystem.getBoxes().get(4));
        when(cardToBoxMockRepo.getSpecific(card,studySystem)).thenReturn(boxToCard);
        Timestamp timestamp = boxToCard.getLearnedNextAt();
        int test = boxToCard.getStudySystemBox().getBoxNumber();
        studySystemLogic.giveAnswer(studySystem,false);
        assertNotEquals(test,boxToCard.getStudySystemBox());
        assertNotEquals(timestamp,boxToCard.getLearnedNextAt());

    }

    @Test
    public void getNextCardEmpty(){
        StudySystem studySystem = new VoteSystem();
        testingBoxMockCards = new ArrayList<>();
        on(studySystemLogic).set("testingBoxCards",testingBoxMockCards);
        studySystem.setNotLearnedYet(false);
        List<Card> list = new ArrayList<>();
        when(cardMockRepo.getAllCardsSortedForVoteSystem(any(StudySystem.class))).thenReturn(list);
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            studySystemLogic.getNextCard(studySystem);
        });
        String expected = exception.getMessage();
        assertEquals(expected,"studysystemnocardsdue");
    }



    @Test
    public void getNextCardTestCustomTiming(){
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
        Card test = studySystemLogic.getNextCard(studySystem);
        assertEquals(test,card);


    }

    @Test
    public void getNextCardTestLeitner(){
        StudySystem studySystem = new LeitnerSystem();
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
        StudySystem studySystem = new VoteSystem();
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

        assertEquals("studysystemnullfalsetype", thrown.getMessage());
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
        List<Card> listcard = new ArrayList<>();
        Card card = new TrueFalseCard();
        Card card1 = new TrueFalseCard();
        listcard.add(card);
        listcard.add(card1);
        StudySystemBox ssb = new StudySystemBox();
        StudySystemBox ssb1 = new StudySystemBox();
        List<StudySystemBox> listbox = new ArrayList<>();
        listbox.add(ssb);
        listbox.add(ssb1);
        studySystem.setBoxes(listbox);
        when(cardMockRepo.getAllCardsInLearnedBox(studySystem)).thenReturn(listcard);
        BoxToCard boxToCard = new BoxToCard(card,studySystem.getBoxes().get(1));
        BoxToCard boxToCard1 = new BoxToCard(card1,studySystem.getBoxes().get(1));
        when(cardToBoxMockRepo.getSpecific(card,studySystem)).thenReturn(boxToCard);
        when(cardToBoxMockRepo.getSpecific(card1,studySystem)).thenReturn(boxToCard1);
        int actual = studySystemLogic.finishTestAndGetResult(studySystem);
        assertNotEquals(ssb1,boxToCard.getStudySystemBox());
        assertNotEquals(ssb1,boxToCard1.getStudySystemBox());
        assertEquals(expected,actual);
        assertEquals(studySystem.getTrueAnswerCount(),0);
        assertEquals(studySystem.getQuestionCount(),0);
    }

    @Test
    public void deleteStudySystemTestNull(){
        StudySystem studySystem = null;
        Exception exception = assertThrows(IllegalStateException.class, () -> {
            studySystemLogic.deleteStudySystem(studySystem);
        });
        String expected = "studysystemnullerror";
        String actual = exception.getMessage();
        assertEquals(expected,actual);
    }

    @Test
    public void deleteStudySystemTest(){
        StudySystem studySystem = new LeitnerSystem();
        List<BoxToCard> list = new ArrayList<>();
        when(cardToBoxMockRepo.getAllB2CForStudySystem(studySystem)).thenReturn(list);
        studySystemLogic.deleteStudySystem(studySystem);
        verify(cardToBoxMockRepo).delete(list);
        verify(studySystemMockRepo).delete(studySystem);
    }

    @Test
    public void deleteStudySystemsTest(){
        StudySystem studySystem = new LeitnerSystem();
        StudySystem studySystem1 = new TimingSystem();
        StudySystem[] listStudy = {studySystem,studySystem1};
        List<BoxToCard> list = new ArrayList<>();
        List<BoxToCard> list1 = new ArrayList<>();
        when(cardToBoxMockRepo.getAllB2CForStudySystem(studySystem)).thenReturn(list);
        when(cardToBoxMockRepo.getAllB2CForStudySystem(studySystem1)).thenReturn(list1);
        studySystemLogic.deleteStudySystem(listStudy);
        verify(cardToBoxMockRepo,times(2)).delete(anyList());
        verify(studySystemMockRepo,times(2)).delete(any(StudySystem.class));
    }


    @Test
    public void moveAllCardsForDeckToFirstBoxTestAlreadyExist(){
        List<Card> list = new ArrayList<>();
        Card card = new TrueFalseCard();
        list.add(card);
        StudySystem studySystem = new TimingSystem();
        when(cardMockRepo.findCardByStudySystem(any(StudySystem.class),any(Card.class))).thenReturn(card);
        List<Card> check = studySystemLogic.moveAllCardsForDeckToFirstBox(list,studySystem);
        assertEquals(list,check);
    }


    @Test
    public void calculateProgressTestNotLeitner(){
        StudySystem studySystem = new VoteSystem();
        List<CardOverview> listfull = new ArrayList<>();
        CardOverview card  = new CardOverview();
        CardOverview card1 = new CardOverview();
        listfull.add(card);
        listfull.add(card1);
        when(cardMockRepo.findCardsByStudySystem(studySystem)).thenReturn(listfull);
        Long value = 1L;
        when(cardMockRepo.getNumberOfLearnedCardsByStudySystem(any(StudySystem.class))).thenReturn(value);
        int expected = 50;
        studySystemLogic.calculateProgress(studySystem);
        assertEquals(expected,studySystem.getProgress());
    }

    @Test
    public void calculateProgressTestLeitner(){
        StudySystem studySystem = new LeitnerSystem();
        List<CardOverview> listfull = new ArrayList<>();
        CardOverview card  = new CardOverview();
        CardOverview card1 = new CardOverview();
        CardOverview card2 = new CardOverview();
        listfull.add(card);
        listfull.add(card1);
        listfull.add(card2);
        when(cardMockRepo.findCardsByStudySystem(studySystem)).thenReturn(listfull);
        Long value = 2L;
        when(cardMockRepo.getNumberOfLearnedCardsByStudySystem(any(StudySystem.class))).thenReturn(value);
        List<Long> progressPerBox = new ArrayList<>();
        progressPerBox.add(1L);
        progressPerBox.add(0L);
        progressPerBox.add(1L);
        progressPerBox.add(0L);
        when(studySystemBoxMockRepo.getProgressForLeitner(studySystem)).thenReturn(progressPerBox);
        int expected = 17;
        studySystemLogic.calculateProgress(studySystem);
        assertEquals(expected,studySystem.getProgress());
    }

    @Test
    public void removeCardsFromStudySystemTestNull(){
        StudySystem studySystem = new TimingSystem();
        List<CardOverview> cardsviews = new ArrayList<>();
        List<Card> cards = new ArrayList<>();
        cards.add(null);
        when(cardMockRepo.getAllCardsForCardOverview(cardsviews)).thenReturn(cards);
        Exception exception = assertThrows(IllegalStateException.class,() -> {
            studySystemLogic.removeCardsFromStudySystem(cardsviews,studySystem);
        });
        String expected = exception.getMessage();
        assertEquals(expected,"cardnullerror");
    }

    @Test
    public void removeCardsFromStudySystemTest(){
        StudySystem studySystem = new TimingSystem();
        List<CardOverview> cardsviews = new ArrayList<>();
        List<Card> cards = new ArrayList<>();
        Card card = new TrueFalseCard();
        cards.add(card);
        BoxToCard boxToCard = new BoxToCard();
        when(cardToBoxMockRepo.getSpecific(any(Card.class),any(StudySystem.class))).thenReturn(boxToCard);
        when(cardMockRepo.getAllCardsForCardOverview(cardsviews)).thenReturn(cards);
        studySystemLogic.removeCardsFromStudySystem(cardsviews,studySystem);
        verify(cardToBoxMockRepo).delete(boxToCard);
    }

    @Test
    public void addCardsToDeckNullTest(){
        StudySystem studySystem = null;
        List<CardOverview> cards = new ArrayList<>();
        Exception exception = assertThrows(IllegalStateException.class,() -> {
            studySystemLogic.addCardsToDeck(cards,studySystem);
        });
        String expected = exception.getMessage();
        assertEquals(expected,"studysystemnullerror");
    }

    @Test
    public void numCardsInDeckTest(){
        StudySystem studySystem = new TimingSystem();
        List<CardOverview> list = new ArrayList<>();
        list.add(new CardOverview());
        when(cardMockRepo.findCardsByStudySystem(any(StudySystem.class))).thenReturn(list);
        assertEquals(list.size(),studySystemLogic.numCardsInDeck(studySystem));
    }


    @Test
    public void resetLearnStatusTest(){
        StudySystem studySystem = new TimingSystem();
        studySystem.setProgress(1);
        List<CardOverview> list = new ArrayList<>();
        when(cardMockRepo.findCardsByStudySystem(studySystem)).thenReturn(list);
        List<Card> cardlist = new ArrayList<>();
        Card card = new TrueFalseCard();
        cardlist.add(card);
        when(cardMockRepo.getAllCardsForCardOverview(list)).thenReturn(cardlist);
        BoxToCard boxToCard = new BoxToCard();
        when(cardToBoxMockRepo.getSpecific(card,studySystem)).thenReturn(boxToCard);
        boxToCard.setStatus(BoxToCard.CardStatus.SKILLED);
        boxToCard.setRating(5);
        boxToCard.setStudySystemBox(studySystem.getBoxes().get(1));
        studySystem.setNotLearnedYet(false);
        studySystemLogic.resetLearnStatus(studySystem);
        verify(cardToBoxMockRepo).update(boxToCard);
        verify(studySystemMockRepo).update(studySystem);
        assertEquals(boxToCard.getStatus(), BoxToCard.CardStatus.NEW);
        assertEquals(boxToCard.getRating(),0);
        assertEquals(boxToCard.getStudySystemBox().getBoxNumber(),0);
        assertEquals(studySystem.getProgress(),0);
        assertTrue(studySystem.isNotLearnedYet());
    }

    @Test
    public void resetLearnStatusTestElse() {
        StudySystem studySystem = new TimingSystem();
        studySystem.setProgress(0);
        studySystemLogic.resetLearnStatus(studySystem);
    }








































    }
