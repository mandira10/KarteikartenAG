package com.swp.Persistence;

import com.swp.DataModel.*;

import java.util.List;

public class CardToDeckRepository extends BaseRepository<CardToDeck> {
    private CardToDeckRepository() {
        super(CardToDeck.class);
    }

    // Singleton
    private static CardToDeckRepository cardToDeckRepository = null;
    public static CardToDeckRepository getInstance()
    {
        if(cardToDeckRepository == null)
            cardToDeckRepository = new CardToDeckRepository();
        return cardToDeckRepository;
    }

    /**
     * Die Funktion `createCardToDeck` erstellt eine neues `CardToDecl`, welches eine Karte mit einem Tag in
     * Verbindung setzt und persistiert dieses in der Datenbank.
     * @param card eine Karte, der ein Tag zugeordnet werden soll
     * @param deck ein Tag, der der Karte zugeordnet werden soll
     */
    public void createCardToDeck(Card card, Deck deck) {
        getEntityManager().persist(new CardToDeck(card, deck));
    }

    public int numCardsInDeck(Deck deck) {
        return getEntityManager()
                .createNamedQuery("CardToDeck.numCardsInDeck", int.class)
                .setParameter("deck", deck)
                .getSingleResult();
    }

    public void removeCardsFromDeck(List<Card> cards, Deck deck) {
        //Todo gucken ob es so etwas in der Art gibt "WHERE ANY :cards = card"
        //ansonsten erstmal sowas (sollte aber auch eher in die Logic)
        for (final Card card : cards) {
            removeCardToDeck(card, deck);
        }
    }

    public void removeCardToDeck(Card card, Deck deck) {
        // darf man sowas machen, oder dann eher im execTransactional von der Logic
        // oder lieber über eigene Query für so einen Fall?
        cardToDeckRepository.delete(cardToDeckRepository.getSpecific(card, deck));
    }

    public List<CardToDeck> getAllC2DForCard(Card card) {
        return getEntityManager()
                .createNamedQuery("CardToDeck.allC2DByCard", CardToDeck.class)
                .setParameter("card", card)
                .getResultList();
    }

    public List<CardToDeck> getAllC2DForDeck(Deck deck) {
        return getEntityManager()
                .createNamedQuery("CardToDeck.allC2DByDeck", CardToDeck.class)
                .setParameter("deck", deck)
                .getResultList();
    }

    public CardToDeck getSpecific(Card card, Deck deck) {
        return getEntityManager()
                .createNamedQuery("CardToDeck.findSpecificC2C", CardToDeck.class)
                .setParameter("card", card)
                .setParameter("deck", deck)
                .getSingleResult();
    }

}
