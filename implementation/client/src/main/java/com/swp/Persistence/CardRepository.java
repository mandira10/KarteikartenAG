package com.swp.Persistence;
import com.swp.DataModel.*;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.DataModel.StudySystem.StudySystemBox;
import jakarta.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
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
     * Funktion um einen bestimmten Abschnitt der persistierten Objekte aus der Datenbank zu holen
     *
     * @param from int: gibt an wie viele Zeilen des Ergebnisses übersprungen werden.
     * @param to   int: gibt an bis zu welcher Zeile die Ergebnisse geholt werden sollen.
     */
    public List<Card> getCardRange(final int from, final int to) {
        assert from <= to : "Ungültiger Bereich: `from` muss kleiner/gleich `to` sein";
        return getEntityManager().createQuery("SELECT c FROM Card c ORDER BY c.title", Card.class)
                .setFirstResult(from).setMaxResults(to - from).getResultList();
    }

    /**
     * @return List<CardOverview> eine Übersicht
     */
    public List<CardOverview> getCardOverview(int from, int to) {
        assert from <= to : "Ungültiger Bereich: `from` muss kleiner/gleich `to` sein";
        return getEntityManager()
                .createQuery("SELECT c FROM CardOverview c", CardOverview.class)
                .setFirstResult(from).setMaxResults(to - from).getResultList();
    }

    /**
     * Die Funktion `findCardsByCategory` such nach allen Karten die einer bestimmten Kategorie zugeordnet sind.
     *
     * @param category eine Category
     * @return List<Card> eine Liste von Karten, die der Kategorie zugeordnet sind.
     */
    public List<Card> findCardsByCategory(Category category) {
        return getEntityManager()
                .createNamedQuery("CardToCategory.allCardsOfCategory", Card.class)
                .setParameter("category", category)
                .getResultList();
    }


    /**
     * Die Funktion `findCardsByTag` sucht nach Karten, der ein bestimmter Tag zugeordnet ist und gibt diese zurück.
     *
     * @param tag ein Tag für den alle Karten gesucht werden sollen, die diesen haben.
     * @return Set<Card> eine Menge von Karten, welche in Verbindung zu dem Tag stehen.
     */
    public List<CardOverview> findCardsByTag(Tag tag) {
        return getEntityManager()
                .createNamedQuery("CardOverview.allCardsWithTag", CardOverview.class)
                .setParameter("tag", tag).getResultList();
    }


    /**
     * Die Funktion `findCardsContaining` durchsucht den Inhalt aller Karten.
     * Es werden alle Karten zurückgegeben, die den übergebenen Suchtext als Teilstring enthalten.
     *
     * @param searchWords ein String nach dem im Inhalt aller Karten gesucht werden soll.
     * @return Set<Card> eine Menge von Karten, welche `searchWords` als Teilstring im Inhalt hat.
     */
    public List<CardOverview> findCardsContaining(String searchWords) {
        return getEntityManager()
                .createNamedQuery("CardOverview.findCardsByContent", CardOverview.class)
                .setParameter("content", "%" + searchWords + "%")
                .getResultList();
    }

    /**
     * Die Funktion `getCardByUUID` sucht anhand einer UUID nach einer Karte und gibt diese zurück.
     * Existiert keine Karte mit angegebener UUID, dann
     *
     * @param uuid eine UUID als String
     * @return eine Card mit entsprechender UUID, oder `null` falls keine gefunden wurde.
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
     *
     * @param category eine Kategorie
     * @return Set<Card> eine Menge von Karten, die der Kategorie zugeordnet sind.
     * @throws NoResultException falls keine Karte mit dieser Kategorie existiert
     */
    public List<CardOverview> getCardsByCategory(Category category) throws NoResultException {
        return getEntityManager()
                .createNamedQuery("CardToCategory.allCardsOfCategory", CardOverview.class)
                .setParameter("category", category)
                .getResultList();
    }



    public List<CardOverview> findCardsByStudySystem(StudySystem oldStudyS) {
        return getEntityManager()
                .createNamedQuery("CardOverview.allCardsWithStudySystem", CardOverview.class)
                .setParameter("studySystem", oldStudyS.getUuid())
                .getResultList();
    }

    /**
     * TODO EFE Hier sollen alle Karten zurückgegeben werden, die in der untersten Box sind bzw. alle, die vom Lerndatum dran sind (CardToBox) learnedAt in SORTIERTER FORM!
     * Schau mal ob getDate() in H2 funktioniert, ansonsten lass dir das aktuelle Datum über System.currentTimeMillis ausgeben.
     * Du brauchst auch einen join damit du vom StudySystem auf die zugehörige Boxen und dann die Karten kommst.
     * @param studySystem
     * @param cardOrder
     * @return
     */
    public List<Card> getAllCardsNeededToBeLearned(StudySystem studySystem, StudySystem.CardOrder cardOrder) {
        return new ArrayList<>();
    }

    public List<Card> getAllCardsSortedForVoteSystem(StudySystem studySystem) {
        //TODO gib mir alle Karten sortiert nach Ranking fürs nächste Lernen,
        return new ArrayList<>();
    }

    public List<Card> getAllCardsForTimingSystem(StudySystem studySystem){
        //TODO gib mir alle Karten in diesem StudySystem for TimingSystem
        return new ArrayList<>();
    }
}



