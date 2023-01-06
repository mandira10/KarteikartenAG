package com.swp.Controller;


import com.swp.DataModel.Card;
import com.swp.DataModel.CardTypes.AudioCard;
import com.swp.DataModel.Tag;
import com.swp.GUI.Extras.Notification;
import com.swp.GUI.Extras.NotificationGUI;
import com.swp.Logic.CardLogic;
import com.swp.Persistence.DataCallback;
import com.swp.Persistence.Exporter.ExportFileType;
import jakarta.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.beanutils.BeanUtilsBean;

import javax.sound.sampled.AudioFileFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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
    public static void getCardsToShow(long begin, long end, DataCallback<Card> callback) 
    {
        CardLogic.getCardsToShow(begin, end, callback);
        //TODO EXCEPTIONS? Interrupted, begin, end?
    }

    /**
     * Nutzung für Display einzelner Karten in Filterfunktion in OverviewPage verwendet.
     * Wird an die CardLogic weitergegeben.
     *
     * @param tag: Der Tag, zu dem die Karten abgerufen werden sollen
     * @return Sets an Karten mit spezifischem Tag
     */
    public static Set<Card> getCardsByTag(String tag) {
        try {
            Set cardsForTag = CardLogic.getCardsByTag(tag);

            if (cardsForTag.isEmpty())
                NotificationGUI.addNotification("Es gibt keine Karten für diesen Tag", Notification.NotificationType.INFO, 5);

            return cardsForTag;

        } catch (IllegalArgumentException ex) { //übergebener Wert ist leer
            NotificationGUI.addNotification(ex.getMessage(), Notification.NotificationType.ERROR, 5);
            return null;
        } catch (final NullPointerException ex) {
            log.info(ex.getMessage());
            NotificationGUI.addNotification(ex.getMessage(), Notification.NotificationType.INFO, 5);
            return null;
        } catch (final Exception ex) {
            log.error("Beim Suchen nach Karten mit Tag {} ist ein Fehler {} aufgetreten", tag
                    , ex);
            NotificationGUI.addNotification(ex.getMessage(), Notification.NotificationType.ERROR, 5);
            return null;
        }

    }

    /**
     * Kann verwendet werden, um einzelne Tags zu Karten in der SingleCardOverviewPage oder im EditModus aufzurufen
     * @param card Die Karte, zu der die Tags abgerufen werden sollen
     * @return Gefundene Tags für die spezifische Karte
     */
    private static Set<Tag> getTagsToCard(Card card) {
        try {
            Set tagsForCard = CardLogic.getTagsToCard(card);

            if (tagsForCard.isEmpty())
                log.info("Keine Tags für diese Karte vorhanden");

            return tagsForCard;

        } catch (final Exception ex) {
            log.error("Beim Suchen nach Tags mit Karten {} ist ein Fehler {} aufgetreten", card, ex);
            return null;
        }
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

            return cardsForSearchTerms;
        } catch (IllegalArgumentException ex) { //übergebener Wert ist leer
            NotificationGUI.addNotification(ex.getMessage(), Notification.NotificationType.ERROR, 5);
            return null;
        } catch (final Exception ex) {
            log.error("Beim Suchen nach Karten mit Inhalt {} ist ein Fehler {} aufgetreten", searchterm
                    , ex);
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
    public static boolean deleteCards(List<Card> cards) {
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



    public static boolean addTagsToCard(Card card, Set<String> tags) {
        return CardLogic.createCardToTags(card, tags);
    }

    public static boolean updateCardData(Card cardToChange, boolean neu){
        //TODO: Kann die GUI in editCard noch den boolean übergeben? Dann müssen wir nicht über die Datenbank prüfen, was vorliegt
        try {
            return CardLogic.updateCardData(cardToChange, neu);
        }
        catch (Exception e) {
            log.warn(String.format("Karte \"%s\" konnte nicht gespeichert oder geupdatet werden", cardToChange.getUuid()));
            return false;
        }
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



    //KEEP FOR TESTING AS FOR NOW
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


}
