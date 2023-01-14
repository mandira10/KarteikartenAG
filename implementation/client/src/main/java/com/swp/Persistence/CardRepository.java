package com.swp.Persistence;
import com.swp.DataModel.*;
import jakarta.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;
import java.util.List;

@Slf4j
public class CardRepository extends BaseRepository<Card>
{
    private CardRepository() {
        super(Card.class);
    }

    // Singleton
    private static CardRepository cardRepository = null;
    public static CardRepository getInstance() {
        if(cardRepository == null)
            cardRepository = new CardRepository();
        return cardRepository;
    }

    /**
     * Funktion um einen bestimmten Abschnitt der persistierten Objekte aus der Datenbank zu holen
     *
     * @param from     int: gibt an wie viele Zeilen des Ergebnisses übersprungen werden.
     * @param to       int: gibt an bis zu welcher Zeile die Ergebnisse geholt werden sollen.
     */
    public List<Card> getCardRange(final int from, final int to) {
        assert from <= to : "Ungültiger Bereich: `from` muss kleiner/gleich `to` sein";
        return getEntityManager().createQuery("SELECT c FROM Card c ORDER BY title", Card.class)
                .setFirstResult(from).setMaxResults(to-from).getResultList();
    }

    /**
     *
     * @return List<CardOverview> eine Übersicht
     */
    public static List<CardOverview> getCardOverview() {
        return getEntityManager()
                .createQuery("SELECT c FROM CardOverview c", CardOverview.class)
                .getResultList();
    }

    /**
     * Die Funktion `findCardsByCategory` such nach allen Karten die einer bestimmten Kategorie zugeordnet sind.
     *
     * @param category eine Category
     * @return List<Card> eine Liste von Karten, die der Kategorie zugeordnet sind.
     */
    public static List<Card> findCardsByCategory(Category category) {
        return getEntityManager()
                .createNamedQuery("CardToCategory.allCardsOfCategory", Card.class)
                .setParameter("category", category)
                .getResultList();
    }


    /**
     * Die Funktion `findCardsByTag` sucht nach Karten, der ein bestimmter Tag zugeordnet ist und gibt diese zurück.
     * @param tag ein Tag für den alle Karten gesucht werden sollen, die diesen haben.
     * @return Set<Card> eine Menge von Karten, welche in Verbindung zu dem Tag stehen.
     */
    public static List<Card> findCardsByTag(Tag tag) {
        return getEntityManager()
                .createNamedQuery("CardToTag.allCardsWithTag", Card.class)
                .setParameter("tag", tag).getResultList();
    }

    /**
     * Die Funktion `findCardsContaining` durchsucht den Inhalt aller Karten.
     * Es werden alle Karten zurückgegeben, die den übergebenen Suchtext als Teilstring enthalten.
     * @param searchWords ein String nach dem im Inhalt aller Karten gesucht werden soll.
     * @return Set<Card> eine Menge von Karten, welche `searchWords` als Teilstring im Inhalt hat.
     */
    public static List<Card> findCardsContaining(String searchWords) {
        return getEntityManager()
                .createNamedQuery("Card.findCardsByContent", Card.class)
                .setParameter("content", "%" + searchWords + "%")
                .getResultList();
    }

    /**
     * Die Funktion `getCardByUUID` sucht anhand einer UUID nach einer Karte und gibt diese zurück.
     * Existiert keine Karte mit angegebener UUID, dann
     * @param uuid eine UUID als String
     * @return eine Card mit entsprechender UUID, oder `null` falls keine gefunden wurde.
     */
    public static Card getCardByUUID(String uuid) {
        return getEntityManager()
                .createNamedQuery("Card.findCardByUUID", Card.class)
                .setParameter("uuid", uuid)
                .getSingleResult();
    }

    /**
     * Die Funktion `deleteCard` löscht eine Karte und alle Verbindungen, in der sie existiert hat.
     * Dazu zählen die Kategorien, Tags und Decks mit der die Karte eine Verbindung hatte.
     * @param card die Card die gelöscht werden soll
     * @return boolean, wenn erfolgreich ein `true`, im Fehlerfall `false`.
     */
    public static boolean deleteCard(Card card) {
        getInstance().delete(card); // kann von der Logic auch direkt verwendet werden
        // wäre äquivalent zu folgendem
        // getEntityManager().remove(card);

        return true; // boolean Rückgabewert macht eigentlich keinen Sinn mehr,
                     // weil bei Fehlern Exceptions geworfen werden.
    }


    //
    // Tags
    //
    /**
     * Die Funktion `getTags` liefer alle gespeicherten Tags zurück.
     * @return Set<Tag> eine Menge mit allen Tags
     */
    public static List<Tag> getTags() {
        return getEntityManager()
                .createQuery("SELECT t FROM Tag t", Tag.class)
                .getResultList();
    }

    /**
     * Die Funktion `getCardToTags` liefer alle `CardToTag`-Objekte zurück.
     * Sie stellen die Verbindungen zwischen Karten und Tags dar.
     * @return List<CardToTag> eine Menge mit allen `CardToTag`-Objekten.
     */
    public static List<CardToTag> getCardToTags() {
        return getEntityManager()
                .createQuery("SELECT CardToTag FROM CardToTag", CardToTag.class)
                .getResultList();
    }

    /**
     * Die Funktion `createCardToTag` erstellt eine neues `CardToTag`, welches eine Karte mit einem Tag in
     * Verbindung setzt und persistiert dieses in der Datenbank.
     * @param card eine Karte, der ein Tag zugeordnet werden soll
     * @param tag ein Tag, der der Karte zugeordnet werden soll
     * @return boolean, wenn erfolgreich ein `true`, im Fehlerfall wird eine Exception geworfen.
     */
    public static boolean createCardToTag(Card card, Tag tag) {
        getEntityManager().persist(new CardToTag(card, tag));
        return true;
    }

    /**
     * Die Funktion `getTagsToCard` liefer alle Tags zurück, die einer Karte zugeordnet sind.
     * @param card eine Karte
     * @return List<Tag> eine Liste von Tags, die der Karte zugeordnet sind.
     */
    public static List<Tag> getTagsToCard(Card card) {
        return  getEntityManager()
                .createNamedQuery("CardToTag.allTagsWithCards", Tag.class)
                .setParameter("card", card)
                .getResultList();
    }

}
