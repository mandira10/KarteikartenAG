package com.swp.Logic;

import com.swp.DataModel.*;
import com.swp.Persistence.*;
import com.swp.Persistence.Exporter.ExportFileType;
import jakarta.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;
import java.util.*;

import static com.swp.Validator.checkNotNullOrBlank;

@Slf4j
public class CardLogic extends BaseLogic<Card>
{
    /**
     * Konstruktor für eine CardLogic-Instanz.
     */
    public CardLogic() {
        super(CardRepository.getInstance());
    }

    private final CardRepository cardRepository = CardRepository.getInstance();
    private final TagRepository tagRepository = TagRepository.getInstance();
    private final CardToTagRepository cardToTagRepository = CardToTagRepository.getInstance();
    private final CardToCategoryRepository cardToCategoryRepository = CardToCategoryRepository.getInstance();

    private final CardToBoxRepository cardToBoxRepository = CardToBoxRepository.getInstance();
    private static CardLogic cardLogic;
    public static CardLogic getInstance() {
        if (cardLogic == null)
            cardLogic = new CardLogic();
        return cardLogic;
    }

    /**
     * Wird in der CardOverviewPage verwendet, um die einzelnen Karten für die Seitenauswahl mitsamt Titel (wenn nicht vorhanden dann die Frage), Typ,
     * Anzahl der Decks und ihrem Erstellzeitpunkt anzuzeigen. Gibt die Methode an das CardRepository weiter.
     * @param begin: Seitenauswahl Anfangswert
     * @param end: Seitenauswahl Endwert
     * @return anzuzeigende Karten
     */
    public List<CardOverview> getCardOverview(int begin, int end) {
        return execTransactional(() -> cardRepository.getCardOverview(begin, end));
    }

    /**
     * Wird verwendet bei einer Filterung nach einem bestimmten Tag. Prüft zunächst, dass der übergebene Tag nicht
     * null oder leer ist und gibt die Funktion dann an das Card Repository weiter.
     * @param tagName: Der textuelle Tag, zu dem die Karten gesucht werden sollen
     * @return Set der Karten, die den Tag enthalten
     */
    public List<CardOverview> getCardsByTag(String tagName) {
        checkNotNullOrBlank(tagName, "Tag",true);
        return execTransactional(() -> cardRepository.findCardsByTag(tagName));
    }

    /**
     * Methode wird verwendet, um passende Karten für die angegebenen Suchwörter zu identifizieren. Prüft zunächst,
     * dass der übergebene Tag nicht null oder leer ist und gibt die Funktion dann an das Card Repository weiter.
     *
     * @param terms Suchwörter, die durch ein Leerzeichen voneinander getrennt sind
     * @return Set der Karten, die Suchwörter enthalten.
     */
    public List<CardOverview> getCardsBySearchterms(String terms)
    {
        checkNotNullOrBlank(terms, "Suchbegriff",true);
        return execTransactional(() -> cardRepository.findCardsContaining(terms));
    }

    /**
     * Wird aufgerufen, wenn eine spezifische Karte gelöscht werden soll. Gibt die Karte weiter
     * an das CardRepository.
     * @param card zu löschende Karte
     */
    public void deleteCard(Card card)
    {
        if(card == null){
            throw new IllegalStateException("Karte existiert nicht");
        }
        execTransactional(() -> {
            log.info("Lösche alle Card To Categories zur Karte");
            cardToCategoryRepository.delete(cardToCategoryRepository.getAllC2CForCard(card));
            log.info("Lösche alle Card To Boxes zur Karte");
            cardToBoxRepository.delete(cardToBoxRepository.getAllB2CForCard(card));
            log.info("Lösche alle Card To Tags zur Karte");
            cardToTagRepository.delete(cardToTagRepository.getAllC2TForCard(card));
            log.info("Lösche die Karte");
            cardRepository.delete(card);
            return null; // Lambda braucht immer einen return
        });
    }

    /**
     * Wird aufgerufen, wenn mehrere Karten gelöscht werden sollen. Gibt die Karten einzeln weiter
     * an das CardRepository.
     * @param cards zu löschende Karten
     */
    public void deleteCards(List<CardOverview> cards)
    {
        //TODO
        //for(CardOverview c : cards)
            //deleteCard(c);
    }


    /**
     * Wird verwendet, um einzelne Karteninformationen über ihre UUID abzurufen. Wird an das CardRepository weitergegeben.
     * @param uuid: UUID der abzurufenden Karte
     * @return Zugehörige Karte
     */
    public Card getCardByUUID(String uuid)
    {
        checkNotNullOrBlank(uuid, "UUID",true);
        return execTransactional(() -> cardRepository.getCardByUUID(uuid));
    }

    /**
     * Lädt alle Tags als Set. Werden in der CardEditPage als Dropdown angezeigt. Wird weitergegeben an das CardRepository.
     * @return Set mit bestehenden Tags.
     */
    public List<Tag> getTags()
    {
        return execTransactional(() -> tagRepository.getAll());
    }


    /**
     * Wird verwendet, um Tags einer Karte zu bekommen. Wird an das TagRepository weitergegeben.
     * @param card: Karte, um Ihre Tags zu bekommen.
     * @return eine Liste von Tag
     */
    public List<Tag> getTagsToCard(Card card) {
        return execTransactional(() -> tagRepository.getTagsToCard(card));
    }

    /**
     * Wird aufgerufen, wenn eine neue Karte erstellt wird oder eine bestehende angepasst.
     * @param cardToChange Die Karte, die neu erstellt werden soll oder die Kopie der Karte mit den Änderungen
     * @param neu gibt an, ob es sich um eine komplett neue Karte handelt
     */
    public void updateCardData(Card cardToChange, boolean neu) {
        if(cardToChange == null){
            throw new IllegalStateException("Karte existiert nicht");
        }
        cardToChange.setContent();
        execTransactional(() -> {
            if (neu)
                cardRepository.save(cardToChange);
            else
                cardRepository.update(cardToChange);
            return true;
        });
    }


    /**
     * Wird verwendet, um die Tags einer Karte zu ändern.
     * @param card: Karte, um Ihre Tags zu ändern.
     * @param tagNew: die Liste von Tags
     */
    public void setTagsToCard(Card card, List<Tag> tagNew) {
            List<Tag> tagOld = getTagsToCard(card); //check Old Tags to remove unused tags
            if (tagOld.isEmpty()) {
                checkAndCreateTags(card, tagNew, tagOld);
            } else if (new HashSet<>(tagOld).containsAll(tagNew)) {
                if (tagOld.size() == tagNew.size()) {
                    //nothing to do
                } else { //nur Löschen
                    checkAndRemoveTags(card, tagNew, tagOld);
                }
            } else if (new HashSet<>(tagNew).containsAll(tagOld)) {  // nur neue hinzufügen
                checkAndCreateTags(card, tagNew, tagOld);
            } else { //neue und alte
                checkAndCreateTags(card, tagNew, tagOld);
                checkAndRemoveTags(card, tagNew, tagOld);
            }
    }

    /**
     * Wird verwendet, Um zu überprüfen, ob die angegebene Liste von Tags bereits Tags für die Karte sind oder nicht. Wird an das CardToTagRepository weitergegeben.
     * @param card: die Karte, um ihre Tags zu überprüfen
     * @param tagOld: die Liste von Tags der Karte
     * @param tagNew: die angegebene Liste von Tags zu vergleichen
     */
    private void checkAndRemoveTags(Card card, List<Tag> tagNew, List<Tag> tagOld) {
        for (Tag t : tagOld) {
            if (!tagNew.contains(t))
                    removeCardToTag(card,t);
        }
    }

    /**
     * Wird verwendet, um einen spezifischen CardToTag zu löschen. Wird ans cardToTagRepo weitegegeben.
     * @param c Karte für den CardToTag
     * @param t Tag für den CardToTag
     */
    private void removeCardToTag(Card c, Tag t) {
        execTransactional(() -> {
            cardToTagRepository.delete(
                    cardToTagRepository.findSpecificCardToTag(c, t));
            return null; // Lambda braucht einen return
        });
    }


    /**
     * Wird verwendet, Um zu überprüfen, ob die angegebene Liste von Tags bereits Tags für die Karte sind oder nicht.
     * Wenn nicht wird diese Liste von Tags in der Karte hinzufügen. Wird an das CardToTagRepository weitergegeben.
     * @param card: die Karte, um ihre Tags zu überprüfen
     * @param tagOld: die Liste von Tags der Karte
     * @param tagNew: die angegebene Liste von Tags zu vergleichen und hinzufügen
     */
    private void checkAndCreateTags(Card card, List<Tag> tagNew, List<Tag> tagOld) {
         execTransactional(() -> {
            for (Tag t : tagNew) {
                if (!tagOld.isEmpty() && tagOld.contains(t)) {
                    log.info("Tag {} bereits für Karte {} in CardToTag enthalten, kein erneutes Hinzufügen notwendig", t.getUuid(), card.getUuid());
                } else {
                    checkNotNullOrBlank(t,"Tag",true);
                    try {
                        Tag tag = tagRepository.findTag(t.getVal());
                            t = tag;
                            log.info("Tag mit Namen {} bereits in Datenbank enthalten", t.getVal());

                    } catch (NoResultException ex) {
                        tagRepository.save(t);
                    }
                    log.info("Tag {} wird zu Karte {} hinzugefügt", t.getUuid(), card.getUuid());
                    cardToTagRepository.createCardToTag(card, t);
                }
            }
            return null;
        });
    }



    //ZUSATZ

    /**
     * Wird aufgerufen, um ausgewählte Karten zu exportieren. Wird an die Exporter Klasse weitergereicht.
     * @param cards Set an Karten, die exportiert werden sollen
     * @param filetype Exporttyp der Karten
     */
    public void exportCards(Card[] cards, ExportFileType filetype) {
        new Exporter(filetype).export(cards);
    }




}
