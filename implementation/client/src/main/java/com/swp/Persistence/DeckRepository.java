package com.swp.Persistence;

import java.util.ArrayList;
import java.util.Set;

import com.gumse.textures.Texture;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardToDeck;
import com.swp.DataModel.Deck;
import com.swp.DataModel.StudySystem;
import com.swp.DataModel.StudySystemType;
import com.swp.DataModel.Card.CardType;
import com.swp.DataModel.CardTypes.AudioCard;
import com.swp.DataModel.CardTypes.ImageDescriptionCard;
import com.swp.DataModel.CardTypes.ImageDescriptionCardAnswer;
import com.swp.DataModel.CardTypes.ImageTestCard;
import com.swp.DataModel.CardTypes.MultipleChoiceCard;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.DataModel.CardTypes.TrueFalseCard;
import com.swp.DataModel.Deck.CardOrder;
import com.swp.DataModel.StudySystemTypes.LeitnerSystem;
import com.swp.DataModel.StudySystemTypes.TimingSystem;
import com.swp.DataModel.StudySystemTypes.VoteSystem;

public class DeckRepository
{

    public static boolean saveDeck(Deck deck)
    {
        //server.send("/createdeck", jsonString);
        return false;
    }

    /**
     *
     * @param olddeck
     * @param newdeck
     */
    public static boolean updateDeck(Deck olddeck, Deck newdeck)
    {

        //server.send("/updatedeckdata", jsonString);
        return false;
    }

    public static boolean deleteDeck(Deck deck)
    {
        //server.send("/deletedeck", jsonString);
        return false;
    }

    public static Set<Deck> getDecks()
    {
        Set<Deck> decks = Cache.getInstance().getDecks();
        if(!decks.isEmpty())
            return decks;


        /////////////////////////////////////////////////////////////////
        //
        // TEMPORARY
        //
        for(int i = 0; i < 6; i += 3)
        {
            Deck deck = new Deck("Deck " + i, null, CardOrder.ALPHABETICAL, true);
            deck.setStudySystem(new LeitnerSystem(deck));
            decks.add(deck);

            deck = new Deck("Deck " + (i+1), null, CardOrder.RANDOM, true); 
            deck.setStudySystem(new TimingSystem(deck,5));
            decks.add(deck); 

            deck = new Deck("Deck " + (i+2), null, CardOrder.REVERSED_ALPHABETICAL, true);
            deck.setStudySystem(new VoteSystem(deck));
            decks.add(deck); 
        }
        Cache.getInstance().setDecks(decks);
        return decks;
        /////////////////////////////////////////////////////////////////

        //server.send("/getdecks", jsonString);
        //return null;
    }

    public static Set<CardToDeck> getCardToDecks()
    {
        Set<CardToDeck> card2decks = Cache.getInstance().getCardToDecks();
        if(!card2decks.isEmpty())
            return card2decks;

        /////////////////////////////////////////////////////////////////
        //
        // TEMPORARY
        //

        Texture ket = new Texture("orangeket");
        ket.load("textures/orange-ket.png", DeckRepository.class);

        ArrayList<Card> cards = new ArrayList<Card>();
        cards.add(new MultipleChoiceCard("Multiple Choice Complicated Question Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque mauris elit, luctus id mollis a, posuere sed ante. Mauris a aliquet est. Integer egestas massa ac erat finibus iaculis. Vestibulum vitae dignissim lacus. Cras augue ante, semper id est sit amet, accumsan porta lacus. Ut rhoncus dui justo", new String[] {"Answer 1", "Answer 2", "Answer 3"}, new int[] {1}, "notitle", false));
        cards.add(new TrueFalseCard("True False Complicated Question Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque mauris elit, luctus id mollis a, posuere sed ante. Mauris a aliquet est. Integer egestas massa ac erat finibus iaculis. Vestibulum vitae dignissim lacus. Cras augue ante, semper id est sit amet, accumsan porta lacus. Ut rhoncus dui justo", true, "notitle", false));
        cards.add(new TextCard("Text Complicated Question Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque mauris elit, luctus id mollis a, posuere sed ante. Mauris a aliquet est. Integer egestas massa ac erat finibus iaculis. Vestibulum vitae dignissim lacus. Cras augue ante, semper id est sit amet, accumsan porta lacus. Ut rhoncus dui justo", "text answer", "notitle", false));
        cards.add(new ImageDescriptionCard("Image Desc Complicated Question Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque mauris elit, luctus id mollis a, posuere sed ante. Mauris a aliquet est. Integer egestas massa ac erat finibus iaculis. Vestibulum vitae dignissim lacus. Cras augue ante, semper id est sit amet, accumsan porta lacus. Ut rhoncus dui justo", new ImageDescriptionCardAnswer[] {}, "notitle", null, false));
        cards.add(new ImageTestCard("Image Test Complicated Question Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque mauris elit, luctus id mollis a, posuere sed ante. Mauris a aliquet est. Integer egestas massa ac erat finibus iaculis. Vestibulum vitae dignissim lacus. Cras augue ante, semper id est sit amet, accumsan porta lacus. Ut rhoncus dui justo", "image answer", null, "notitle", false, false));
        cards.add(new AudioCard(null, "notitle", "Audio Complicated Question Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque mauris elit, luctus id mollis a, posuere sed ante. Mauris a aliquet est. Integer egestas massa ac erat finibus iaculis. Vestibulum vitae dignissim lacus. Cras augue ante, semper id est sit amet, accumsan porta lacus. Ut rhoncus dui justo", "audio answer", false, false));
        for(Deck deck : getDecks())
        {
            for(Card card : cards)
            {
                CardToDeck cardtodeck = new CardToDeck(card, deck);
                card2decks.add(cardtodeck);
            }
        }
        Cache.getInstance().setCardToDecks(card2decks);
        return card2decks;
        /////////////////////////////////////////////////////////////////

        //server.send("/getcardtodecks", jsonString);
        //return null;
    }

    //
    // StudySystem
    //
    public static boolean updateStudySystem(Deck deck, StudySystem system)
    {

        //server.send("/updatestudysystem", jsonString);
        return false;
    }

    public static boolean addStudySystemType(StudySystemType type)
    {
        //True if successfully added to cache
        return false;
    }

    public static boolean updateStudySystemTypes()
    {

        //server.send("/updatestudysystemtypes", jsonString);
        return false;
    }
    
    public static StudySystem getStudySystem(Deck deck)
    {
        //server.send("/getstudysystem", jsonString);
        return null;
    }
    
    public static Set<StudySystemType> getStudySystemTypes()
    {
        Set<StudySystemType> types = Cache.getInstance().getStudySystemTypes();
        if(!types.isEmpty())
            return types;

        //server.send("/getstudysystemtypes", jsonString);
        return null;
    }
}