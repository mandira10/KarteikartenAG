package com.swp.Persistence;

import com.swp.DataModel.Card;
import com.swp.DataModel.CardToStudySystem;
import com.swp.DataModel.StudySystem.StudySystem;
import java.util.List;

public class CardToStudySystemRepository extends BaseRepository<CardToStudySystem>
{
    private CardToStudySystemRepository() {
        super(CardToStudySystem.class);
    }

    // Singleton
    public static CardToStudySystemRepository cardToStudySystemRepository;
    public static CardToStudySystemRepository getInstance() {
        if (cardToStudySystemRepository == null)
            cardToStudySystemRepository = new CardToStudySystemRepository();
        return cardToStudySystemRepository;
    }

    // Queries
    public CardToStudySystem findSpecific(final Card card, final StudySystem studySystem) {
        return getEntityManager()
                .createNamedQuery("CardToStudySystem.findSpecificC2S", CardToStudySystem.class)
                .setParameter("card", card)
                .setParameter("studySystem", studySystem)
                .getSingleResult();
    }

    public CardToStudySystem findSpecific(final int id) {
        return getEntityManager()
                .createNamedQuery("CardToStudySystem.getBoxNrForC2SByID", CardToStudySystem.class)
                .setParameter("id", id)
                .getSingleResult();
    }

    public List<CardToStudySystem> getAll(final StudySystem studySystem) {
        return getEntityManager()
                .createNamedQuery("CardToStudySystem.getAllC2SForStudySystem",CardToStudySystem.class)
                .setParameter("studySystem", studySystem)
                .getResultList();
    }

    public void moveCardAhead(final CardToStudySystem cardToStudySystem) {
        getEntityManager()
                .merge(cardToStudySystem.incrementBoxNr());
    }

    public void moveCardBack(final CardToStudySystem cardToStudySystem) {
        getEntityManager()
                .merge(cardToStudySystem.decrementBoxNr());
    }

    public void moveCardToBox(final CardToStudySystem cardToStudySystem, final int boxNr) {
        getEntityManager()
                .merge(cardToStudySystem.moveToBoxNr(boxNr));
    }

    public void moveCardToBox(final CardToStudySystem cardToStudySystem) {
        // if not specified, move card to box 0
        moveCardToBox(cardToStudySystem, 0);
    }
}
