package com.swp.Persistence;

import com.swp.DataModel.Card;
import com.swp.DataModel.StudySystem.BoxToCard;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.DataModel.StudySystem.StudySystemBox;
import jakarta.persistence.NoResultException;

import java.util.List;


public class CardToBoxRepository extends BaseRepository<BoxToCard> {

    private CardToBoxRepository() {
        super(BoxToCard.class);
    }

    private static CardToBoxRepository cardToBoxRepository = null;

    public static CardToBoxRepository getInstance() {
        if (cardToBoxRepository == null)
            cardToBoxRepository = new CardToBoxRepository();
        return cardToBoxRepository;
    }

    /**
     * Gibt alle Karten einer Box für das aktuelle Testen in der angegebenen Reihenfolge wider.
     *
     * @param studySystemBox //TOTEST Efe
     * @param cardOrder
     * @return
     */
    public List<BoxToCard> getAllForBox(StudySystemBox studySystemBox, StudySystem.CardOrder cardOrder) {
        return getEntityManager()
                .createNamedQuery("BoxToCard.allByBox", BoxToCard.class)
                .setParameter("box", studySystemBox)
                .setParameter("cardOrder", cardOrder) // cardOrder wird in der Query noch nicht berücksichtigt.
                .getResultList();
    }

    /**
     * Holt eine Liste von Karten-zu-LernsystemBox Verbindungen aus der Datenbank.
     * Dabei werden nur Verbindungen zu dem angegebenen Lernsystem berücksichtigt.
     * Gibt es keine Karten in dem Lernsystem, so wird eine leere Liste zurückgegeben.
     *
     * @param studySystem das Lernsystem, für die die Karten-Verbindungen gesucht werden.
     * @return List<BoxToCard> eine Liste von Karten-zu-LernsystemBox Verbindungen.
     */
    public List<BoxToCard> getAllBoxToCardsForStudySystem(StudySystem studySystem) {
        return getEntityManager()
                .createNamedQuery("BoxToCard.allB2CByStudySystem", BoxToCard.class)
                .setParameter("studySystem", studySystem)
                .getResultList();
    }


    /**
     * Holt für eine Karte alle Karten-zu-LernsystemBox Verbindungen aus der Datenbank.
     * Ist die Karte in keinem Lernsystem enthalten, so wird eine leere Liste zurückgegeben.
     *
     * @param card die Karte für die die Verbindungen zu Lernsystem-Kästen geholt werden sollen.
     * @return List<BoxToCard> eine Liste von Karten-zu-LernsystemBox Verbindungen.
     */
    public List<BoxToCard> getAllB2CForCard(Card card) {
        return getEntityManager()
                .createNamedQuery("BoxToCard.allb2cByCard", BoxToCard.class)
                .setParameter("card", card)
                .getResultList();
    }

    /**
     * Holt für ein angegebenes Lernsystem alle Karten-zu-LernsystemBox Verbindungen aus der Datenbank.
     * Ist dem Lernsystem keine Karte zugeordnet, so wird eine leere Liste zurückgegeben.
     *
     * @param studySystem das Lernsystem, für das die Karten-zu-LernsystemBox Verbindungen zurückgegeben werden sollen.
     * @return List<BoxToCard> eine Liste von Karten-zu-LernsystemBox Verbindungen
     */
    public List<BoxToCard> getAllB2CForStudySystem(StudySystem studySystem) {
        return getEntityManager()
                .createNamedQuery("BoxToCard.allByStudySystem", BoxToCard.class)
                .setParameter("studySystem", studySystem.getUuid())
                .getResultList();
    }

    /**
     * Holt für eine angegebene Karte und ein angegebenes Lernsystem, die Karte-zu-LernsystemBox Verbindung
     * aus der Datenbank. Sollte die Karte nicht dem Lernsystem zugeordnet sein, so wird eine Exception geworfen.
     *
     * @param card        eine Karte.
     * @param studySystem ein Lernsystem.
     * @return eine Karte-zu-LernsystemBox Verbindung
     * @throws NoResultException falls die Karte nicht dem Lernsystem zugeordnet ist.
     */
    public BoxToCard getSpecific(Card card, StudySystem studySystem) throws NoResultException {
        return getEntityManager()
                .createNamedQuery("BoxToCard.findSpecificC2C", BoxToCard.class)
                .setParameter("card", card)
                .setParameter("studySystem", studySystem.getUuid())
                .getSingleResult();
    }
}
