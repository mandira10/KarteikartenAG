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
    public List<Card> getCardsToShow(int begin, int end)
    {
        return execTransactional(() -> cardRepository.getCardRange(begin, end));
    }

    /**
     * Wird verwendet bei einer Filterung nach einem bestimmten Tag. Prüft zunächst, dass der übergebene Tag nicht
     * null oder leer ist und gibt die Funktion dann an das Card Repository weiter.
     * @param tagName: Der textuelle Tag, zu dem die Karten gesucht werden sollen
     * @return Set der Karten, die Tag enthalten
     * @throws NoResultException falls es keinen Tag, oder Karte mit entsprechendem Tag gibt.
     */
    public List<Card> getCardsByTag(String tagName) {
        checkNotNullOrBlank(tagName, "Tag",true);
        return execTransactional(() -> cardRepository.findCardsByTag(
                tagRepository.findTag(tagName)));
    }

    /**
     * Methode wird verwendet, um passende Karten für die angegebenen Suchwörter zu identifizieren. Prüft zunächst,
     * dass der übergebene Tag nicht null oder leer ist und gibt die Funktion dann an das Card Repository weiter.
     * @param terms Suchwörter, die durch ein Leerzeichen voneinander getrennt sind
     * @return Set der Karten, die Suchwörter enthalten.
     */
    public List<Card> getCardsBySearchterms(String terms)
    {
        checkNotNullOrBlank(terms, "Suchbegriff",true);
        return execTransactional(() -> cardRepository.findCardsContaining(terms));
    }

    /**
     * Wird aufgerufen, wenn eine spezifische Karte gelöscht werden soll. Gibt die Karte weiter
     * an das CardRepository und erhält ein positives Ergebnis, wenn die Aktion erfolgreich war.
     * @param card zu löschende Karte
     */
    public void deleteCard(Card card)
    {
        if(card == null){
            throw new IllegalStateException("Karte existiert nicht");
        }
        execTransactional(() -> {
            cardRepository.delete(card);
            //TODO: lösche alle Card2Tags
            //TODO: lösche alle Card2Decks
            //TODO: lösche alle Card2Categories
            return null; // Lambda braucht immer einen return
        });

    }

    /**
     * Wird aufgerufen, wenn mehrere Karten gelöscht werden sollen. Gibt die Karten einzeln weiter
     * an das CardRepository und erhält ein positives Ergebnis, wenn alle Karten erfolgreich gelöscht wurden.
     * @param cards zu löschende Karten
     */
    public void deleteCards(List<Card> cards)
    {
        for(Card c : cards)
        deleteCard(c);
    }



    //SINGLEVIEWCARDPAGE, CARDEDITPAGE

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


    public List<Tag> getTagsToCard(Card card) {
        return execTransactional(() -> tagRepository.getTagsToCard(card));
    }

    /**
     * Wird aufgerufen, wenn eine neue Karte erstellt wird oder eine bestehende angepasst.
     * @param cardToChange Die Karte, die neu erstellt werden soll oder die Kopie der Karte mit den Änderungen
     * @param neu gibt an, ob es sich um eine komplett neue Karte handelt
     * @return
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


    public void setTagsToCard(Card card, List<Tag> tagNew) {
        if(!tagNew.isEmpty()) {
            List<Tag> tagOld = getTagsToCard(card); //check Old Tags to remove unused tags
            if (tagOld == null) {
                checkAndCreateTags(card, tagNew, tagOld);
            } else if (tagOld.containsAll(tagNew) && tagOld.size() == tagNew.size()) //no changes
                return;

            else if (tagOld.containsAll(tagNew) || tagOld.size() > tagNew.size()) { //es wurden nur tags gelöscht
                checkAndRemoveTags(card, tagNew, tagOld);

            } else if (tagNew.containsAll(tagOld)) {
                checkAndCreateTags(card, tagNew, tagOld);
            } else { //neue und alte
                checkAndCreateTags(card, tagNew, tagOld);
                checkAndRemoveTags(card, tagNew, tagOld);
            }
        }
    }

    private void checkAndRemoveTags(Card card, List<Tag> tagNew, List<Tag> tagOld) {
        for (Tag t : tagOld) {
            if (!tagNew.contains(t))
                execTransactional(() -> {
                    CardToTag c2t = cardToTagRepository.findSpecificCardToTag(card, t);
                    cardToTagRepository.delete(c2t);
                    return null;
                });
        }
    }


    private void checkAndCreateTags(Card card, List<Tag> tagNew, List<Tag> tagOld) {
         execTransactional(() -> {
            for (Tag t : tagNew) {
                if (tagOld != null && tagOld.contains(t)) {
                    log.info("Tag {} bereits für Karte {} in CardToTag enthalten, kein erneutes Hinzufügen notwendig", t.getUuid(), card.getUuid());
                } else {
                    try {
                        Tag tag = tagRepository.findTag(t.getVal());
                        t = tag;
                        log.info("Kategorie mit Namen {} bereits in Datenbank enthalten", t.getVal());
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
     * @return Exportierte Datei
     */
    public void exportCards(Card[] cards, ExportFileType filetype) {
        new Exporter(filetype).export(cards);
    }

    private void removeCardToTag(Card c, Tag t) {
        execTransactional(() -> {
            cardToTagRepository.delete(
                    cardToTagRepository.findSpecificCardToTag(c, t));
            return null; // Lambda braucht einen return
        });
    }

}
