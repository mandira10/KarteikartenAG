package com.swp.Persistence;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardToDeck;
import com.swp.DataModel.Deck;
import com.swp.DataModel.CardTypes.AudioCard;
import com.swp.DataModel.CardTypes.ImageDescriptionCard;
import com.swp.DataModel.CardTypes.ImageDescriptionCardAnswer;
import com.swp.DataModel.CardTypes.ImageTestCard;
import com.swp.DataModel.CardTypes.MultipleChoiceCard;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.DataModel.CardTypes.TrueFalseCard;
import com.swp.DataModel.Deck.CardOrder;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.DataModel.StudySystem.StudySystemType;
import com.swp.DataModel.StudySystem.*;
import jakarta.persistence.EntityManagerFactory;

public class DeckRepository
{
    private final static EntityManagerFactory emf = PersistenceManager.emFactory;

    public static boolean saveDeck(Deck deck)
    {
        //server.send("/createdeck", jsonString);
        return false;
    }

    /**
     *
     * @param deck
     */
    public static boolean updateDeck(Deck deck)
    {

        //server.send("/updatedeckdata", jsonString);
        return false;
    }

    public static boolean deleteDeck(Deck deck)
    {
        //CARDTODECK ebenso löschen, wenn hier
        return false;
    }

    public static List<Deck> getDecks()
    {

        List<Deck> decks = new ArrayList<>();
        //Cache.getInstance().getDecks().stream().toList(); THROWS ERROR
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

        /////////////////////////////////////////////////////////////////

        //server.send("/getdecks", jsonString);
        //return null;

//        try (final EntityManager em = emf.createEntityManager()) {
//            em.getTransaction().begin();
//            decks = em.createQuery("SELECT Deck FROM Deck ORDER BY name").getResultList();
//            em.getTransaction().commit();
//            //callback.onSuccess(decks);
//        } catch (final Exception e) {
//            // wie soll die Fehlermeldung zur GUI gelangen?
//            //callback.onFailure("Beim Abrufen aller Karten ist einer Fehler aufgetreten: " + e);
//        }
        Cache.getInstance().setDecks(new ArrayList<>(decks));
        return decks;
    }

    public static List<CardToDeck> getCardToDecks()
    {
        List<CardToDeck> card2decks = new ArrayList<>();//Cache.getInstance().getCardToDecks().stream().toList(); //THROWS ERROR
        if(!card2decks.isEmpty())
            return card2decks;

        /////////////////////////////////////////////////////////////////
        //
        // TEMPORARY
        //
        ImageDescriptionCardAnswer[] answers = new ImageDescriptionCardAnswer[] {
            new ImageDescriptionCardAnswer("Orangenblatt", 75, 5),
            new ImageDescriptionCardAnswer("Orange",       61, 26),
            new ImageDescriptionCardAnswer("Nase",         40, 67),
            new ImageDescriptionCardAnswer("Hand",         82, 58),
            new ImageDescriptionCardAnswer("Fuß",          62, 89),
        };
        ArrayList<Card> cards = new ArrayList<Card>();
        cards.add(new MultipleChoiceCard("Multiple Choice Complicated Question Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque mauris elit, luctus id mollis a, posuere sed ante. Mauris a aliquet est. Integer egestas massa ac erat finibus iaculis. Vestibulum vitae dignissim lacus. Cras augue ante, semper id est sit amet, accumsan porta lacus. Ut rhoncus dui justo", new String[] {"Answer 1", "Answer 2", "Answer 3"}, new int[] {1}, "MultipleChoiceCard", false));
        cards.add(new TrueFalseCard("True False Complicated Question Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque mauris elit, luctus id mollis a, posuere sed ante. Mauris a aliquet est. Integer egestas massa ac erat finibus iaculis. Vestibulum vitae dignissim lacus. Cras augue ante, semper id est sit amet, accumsan porta lacus. Ut rhoncus dui justo", true, "TrueFalseCard", false));
        cards.add(new TextCard("Text Complicated Question Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque mauris elit, luctus id mollis a, posuere sed ante. Mauris a aliquet est. Integer egestas massa ac erat finibus iaculis. Vestibulum vitae dignissim lacus. Cras augue ante, semper id est sit amet, accumsan porta lacus. Ut rhoncus dui justo", "text answer", "TextCard", false));
        cards.add(new ImageDescriptionCard("Image Desc Complicated Question Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque mauris elit, luctus id mollis a, posuere sed ante. Mauris a aliquet est. Integer egestas massa ac erat finibus iaculis. Vestibulum vitae dignissim lacus. Cras augue ante, semper id est sit amet, accumsan porta lacus. Ut rhoncus dui justo", answers, "ImageDescriptionCard", "textures/orange-ket.png", false));
        cards.add(new ImageTestCard("Image Test Complicated Question Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque mauris elit, luctus id mollis a, posuere sed ante. Mauris a aliquet est. Integer egestas massa ac erat finibus iaculis. Vestibulum vitae dignissim lacus. Cras augue ante, semper id est sit amet, accumsan porta lacus. Ut rhoncus dui justo", "image answer", "textures/orange-ket.png", "ImageTestCard", false, false));
        cards.add(new AudioCard("audios/thud.wav", "AudioCard", "Audio Complicated Question Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque mauris elit, luctus id mollis a, posuere sed ante. Mauris a aliquet est. Integer egestas massa ac erat finibus iaculis. Vestibulum vitae dignissim lacus. Cras augue ante, semper id est sit amet, accumsan porta lacus. Ut rhoncus dui justo", "audio answer", false, false));
        for(Deck deck : getDecks())
        {
            for(Card card : cards)
            {
                CardToDeck cardtodeck = new CardToDeck(card, deck);
                card2decks.add(cardtodeck);
            }
        }
        return card2decks;
        /////////////////////////////////////////////////////////////////

        //server.send("/getcardtodecks", jsonString);
        //return null;
//        try (final EntityManager em = emf.createEntityManager()) {
//            em.getTransaction().begin();
//            card2decks = em.createQuery("SELECT CardToDeck FROM CardToDeck").getResultList();
//            em.getTransaction().commit();
//        }
//        Cache.getInstance().setCardToDecks(new HashSet<>(card2decks));
//        return card2decks;
    }

    /* TODO
    List<Deck> getDecksBySearchterm(String searchterm)
    void removeCardsFromDeck(List<Card> cards, Deck deck)
    Deck getDeckByUUID(String uuid)
    Set<Card> getCardsInDeck(Deck deck)
    int numCardsInDeck(Deck deck)
    Set<StudySystemType> getStudySystemTypes()
     */

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

    public static List<Card> getCardsInDeck(Deck deck) {
        //TODO
        return null;
    }

    public static boolean createCardToDeck(Card card, Deck deck) {
        //TODO
        return false;
    }

    public static List<Deck> getDecksWithSearchterm(String searchterm) {
        //TODO
        return null;
    }

    public static void removeCardToDeck(Card c, Deck deck) {
        //TODO

    }
}