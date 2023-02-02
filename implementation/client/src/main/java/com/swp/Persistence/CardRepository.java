package com.swp.Persistence;

import com.swp.DataModel.Card;
import com.swp.DataModel.CardOverview;
import com.swp.DataModel.Category;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.DataModel.Tag;
import jakarta.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

@Slf4j
public class CardRepository extends BaseRepository<Card> {
    private CardRepository() {
        super(Card.class);
    }

    // Singleton
    private static CardRepository cardRepository = null;

    public static CardRepository getInstance() {
        if (cardRepository == null)
            cardRepository = new CardRepository();
        return cardRepository;
    }

    /**
     * Holt eine Liste von Karten in sortierter Reihenfolge aus der CardOverview.
     * Diese Overview ist eine View über mehrere Tabellen.
     * Sie enthält je Karte noch weitere Informationen, wie die Anzahl der
     * Lernsystem-Decks in der sie enthalten ist.
     *
     * @param from int: unterer Index für den angeforderten Bereich von Karten-Übersichten.
     * @param to   int: oberer Index für den angeforderten Bereich von Karten-Übersichten.
     * @return List<CardOverview> eine Liste von Karten-Übersichten.
     * @throws AssertionError falls der angegebene Bereich (from, to) ungültig ist.
     */
    public List<CardOverview> getCardOverview(int from, int to) throws AssertionError {
        assert from <= to : "Ungültiger Bereich: `from` muss kleiner/gleich `to` sein";
        return getEntityManager()
                .createQuery("SELECT c FROM CardOverview c", CardOverview.class)
                .setFirstResult(from).setMaxResults(to - from).getResultList();
    }

    public List<CardOverview> getCardOverview(int from, int to, String orderBy, String order) throws AssertionError {
        assert from <= to : "Ungültiger Bereich: `from` muss kleiner/gleich `to` sein";
        return getEntityManager()
                .createQuery("SELECT c FROM CardOverview c ORDER BY " + orderBy +" " + order, CardOverview.class)
                .setFirstResult(from).setMaxResults(to - from).getResultList();
    }

    /**
     * Holt aus der Datenbank eine Liste von Karten-Übersichten.
     * Es wird danach gefiltert, ob die Karte das angegebene Suchwort als
     * Teilstring im Inhalt hat.
     *
     * @param searchWords ein String nach dem im Inhalt aller Karten gesucht werden soll.
     * @return List<CardOverview> eine List von Karten-Übersichten, welche `searchWords` als Teilstring im Inhalt hat.
     */
    public List<CardOverview> findCardsContaining(String searchWords) {
        return getEntityManager()
                .createNamedQuery("CardOverview.findCardsByContent", CardOverview.class)
                .setParameter("content", "%" + searchWords + "%")
                .getResultList();
    }

    /**
     * Holt anhand der übergebenen UUID aus der Datenbank eine Karte.
     *
     * @param uuid eine Karten-UUID als String.
     * @return eine Card mit entsprechender UUID, oder `null` falls keine gefunden wurde.
     * @throws NoResultException falls keine Karte mit angegebener UUID in der Datenbank existiert.
     */
    public Card getCardByUUID(String uuid) throws NoResultException {
        return getEntityManager()
                .createNamedQuery("Card.findCardByUUID", Card.class)
                .setParameter("uuid", uuid)
                .getSingleResult();
    }

    /**
     * Der Funktion `getCardsByCategory` wird eine Kategorie übergeben.
     * Es werden alle Karten zurückgegeben, die dieser Kategorie zugeordnet sind.
     * Gibt es keine Karten in dieser Kategorie, so wird eine leere Liste zurückgegeben.
     *
     * @param category eine Kategorie
     * @return List<CardOverview> eine Menge von Karten-Übersichten, die der Kategorie zugeordnet sind.
     */
    public List<CardOverview> getCardsByCategory(Category category) {
        if (category == null)
            throw new NullPointerException();
        return getEntityManager()
                .createNamedQuery("CardToCategory.allCardsOfCategory", CardOverview.class)
                .setParameter("category", category)
                .getResultList();
    }

    /**
     * Der Funktion `getCardsByCategory` wird ein Kategorie-Name übergeben.
     * Es werden alle Karten zurückgegeben, die dieser Kategorie zugeordnet sind.
     * Gibt es keine Karten in dieser Kategorie, so wird eine leere Liste zurückgegeben.
     *
     * @param categoryName ein Kategorie-Name für die alle Karten gesucht werden sollen, die diesen haben.
     * @return List<CardOverview> eine Menge von Karten-Übersichten, welche der Kategorie zugeordnet sind.
     */
    public List<CardOverview> getCardsByCategory(String categoryName) {
        return getEntityManager()
                .createNamedQuery("CardOverview.allCardsWithCategoryName", CardOverview.class)
                .setParameter("categoryName", "%" + categoryName + "%")
                .getResultList();
    }

    /**
     * Holt eine List von Karten-Übersichten aus der Datenbank.
     * Es wird nach der Zugehörigkeit zu einer angegebenen Lernsystem-Instanz gefiltert.
     * Ist in dem Lernsystem keine Karte enthalten, wird eine leere Liste zurückgegeben.
     *
     * @param studySystem ein Lernsystem
     * @return List<CardOverview> eine List von Karten-Übersichten, aller Karten im angegeben Lernsystem.
     */
    public List<CardOverview> findCardsByStudySystem(StudySystem studySystem) {
        return getEntityManager()
                .createNamedQuery("CardOverview.allCardsWithStudySystem", CardOverview.class)
                .setParameter("studySystem", studySystem.getUuid())
                .getResultList();
    }

    /**
     * Holt eine List von Karten-Übersichten aus der Datenbank.
     * Es wird nach der Zugehörigkeit zu einer angegebenen Lernsystem-Instanz gefiltert.
     * Ist in dem Lernsystem keine Karte enthalten, wird eine leere Liste zurückgegeben.
     *
     * @param studySystem ein Lernsystem
     * @return List<CardOverview> eine List von Karten-Übersichten, aller Karten im angegeben Lernsystem.
     * @throws NoResultException falls keine Karte gefunden wurde
     */
    public Card findCardByStudySystem(StudySystem studySystem, Card card) throws NoResultException{
        return getEntityManager()
                .createNamedQuery("Card.ByStudySystem", Card.class)
                .setParameter("studySystem", studySystem.getUuid())
                .setParameter("card", card)
                .getSingleResult();
    }



    /**
     * Holt für eine Liste von Karten-Übersichten die entsprechenden Karten aus der Datenbank.
     *
     * @param overviews eine Liste von Karten-Übersichten.
     * @return List<Card> eine Liste von Karten, welche in der Übersicht-Liste referenziert wurden.
     */
    public List<Card> getAllCardsForCardOverview(List<CardOverview> overviews){
        return getEntityManager()
                .createNamedQuery("CardOverview.getCardsForUUIDs",Card.class)
                .setParameter("uuids",overviews)
                .getResultList();
    }


    /**
     * Holt aus der Datenbank eine Liste von Karten, welche einen angegebenen Tag zugeordnet haben.
     * Gibt es keine Karte mit dem angegebene Tag, so wird eine leere Liste zurückgegeben.
     *
     * @param tag ein Tag für den alle Karten gesucht werden sollen, die diesen haben.
     * @return List<CardOverview> eine Liste von Karten-Übersichten, welche in Verbindung zu dem Tag stehen.
     */
    public List<CardOverview> findCardsByTag(Tag tag) {
        return getEntityManager()
                .createNamedQuery("CardOverview.allCardsWithTag", CardOverview.class)
                .setParameter("tag", tag)
                .getResultList();
    }

    /**
     * Holt aus der Datenbank eine Liste von Karten, welche einen angegebenen Tag zugeordnet haben.
     * Gibt es keine Karte mit dem angegebene Tag, so wird eine leere Liste zurückgegeben.
     *
     * @param tagName Teilstring der einem bestehenden Tag matchen sollte.
     * @return List<CardOverview> eine Liste von Karten-Übersichten, welche in Verbindung zu dem Tag stehen.
     */
    public List<CardOverview> findCardsByTag(String tagName) {
        return getEntityManager()
                .createNamedQuery("CardOverview.allCardsWithTagName", CardOverview.class)
                .setParameter("tagName", "%" + tagName + "%")
                .getResultList();
    }

    public Long getNumberOfLearnedCardsByStudySystem(StudySystem studySystem) {
        return getEntityManager()
                .createNamedQuery("BoxToCard.numberOfLearnedCards", Long.class)
                .setParameter("studySystem", studySystem.getUuid())
                .getSingleResult();
    }

    /**
     * Gibt alle Karten beim Beginn vom Lernen nach der initialen angegebenen Kartenreihenfolge alphabetical/
     * reversed alphabetical wider.
     *
     * @param studySystem Das StudySystem, das gelernt werden soll
     * @param order Reihenfolge der Karten
     * @return Liste mit Karten fürs Lernen
     */
    public List<Card> getAllCardsInitiallyOrdered(StudySystem studySystem, String order) {
        return getEntityManager().
                createQuery("SELECT c.card FROM BoxToCard c LEFT JOIN StudySystemBox sbox ON sbox.id = c.studySystemBox LEFT JOIN StudySystem s ON s.uuid = sbox.studySystem WHERE s.uuid = :studySystem ORDER BY c.card.question " + order, Card.class)
                .setParameter("studySystem", studySystem.getUuid())
                .getResultList();
    }

            /**
             * Gibt alle Karten beim Beginn vom Lernen nach der initialen angegebenen Kartenreihenfolge ALPHABETICAL zurück.
             *
             * @param studySystem Das StudySystem, das gelernt werden soll
             * @return Liste mit Karten fürs Lernen
             */
            public List<Card> getAllCardsForStudySystem(StudySystem studySystem){
                return getEntityManager().
                        createNamedQuery("BoxToCard.allByStudySystem", Card.class)
                        .setParameter("studySystem", studySystem.getUuid())
                        .getResultList();
            }

    /**
     * Hier sollen alle Karten zurückgegeben werden, die in der untersten Box sind bzw. alle, die vom Lerndatum dran sind (CardToBox).
     * Die Sortierung ist dabei nach dem Datum, dann nach dem Kartenstatus. Neue Karten werden als erstes zurückgegeben.
     *
     * @param studySystem das zu durchsuchende Lernsystem.
     * @return List<BoxToCard> eine Liste von Karten, sortiert nach ihrem nächst-anstehenden Lernzeitpunkt.
     */
    public List<Card> getAllCardsNeededToBeLearned(StudySystem studySystem) {
        return getEntityManager()
                .createNamedQuery("BoxToCard.allCardNextLearnedAtOlderThanNowAscending", Card.class)
                .setParameter("studySystem", studySystem.getUuid())
                .getResultList();
    }

    /**
     * Holt eine Liste von Karten aus der Datenbank, welche sich in einem angegebenen Lernsystem befinden.
     * Es wird nach der benutzerdefinierten Bewertung für die Karten sortiert.
     * Gibt es keine Karten in dem Lernsystem, so wird eine leere Liste zurückgegeben.
     *
     * @param studySystem das zu durchsuchende Lernsystem.
     * @return List<Card> eine Liste von Karten in dem Lernsystem, sortiert nach ihrer Bewertung.
     */
    public List<Card> getAllCardsSortedForVoteSystem(StudySystem studySystem) {
             return getEntityManager()
                .createNamedQuery("BoxToCard.allCardsSortedByRating", Card.class)
                .setParameter("studySystem", studySystem.getUuid())
                .getResultList();
    }

    /**
     * Holt alle Karten für ein angegebenes Lernsystem aus der Datenbank.
     * Gibt es keine Karten in dem Lernsystem, so wird eine leere Liste zurückgegeben.
     *
     * @param studySystem das zu durchsuchende Lernsystem.
     * @return List<Card> eine Liste der Karten in dem Lernsystem.
     */
    public List<Card> getAllCardsForTimingSystem(StudySystem studySystem) {
        return getEntityManager()
                .createNamedQuery("BoxToCard.allCardsForTiming", Card.class)
                .setParameter("studySystem", studySystem.getUuid())
                .getResultList();
    }

    /**
     * Methode, um die gelernten Karten in einem Vote/Timing System zu kriegen.
     * Wird in finishTestAndGetResult aufgerufen.
     * @param studySystem Das StudySystem für die die Karten abgerufen werden sollen
     * @return Liste mit Karten in der spezifischen Box
     */
    public List<Card>  getAllCardsInLearnedBox(StudySystem studySystem) {
        return getEntityManager()
                .createNamedQuery("StudySystemBox.allByStudySystemBox", Card.class)
                .setParameter("studySystem", studySystem.getUuid())
                .getResultList();
    }
}



