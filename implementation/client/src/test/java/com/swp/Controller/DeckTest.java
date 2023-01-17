package com.swp.Controller;

import com.swp.DataModel.Deck;
import com.swp.DataModel.StudySystem.LeitnerSystem;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.Logic.DeckLogic;
import org.junit.Test;

public class DeckTest {
    private final DeckLogic deckLogic = DeckLogic.getInstance();

    @Test
    public void testCreateDeck(){
        StudySystem studySystem = new LeitnerSystem();
        Deck deck = new Deck("Schule", studySystem, Deck.CardOrder.ALPHABETICAL, false);
        deckLogic.updateDeckData(null, deck, true);
    }

}
