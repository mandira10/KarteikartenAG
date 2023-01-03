package com.swp.Controller;


import com.swp.DataModel.Card;
import com.swp.DataModel.CardTypes.AudioCard;
import com.swp.DataModel.Tag;
import com.swp.GUI.Extras.Notification;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.Logic.CardLogic;
import com.swp.Persistence.Exporter.ExportFileType;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtilsBean;

import javax.sound.sampled.AudioFileFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

@Slf4j
public class CardController {

    //CARDOVERVIEW

    /**
     * Wird in der CardOverviewPage verwendet, um die einzelnen Karten für die Seitenauswahl mitsamt Titel (wenn nicht vorhanden dann die Frage), Typ,
     * Anzahl der Decks und ihrem Erstellzeitpunkt anzuzeigen. Gibt die Methode an die CardLogic weiter.
     * @param begin: Seitenauswahl Anfangswert
     * @param end: Seitenauswahl Endwert
     * @return anzuzeigende Karten
     */
    public static Set<Card> getCardsToShow(long begin, long end) {

            return CardLogic.getCardsToShow(begin, end);

    //TODO EXCEPTIONS? Interrupted, begin, end?
    }

    /**
     * Nutzung für Display einzelner Karten in SingleCardViewPage(?), Filterfunktion in OverviewPage.
     * Wird an die CardLogic weitergegeben.
     *
     * @param tag: Der Tag, zu dem die Karten abgerufen werden sollen
     * @return Sets an Karten mit spezifischem Tag
     */
    public static Set<Card> getCardsByTag(Tag tag) {
        try {
            Set cardsForTag = CardLogic.getCardsByTag(tag);

            if (cardsForTag.isEmpty()) {
                NotificationGUI.addNotification("Es gibt keine Karten für diesen Tag", Notification.NotificationType.INFO, 5);
            }

            return cardsForTag; //TODO Verknüpfung mit GUI und Darstellung im Overview

        } catch (IllegalArgumentException ex) { //übergebener Wert ist leer
            NotificationGUI.addNotification(ex.getMessage(), Notification.NotificationType.ERROR, 5);
            return null;
        }

        //TODO zusätzlich findTagByString? wie soll Filterfunktion aussehen?
    }

    /**
     * Nutzung für Display bestimmter Karten bei CardOverviewPage. Wird an die CardLogic weitergegeben.
     * @param searchterm Übergebener String mit dem Suchwort
     * @return Sets an Karten, die das Suchwort enthalten
     */
    public static Set<Card> getCardsBySearchterms(String searchterm) {
        try {
            Set cardsForSearchTerms = CardLogic.getCardsBySearchterms(searchterm);


            if (cardsForSearchTerms.isEmpty()) {
                NotificationGUI.addNotification("Es gibt keine Karten für dieses Suchwort", Notification.NotificationType.INFO, 5);
            }

            return cardsForSearchTerms; //TODO Verknüpfung mit GUI und Darstellung im Overview
        } catch (IllegalArgumentException ex) { //übergebener Wert ist leer
            NotificationGUI.addNotification(ex.getMessage(), Notification.NotificationType.ERROR, 5);
            return null;
        }
    }

    /**
     * Dient dem Löschen einzelner Karten. Wird an die CardLogic weitergegeben.
     * @param card Die zu löschende Karte
     * @return true, wenn ausgeführt, ansonsten false
     */
    public static boolean deleteCard(Card card) {
        try {
            return CardLogic.deleteCard(card);
        } catch (IllegalStateException ex) {
            NotificationGUI.addNotification(ex.getMessage(), Notification.NotificationType.ERROR, 5);
            return false;
        }
    }

    /**
     * Dient dem Löschen mehrerer Karten. Wird an die CardLogic weitergegeben.
     * @param cards Die zu löschenden Karten
     * @return true, wenn ausgeführt, ansonsten false
     */
    public static boolean deleteCards(Card[] cards) {
        try {
            return CardLogic.deleteCards(cards);
        } catch (IllegalStateException ex) {
            NotificationGUI.addNotification(ex.getMessage(), Notification.NotificationType.ERROR, 5);
            return false;
        }
    }


    //SINGLEVIEWCARDPAGE, CARDEDITPAGE

    /**
     * Wird verwendet, um einzelne Karteninformationen über ihre UUID abzurufen. Wird an die CardLogic weitergegeben.
     * @param uuid: UUID der abzurufenden Karte
     * @return Zugehörige Karte
     */
    public static Card getCardByUUID(String uuid) {
        try {
            return CardLogic.getCardByUUID(uuid);
        } catch (IllegalArgumentException ex) {
            //TODO: nur internes Handling, da interner Fehler? UUID nicht richtig übergeben
            NotificationGUI.addNotification("Karte konnte nicht gefunden werden", Notification.NotificationType.ERROR, 5);
            return null;
        }
    }

    /**
     * Lädt alle Tags als Set. Werden in der CardEditPage als Dropdown angezeigt. Wird weitergegeben an die CardLogic.
     * @return Set mit bestehenden Tags.
     */
    public static Set<Tag> getTags() {
        return CardLogic.getTags();
    }

    /**
     * Methode zum Erstellen und Aktualisieren von Karten. Wird an die Logik weitergegeben.
     * @param card Karte zum Aktualisieren, wenn null, dann muss eine neue Karte erstellt werden
     * @param type Der Kartentyp als String von der GUI übergeben
     * @param attributes Die HashMap mit den Attributen
     * @param tags Zugehörige Tags als String Set für die Karte
     * @param categories Zugehörige Categories als String Set für die Karte
     * @return true, wenn erfolgreich erstellt
     */
    public static boolean updateCardData(Card card, String type, HashMap<String, Object> attributes, Set<String> tags, Set<String> categories){
       

        return CardLogic.updateCardData(card, type, attributes, tags, categories);

    }

    public static boolean addTagsToCard(Card card, Set<String> tags) {
        return CardLogic.createCardToTags(card, tags);
    }



    //ZUSATZ

    /**
     * Wird aufgerufen, um ausgewählte Karten zu exportieren. Wird an die CardLogic weitergereicht.
     * @param cards Set an Karten, die exportiert werden sollen
     * @param filetype Exporttyp der Karten
     * @return Exportierte Datei
     */
    public static boolean exportCards(Card[] cards, ExportFileType filetype) {
        return CardLogic.exportCards(cards, filetype);
    }


    //TO DISCUSS

    public static boolean createTag(String value) { //TODO:auch einzeln aufgerufen? wo? ansonsten nur über Logik bei CardCreate?
        return CardLogic.createTag(value);
    }


}
