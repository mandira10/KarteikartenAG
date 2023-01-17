package com.swp.Controller;

import com.swp.DataModel.Card;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.DataModel.Deck;
import com.swp.DataModel.StudySystem.LeitnerSystem;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.Logic.CardLogic;
import com.swp.Logic.DeckLogic;
import org.junit.Test;

import java.util.Arrays;
import java.util.List;

public class DeckTest {
    private final DeckLogic deckLogic = DeckLogic.getInstance();
    private final CardLogic cardLogic = CardLogic.getInstance();

    @Test
    public void testCreateDeck(){

        Card card1  = new TextCard("Testfrage","Testantwort","Testtitel",true);
        Card card2  = new TextCard("Testfrage1","Testantwort1","Testtitel1",true);
        Card card3  = new TextCard("Testfrage2","Testantwort2","Testtitel2",true);
        final List<Card> cards = Arrays.asList(new Card[]{card1,card2,card3});
        for (Card c: cards){
            cardLogic.updateCardData(c,true);
        }
        Deck deck = new Deck("Schule", Deck.CardOrder.ALPHABETICAL,false);
        StudySystem studySystem = new LeitnerSystem(deck);
        studySystem.moveAllCardsForDeckToFirstBox(cards);
        deckLogic.updateDeckData(null, deck, true);
        List<Deck> decks = deckLogic.getDecks();
    }

}
