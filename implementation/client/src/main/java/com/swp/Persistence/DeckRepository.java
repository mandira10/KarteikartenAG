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
        return getEntityManager().createQuery("SELECT d FROM Deck d LEFT JOIN StudySystem s ON s.deck = d.uuid " +
                "ORDER BY d.name",Deck.class).getResultList();
    }

    /* TODO
    List<Deck> getDecksBySearchterm(String searchterm)
    void removeCardsFromDeck(List<Card> cards, Deck deck)
    Deck getDeckByUUID(String uuid)
    Set<Card> getCardsInDeck(Deck deck)
    int numCardsInDeck(Deck deck)
    Set<StudySystemType> getStudySystemTypes()
     */

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
            return getEntityManager()
                    .createNamedQuery("CardToDeck.allDecksWithCard", Deck.class)
                    .setParameter("card", card).getResultList();
        }

    }
