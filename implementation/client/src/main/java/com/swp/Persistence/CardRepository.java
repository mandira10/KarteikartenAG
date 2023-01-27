package com.swp.Persistence;

import com.swp.DataModel.*;
import com.swp.DataModel.StudySystem.BoxToCard;
import com.swp.DataModel.StudySystem.StudySystem;
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

    /**
     * Holt aus der Datenbank eine Liste von Karten-Übersichten.
     * Es wird danach gefiltert, ob die Karte das angegebene Suchwort als
     * Teilstring im Inhalt hat.
     *
     * @param searchWords ein String nach dem im Inhalt aller Karten gesucht werden soll.
     * @return Set<CardOverview> eine Menge von Karten, welche `searchWords` als Teilstring im Inhalt hat.
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
     * @return Set<CardOverview> eine Menge von Karten-Übersichten, die der Kategorie zugeordnet sind.
     */
    public List<CardOverview> getCardsByCategory(Category category) {
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
     * @return Set<CardOverview> eine Menge von Karten-Übersichten, welche der Kategorie zugeordnet sind.
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
     * TOTEST EFE Hier sollen alle Karten zurückgegeben werden, die in der untersten Box sind bzw. alle, die vom Lerndatum dran sind (CardToBox) learnedAt in SORTIERTER FORM!
     * Schau mal ob getDate() in H2 funktioniert, ansonsten lass dir das aktuelle Datum über System.currentTimeMillis ausgeben.
     * Du brauchst auch einen join damit du vom StudySystem auf die zugehörige Boxen und dann die Karten kommst.
     *
     * @param studySystem das zu durchsuchende Lernsystem.
     * @return List<BoxToCard> eine Liste von Karten (?), sortiert nach ihrem nächst-anstehenden Lernzeitpunkt.
     */
    public List<Card> getAllCardsNeededToBeLearned(StudySystem studySystem) {
        return getEntityManager()
                .createNamedQuery("BoxToCard.allCardNextLearnedAtOlderThanNowAscending", Card.class)
                .setParameter("now", System.currentTimeMillis()) //TODO in der Query muss wahrscheinlich noch auf das StudySystem gefiltert werden
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
        //TOTEST gib mir alle Karten sortiert nach Rating fürs nächste Lernen, //TODO: wir haben zwei Ratingmöglichkeiten für Karten, das ist grad die falsche die verwendet wird
        return getEntityManager()
                .createNamedQuery("Card.allCardsSortedByRating", Card.class)
                .setParameter("studySystem", studySystem)
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
        //TOTEST gib mir alle Karten in diesem StudySystem for TimingSystem
        return getEntityManager()
                .createNamedQuery("BoxToCard.allCardsOfEveryBoxesOfTheStudySystem", Card.class)
                .setParameter("studySystem", studySystem)
                .getResultList();
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
     * @return Set<Card> eine Menge von Karten, welche in Verbindung zu dem Tag stehen.
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
     * @return Set<CardOverview> eine Menge von Karten, welche in Verbindung zu dem Tag stehen.
     */
    public List<CardOverview> findCardsByTag(String tagName) {
        return getEntityManager()
                .createNamedQuery("CardOverview.allCardsWithTagName", CardOverview.class)
                .setParameter("tagName", "%" + tagName + "%")
                .getResultList();
    }
}



