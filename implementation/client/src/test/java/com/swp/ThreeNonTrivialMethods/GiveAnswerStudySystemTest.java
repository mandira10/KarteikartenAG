package com.swp.ThreeNonTrivialMethods;

import com.gumse.gui.Locale;
import com.gumse.tools.Output;
import com.gumse.tools.Toolbox;
import com.swp.Controller.*;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardToTag;
import com.swp.DataModel.CardTypes.MultipleChoiceCard;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.DataModel.CardTypes.TrueFalseCard;
import com.swp.DataModel.Language.German;
import com.swp.DataModel.StudySystem.*;
import com.swp.DataModel.Tag;
import com.swp.Logic.CardLogic;
import com.swp.Logic.StudySystemLogic;
import com.swp.Persistence.*;
import jakarta.persistence.NoResultException;
import org.glassfish.jaxb.runtime.v2.runtime.unmarshaller.XsiNilLoader;
import org.junit.jupiter.api.*;
import org.mockito.ArgumentMatcher;

import java.sql.Timestamp;
import java.util.*;

import static org.joor.Reflect.on;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * In dieser Testklasse werden die Methoden getestet, die hauptsöchlich beim Lernen eines Karteikasten verwendet werden.
 * GetNextCard() und giveAnswer(). Vorm Beginn des Lernen eines Karteikastens wird getNextCard() aufgerufen.
 * Nach Anzeigen der Karte und geben der Antwort gibt die GUI die gegebene Antwort und ob diese richtig/falsch war mittels
 * giveAnswer() wieder.
 * Danach wird erneut getNextCard() aufgerufen, um die nächste zu lernende Karte abzurufen.
 */

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GiveAnswerStudySystemTest {

       /*
        GUI:
        Beim jeden Testen wird zunächst nextCard() aufgerufen, das die nächste zu lernende Karte abruft.
        In der TestDeckPage ruft die Methode giveAnswer den StudySystemController auf. Nach Success wird die nächste Karte mittels nextCard() aufgerufen.

        Controller:
        Für die Funktion giveAnswer() müssen 2 Szenarios getestet werden.
        Für die Funktion getNextCard() müssen 3 Szenarios getestet werden.

        (Zum Beenden eines Testing gibt getNextCard() null an die GUI zurück, wodurch finishTestAndGetResult von der
        GUI aufgerufen wird, i.d.T. nicht beleuchtet).

        Logic:
         In der Logik wird bei giveAnswer() auf folgende Hilfsmethoden zugegriffen
       - moveCardToBox
       - changeCardDueDate

       Bei getNextCard() werden folgende keine Hilfsmehtoden aufgerufen, sondern mehrere Repo Funktionen.

        Persistence:
        Bei giveAnswer():
        CardToBoxRepository.getSpecific
        CardToBoxRepository.update

        Bei GetNextCard():
        CardRepository.getAllCardsInitiallyOrdered
        CardRepository.getAllCardsForStudySystem
        cardRepository.getAllCardsForTimingSystem
        cardRepository.getAllCardsNeededToBeLearned
        cardRepository.getAllCardsSortedForVoteSystem
        */

    StudySystemController studySystemController;
    StudySystemLogic studySystemLogic;
    CardLogic cardLogic;
    CardToBoxRepository cardToBoxRepository;
    CardToBoxRepository cardToBoxMockRepo  = mock(CardToBoxRepository.class);
    CardRepository cardRepository;
    CardRepository cardMockRepo  = mock(CardRepository.class);
    List<Card> testingCardsForStudySystem = new ArrayList<>();
    StudySystem studySystem1;
    StudySystem studySystem2;
    StudySystem studySystem3;
    StudySystem studySystem4;

    List testingBoxMockCards;



    @BeforeAll
    public static void before()
    {
        PersistenceManager.init("KarteikartenDBTest");
        German.getInstance().activate();
        ControllerThreadPool.getInstance().synchronizedTasks(true);
    }


    @BeforeEach
    public void setup() {
        studySystemController = StudySystemController.getInstance();
        studySystemLogic = StudySystemLogic.getInstance();
        cardLogic = CardLogic.getInstance();
        cardToBoxRepository = CardToBoxRepository.getInstance();

        testingCardsForStudySystem.add(new TextCard());
        testingCardsForStudySystem.add(new TrueFalseCard());
        testingCardsForStudySystem.add(new TextCard());
        testingCardsForStudySystem.add(new MultipleChoiceCard());
        testingCardsForStudySystem.add(new MultipleChoiceCard());
        testingCardsForStudySystem.add(new MultipleChoiceCard());
        testingCardsForStudySystem.add(new MultipleChoiceCard());

        for (Card c : testingCardsForStudySystem) {
            cardLogic.updateCardData(c, true);
        }
        studySystem4 = new TimingSystem("testStudySystem4", StudySystem.CardOrder.REVERSED_ALPHABETICAL, 10);

    }

    @Test
    @Order(2)
    /**
     * Testet die Hauptlogik beim Antwortgeben eines Timing/Vote Systems.
     * Prüft die beiden möglichen Fälle, dass die Antwort falsch ist oder dass die Antwort richtig ist.
     */
    public void giveAnswerTestNotLeitner(){
        studySystem2 = new TimingSystem("testStudySystem2", StudySystem.CardOrder.REVERSED_ALPHABETICAL, 10);
        studySystemLogic.updateStudySystemData(null, studySystem2, true);
        studySystemLogic.moveAllCardsForDeckToFirstBox(testingCardsForStudySystem, studySystem2);

        //setup
        SingleDataCallback<Boolean> mockSingleDataCallback = mock(SingleDataCallback.class);
        testingBoxMockCards = new ArrayList(Arrays.asList(testingCardsForStudySystem.get(0), testingCardsForStudySystem.get(1), testingCardsForStudySystem.get(2), testingCardsForStudySystem.get(3))); //mock so that list ist not empty
        on(studySystemLogic).set("testingBoxCards",testingBoxMockCards);

        //Testfall 1: Antwort falsch
        assertDoesNotThrow(() -> studySystemController.giveAnswer(studySystem2, false, mockSingleDataCallback));
        verify(mockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Boolean>() {

            @Override
            public boolean matches(Boolean aBoolean) {
                assertTrue(aBoolean);
                return true;
            }
        }));
        assertEquals(0,studySystem2.getTrueAnswerCount());
        assertEquals(1, studySystem2.getQuestionCount());
        assertEquals(4,testingBoxMockCards.size());
        BoxToCard boxToCard1 = studySystemLogic.getBoxToCard(testingCardsForStudySystem.get(0),studySystem2);
        assertEquals(0,boxToCard1.getStudySystemBox().getBoxNumber()); //no box moving, if true or false
        assertTrue(boxToCard1.getStatus().equals(BoxToCard.CardStatus.RELEARN));

        reset(mockSingleDataCallback);

        //Testfall 2: Antwort richtig
        assertDoesNotThrow(() -> studySystemController.giveAnswer(studySystem2, true, mockSingleDataCallback));
        verify(mockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Boolean>() {

            @Override
            public boolean matches(Boolean aBoolean) {
                assertTrue(aBoolean);
                return true;
            }
        }));
        assertEquals(1,studySystem2.getTrueAnswerCount());
        assertEquals(2, studySystem2.getQuestionCount());
        assertEquals(3,testingBoxMockCards.size());
        BoxToCard boxToCard2 = studySystemLogic.getBoxToCard(testingCardsForStudySystem.get(1),studySystem2);
        assertEquals(1,boxToCard2.getStudySystemBox().getBoxNumber());
        assertTrue(boxToCard2.getStatus().equals(BoxToCard.CardStatus.SKILLED));
        reset(mockSingleDataCallback);

    }


    /**
     * Testet die Hauptlogik beim Antwortgeben eines Leitner Systems.
     * Prüft die 4 möglichen Fälle bei Leitner und das Handling.
     * Testet auch die Hilfsmethode zum Setzen des Datums mit.
     * Test 1: Die Antwort ist richtig und die Karte befindet sich in Box 0
     * Test 2: Die Antwort ist falsch und die Karte befindet sich in Box 0
     * Test 3: Die Antwort ist falsch und die Karte befindet sich in Box 1
     * Test 4: Die Antwort ist richtig und die Karte befindet sich in Box 4
     * Test 5: Die Antwort ist falsch und die Karte befindet sich in Box 5
     */
    @Test
    @Order(5)
    public void testGiveAnswerLeitner() {
        //setup
        studySystem1 = new LeitnerSystem("testStudySystem1", StudySystem.CardOrder.ALPHABETICAL);
        studySystemLogic.updateStudySystemData(null, studySystem1, true);
        studySystemLogic.moveAllCardsForDeckToFirstBox(testingCardsForStudySystem, studySystem1);

        SingleDataCallback<Boolean> mockSingleDataCallback = mock(SingleDataCallback.class);
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Timestamp(System.currentTimeMillis()));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        //zu Anfang sollte noch nichts passiert sein im StudySystem
        assertEquals(0, studySystem1.getTrueAnswerCount());
        assertEquals(0, studySystem1.getQuestionCount());

        //Testfall1 : Karte initial in Box 0, Antwort richtig

        //mock Hilfsattribut testingBoxMockCards
        testingBoxMockCards = new ArrayList(Arrays.asList(testingCardsForStudySystem.get(0), testingCardsForStudySystem.get(1), testingCardsForStudySystem.get(2), testingCardsForStudySystem.get(3),testingCardsForStudySystem.get(4))); //mock so that list ist not empty
        on(studySystemLogic).set("testingBoxCards", testingBoxMockCards);

        assertDoesNotThrow(() -> studySystemController.giveAnswer(studySystem1, true, mockSingleDataCallback));
        verify(mockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Boolean>() {

            @Override
            public boolean matches(Boolean aBoolean) {
                assertTrue(aBoolean);
                return true;
            }
        }));
        //Teste die zu setzenden Variablen und dass die Karte aus textingBoxMockCards entfernt wurde
        assertEquals(1, studySystem1.getTrueAnswerCount());
        assertEquals(1, studySystem1.getQuestionCount());
        assertEquals(4, testingBoxMockCards.size());
        BoxToCard boxToCard1 = studySystemLogic.getBoxToCard(testingCardsForStudySystem.get(0), studySystem1);
        assertEquals(1, boxToCard1.getStudySystemBox().getBoxNumber());
        assertTrue(boxToCard1.getStatus().equals(BoxToCard.CardStatus.RELEARN));

        //Check Datum
        cal.add(Calendar.DATE, 1);
        assertEquals(cal.getTime(), boxToCard1.getLearnedNextAt());

        //reset date and mock
        cal.add(Calendar.DATE, -1);
        reset(mockSingleDataCallback);

        //Testfall 2:  Karte initial in Box 0, Antwort falsch
        assertDoesNotThrow(() -> studySystemController.giveAnswer(studySystem1, false, mockSingleDataCallback));
        verify(mockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Boolean>() {

            @Override
            public boolean matches(Boolean aBoolean) {
                assertTrue(aBoolean);
                return true;
            }
        }));
        assertEquals(1, studySystem1.getTrueAnswerCount());
        assertEquals(2, studySystem1.getQuestionCount());
        assertEquals(4, testingBoxMockCards.size());
        assertEquals(testingCardsForStudySystem.get(1), testingBoxMockCards.get(3));
        BoxToCard boxToCard2 = studySystemLogic.getBoxToCard(testingCardsForStudySystem.get(1), studySystem1);
        assertEquals(0, boxToCard2.getStudySystemBox().getBoxNumber());
        assertTrue(boxToCard2.getStatus().equals(BoxToCard.CardStatus.RELEARN));

        cal.add(Calendar.DATE, 0);
        assertEquals(cal.getTime(), boxToCard2.getLearnedNextAt());

        //reset mock
        reset(mockSingleDataCallback);

        //before mock of CardToBoxMockRepo, get actual CardToBox we want to use
        BoxToCard boxToCard3 = studySystemLogic.getBoxToCard(testingCardsForStudySystem.get(2), studySystem1);
        BoxToCard boxToCard4 = studySystemLogic.getBoxToCard(testingCardsForStudySystem.get(3), studySystem1);
        BoxToCard boxToCard5 = studySystemLogic.getBoxToCard(testingCardsForStudySystem.get(4), studySystem1);

        //Testfall 3: Karte initial in Box 1, Antwort falsch
        //als erstes bewege die BoxToCard in Box 1
        studySystemLogic.moveCardToBox(boxToCard3, 1, studySystem1);

        //mock, so dass dieses BoxToCard zurückgegeben wird mit richtiger Box
        on(studySystemLogic).set("cardToBoxRepository", cardToBoxMockRepo);
        when(cardToBoxMockRepo.getSpecific(testingCardsForStudySystem.get(2), studySystem1)).thenReturn(boxToCard3);

        assertDoesNotThrow(() -> studySystemController.giveAnswer(studySystem1, false, mockSingleDataCallback));
        verify(mockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Boolean>() {

            @Override
            public boolean matches(Boolean aBoolean) {
                assertTrue(aBoolean);
                return true;
            }
        }));
        assertEquals(1, studySystem1.getTrueAnswerCount());
        assertEquals(3, studySystem1.getQuestionCount());
        assertEquals(4, testingBoxMockCards.size());
        assertEquals(testingCardsForStudySystem.get(2), testingBoxMockCards.get(3));

        assertEquals(0, boxToCard3.getStudySystemBox().getBoxNumber());
        assertTrue(boxToCard3.getStatus().equals(BoxToCard.CardStatus.RELEARN));

        cal.add(Calendar.DATE, 0);
        assertEquals(cal.getTime(), boxToCard3.getLearnedNextAt());

        //reset mock
        reset(mockSingleDataCallback);

        //Testfall 4: Karte initial in Box 4, Antwort richtig
        //als erstes bewege die BoxToCard in Box 4
        studySystemLogic.moveCardToBox(boxToCard4, 4, studySystem1);
        //mock so dass diese zurückgegeben wird
        when(cardToBoxMockRepo.getSpecific(testingCardsForStudySystem.get(3), studySystem1)).thenReturn(boxToCard4);

        assertDoesNotThrow(() -> studySystemController.giveAnswer(studySystem1, true, mockSingleDataCallback));
        verify(mockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Boolean>() {

            @Override
            public boolean matches(Boolean aBoolean) {
                assertTrue(aBoolean);
                return true;
            }
        }));

        assertEquals(2, studySystem1.getTrueAnswerCount());
        assertEquals(4, studySystem1.getQuestionCount());
        assertEquals(3, testingBoxMockCards.size());

        assertEquals(4, boxToCard4.getStudySystemBox().getBoxNumber());
        assertTrue(boxToCard4.getStatus().equals(BoxToCard.CardStatus.SKILLED));

        //Check date and reset
        cal.add(Calendar.DATE, 14);
        assertEquals(cal.getTime(), boxToCard4.getLearnedNextAt());
        cal.add(Calendar.DATE, -14);
        reset(cardToBoxMockRepo);

        reset(mockSingleDataCallback);

        //Testfall 5: Karte initial in Box 4, Antwort falsch (zum testen des Box == 0 Pfad false)
        //als erstes bewege die BoxToCard in Box 4
        studySystemLogic.moveCardToBox(boxToCard5, 4, studySystem1);
        //mock so dass diese zurückgegeben wird
        when(cardToBoxMockRepo.getSpecific(testingCardsForStudySystem.get(4), studySystem1)).thenReturn(boxToCard5);

        assertDoesNotThrow(() -> studySystemController.giveAnswer(studySystem1, false, mockSingleDataCallback));
        verify(mockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Boolean>() {

            @Override
            public boolean matches(Boolean aBoolean) {
                assertTrue(aBoolean);
                return true;
            }
        }));

        assertEquals(2, studySystem1.getTrueAnswerCount());
        assertEquals(5, studySystem1.getQuestionCount());
        assertEquals(2, testingBoxMockCards.size());

        assertEquals(3, boxToCard5.getStudySystemBox().getBoxNumber());
        assertTrue(boxToCard5.getStatus().equals(BoxToCard.CardStatus.RELEARN));

        //Check date and reset
        cal.add(Calendar.DATE, 7);
        assertEquals(cal.getTime(), boxToCard5.getLearnedNextAt());
        cal.add(Calendar.DATE, -7);
        reset(cardToBoxMockRepo);
        reset(mockSingleDataCallback);
    }


    /**
     * Testet die Hilfsmethode zum Setzen des Datums.
     */
    @Test
    @Order(2)
    public void testDueDate(){
        studySystem3 = new LeitnerSystem("testStudySystem3", StudySystem.CardOrder.REVERSED_ALPHABETICAL);
        studySystemLogic.updateStudySystemData(null, studySystem3, true);
        studySystemLogic.moveAllCardsForDeckToFirstBox(testingCardsForStudySystem, studySystem3);

        BoxToCard boxToCard = studySystemLogic.getBoxToCard(testingCardsForStudySystem.get(0), studySystem3);

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Timestamp(System.currentTimeMillis()));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        studySystemLogic.changeCardDueDate(boxToCard);
        assertEquals(cal.getTime(), boxToCard.getLearnedNextAt());

        studySystemLogic.moveCardToBox(boxToCard,1,studySystem3);
        studySystemLogic.changeCardDueDate(boxToCard);
        cal.add(Calendar.DATE,1);
        assertEquals(cal.getTime(), boxToCard.getLearnedNextAt());
        cal.add(Calendar.DATE,-1);//reset

        studySystemLogic.moveCardToBox(boxToCard,2,studySystem3);
        studySystemLogic.changeCardDueDate(boxToCard);
        cal.add(Calendar.DATE,3);
        assertEquals(cal.getTime(), boxToCard.getLearnedNextAt());
        cal.add(Calendar.DATE,-3); //reset


        studySystemLogic.moveCardToBox(boxToCard,3,studySystem3);
        studySystemLogic.changeCardDueDate(boxToCard);
        cal.add(Calendar.DATE,7);
        assertEquals(cal.getTime(), boxToCard.getLearnedNextAt());
        cal.add(Calendar.DATE,-7); //reset

        studySystemLogic.moveCardToBox(boxToCard,4,studySystem3);
        studySystemLogic.changeCardDueDate(boxToCard);
        cal.add(Calendar.DAY_OF_YEAR,14);
        assertEquals(cal.getTime(), boxToCard.getLearnedNextAt());

    }

    /**
     * Testet die Kartenreihenfolge alphabetisch sortiert und gleichzeitig das Zusammenspiel zwischen
     * getNextCard und giveAnswer.
     */
    @Test
    @Order(1)
    public void testGetNextCardsInitiallyAlphabetical(){
        //setup
        testingBoxMockCards = new ArrayList(); //setTestingBoxCardsToEmpty
        on(studySystemLogic).set("testingBoxCards", testingBoxMockCards);
        on(studySystemLogic).set("testingStarted", false);
        SingleDataCallback<Card> mockSingleDataCallback = mock(SingleDataCallback.class);
        SingleDataCallback<Boolean> mockSingleDataCallback1 = mock(SingleDataCallback.class);

        Card card1 = new TextCard("A beginnt","B verliert","C-Titel");
        Card card2 = new TrueFalseCard("Z beginnt",true,"B-Titel");
        Card card3 = new TextCard("L beginnt","B verliert","C-Titel");
        List <Card>  random1 = Arrays.asList(card1,card2,card3);
        for (Card c : random1) {
            cardLogic.updateCardData(c, true);
        }
        StudySystem studySystem5 = new LeitnerSystem("Alpha", StudySystem.CardOrder.ALPHABETICAL);
        studySystemLogic.updateStudySystemData(null, studySystem5, true);
        studySystemLogic.moveAllCardsForDeckToFirstBox(random1, studySystem5);

        //Get cards out of studySystem
        assertDoesNotThrow(() -> studySystemController.getNextCard(studySystem5,mockSingleDataCallback));
        verify(mockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Card>() {

            @Override public boolean matches(Card card) {
                assertEquals(card1,card);
                return true;
            }
        }));

        assertDoesNotThrow(() -> studySystemController.giveAnswer(studySystem5, true, mockSingleDataCallback1));
        verify(mockSingleDataCallback1).callSuccess(argThat(new ArgumentMatcher<Boolean>() {

            @Override
            public boolean matches(Boolean aBoolean) {
                assertTrue(aBoolean);
                return true;
            }
        }));
        reset(mockSingleDataCallback);
        reset(mockSingleDataCallback1);

        assertDoesNotThrow(() -> studySystemController.getNextCard(studySystem5,mockSingleDataCallback));
        verify(mockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Card>() {

            @Override public boolean matches(Card card) {
                assertEquals(card3,card);
                return true;
            }
        }));

        assertDoesNotThrow(() -> studySystemController.giveAnswer(studySystem5, true, mockSingleDataCallback1));
        verify(mockSingleDataCallback1).callSuccess(argThat(new ArgumentMatcher<Boolean>() {

            @Override
            public boolean matches(Boolean aBoolean) {
                assertTrue(aBoolean);
                return true;
            }
        }));
        reset(mockSingleDataCallback);
        reset(mockSingleDataCallback1);

        assertDoesNotThrow(() -> studySystemController.getNextCard(studySystem5,mockSingleDataCallback));
        verify(mockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Card>() {

            @Override public boolean matches(Card card) {
                assertEquals(card2,card);
                return true;
            }
        }));

        assertDoesNotThrow(() -> studySystemController.giveAnswer(studySystem5, true, mockSingleDataCallback1));
        verify(mockSingleDataCallback1).callSuccess(argThat(new ArgumentMatcher<Boolean>() {

            @Override
            public boolean matches(Boolean aBoolean) {
                assertTrue(aBoolean);
                return true;
            }
        }));
        reset(mockSingleDataCallback);
        reset(mockSingleDataCallback1);

        //Zu Ende ist die Liste leer
        //Alle Karten sind in Box 1, also sollte es für Leitner zum aktuellen Zeitpunkt nichts mehr zu lernen geben und null
        //zurückgegeben werden
        assertTrue(testingBoxMockCards.isEmpty());

        assertDoesNotThrow(() -> studySystemController.getNextCard(studySystem5,mockSingleDataCallback));
        verify(mockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Card>() {

            @Override public boolean matches(Card card) {
                assertNull(card);
                return true;
            }
        }));
        reset(mockSingleDataCallback);
        reset(mockSingleDataCallback1);
    }

    /**
     * Testet die Kartenreihenfolge reverse alphabetisch sortiert und gleichzeitig das Zusammenspiel zwischen
     * getNextCard und giveAnswer.
     */
    @Test
    @Order(3)
    public void testGetNextCardsInitiallyRevAlphabetical(){
    //Setup
        testingBoxMockCards = new ArrayList(); //setTestingBoxCardsToEmpty
        on(studySystemLogic).set("testingBoxCards", testingBoxMockCards);
        SingleDataCallback<Card> mockSingleDataCallback = mock(SingleDataCallback.class);
        SingleDataCallback<Boolean> mockSingleDataCallback1 = mock(SingleDataCallback.class);

        Card card1 = new TextCard("A beginnt","B verliert","C-Titel");
        Card card2 = new TrueFalseCard("Z beginnt",true,"B-Titel");
        Card card3 = new TextCard("L beginnt","B verliert","C-Titel");
        List <Card>  random1 = Arrays.asList(card1,card2,card3);
        for (Card c : random1) {
            cardLogic.updateCardData(c, true);
        }
        StudySystem studySystem6 = new TimingSystem("revAlpha", StudySystem.CardOrder.REVERSED_ALPHABETICAL, 10);
        studySystemLogic.updateStudySystemData(null, studySystem6, true);
        studySystemLogic.moveAllCardsForDeckToFirstBox(random1, studySystem6);
        on(studySystemLogic).set("testingStarted",false);

        //Get cards out of studySystem
        assertDoesNotThrow(() -> studySystemController.getNextCard(studySystem6,mockSingleDataCallback));
        verify(mockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Card>() {

            @Override public boolean matches(Card card) {
                assertEquals(card2,card);
                return true;
            }
        }));

        assertDoesNotThrow(() -> studySystemController.giveAnswer(studySystem6, true, mockSingleDataCallback1));
        verify(mockSingleDataCallback1).callSuccess(argThat(new ArgumentMatcher<Boolean>() {

            @Override
            public boolean matches(Boolean aBoolean) {
                assertTrue(aBoolean);
                return true;
            }
        }));
        reset(mockSingleDataCallback);
        reset(mockSingleDataCallback1);

        assertDoesNotThrow(() -> studySystemController.getNextCard(studySystem6,mockSingleDataCallback));
        verify(mockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Card>() {

            @Override public boolean matches(Card card) {
                assertEquals(card3,card);
                return true;
            }
        }));

        assertDoesNotThrow(() -> studySystemController.giveAnswer(studySystem6, true, mockSingleDataCallback1));
        verify(mockSingleDataCallback1).callSuccess(argThat(new ArgumentMatcher<Boolean>() {

            @Override
            public boolean matches(Boolean aBoolean) {
                assertTrue(aBoolean);
                return true;
            }
        }));
        reset(mockSingleDataCallback);
        reset(mockSingleDataCallback1);

        assertDoesNotThrow(() -> studySystemController.getNextCard(studySystem6,mockSingleDataCallback));
        verify(mockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Card>() {

            @Override public boolean matches(Card card) {
                assertEquals(card1,card);
                return true;
            }
        }));

        assertDoesNotThrow(() -> studySystemController.giveAnswer(studySystem6, true, mockSingleDataCallback1));
        verify(mockSingleDataCallback1).callSuccess(argThat(new ArgumentMatcher<Boolean>() {

            @Override
            public boolean matches(Boolean aBoolean) {
                assertTrue(aBoolean);
                return true;
            }
        }));
        reset(mockSingleDataCallback);
        reset(mockSingleDataCallback1);

        //Zu Ende ist die Liste leer
        //Alle Karten sind in Box 1, also sollte es für Leitner zum aktuellen Zeitpunkt nichts mehr zu lernen geben und null
        //zurückgegeben werden
        assertTrue(testingBoxMockCards.isEmpty());

        assertDoesNotThrow(() -> studySystemController.getNextCard(studySystem6,mockSingleDataCallback));
        verify(mockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Card>() {

            @Override public boolean matches(Card card) {
                assertNull(card);
                return true;
            }
        }));
        reset(mockSingleDataCallback);
        reset(mockSingleDataCallback1);
    }

    /**
     * Testet die Kartenreihenfolge random und die Errormeldung, falls
     * das Lernsystem bei Beginn des Lernens noch keine Karten enthält.
     */
    @Test
    @Order(0)
    public void testGetNextCardsInitiallyRandomAndEmpty() {
        SingleDataCallback<Card> mockSingleDataCallback = mock(SingleDataCallback.class);
        testingBoxMockCards = new ArrayList(); //setTestingBoxCardsToEmpty
        on(studySystemLogic).set("testingBoxCards", testingBoxMockCards);
        Card card1 = new TextCard("A beginnt", "B verliert", "C-Titel");
        Card card2 = new TrueFalseCard("Z beginnt", true, "B-Titel");
        Card card3 = new TextCard("L beginnt", "B verliert", "C-Titel");
        List<Card> random1 = Arrays.asList(card1, card2, card3);
        for (Card c : random1) {
            cardLogic.updateCardData(c, true);
        }

        StudySystem studySystem7 = new VoteSystem("Random3", StudySystem.CardOrder.RANDOM,5);
        studySystemLogic.updateStudySystemData(null, studySystem7, true);

        //Test Lernsystem ohne Karten
        assertDoesNotThrow(() -> studySystemController.getNextCard(studySystem7,mockSingleDataCallback));
        verify(mockSingleDataCallback, times(1))
                .callFailure(Locale.getCurrentLocale().getString("studysystemlearningerror"));

        studySystemLogic.moveAllCardsForDeckToFirstBox(random1, studySystem7);

        //Test Lernsystem mit Karten
        assertDoesNotThrow(() -> studySystemController.getNextCard(studySystem7,mockSingleDataCallback));
        verify(mockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Card>() {

            @Override public boolean matches(Card card) {
                assertNotNull(card);
                return true;
            }
        }));

        reset(mockSingleDataCallback);
    }

    @Test
    @Order(1)
    public void testGetNextCardsForLeitnerNotNew() {
        testingBoxMockCards = new ArrayList(); //setTestingBoxCardsToEmpty
        on(studySystemLogic).set("testingBoxCards", testingBoxMockCards);
        on(studySystemLogic).set("testingStarted", false);
        
        SingleDataCallback<Card> mockSingleDataCallback = mock(SingleDataCallback.class);
        SingleDataCallback<Boolean> mockSingleDataCallback1 = mock(SingleDataCallback.class);

        //setup cal to mock dates
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Timestamp(System.currentTimeMillis()));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        //createStudySystem
        StudySystem studySystem8 = new LeitnerSystem("LeitnerNotNew", StudySystem.CardOrder.ALPHABETICAL);
        studySystem8.setNotLearnedYet(false);
        studySystemLogic.updateStudySystemData(null, studySystem8, true);
        studySystemLogic.moveAllCardsForDeckToFirstBox(testingCardsForStudySystem, studySystem8);

            //Karte 0 wird auf gestern due gesetzt und Status Relearned
            cal.add(Calendar.DATE,-1); //Card0 is set to yesterday due and box1
            BoxToCard b2c = studySystemLogic.getBoxToCard(testingCardsForStudySystem.get(0), studySystem8);
            b2c.setLearnedNextAt(new Timestamp(cal.getTimeInMillis()));
            studySystemLogic.moveCardToBox(b2c, 1, studySystem8);
            b2c.setStatus(BoxToCard.CardStatus.RELEARN);
            studySystemLogic.updateCardToBox(b2c);

            //Karte 1 wird auf vorgestern due gesetzt und Status Relearned
             cal.add(Calendar.DATE,-1); //Card1 is set to the day before yesterday due and stays in box 0, state new
             b2c = studySystemLogic.getBoxToCard(testingCardsForStudySystem.get(1), studySystem8);
            b2c.setLearnedNextAt(new Timestamp(cal.getTimeInMillis()));
            studySystemLogic.moveCardToBox(b2c, 1, studySystem8);
            b2c.setStatus(BoxToCard.CardStatus.RELEARN);
            studySystemLogic.updateCardToBox(b2c);

            //Karte 2 wird auf vorgestern due gesetzt und Status New
            b2c = studySystemLogic.getBoxToCard(testingCardsForStudySystem.get(2), studySystem8);
            b2c.setLearnedNextAt(new Timestamp(cal.getTimeInMillis()));
            studySystemLogic.updateCardToBox(b2c);

            cal.add(Calendar.DATE,2); //Datum Heute
            //Karte 3 wird auf heute due gesetzt und Status neu beibahlten
            b2c = studySystemLogic.getBoxToCard(testingCardsForStudySystem.get(3), studySystem8);
            b2c.setLearnedNextAt(new Timestamp(cal.getTimeInMillis()));
            studySystemLogic.updateCardToBox(b2c);

            //Karte 4 wird auf heute due gesetzt und Status skilled gesetzt
            b2c = studySystemLogic.getBoxToCard(testingCardsForStudySystem.get(4), studySystem8);
            b2c.setLearnedNextAt(new Timestamp(cal.getTimeInMillis()));
            b2c.setStatus(BoxToCard.CardStatus.SKILLED);
            studySystemLogic.updateCardToBox(b2c);

            b2c = studySystemLogic.getBoxToCard(testingCardsForStudySystem.get(5), studySystem8);
            b2c.setLearnedNextAt(new Timestamp(cal.getTimeInMillis()));
            b2c.setStatus(BoxToCard.CardStatus.RELEARN);
            studySystemLogic.updateCardToBox(b2c);

            //Karte6 wird in Box 1 gesetzt und Datum analog zur Box gesetzt. Nicht due
            b2c = studySystemLogic.getBoxToCard(testingCardsForStudySystem.get(6), studySystem8);
            studySystemLogic.moveCardToBox(b2c,2,studySystem8);
            studySystemLogic.changeCardDueDate(b2c);
             studySystemLogic.updateCardToBox(b2c);



            //Teste Reihenfolge, sollte sein: 2,1,0,3,5,4
            //danach kommt null zurück, da Karte 6 nicht fällig ist

            assertDoesNotThrow(() -> studySystemController.getNextCard(studySystem8,mockSingleDataCallback));
            verify(mockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Card>() {

            @Override public boolean matches(Card card) {
                assertEquals(testingCardsForStudySystem.get(2),card);
                return true;
            }
            }));

            assertDoesNotThrow(() -> studySystemController.giveAnswer(studySystem8, true, mockSingleDataCallback1));
            verify(mockSingleDataCallback1).callSuccess(argThat(new ArgumentMatcher<Boolean>() {

            @Override
            public boolean matches(Boolean aBoolean) {
                assertTrue(aBoolean);
                return true;
            }
            }));
            reset(mockSingleDataCallback);
            reset(mockSingleDataCallback1);

        assertDoesNotThrow(() -> studySystemController.getNextCard(studySystem8,mockSingleDataCallback));
        verify(mockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Card>() {

            @Override public boolean matches(Card card) {
                assertEquals(testingCardsForStudySystem.get(1),card);
                return true;
            }
        }));

        assertDoesNotThrow(() -> studySystemController.giveAnswer(studySystem8, true, mockSingleDataCallback1));
        verify(mockSingleDataCallback1).callSuccess(argThat(new ArgumentMatcher<Boolean>() {

            @Override
            public boolean matches(Boolean aBoolean) {
                assertTrue(aBoolean);
                return true;
            }
        }));
        reset(mockSingleDataCallback);
        reset(mockSingleDataCallback1);


        assertDoesNotThrow(() -> studySystemController.getNextCard(studySystem8,mockSingleDataCallback));
        verify(mockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Card>() {

            @Override public boolean matches(Card card) {
                assertEquals(testingCardsForStudySystem.get(0),card);
                return true;
            }
        }));

        assertDoesNotThrow(() -> studySystemController.giveAnswer(studySystem8, true, mockSingleDataCallback1));
        verify(mockSingleDataCallback1).callSuccess(argThat(new ArgumentMatcher<Boolean>() {

            @Override
            public boolean matches(Boolean aBoolean) {
                assertTrue(aBoolean);
                return true;
            }
        }));
        reset(mockSingleDataCallback);
        reset(mockSingleDataCallback1);


        assertDoesNotThrow(() -> studySystemController.getNextCard(studySystem8,mockSingleDataCallback));
        verify(mockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Card>() {

            @Override public boolean matches(Card card) {
                assertEquals(testingCardsForStudySystem.get(3),card);
                return true;
            }
        }));

        assertDoesNotThrow(() -> studySystemController.giveAnswer(studySystem8, true, mockSingleDataCallback1));
        verify(mockSingleDataCallback1).callSuccess(argThat(new ArgumentMatcher<Boolean>() {

            @Override
            public boolean matches(Boolean aBoolean) {
                assertTrue(aBoolean);
                return true;
            }
        }));
        reset(mockSingleDataCallback);
        reset(mockSingleDataCallback1);


        assertDoesNotThrow(() -> studySystemController.getNextCard(studySystem8,mockSingleDataCallback));
        verify(mockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Card>() {

            @Override public boolean matches(Card card) {
                assertEquals(testingCardsForStudySystem.get(5),card);
                return true;
            }
        }));

        assertDoesNotThrow(() -> studySystemController.giveAnswer(studySystem8, true, mockSingleDataCallback1));
        verify(mockSingleDataCallback1).callSuccess(argThat(new ArgumentMatcher<Boolean>() {

            @Override
            public boolean matches(Boolean aBoolean) {
                assertTrue(aBoolean);
                return true;
            }
        }));
        reset(mockSingleDataCallback);
        reset(mockSingleDataCallback1);


        assertDoesNotThrow(() -> studySystemController.getNextCard(studySystem8,mockSingleDataCallback));
        verify(mockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Card>() {

            @Override public boolean matches(Card card) {
                assertEquals(testingCardsForStudySystem.get(4),card);
                return true;
            }
        }));

        assertDoesNotThrow(() -> studySystemController.giveAnswer(studySystem8, true, mockSingleDataCallback1));
        verify(mockSingleDataCallback1).callSuccess(argThat(new ArgumentMatcher<Boolean>() {

            @Override
            public boolean matches(Boolean aBoolean) {
                assertTrue(aBoolean);
                return true;
            }
        }));
        reset(mockSingleDataCallback);
        reset(mockSingleDataCallback1);


        assertDoesNotThrow(() -> studySystemController.getNextCard(studySystem8,mockSingleDataCallback));
        verify(mockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Card>() {

            @Override public boolean matches(Card card) {
               assertNull(card);
                return true;
            }
        }));


        reset(mockSingleDataCallback);
        reset(mockSingleDataCallback1);

    }

    @Test
    @Order(1)
    public void testGetNextCardsForTimingNotNew() {
        testingBoxMockCards = new ArrayList(); //setTestingBoxCardsToEmpty
        on(studySystemLogic).set("testingBoxCards", testingBoxMockCards);
        on(studySystemLogic).set("testingStarted", false);

        SingleDataCallback<Card> mockSingleDataCallback = mock(SingleDataCallback.class);
        SingleDataCallback<Boolean> mockSingleDataCallback1 = mock(SingleDataCallback.class);
        
        //createStudySystem
        StudySystem studySystem9 = new TimingSystem("TimingNotNew", StudySystem.CardOrder.ALPHABETICAL,10);
        studySystem9.setNotLearnedYet(false);
        studySystemLogic.updateStudySystemData(null, studySystem9, true);
        studySystemLogic.moveAllCardsForDeckToFirstBox(Arrays.asList(testingCardsForStudySystem.get(0),testingCardsForStudySystem.get(1),testingCardsForStudySystem.get(2)), studySystem9);

        //Karte 0 wird als gelernt gesetzt und in Box 1 geschoben
        BoxToCard b2c = studySystemLogic.getBoxToCard(testingCardsForStudySystem.get(0), studySystem9);
        studySystemLogic.moveCardToBox(b2c, 1, studySystem9);
        b2c.setStatus(BoxToCard.CardStatus.SKILLED);
        studySystemLogic.updateCardToBox(b2c);

        //Karte 1 wird als relearn gesetzt
        b2c = studySystemLogic.getBoxToCard(testingCardsForStudySystem.get(1), studySystem9);
        b2c.setStatus(BoxToCard.CardStatus.RELEARN);
        studySystemLogic.updateCardToBox(b2c);

        //Teste Reihenfolge, sollte sein: 2,1
        //danach kommt null zurück, da Karte 0 in Box 1 ist und zurzeit nicht gelernt werden muss.

        assertDoesNotThrow(() -> studySystemController.getNextCard(studySystem9,mockSingleDataCallback));
        verify(mockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Card>() {

            @Override public boolean matches(Card card) {
                assertEquals(testingCardsForStudySystem.get(2),card);
                return true;
            }
        }));

        assertDoesNotThrow(() -> studySystemController.giveAnswer(studySystem9, true, mockSingleDataCallback1));
        verify(mockSingleDataCallback1).callSuccess(argThat(new ArgumentMatcher<Boolean>() {

            @Override
            public boolean matches(Boolean aBoolean) {
                assertTrue(aBoolean);
                return true;
            }
        }));
        reset(mockSingleDataCallback);
        reset(mockSingleDataCallback1);

        assertDoesNotThrow(() -> studySystemController.getNextCard(studySystem9,mockSingleDataCallback));
        verify(mockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Card>() {

            @Override public boolean matches(Card card) {
                assertEquals(testingCardsForStudySystem.get(1),card);
                return true;
            }
        }));

        assertDoesNotThrow(() -> studySystemController.giveAnswer(studySystem9, true, mockSingleDataCallback1));
        verify(mockSingleDataCallback1).callSuccess(argThat(new ArgumentMatcher<Boolean>() {

            @Override
            public boolean matches(Boolean aBoolean) {
                assertTrue(aBoolean);
                return true;
            }
        }));
        reset(mockSingleDataCallback);
        reset(mockSingleDataCallback1);


        assertDoesNotThrow(() -> studySystemController.getNextCard(studySystem9,mockSingleDataCallback));
        verify(mockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Card>() {

            @Override public boolean matches(Card card) {
                assertNull(card);
                return true;
            }
        }));


        reset(mockSingleDataCallback);
        reset(mockSingleDataCallback1);

    }

    @Test
    @Order(1)
    public void testGetNextCardsForRatingNotNew() {
        testingBoxMockCards = new ArrayList(); //setTestingBoxCardsToEmpty
        on(studySystemLogic).set("testingBoxCards", testingBoxMockCards);
        on(studySystemLogic).set("testingStarted", false);

        SingleDataCallback<Card> mockSingleDataCallback = mock(SingleDataCallback.class);
        SingleDataCallback<Boolean> mockSingleDataCallback1 = mock(SingleDataCallback.class);

        //createStudySystem
        StudySystem studySystem10 = new VoteSystem("VoteNotNew", StudySystem.CardOrder.ALPHABETICAL,5);
        studySystem10.setNotLearnedYet(false);
        studySystemLogic.updateStudySystemData(null, studySystem10, true);
        studySystemLogic.moveAllCardsForDeckToFirstBox(testingCardsForStudySystem, studySystem10);

        //Karte 0 wird als gelernt gesetzt und in Box 1 geschoben
        BoxToCard b2c = studySystemLogic.getBoxToCard(testingCardsForStudySystem.get(0), studySystem10);
        studySystemLogic.moveCardToBox(b2c, 1, studySystem10);
        b2c.setStatus(BoxToCard.CardStatus.SKILLED);
        studySystemLogic.updateCardToBox(b2c);

        //Karte 1 wird als relearn gesetzt, Rating 5
        b2c = studySystemLogic.getBoxToCard(testingCardsForStudySystem.get(1), studySystem10);
        b2c.setStatus(BoxToCard.CardStatus.RELEARN);
        b2c.setRating(5);
        studySystemLogic.updateCardToBox(b2c);

        //Karte 2 wird als relearn gesetzt, Rating 1
        b2c = studySystemLogic.getBoxToCard(testingCardsForStudySystem.get(2), studySystem10);
        b2c.setStatus(BoxToCard.CardStatus.RELEARN);
        b2c.setRating(1);
        studySystemLogic.updateCardToBox(b2c);

        //Karte 3 wird als relearn gesetzt, Rating 3
        b2c = studySystemLogic.getBoxToCard(testingCardsForStudySystem.get(3), studySystem10);
        b2c.setStatus(BoxToCard.CardStatus.RELEARN);
        b2c.setRating(3);
        studySystemLogic.updateCardToBox(b2c);



        //Karte 4 wird als skilled gesetzt, Rating 3
        b2c = studySystemLogic.getBoxToCard(testingCardsForStudySystem.get(4), studySystem10);
        b2c.setStatus(BoxToCard.CardStatus.SKILLED);
        b2c.setRating(3);
        studySystemLogic.updateCardToBox(b2c);

        //Karte 5 wird als new gesetzt, Rating 5
        b2c = studySystemLogic.getBoxToCard(testingCardsForStudySystem.get(5), studySystem10);
        b2c.setRating(5);
        studySystemLogic.updateCardToBox(b2c);

        //Karte 6 bleibt als neue Karte in Box0, no rating

        //Teste Reihenfolge, sollte sein: 6,5,2,3,1,4
        //danach kommt null zurück, da Karte 0 in Box 1 ist und zurzeit nicht gelernt werden muss.

        assertDoesNotThrow(() -> studySystemController.getNextCard(studySystem10,mockSingleDataCallback));
        verify(mockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Card>() {

            @Override public boolean matches(Card card) {
                assertEquals(testingCardsForStudySystem.get(6),card);
                return true;
            }
        }));

        assertDoesNotThrow(() -> studySystemController.giveAnswer(studySystem10, true, mockSingleDataCallback1));
        verify(mockSingleDataCallback1).callSuccess(argThat(new ArgumentMatcher<Boolean>() {

            @Override
            public boolean matches(Boolean aBoolean) {
                assertTrue(aBoolean);
                return true;
            }
        }));
        reset(mockSingleDataCallback);
        reset(mockSingleDataCallback1);

        assertDoesNotThrow(() -> studySystemController.getNextCard(studySystem10,mockSingleDataCallback));
        verify(mockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Card>() {

            @Override public boolean matches(Card card) {
                assertEquals(testingCardsForStudySystem.get(5),card);
                return true;
            }
        }));

        assertDoesNotThrow(() -> studySystemController.giveAnswer(studySystem10, true, mockSingleDataCallback1));
        verify(mockSingleDataCallback1).callSuccess(argThat(new ArgumentMatcher<Boolean>() {

            @Override
            public boolean matches(Boolean aBoolean) {
                assertTrue(aBoolean);
                return true;
            }
        }));
        reset(mockSingleDataCallback);
        reset(mockSingleDataCallback1);

        assertDoesNotThrow(() -> studySystemController.getNextCard(studySystem10,mockSingleDataCallback));
        verify(mockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Card>() {

            @Override public boolean matches(Card card) {
                assertEquals(testingCardsForStudySystem.get(2),card);
                return true;
            }
        }));

        assertDoesNotThrow(() -> studySystemController.giveAnswer(studySystem10, true, mockSingleDataCallback1));
        verify(mockSingleDataCallback1).callSuccess(argThat(new ArgumentMatcher<Boolean>() {

            @Override
            public boolean matches(Boolean aBoolean) {
                assertTrue(aBoolean);
                return true;
            }
        }));
        reset(mockSingleDataCallback);
        reset(mockSingleDataCallback1);


        assertDoesNotThrow(() -> studySystemController.getNextCard(studySystem10,mockSingleDataCallback));
        verify(mockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Card>() {

            @Override public boolean matches(Card card) {
                assertEquals(testingCardsForStudySystem.get(3),card);
                return true;
            }
        }));

        assertDoesNotThrow(() -> studySystemController.giveAnswer(studySystem10, true, mockSingleDataCallback1));
        verify(mockSingleDataCallback1).callSuccess(argThat(new ArgumentMatcher<Boolean>() {

            @Override
            public boolean matches(Boolean aBoolean) {
                assertTrue(aBoolean);
                return true;
            }
        }));
        reset(mockSingleDataCallback);
        reset(mockSingleDataCallback1);


        assertDoesNotThrow(() -> studySystemController.getNextCard(studySystem10,mockSingleDataCallback));
        verify(mockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Card>() {

            @Override public boolean matches(Card card) {
                assertEquals(testingCardsForStudySystem.get(1),card);
                return true;
            }
        }));

        assertDoesNotThrow(() -> studySystemController.giveAnswer(studySystem10, true, mockSingleDataCallback1));
        verify(mockSingleDataCallback1).callSuccess(argThat(new ArgumentMatcher<Boolean>() {

            @Override
            public boolean matches(Boolean aBoolean) {
                assertTrue(aBoolean);
                return true;
            }
        }));
        reset(mockSingleDataCallback);
        reset(mockSingleDataCallback1);


        assertDoesNotThrow(() -> studySystemController.getNextCard(studySystem10,mockSingleDataCallback));
        verify(mockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Card>() {

            @Override public boolean matches(Card card) {
                assertEquals(testingCardsForStudySystem.get(4),card);
                return true;
            }
        }));

        assertDoesNotThrow(() -> studySystemController.giveAnswer(studySystem10, true, mockSingleDataCallback1));
        verify(mockSingleDataCallback1).callSuccess(argThat(new ArgumentMatcher<Boolean>() {

            @Override
            public boolean matches(Boolean aBoolean) {
                assertTrue(aBoolean);
                return true;
            }
        }));
        reset(mockSingleDataCallback);
        reset(mockSingleDataCallback1);



        assertDoesNotThrow(() -> studySystemController.getNextCard(studySystem10,mockSingleDataCallback));
        verify(mockSingleDataCallback).callSuccess(argThat(new ArgumentMatcher<Card>() {

            @Override public boolean matches(Card card) {
                assertNull(card);
                return true;
            }
        }));


        reset(mockSingleDataCallback);
        reset(mockSingleDataCallback1);

    }




    @Test
    public void ControllerGiveAnswerException() {
        StudySystemLogic studySystemMockLogic = mock(StudySystemLogic.class);
        on(studySystemController).set("studySystemLogic",studySystemMockLogic);
        doThrow(new RuntimeException("MockError")).when(studySystemMockLogic).giveAnswer(studySystem4,false);
        //mock DataCallback
        SingleDataCallback<Boolean> mockSingleDataCallback = mock(SingleDataCallback.class);
        assertDoesNotThrow(() -> studySystemController.giveAnswer(studySystem4, false, mockSingleDataCallback));
        verify(mockSingleDataCallback, times(1))
                .callFailure(Locale.getCurrentLocale().getString("giveanswererror"));
    }

    @Test
    public void ControllerGetNextCardException() {
        StudySystemLogic studySystemMockLogic = mock(StudySystemLogic.class);
        on(studySystemController).set("studySystemLogic",studySystemMockLogic);
        doThrow(new RuntimeException("MockError")).when(studySystemMockLogic).getNextCard(studySystem4);
        //mock DataCallback
        SingleDataCallback<Card> mockSingleDataCallback = mock(SingleDataCallback.class);
        assertDoesNotThrow(() -> studySystemController.getNextCard(studySystem4, mockSingleDataCallback));
        verify(mockSingleDataCallback, times(1))
                .callFailure(Locale.getCurrentLocale().getString("getnextcarderror"));
    }


    @Test
    public void LogicRepoExceptionGetSpecificCardToBoxException(){
        CardToBoxRepository cardToBoxMockRepo = mock(CardToBoxRepository.class);
        testingBoxMockCards = new ArrayList(Arrays.asList(testingCardsForStudySystem.get(0))); //mock so that list ist not empty
        on(studySystemLogic).set("cardToBoxRepository", cardToBoxMockRepo);
        on(studySystemLogic).set("testingBoxCards", testingBoxMockCards);
        when(cardToBoxMockRepo.getSpecific(testingCardsForStudySystem.get(0),studySystem4)).thenThrow(new RuntimeException("Mock"));
        assertThrows(RuntimeException.class, () -> studySystemLogic.giveAnswer(studySystem4,false));
    }

    @Test
    public void LogicRepoExceptionUpdateCardToBoxException(){
        CardToBoxRepository cardToBoxMockRepo = mock(CardToBoxRepository.class);
        testingBoxMockCards = new ArrayList(Arrays.asList(testingCardsForStudySystem.get(0))); //mock so that list ist not empty
        on(studySystemLogic).set("cardToBoxRepository", cardToBoxMockRepo);
        on(studySystemLogic).set("testingBoxCards", testingBoxMockCards);
        when(cardToBoxMockRepo.getSpecific(testingCardsForStudySystem.get(0),studySystem4)).thenReturn(new BoxToCard());
        doThrow(new RuntimeException("Mockter Fehler")).when(cardToBoxMockRepo).update(any(BoxToCard.class));
        assertThrows(RuntimeException.class, () -> studySystemLogic.giveAnswer(studySystem4,false));
    }

    @Test
    public void LogicRepoExceptionGetAllCardsInitiallyOrdered(){
        testingBoxMockCards = new ArrayList(new ArrayList()); //mock so that list ist  empty
        on(studySystemLogic).set("cardRepository", cardMockRepo);
        on(studySystemLogic).set("testingBoxCards", testingBoxMockCards);
        on(studySystemLogic).set("testingStarted", false);
        studySystem4.setNotLearnedYet(true);
        studySystem4.setCardOrder(StudySystem.CardOrder.ALPHABETICAL);
        when(cardMockRepo.getAllCardsInitiallyOrdered(studySystem4,"asc")).thenThrow(new RuntimeException("Mock"));
        assertThrows(RuntimeException.class, () -> studySystemLogic.getNextCard(studySystem4));
    }
    @Test
    public void LogicRepoExceptionGetAllCardsForStudySystem(){
        testingBoxMockCards = new ArrayList(new ArrayList()); //mock so that list ist  empty
        on(studySystemLogic).set("cardRepository", cardMockRepo);
        on(studySystemLogic).set("testingBoxCards", testingBoxMockCards);
        on(studySystemLogic).set("testingStarted", false);
        studySystem4.setNotLearnedYet(true);
        studySystem4.setCardOrder(StudySystem.CardOrder.REVERSED_ALPHABETICAL);
        when(cardMockRepo.getAllCardsInitiallyOrdered(studySystem4,"desc")).thenThrow(new RuntimeException("Mock"));
        assertThrows(RuntimeException.class, () -> studySystemLogic.getNextCard(studySystem4));
    }
    @Test
    public void LogicRepoExceptionGetLeitner(){
        StudySystem leitner  = new LeitnerSystem();
        testingBoxMockCards = new ArrayList(new ArrayList()); //mock so that list ist  empty
        on(studySystemLogic).set("cardRepository", cardMockRepo);
        on(studySystemLogic).set("testingBoxCards", testingBoxMockCards);
        on(studySystemLogic).set("testingStarted", false);
        leitner.setNotLearnedYet(false);
        when(cardMockRepo.getAllCardsNeededToBeLearned(leitner)).thenThrow(new RuntimeException("Mock"));
        assertThrows(RuntimeException.class, () -> studySystemLogic.getNextCard(leitner));
    }
    @Test
    public void LogicRepoExceptionGetTiming(){
        StudySystem timing  = new TimingSystem();
        testingBoxMockCards = new ArrayList(new ArrayList()); //mock so that list ist  empty
        on(studySystemLogic).set("cardRepository", cardMockRepo);
        on(studySystemLogic).set("testingBoxCards", testingBoxMockCards);
        on(studySystemLogic).set("testingStarted", false);
        timing.setNotLearnedYet(false);
        when(cardMockRepo.getAllCardsForTimingSystem(timing)).thenThrow(new RuntimeException("Mock"));
        assertThrows(RuntimeException.class, () -> studySystemLogic.getNextCard(timing));
    }
    @Test
    public void LogicRepoExceptionGetVote(){
        StudySystem vote  = new VoteSystem();
        testingBoxMockCards = new ArrayList(new ArrayList()); //mock so that list ist  empty
        on(studySystemLogic).set("cardRepository", cardMockRepo);
        on(studySystemLogic).set("testingBoxCards", testingBoxMockCards);
        on(studySystemLogic).set("testingStarted", false);
        vote.setNotLearnedYet(false);
        when(cardMockRepo.getAllCardsSortedForVoteSystem(vote)).thenThrow(new RuntimeException("Mock"));
        assertThrows(RuntimeException.class, () -> studySystemLogic.getNextCard(vote));
    }


    @Test
    public void PersistenceNoTransaction() {
        assertThrows(NullPointerException.class, () -> cardToBoxRepository.getSpecific(testingCardsForStudySystem.get(0),studySystem4));
        assertThrows(IllegalStateException.class, () -> cardToBoxRepository.update(new BoxToCard(testingCardsForStudySystem.get(0),studySystem4.getBoxes().get(0))));
        assertThrows(NullPointerException.class, () -> cardRepository.getAllCardsInitiallyOrdered(studySystem4,"asc"));
        assertThrows(NullPointerException.class, () -> cardRepository.getAllCardsForStudySystem(studySystem4));
        assertThrows(NullPointerException.class, () -> cardRepository.getAllCardsForTimingSystem(studySystem4));
        assertThrows(NullPointerException.class, () -> cardRepository.getAllCardsNeededToBeLearned(studySystem4));
        assertThrows(NullPointerException.class, () -> cardRepository.getAllCardsSortedForVoteSystem(studySystem4));
    }

}
