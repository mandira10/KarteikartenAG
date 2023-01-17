package com.swp.Persistence;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardToDeck;
import com.swp.DataModel.Deck;
import com.swp.DataModel.CardTypes.ImageDescriptionCard;
import com.swp.DataModel.CardTypes.ImageDescriptionCardAnswer;
import com.swp.DataModel.CardTypes.ImageTestCard;
import com.swp.DataModel.CardTypes.MultipleChoiceCard;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.DataModel.CardTypes.TrueFalseCard;
import com.swp.DataModel.Deck.CardOrder;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.DataModel.StudySystem.*;
import jakarta.persistence.EntityManagerFactory;

public class DeckRepository extends BaseRepository<Deck>
{
    private DeckRepository() {
        super(Deck.class);
    }

    // Singleton
    private static DeckRepository deckRepository = null;
    public static DeckRepository getInstance()
    {
        if(deckRepository == null)
            deckRepository = new DeckRepository();
        return deckRepository;
    }


    public List<Deck> getDecks()
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
        Cache.getInstance().setDecks(new ArrayList<>(decks));
        return decks;
    }

    public List<CardToDeck> getCardToDecks()
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
        //cards.add(new AudioCard("audios/thud.wav", "AudioCard", "Audio Complicated Question Lorem ipsum dolor sit amet, consectetur adipiscing elit. Pellentesque mauris elit, luctus id mollis a, posuere sed ante. Mauris a aliquet est. Integer egestas massa ac erat finibus iaculis. Vestibulum vitae dignissim lacus. Cras augue ante, semper id est sit amet, accumsan porta lacus. Ut rhoncus dui justo", "audio answer", false, false));
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
    public void updateStudySystem(Deck deck, StudySystem system)
    {
        //TODO needed?
    }


    /**
     * Die Funktion `findDecksContaining` durchsucht die Namen aller Decks.
     * Es werden alle Decks zurückgegeben, die den übergebenen Suchtext als Teilstring im Namen enthalten.
     * @param searchWords ein String nach dem in den Namen gesucht werden soll.
     * @return Set<Deck> eine Menge von Deck, welche `searchWords` als Teilstring im Inhalt hat.
     */
    public List<Deck> findDecksContaining(String searchWords) {
        return getEntityManager()
                .createNamedQuery("Deck.findDecksByContent", Deck.class)
                .setParameter("name", "%" + searchWords + "%")
                .getResultList();
    }




    public Deck getDeckByUUID(String uuid) {
        //TODO
        return null;
    }

    public boolean updateDeckCards(Deck deck){
        //TODO
        return false;
    }

    public List<Deck> findDecksByCard(Card card) {
        return new ArrayList<>();
    }
}