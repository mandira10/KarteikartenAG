package com.swp.Logic;

import com.swp.DataModel.Card;
import com.swp.DataModel.CardToTag;
import com.swp.DataModel.CardTypes.*;
import com.swp.DataModel.Tag;
import com.swp.Persistence.Cache;
import com.swp.Persistence.CardRepository;
import com.swp.Persistence.Exporter;
import com.swp.Persistence.Exporter.ExportFileType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtils;

import java.lang.reflect.InvocationTargetException;
import java.util.*;

import static com.swp.Validator.checkNotNullOrBlank;

@Slf4j
public class CardLogic
{

    //OVERVIEW

    /**
     * Wird in der CardOverviewPage verwendet, um die einzelnen Karten für die Seitenauswahl mitsamt Titel (wenn nicht vorhanden dann die Frage), Typ,
     * Anzahl der Decks und ihrem Erstellzeitpunkt anzuzeigen. Gibt die Methode an das CardRepository weiter.
     * @param begin: Seitenauswahl Anfangswert
     * @param end: Seitenauswahl Endwert
     * @return anzuzeigende Karten
     */
    public static Set<Card> getCardsToShow(long begin, long end){
        return CardRepository.getCards(begin, end);
    }

    /**
     * Wird verwendet bei einer Filterung nach einem bestimmten Tag. Prüft zunächst, dass der übergebene Tag nicht
     * null oder leer ist und gibt die Funktion dann an das Card Repository weiter.
     * @param tag: Der Tag zu dem die Karten gesucht werden sollen
     * @return Set der Karten, die Tag enthalten
     */
    public static Set<Card> getCardsByTag(Tag tag)
	{
        checkNotNullOrBlank(tag, "Tag");
        return CardRepository.findCardsByTag(tag);

    }

    /**
     * Methode wird verwendet, um passende Karten für die angegebenen Suchwörter zu identifizieren. Prüft zunächst,
     * dass der übergebene Tag nicht null oder leer ist und gibt die Funktion dann an das Card Repository weiter.
     * @param terms Suchwörter, die durch ein Leerzeichen voneinander getrennt sind
     * @return Set der Karten, die Suchwörter enthalten.
     */
    public static Set<Card> getCardsBySearchterms(String terms)
    {
        checkNotNullOrBlank(terms, "Suchbegriffe");
        return CardRepository.findCardsContaining(terms);
    }

    /**
     * Wird aufgerufen, wenn eine spezifische Karte gelöscht werden soll. Gibt die Karte weiter
     * an das CardRepository und erhält ein positives Ergebnis, wenn die Aktion erfolgreich war.
     * @param card zu löschende Karte
     * @return wahr, wenn Karte gelöscht wurde
     */
    public static boolean deleteCard(Card card)
    {
        if(card == null){
            throw new IllegalStateException("Karte existiert nicht");

        }
        return CardRepository.deleteCard(card); //TODO:Exceptions (wenn Karte nicht gelöscht werden kann)
    }

    /**
     * Wird aufgerufen, wenn mehrere Karten gelöscht werden sollen. Gibt die Karten einzeln weiter
     * an das CardRepository und erhält ein positives Ergebnis, wenn alle Karten erfolgreich gelöscht wurden.
     * @param cards zu läschende Karten
     * @return wahr, wenn alle Karten erfolgreich gelöscht wurden
     */
    public static boolean deleteCards(Card[] cards)
    {
        boolean ret = true;
        for(Card c : cards)
        {
            if(!deleteCard(c))
                ret = false;
        }
        return ret;
    }



    //SINGLEVIEWCARDPAGE, CARDEDITPAGE

    /**
     * Wird verwendet, um einzelne Karteninformationen über ihre UUID abzurufen. Wird an das CardRepository weitergegeben.
     * @param uuid: UUID der abzurufenden Karte
     * @return Zugehörige Karte
     */
    public static Card getCardByUUID(String uuid)
    {
        checkNotNullOrBlank(uuid, "UUID");
        return CardRepository.getCardByUUID(uuid);
    }

    /**
     * Lädt alle Tags als Set. Werden in der CardEditPage als Dropdown angezeigt. Wird weitergegeben an das CardRepository.
     * @return Set mit bestehenden Tags.
     */
    public static Set<Tag> getTags()
    {
        return CardRepository.getTags();
        //TODO: was ist mit Cache
    }


    /**
     * Methode zum Hinzufügen einzelner Tags zu Karten. Wird bei Erstellen und Aktualisieren aufgerufen, wenn Tags für die
     * Karte mit übergeben worden sind. Prüft zunächst, ob der Tag bereit für die Karte in CardToTag enthalten ist.
     * @param card Übergebende Karte
     * @param tag Übergebender Tag
     * @return true, wenn erfolgreich oder bereits enthalten
     */
    public static boolean createCardToTag(Card card, Tag tag) {
        if (getCardsByTag(tag).contains(card)) {
            log.info("Tag {} bereits für Karte {} in CardToTag enthalten, kein erneutes Hinzufügen notwendig", tag.getUuid(), card.getUuid());
            return true;
        }

        log.info("Tag {} wird zu Karte {} hinzugefügt", tag.getUuid(), card.getUuid());
        return CardRepository.createCardToTag(card, tag);
    }


    public static boolean createTag(Tag tag)
    {
        return CardRepository.saveTag(tag);
    }

    /**
     * Teilmethode zum Erstellen/Aktualisieren von Karten. Wird über den Controller aufgerufen und prüft zunächst, ob eine
     * neue Karte erstellt werden muss. Das Setzen der Attribute erfolgt über die Methode updateCardContent.
     * @param card Karte zum Aktualisieren, wenn null, dann muss eine neue Karte erstellt werden
     * @param type Der Kartentyp als String von der GUI übergeben
     * @param attributes Die HashMap mit den Attributen
     * @param tags Zugehörige Tags als String Set für die Karte
     * @param categories Zugehörige Categories als String Set für die Karte
     * @return true, wenn erfolgreich erstellt
     */
    public static boolean updateCardData(Card card, String type, HashMap<String, Object> attributes, Set<String> tags, Set<String> categories) {
        if (card == null) {
            switch (type) {
                case "TRUEFALSE":
                    TrueFalseCard cardTF = new TrueFalseCard();
                    return updateCardContent(cardTF, attributes, tags, categories, true);
                case "IMAGETEST":
                    ImageTestCard cardIT = new ImageTestCard();
                    return updateCardContent(cardIT, attributes, tags, categories, true);
                case "IMAGEDESC":
                    ImageDescriptionCard cardID = new ImageDescriptionCard();
                    return updateCardContent(cardID, attributes, tags, categories, true);
                case "MULTIPLECHOICE":
                    MultipleChoiceCard cardMC = new MultipleChoiceCard();
                    return updateCardContent(cardMC, attributes, tags, categories, true);
                case "TEXT":
                    TextCard cardT = new TextCard();
                    return updateCardContent(cardT, attributes, tags, categories, true);
                case "AUDIO":
                    AudioCard cardAu = new AudioCard();
                    return updateCardContent(cardAu, attributes, tags, categories, true);
                default:
                  return false;
            }
        } else {
            return updateCardContent(card, attributes, tags, categories, false);
        }
        //TODO: Exceptions, Erfolgsmeldungen..
    }

    /**
     * Teilmethode zum Erstellen/Aktualisieren von Karten. Wird verwendet, um die einzelnen Attribute der neuen Karten oder bestehenden Karten anzupassen.
     * @param card Die zu bearbeitende Karte
     * @param attributes Attribute der Karte
     * @param tags Tags, die angepasst werden
     * @param categories Kategorien, die angepasst werden
     * @param neu wenn true, dann ist die Karte neu erstellt worden
     * @return ob erfolgreich
     */
    private static boolean updateCardContent(Card card, HashMap<String, Object> attributes, Set<String> tags, Set<String> categories, boolean neu) {
        try {
            log.info("Versuche die Attribute der Karte {} zu aktualisieren", card.getUuid());
            BeanUtils.populate(card, attributes);
            card.setContent();
        } catch (IllegalAccessException | InvocationTargetException ex) {
            //TODO + weitere Exceptions, Prüfung mit Validator? in Constructor einnbauen
        }
        if (neu) {
            if (CardRepository.saveCard(card)) {
                if (tags != null) {
                    log.info("Versuche übergebene Tag(s) der Karte {} zuzuordnen", card.getUuid());
                    return createCardToTags(card, tags);
                }
                if (categories != null) {
                    log.info("Versuche übergebene Kategorie(n) der Karte {} zuzuordnen", card.getUuid());
                    return CategoryLogic.createCardToCategory(card, categories);
                }
                return true;
            } else {
                throw new IllegalArgumentException("Probleme beim Speichern der Karte"); //TODO: genauer aufschlüsseln
            }
        } else {
            if (CardRepository.updateCard(card)) {
                if (tags != null) {
                    log.info("Versuche übergebene Tag(s) der Karte {} zuzuordnen", card.getUuid());
                    createCardToTags(card, tags);
                }
                if (categories != null) {
                    log.info("Versuche übergebene Kategorie(n) der Karte {} zuzuordnen", card.getUuid());
                    CategoryLogic.createCardToCategory(card, categories);
                }
                return true;
            } else {
                throw new IllegalArgumentException("Probleme beim Updaten der Karte"); //TODO: genauer aufschlüsseln
            }


        }
    }

    public static boolean createCardToTags(Card card, Set<String> tags) {
        boolean ret = true;
        for (String name : tags) {
            checkNotNullOrBlank(name, "Tag Name");
            final Optional<Tag> optionalTag = CardRepository.find(name);
            if (optionalTag.isPresent()) {
                final Tag tag = optionalTag.get();
                if(!createCardToTag(card,tag))
                ret = false;
            }
            else{
                Tag tag = new Tag(name);
                CardRepository.saveTag(tag);
                if(!createCardToTag(card,tag))
                ret = false;
            }
        }
        return ret;
    }




    //ZUSATZ

    /**
     * Wird aufgerufen, um ausgewählte Karten zu exportieren. Wird an die Exporter Klasse weitergereicht.
     * @param cards Set an Karten, die exportiert werden sollen
     * @param filetype Exporttyp der Karten
     * @return Exportierte Datei
     */
    public static boolean exportCards(Card[] cards, ExportFileType filetype)
    {
        return (new Exporter(filetype)).export(cards);
        //TODO:Exceptions?
    }




    //private void changeCard() {}

}
