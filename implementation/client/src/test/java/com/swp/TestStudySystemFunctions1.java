package com.swp;

import com.gumse.gui.Locale;
import com.gumse.tools.Output;
import com.gumse.tools.Toolbox;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardTypes.MultipleChoiceCard;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.DataModel.CardTypes.TrueFalseCard;
import com.swp.DataModel.StudySystem.*;
import com.swp.Logic.CardLogic;
import com.swp.Logic.StudySystemLogic;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;

import java.sql.Timestamp;
import java.util.*;

import static org.joor.Reflect.on;
import static org.junit.jupiter.api.Assertions.*;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class TestStudySystemFunctions1 {

    public StudySystemLogic studySystemLogic = StudySystemLogic.getInstance();
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
        locale = new Locale("German", "de");
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

        erdkunde.add(new TextCard());
        erdkunde.add(new TrueFalseCard());
        erdkunde.add(new TextCard());
        erdkunde.add(new MultipleChoiceCard());
        erdkunde.add(new MultipleChoiceCard());

        for (Card c : erdkunde) {
            cardLogic.updateCardData(c, true);
        }
         studySystem1 = new LeitnerSystem("Erdkunde", StudySystem.CardOrder.ALPHABETICAL);
        studySystemLogic.updateStudySystemData(null, studySystem1, true);
        studySystemLogic.moveAllCardsForDeckToFirstBox(erdkunde, studySystem1);

        studySystem2 = new TimingSystem("Erdkunde1", StudySystem.CardOrder.REVERSED_ALPHABETICAL, 10);
        studySystemLogic.updateStudySystemData(null, studySystem2, true);
        studySystemLogic.moveAllCardsForDeckToFirstBox(erdkunde, studySystem2);

        studySystem3 = new VoteSystem("Erdkunde2", StudySystem.CardOrder.RANDOM);
        studySystemLogic.updateStudySystemData(null, studySystem3, true);
        studySystemLogic.moveAllCardsForDeckToFirstBox(erdkunde, studySystem3);

        studySystem4 = new VoteSystem("Erdkunde3", StudySystem.CardOrder.RANDOM);
        studySystemLogic.updateStudySystemData(null, studySystem4, true);
        studySystemLogic.moveAllCardsForDeckToFirstBox(erdkunde, studySystem4);

    }

    @Test
    public void getNextCardsForLeitnerSystem() {
        Card card = studySystemLogic.getNextCard(studySystem1);
        assertNotNull(card);
    }

    @Test
    public void getNextCardsForTimingSystem() {
        Card card = studySystemLogic.getNextCard(studySystem1);
        assertNotNull(card);
         card = studySystemLogic.getNextCard(studySystem1);
        assertNotNull(card);
         card = studySystemLogic.getNextCard(studySystem1);
        assertNotNull(card);
      //  card = studySystemLogic.getNextCard(studySystem1);

    }

    @Test
    public void getNextCardsForVoteSystem() {
        Card card = studySystemLogic.getNextCard(studySystem1);
        assertNotNull(card);
    }

    @Test
    public void testDueDate(){
        BoxToCard boxToCard = studySystemLogic.getBoxToCard(erdkunde.get(0), studySystem1);

        GregorianCalendar cal = new GregorianCalendar();
        cal.setTime(new Timestamp(System.currentTimeMillis()));
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);

        studySystemLogic.changeCardDueDate(boxToCard);
        assertEquals(cal.getTime(), boxToCard.getLearnedNextAt());

        studySystemLogic.moveCardToBox(boxToCard,1,studySystem1);
        studySystemLogic.changeCardDueDate(boxToCard);
        cal.add(Calendar.DATE,1);
        assertEquals(cal.getTime(), boxToCard.getLearnedNextAt());
        cal.add(Calendar.DATE,-1);//reset

        studySystemLogic.moveCardToBox(boxToCard,2,studySystem1);
        studySystemLogic.changeCardDueDate(boxToCard);
        cal.add(Calendar.DATE,3);
        assertEquals(cal.getTime(), boxToCard.getLearnedNextAt());
        cal.add(Calendar.DATE,-3); //reset


        studySystemLogic.moveCardToBox(boxToCard,3,studySystem1);
        studySystemLogic.changeCardDueDate(boxToCard);
        cal.add(Calendar.DATE,7);
        assertEquals(cal.getTime(), boxToCard.getLearnedNextAt());
        cal.add(Calendar.DATE,-7); //reset

        studySystemLogic.moveCardToBox(boxToCard,4,studySystem1);
        studySystemLogic.changeCardDueDate(boxToCard);
        cal.add(Calendar.DAY_OF_YEAR,14);
        assertEquals(cal.getTime(), boxToCard.getLearnedNextAt());

    }




    @Test
    public void giveAnswerTestNotLeitner(){
        testingBoxMockCards = new ArrayList(Arrays.asList(erdkunde.get(0), erdkunde.get(1), erdkunde.get(2), erdkunde.get(3))); //mock so that list ist not empty
        on(studySystemLogic).set("testingBoxCards",testingBoxMockCards);

        //Testfall 1: Antwort richtig
        studySystemLogic.giveAnswer(studySystem2,true);
        assertEquals(1,studySystem2.getTrueAnswerCount());
        assertEquals(1, studySystem2.getQuestionCount());
        assertEquals(3,testingBoxMockCards.size());
        BoxToCard boxToCard1 = studySystemLogic.getBoxToCard(erdkunde.get(0),studySystem2);
        assertEquals(0,boxToCard1.getStudySystemBox().getBoxNumber()); //no box moving, if true or false
        assertTrue(boxToCard1.getStatus().equals(BoxToCard.CardStatus.LEARNED));

        //Testfall 2: Antwort falsch
        studySystemLogic.giveAnswer(studySystem2,false);
        assertEquals(1,studySystem2.getTrueAnswerCount());
        assertEquals(2, studySystem2.getQuestionCount());
        assertEquals(3,testingBoxMockCards.size());
        BoxToCard boxToCard2 = studySystemLogic.getBoxToCard(erdkunde.get(1),studySystem2);
        assertEquals(0,boxToCard1.getStudySystemBox().getBoxNumber()); //no box moving, if true or false
        assertTrue(boxToCard1.getStatus().equals(BoxToCard.CardStatus.LEARNED));
    }



    @Test
    public void testNumOfLearnedCards(){
        assertEquals(0, studySystemLogic.getAllCardsLearnedInStudySystem(studySystem4));
        BoxToCard boxToCard1 = studySystemLogic.getBoxToCard(erdkunde.get(0),studySystem4);
        BoxToCard boxToCard2 = studySystemLogic.getBoxToCard(erdkunde.get(1),studySystem4);
        BoxToCard boxToCard3 = studySystemLogic.getBoxToCard(erdkunde.get(2),studySystem4);
        BoxToCard boxToCard4 = studySystemLogic.getBoxToCard(erdkunde.get(3),studySystem4);

        boxToCard1.setStatus(BoxToCard.CardStatus.LEARNED);
        boxToCard2.setStatus(BoxToCard.CardStatus.RELEARN);
        boxToCard3.setStatus(BoxToCard.CardStatus.LEARNED);
        boxToCard4.setStatus(BoxToCard.CardStatus.LEARNED);

        for(BoxToCard c : Arrays.asList(boxToCard1,boxToCard2,boxToCard3,boxToCard4)){
            studySystemLogic.updateCardToBox(c);
        }

        assertEquals(3,studySystemLogic.getAllCardsLearnedInStudySystem(studySystem4));
        assertEquals(5,studySystemLogic.numCardsInDeck(studySystem4));
    }
}
