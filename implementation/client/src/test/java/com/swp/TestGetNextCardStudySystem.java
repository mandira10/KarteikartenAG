package com.swp;

import com.gumse.gui.Locale;
import com.gumse.tools.Output;
import com.gumse.tools.Toolbox;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardTypes.MultipleChoiceCard;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.DataModel.CardTypes.TrueFalseCard;
import com.swp.DataModel.StudySystem.LeitnerSystem;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.DataModel.StudySystem.TimingSystem;
import com.swp.DataModel.StudySystem.VoteSystem;
import com.swp.Logic.CardLogic;
import com.swp.Logic.StudySystemLogic;
import com.swp.Persistence.CardRepository;
import com.swp.Persistence.CardToBoxRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.joor.Reflect.on;
import static org.junit.jupiter.api.Assertions.*;

/**
 * Klasset testet die Funktion getNextCard() im StudySystem
 */
public class TestGetNextCardStudySystem {

    public StudySystemLogic studySystemLogic = StudySystemLogic.getInstance();
    public CardLogic cardLogic = CardLogic.getInstance();

    private Locale locale;
    private int i;


    @BeforeEach
    public void beforeEach(){
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
    });}

    @Test
    public void testGetNextCardsInitiallyAlphabetical(){
        Card card1 = new TextCard("A beginnt","B verliert","C-Titel");
        Card card2 = new TrueFalseCard("Z beginnt",true,"B-Titel");
        Card card3 = new TextCard("L beginnt","B verliert","C-Titel");
        Card card4 = new TextCard("- beginnt","C verliert","D-Titel");
        Card card5 = new TextCard("Öffis beginnt","D verliert","E-Titel");
        List <Card>  random1 = Arrays.asList(card1, card5,card2,card4,card3);
        for (Card c : random1) {
            cardLogic.updateCardData(c, true);
        }
        StudySystem studySystem1 = new LeitnerSystem("Alpha", StudySystem.CardOrder.ALPHABETICAL);
        studySystemLogic.updateStudySystemData(null, studySystem1, true);
        studySystemLogic.moveAllCardsForDeckToFirstBox(random1, studySystem1);
        
     
        Card firstCard = studySystemLogic.getNextCard(studySystem1);
        assertEquals(card4,firstCard);
        studySystemLogic.giveAnswer(studySystem1,true);
        Card secondCard = studySystemLogic.getNextCard(studySystem1);
        assertEquals(card1,secondCard);
        studySystemLogic.giveAnswer(studySystem1,true);
        Card thirdCard = studySystemLogic.getNextCard(studySystem1);
        assertEquals(card3,thirdCard);
        studySystemLogic.giveAnswer(studySystem1,true);
        Card fourthCard = studySystemLogic.getNextCard(studySystem1);
        assertEquals(card2,fourthCard);
        studySystemLogic.giveAnswer(studySystem1,true);
        Card fifthCard = studySystemLogic.getNextCard(studySystem1);
        assertEquals(card5,fifthCard);
        studySystemLogic.giveAnswer(studySystem1,true);

        on(studySystemLogic).set("testingStarted",false);
        //relearn again

        assertNull(studySystemLogic.getNextCard(studySystem1));
    }

    @Test
    public void testGetNextCardsInitiallyRevAlphabetical(){
        Card card1 = new TextCard("A beginnt","B verliert","C-Titel");
        Card card2 = new TrueFalseCard("Z beginnt",true,"B-Titel");
        Card card3 = new TextCard("L beginnt","B verliert","C-Titel");
        Card card4 = new TextCard("- beginnt","C verliert","D-Titel");
        Card card5 = new TextCard("Öffis beginnt","D verliert","E-Titel");
        List <Card>  random1 = Arrays.asList(card1, card5,card2,card4,card3);
        for (Card c : random1) {
            cardLogic.updateCardData(c, true);
        }

        StudySystem studySystem2 = new TimingSystem("revAlpha", StudySystem.CardOrder.REVERSED_ALPHABETICAL, 10);
        studySystemLogic.updateStudySystemData(null, studySystem2, true);
        studySystemLogic.moveAllCardsForDeckToFirstBox(random1, studySystem2);
        on(studySystemLogic).set("testingStarted",false);

        Card firstCard = studySystemLogic.getNextCard(studySystem2);
        assertEquals(card5,firstCard);
        studySystemLogic.giveAnswer(studySystem2,true);
        Card secondCard = studySystemLogic.getNextCard(studySystem2);
        assertEquals(card2,secondCard);
        studySystemLogic.giveAnswer(studySystem2,true);
        Card thirdCard = studySystemLogic.getNextCard(studySystem2);
        assertEquals(card3,thirdCard);
        studySystemLogic.giveAnswer(studySystem2,true);
        Card fourthCard = studySystemLogic.getNextCard(studySystem2);
        assertEquals(card1,fourthCard);
        studySystemLogic.giveAnswer(studySystem2,true);
        Card fifthCard = studySystemLogic.getNextCard(studySystem2);
        assertEquals(card4,fifthCard);
        studySystemLogic.giveAnswer(studySystem2,true);

        on(studySystemLogic).set("testingStarted",false);
        //relearn again

        assertNotNull(studySystemLogic.getNextCard(studySystem2)); //nur eine Box bei Timing
    }

    @Test
    public void testGetNextCardsInitiallyRandom(){
        Card card1 = new TextCard("A beginnt","B verliert","C-Titel");
        Card card2 = new TrueFalseCard("Z beginnt",true,"B-Titel");
        Card card3 = new TextCard("L beginnt","B verliert","C-Titel");
        Card card4 = new TextCard("- beginnt","C verliert","D-Titel");
        Card card5 = new TextCard("Öffis beginnt","D verliert","E-Titel");
        List <Card>  random1 = Arrays.asList(card1, card5,card2,card4,card3);
        for (Card c : random1) {
            cardLogic.updateCardData(c, true);
        }
        
        StudySystem studySystem3 = new VoteSystem("Random3", StudySystem.CardOrder.RANDOM);
        studySystemLogic.updateStudySystemData(null, studySystem3, true);
        studySystemLogic.moveAllCardsForDeckToFirstBox(random1, studySystem3);
        on(studySystemLogic).set("testingStarted",false);


        Card firstCard = studySystemLogic.getNextCard(studySystem3);
        //TODO test queries of studySystemtypes
        //TODO verify with mockito that Collections random was called

        //        assertEquals(card5,firstCard);
//        studySystemLogic.giveAnswer(studySystem3,true);
//        Card secondCard = studySystemLogic.getNextCard(studySystem3);
//        assertEquals(card2,secondCard);
//        studySystemLogic.giveAnswer(studySystem3,true);
//        Card thirdCard = studySystemLogic.getNextCard(studySystem3);
//        assertEquals(card3,thirdCard);
//        studySystemLogic.giveAnswer(studySystem3,true);
//        Card fourthCard = studySystemLogic.getNextCard(studySystem3);
//        assertEquals(card1,fourthCard);
//        studySystemLogic.giveAnswer(studySystem3,true);
//        Card fifthCard = studySystemLogic.getNextCard(studySystem3);
//        assertEquals(card4,fifthCard);
//        studySystemLogic.giveAnswer(studySystem3,true);
//
//        on(studySystemLogic).set("testingStarted",false);
//        //relearn again

    }
    


}
