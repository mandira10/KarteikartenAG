package com.swp;

import com.gumse.gui.Locale;
import com.gumse.tools.Output;
import com.gumse.tools.Toolbox;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardOverview;
import com.swp.DataModel.CardTypes.MultipleChoiceCard;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.DataModel.CardTypes.TrueFalseCard;
import com.swp.DataModel.StudySystem.*;
import com.swp.Logic.CardLogic;
import com.swp.Logic.StudySystemLogic;
import com.swp.Persistence.CardRepository;
import com.swp.Persistence.CardToBoxRepository;
import com.swp.Persistence.StudySystemRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.Timestamp;
import java.util.*;

import static org.joor.Reflect.on;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;
import static org.mockito.Mockito.mock;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestStudySystemFunctions2 {

    public StudySystemLogic studySystemLogic = StudySystemLogic.getInstance();
    public CardToBoxRepository cardToBoxMockRepo  = mock(CardToBoxRepository.class);
    public CardRepository cardMockRepo = mock(CardRepository.class);
    public CardLogic cardLogic = CardLogic.getInstance();

    private Locale locale;
    private int i;

    List<Card> erdkunde = new ArrayList<>();
    StudySystem studySystem1;
    StudySystem studySystem2;
    StudySystem studySystem3;

    StudySystem studySystem4;

    List testingBoxMockCards;


    @BeforeAll
    public void beforeAll(){
        locale = new com.gumse.gui.Locale("German", "de");
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

        //Karten 2: Spanisch
        erdkunde.add(new MultipleChoiceCard("Welches der folgenden ist das flächenmäßig kleinste Bundesland?", new String[]{"Thüringen", "Sachsen", "Saarland"}, new int[]{2}, "Bundesländer Größe"));
        erdkunde.add(new MultipleChoiceCard("Welcher See liegt nicht in Bayern?", new String[]{"Ammersee", "Schweriner See", "Starnberger See"}, new int[]{0}, "Sees in Bayern"));
        erdkunde.add(new TextCard("Über welche Länge erstreckt sich das Uralgebirge?", "2.400 Kilometer", "Strecke des Uralgebirges"));
        erdkunde.add(new MultipleChoiceCard("Welche dieser Städte liegt am nördlichsten?", new String[]{"Adelaide", "Perth", "Melbourne", "Brisbane"}, new int[]{3}, "Nördlichste Stadt Australiens"));
        erdkunde.add(new TrueFalseCard("Die USA hat insgesamt 50 Bundesstaaten", true, "USA Bundesstaaten"));


        for (Card c : erdkunde) {
            cardLogic.updateCardData(c, true);
        }
        studySystem1 = new LeitnerSystem("Erdkunde$", StudySystem.CardOrder.ALPHABETICAL);
        studySystemLogic.updateStudySystemData(null, studySystem1, true);
        studySystemLogic.moveAllCardsForDeckToFirstBox(erdkunde, studySystem1);

        studySystem2 = new TimingSystem("Erdkunde6", StudySystem.CardOrder.REVERSED_ALPHABETICAL, 10);
        studySystemLogic.updateStudySystemData(null, studySystem2, true);
        studySystemLogic.moveAllCardsForDeckToFirstBox(erdkunde, studySystem2);

        studySystem3 = new VoteSystem("Erdkunde7", StudySystem.CardOrder.RANDOM);
        studySystemLogic.updateStudySystemData(null, studySystem3, true);
        studySystemLogic.moveAllCardsForDeckToFirstBox(erdkunde, studySystem3);

        studySystem4 = new VoteSystem("Erdkunde8", StudySystem.CardOrder.RANDOM);
        studySystemLogic.updateStudySystemData(null, studySystem4, true);
        studySystemLogic.moveAllCardsForDeckToFirstBox(erdkunde, studySystem4);

    }


    @Test
    public void testFunctionOfGiveAnswerLeitnerTrueAndFalseFunction(){
        //Testfall1 : Answer true and in box 0
        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Timestamp(System.currentTimeMillis()));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        assertEquals(0,studySystem1.getTrueAnswerCount());
        assertEquals(0, studySystem1.getQuestionCount());
        //mock
        testingBoxMockCards = new ArrayList(Arrays.asList(erdkunde.get(0), erdkunde.get(1), erdkunde.get(2), erdkunde.get(3))); //mock so that list ist not empty
        on(studySystemLogic).set("testingBoxCards",testingBoxMockCards);
        studySystemLogic.giveAnswer(studySystem1,true);
        assertEquals(1,studySystem1.getTrueAnswerCount());
        assertEquals(1, studySystem1.getQuestionCount());
        assertEquals(3,testingBoxMockCards.size());
        BoxToCard boxToCard1 = studySystemLogic.getBoxToCard(erdkunde.get(0),studySystem1);
        assertEquals(1,boxToCard1.getStudySystemBox().getBoxNumber());
        assertTrue(boxToCard1.getStatus().equals(BoxToCard.CardStatus.RELEARN));

        //Check date and reset
        cal.add(Calendar.DATE,1);
        assertEquals(cal.getTime(),boxToCard1.getLearnedNextAt() );
        cal.add(Calendar.DATE,-1);

        //Test 2: Antwort falsch und in Box 0
        studySystemLogic.giveAnswer(studySystem1,false);
        assertEquals(1,studySystem1.getTrueAnswerCount());
        assertEquals(2, studySystem1.getQuestionCount());
        assertEquals(3,testingBoxMockCards.size());
        assertEquals(erdkunde.get(1),testingBoxMockCards.get(2));
        BoxToCard boxToCard2 = studySystemLogic.getBoxToCard(erdkunde.get(1),studySystem1);
        assertEquals(0,boxToCard2.getStudySystemBox().getBoxNumber());
        assertTrue(boxToCard2.getStatus().equals(BoxToCard.CardStatus.RELEARN));

        cal.add(Calendar.DATE,0);
        assertEquals(cal.getTime(),boxToCard2.getLearnedNextAt() );
        cal.add(Calendar.DATE,0);

        //before mock, get actual CardToBox we want to use
        BoxToCard boxToCard3 = studySystemLogic.getBoxToCard(erdkunde.get(2),studySystem1);
        BoxToCard boxToCard4 = studySystemLogic.getBoxToCard(erdkunde.get(3),studySystem1);

        //Test 3: Antwort falsch, Box 1
        studySystemLogic.moveCardToBox(boxToCard3, 1,studySystem1);
        //mock CardToBox to level 1

        on(studySystemLogic).set("cardToBoxRepository", cardToBoxMockRepo);
        when(cardToBoxMockRepo.getSpecific(erdkunde.get(2),studySystem1)).thenReturn(boxToCard3);
        studySystemLogic.giveAnswer(studySystem1,false);
        assertEquals(1,studySystem1.getTrueAnswerCount());
        assertEquals(3, studySystem1.getQuestionCount());
        assertEquals(3,testingBoxMockCards.size());
        assertEquals(erdkunde.get(2),testingBoxMockCards.get(2));

        assertEquals(0,boxToCard3.getStudySystemBox().getBoxNumber());
        assertTrue(boxToCard3.getStatus().equals(BoxToCard.CardStatus.RELEARN));

        cal.add(Calendar.DATE,0);
        assertEquals(cal.getTime(),boxToCard3.getLearnedNextAt() );
        cal.add(Calendar.DATE,0);

        //Test 3: Antwort richtig, Box 4
        studySystemLogic.moveCardToBox(boxToCard4, 4,studySystem1);
        //mock CardToBox to level 4
        when(cardToBoxMockRepo.getSpecific(erdkunde.get(3),studySystem1)).thenReturn(boxToCard4);
        studySystemLogic.giveAnswer(studySystem1,true);
        assertEquals(2,studySystem1.getTrueAnswerCount());
        assertEquals(4, studySystem1.getQuestionCount());
        assertEquals(2,testingBoxMockCards.size());

        assertEquals(4,boxToCard4.getStudySystemBox().getBoxNumber());
        assertTrue(boxToCard4.getStatus().equals(BoxToCard.CardStatus.RELEARN));

        //Check date and reset
        cal.add(Calendar.DATE,14);
        assertEquals(cal.getTime(),boxToCard4.getLearnedNextAt() );
        cal.add(Calendar.DATE,-14);
        reset(cardToBoxMockRepo);
    }

    @Test
    public void testFinishTestAndGetResult(){
        testingBoxMockCards = new ArrayList(Arrays.asList(erdkunde.get(0), erdkunde.get(1), erdkunde.get(2), erdkunde.get(3))); //mock so that list ist not empty
        on(studySystemLogic).set("testingBoxCards",testingBoxMockCards);
        assertEquals(0,studySystem3.getTrueAnswerCount());
        assertEquals(0,studySystem3.getQuestionCount());

        //first test nothing was learned
        //assertThrows(NoResultException.class,() -> studySystemLogic.finishTestAndGetResult(studySystem3));
        assertEquals(0,studySystemLogic.finishTestAndGetResult(studySystem3));
        assertEquals(0,studySystem3.getTrueAnswerCount());
        assertEquals(0,studySystem3.getQuestionCount());
        assertEquals(0,studySystem3.getProgress());
        assertTrue(testingBoxMockCards.isEmpty());

        testingBoxMockCards = new ArrayList(Arrays.asList(erdkunde.get(0), erdkunde.get(1), erdkunde.get(2), erdkunde.get(3))); //mock so that list ist not empty
        on(studySystemLogic).set("testingBoxCards",testingBoxMockCards);
        studySystem3.setQuestionCount(20); //nothing will change as long the trueAnswerCount is 0
        assertEquals(0,studySystemLogic.finishTestAndGetResult(studySystem3));
        assertEquals(0,studySystem3.getProgress());
        assertTrue(testingBoxMockCards.isEmpty());


        testingBoxMockCards = new ArrayList(Arrays.asList(erdkunde.get(0), erdkunde.get(1), erdkunde.get(2), erdkunde.get(3))); //mock so that list ist not empty
        on(studySystemLogic).set("testingBoxCards",testingBoxMockCards);
        studySystem3.setQuestionCount(20); //nothing will change as long the trueAnswerCount is 0
        studySystem3.setTrueAnswerCount(20);

        on(studySystemLogic).set("cardRepository",cardMockRepo);


        List<CardOverview> cards = new ArrayList<>();
        for ( int i = 0; i < 33; i++){
            cards.add(new CardOverview());
        }

        when(cardMockRepo.findCardsByStudySystem(studySystem3)).thenReturn(cards);
        when(cardMockRepo.getNumberOfLearnedCardsByStudySystem(studySystem3)).thenReturn(20L);

        assertEquals(100,studySystemLogic.finishTestAndGetResult(studySystem3));
        assertEquals(61,studySystem3.getProgress());
        assertTrue(testingBoxMockCards.isEmpty());
        assertEquals(0,studySystem3.getTrueAnswerCount());
        assertEquals(0,studySystem3.getQuestionCount());
        reset(cardMockRepo);
    }

}
