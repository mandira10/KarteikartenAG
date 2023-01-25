package com.swp.Persistence;

import com.swp.DataModel.Card;
import com.swp.DataModel.StudySystem.BoxToCard;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.DataModel.StudySystem.StudySystemBox;

import java.util.List;


public class CardToBoxRepository extends BaseRepository<BoxToCard> {

    private CardToBoxRepository() {
        super(BoxToCard.class);
    }

    private static CardToBoxRepository cardToBoxRepository = null;
    public static CardToBoxRepository getInstance()
    {
        if(cardToBoxRepository == null)
            cardToBoxRepository = new CardToBoxRepository();
        return cardToBoxRepository;
    }

    /**
     * Gibt alle Karten einer Box für das aktuelle Testen in der angegebenen Reihenfolge wider.
     * @param studySystemBox //TOTEST Efe
     * @param cardOrder
     * @return
     */
    public  List<BoxToCard> getAllForBox(StudySystemBox studySystemBox, StudySystem.CardOrder cardOrder) {
        return getEntityManager()
                .createNamedQuery("BoxToCard.allByBox", BoxToCard.class)
                .setParameter("box", studySystemBox)
                .setParameter("cardOrder", cardOrder)
                .getResultList();
    }


    public List<BoxToCard> getAllBoxToCardsForStudySystem(StudySystem studySystem) {
        return getEntityManager()
                .createNamedQuery("BoxToCard.allByStudySystem", BoxToCard.class)
                .setParameter("studySystem", studySystem)
                .getResultList();
    }

    /**
     * Die Funktion `createCardToDeck` erstellt eine neues `CardToDecl`, welches eine Karte mit einem Tag in
     * Verbindung setzt und persistiert dieses in der Datenbank.
     * @param card eine Karte, der ein Tag zugeordnet werden soll
     * @param studySystemBox ein Tag, der der Karte zugeordnet werden soll
     */
    public void createCardToBox(Card card, StudySystemBox studySystemBox, int box) {
        getEntityManager().persist(new BoxToCard(card, studySystemBox, box));
    }


    public int numCardsInStudySystem(StudySystem studySystem) {
        return getEntityManager()
                .createNamedQuery("BoxToCard.numCardsInStudySystem", int.class)
                .setParameter("studySystem", studySystem.getUuid())
                .getSingleResult();
    }



    public List<BoxToCard> getAllB2CForCard(Card card) {
        return getEntityManager()
                .createNamedQuery("BoxToCard.allb2cByCard", BoxToCard.class)
                .setParameter("card", card)
                .getResultList();
    }

    public List<BoxToCard> getAllB2CForStudySystem(StudySystem studySystem) {
        return getEntityManager()
                .createNamedQuery("BoxToCard.allB2CByStudySystem", BoxToCard.class)
                .setParameter("studySystem", studySystem.getUuid())
                .getResultList();
    }

    public BoxToCard getSpecific(Card card, StudySystem studySystem) {
        return getEntityManager()
                .createNamedQuery("BoxToCard.findSpecificC2C", BoxToCard.class)
                .setParameter("card", card)
                .setParameter("studySystem", studySystem.getUuid())
                .getSingleResult();
    }

}
