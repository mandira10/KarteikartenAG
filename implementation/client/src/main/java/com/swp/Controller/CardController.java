package com.swp.Controller;

import java.util.List;

import com.swp.DataModel.Card;
import com.swp.DataModel.CardOverview;
import com.swp.DataModel.Tag;
import com.swp.GUI.Extras.ListOrder;
import com.swp.Logic.CardLogic;
import com.swp.Persistence.Exporter.ExportFileType;


/**
 * CardController Klasse. Wird in der GUI für alle Funktionen zur Karte aufgerufen und
 * gibt Ergebnisse an die GUI weiter.
 */
public class CardController extends Controller
{
    /**
     * Instanz von CardController
     */
    private static CardController cardController;

    /**
     * Hilfsmethode. Wird genutzt, damit nicht mehrere Instanzen vom CardController entstehen.
     *
     * @return cardController Instanz, die benutzt werden kann.
     */
    public static CardController getInstance() 
    {
        if (cardController == null)
            cardController = new CardController();
        return cardController;
    }

    /**
     * Benutze Logiken, auf die zugegriffen wird.
     */
    private final CardLogic cardLogic = CardLogic.getInstance();

    /**
     * Wird in der CardOverviewPage verwendet, um die einzelnen Karten für die Seitenauswahl mitsamt Titel (wenn nicht vorhanden dann die Frage), Typ,
     * Anzahl der Decks und ihrem Erstellzeitpunkt anzuzeigen. Gibt die Methode an die CardLogic weiter.
     *
     * @param begin  Seitenauswahl Anfangswert
     * @param end  Seitenauswahl Endwert
     * @param callback  Callback für die GUI, gibt bei success Liste an Daten weiter, bei Fehler die Exception message.
     */
    public void getCardsToShow(int begin, int end, DataCallback<CardOverview> callback) 
    {
        callLogicFuncInThread(
            () -> { return cardLogic.getCardOverview(begin, end); }, 
            "getcardstoshowempty", "Es wurden keine zugehörigen Karten gefunden", 
            "getcardstoshowerror", "Beim Suchen nach Karten ist ein Fehler $ aufgetreten",
            callback, "");
    }

    /**
     * Wird in der CardOverviewPage verwendet, um die einzelnen Karten für die Seitenauswahl mitsamt Titel (wenn nicht vorhanden dann die Frage), Typ,
     * Anzahl der Decks und ihrem Erstellzeitpunkt anzuzeigen. Gibt die Methode an die CardLogic weiter.
     * Gibt eine sortierte Liste von Karten wieder.
     *
     * @param begin  Seitenauswahl Anfangswert
     * @param end  Seitenauswahl Endwert
     * @param callback  Callback für die GUI, gibt bei success Liste an Daten weiter, bei Fehler die Exception message.
     * @param order Der Parameter nach dem die Karten sortiert werden
     * @param reverseOrder Gibt die Sortierreihenfolge an
     */

    public void getCardsToShow(int begin, int end, ListOrder.Order order, boolean reverseOrder, DataCallback<CardOverview> callback)
    {
        callLogicFuncInThread(
            () -> { return cardLogic.getCardOverview(begin, end, order, reverseOrder); },
            "getcardstoshowempty", "Es wurden keine zugehörigen Karten gefunden", 
            "getcardstoshowerror", "Beim Suchen nach Karten ist ein Fehler $ aufgetreten",
            callback, "");
    }

    /**
     * Nutzung für Display einzelner Karten in Filterfunktion in OverviewPage verwendet.
     * Wird an die CardLogic weitergegeben.
     * Gibt eine sortierte Liste von Karten wieder.
     *
     * @param tag  Der Tag, zu dem die Karten abgerufen werden sollen
     * @param callback  Callback für die GUI, gibt bei success Liste an Daten weiter, bei Fehler die Exception message.
     * @param order Der Parameter nach dem die Karten sortiert werden
     * @param reverseorder Gibt die Sortierreihenfolge an
     */
    public void getCardsByTag(String tag, DataCallback<CardOverview> callback, ListOrder.Order order, boolean reverseorder)
    {
        callLogicFuncInThread(
            () -> { return cardLogic.getCardsByTag(tag, order, reverseorder); },
            "getcardsbytagempty", "Es wurden keine Karten für den Tag gefunden", 
            "getcardsbytagerror", "Beim Suchen nach Karten mit Tag " + tag + " ist ein Fehler $ aufgetreten",
            callback, "tag");
    }

    /**
     * Nutzung für Display einzelner Karten in Filterfunktion in OverviewPage verwendet.
     * Wird an die CardLogic weitergegeben.
     *
     * @param tag  Der Tag, zu dem die Karten abgerufen werden sollen
     * @param callback  Callback für die GUI, gibt bei success Liste an Daten weiter, bei Fehler die Exception message.
     */
    public void getCardsByTag(String tag, DataCallback<CardOverview> callback)
    {
        callLogicFuncInThread(
                () -> { return cardLogic.getCardsByTag(tag); },
                "getcardsbytagempty", "Es wurden keine Karten für den Tag gefunden",
                "getcardsbytagerror", "Beim Suchen nach Karten mit Tag " + tag + " ist ein Fehler $ aufgetreten",
                callback, "tag");
    }

    /**
     * Kann verwendet werden, um einzelne Tags zu Karten in der SingleCardOverviewPage oder im EditModus aufzurufen
     *
     * @param card Die Karte, zu der die Tags abgerufen werden sollen
     * @param callback  Callback für die GUI, gibt bei success Liste an Daten weiter, bei Fehler die Exception message.
     */
    public void getTagsToCard(Card card, DataCallback<Tag> callback)
    {
        String cardString = card != null ? card.toString() : "";
        callLogicFuncInThread(
            () -> { return cardLogic.getTagsToCard(card); },
            "gettagstocardempty", "",
            "gettagstocarderror", "Beim Suchen nach Tags der Karte "+ cardString +" ist ein Fehler $ aufgetreten",
            callback, "");
    }

    /**
     * Nutzung für Display bestimmter Karten bei CardOverviewPage. Wird an die CardLogic weitergegeben.
     * Gibt eine sortierte Liste von Karten wieder.
     *
     * @param searchterm Übergebener String mit dem Suchwort
     * @param callback  Callback für die GUI, gibt bei success Liste an Daten weiter, bei Fehler die Exception message.
     * @param order Der Parameter nach dem die Karten sortiert werden
     * @param reverseorder Gibt die Sortierreihenfolge an
     */
    public void getCardsBySearchterms(String searchterm, DataCallback<CardOverview> callback, ListOrder.Order order, boolean reverseorder)
    {
        callLogicFuncInThread(
            () -> { return cardLogic.getCardsBySearchterms(searchterm, order, reverseorder); },
            "getcardsbysearchtermsempty", "Es gibt keine Karten für dieses Suchwort",
            "getcardsbysearchtermserror", "Beim Suchen nach Karten mit dem Suchbegriff "+searchterm+" ist ein Fehler $ aufgetreten", 
            callback, "searchterm");
    }

    /**
     * Nutzung für Display bestimmter Karten bei CardOverviewPage. Wird an die CardLogic weitergegeben.
     *
     * @param searchterm Übergebener String mit dem Suchwort
     * @param callback  Callback für die GUI, gibt bei success Liste an Daten weiter, bei Fehler die Exception message.
     */
    public void getCardsBySearchterms(String searchterm, DataCallback<CardOverview> callback)
    {
        callLogicFuncInThread(
                () -> { return cardLogic.getCardsBySearchterms(searchterm); },
                "getcardsbysearchtermsempty", "Es gibt keine Karten für dieses Suchwort",
                "getcardsbysearchtermserror", "Beim Suchen nach Karten mit dem Suchbegriff "+searchterm+" ist ein Fehler $ aufgetreten",
                callback, "searchterm");
    }


    /**
     * Dient dem Löschen einzelner Karten. Wird an die CardLogic weitergegeben.
     *
     * @param card     Die zu löschende Karte
     * @param callback Callback für die GUI, bei success passiert nichts, bei Fehler wird die Exception message an GUI weitergegeben.
     */
    public void deleteCard(Card card, SingleDataCallback<Boolean> callback) 
    {
        callLogicFuncInThread(
            () -> { cardLogic.deleteCard(card); return true; },
            "", "",
            "deletecarderror", "Beim Löschen der Karte "+card.toString()+" ist ein Fehler $ aufgetreten", 
            callback, "");

    }

    /**
     * Dient dem Löschen mehrerer Karten. Wird an die CardLogic weitergegeben.
     *
     * @param cards Die zu löschenden Karten
     * @param callback  Callback für die GUI, bei success passiert nichts, bei Fehler wird die Exception message an GUI weitergegeben.
     */
    public void deleteCards(List<CardOverview> cards, SingleDataCallback<Boolean> callback) 
    {
        callLogicFuncInThread(
            () -> { cardLogic.deleteCards(cards); return true; }, 
            "", "",
            "deletecardserror", "Beim Löschen der Karten "+cards.toString()+" ist ein Fehler $ aufgetreten", 
            callback, "");
    }

    /**
     * Wird verwendet, um einzelne Karteninformationen über ihre UUID abzurufen. Wird an die CardLogic weitergegeben.
     *
     * @param uuid     UUID der abzurufenden Karte
     * @param callback Callback für die GUI, bei success wird Karte an GUI weitergegeben, bei Fehler wird die Exception message an GUI weitergegeben.
     *
     */
    public void getCardByUUID(String uuid, SingleDataCallback<Card> callback) 
    {
        callLogicFuncInThread(
            () -> cardLogic.getCardByUUID(uuid),
            "getcardbyuuidempty", "Es wurde keine Karte zur UUID "+uuid+" gefunden",
            "getcardbyuuiderror", "Beim Abrufen der Karte ist ein Fehler $ aufgetreten", 
            callback, "UUID");
    }


    /**
     * Wird verwendet, um Tags für eine Karte festzulegen. Wird an die CardLogic weitergegeben.
     *
     * @param card     die Karte
     * @param set      die Liste der Tags
     * @param callback Bei Success passiert nichts, bei Failure wird Exception an GUI weitergegeben.
     */
    public void setTagsToCard(Card card, List<Tag> set, SingleDataCallback<Boolean> callback) 
    {
        String cardString = card != null ? card.toString() : "";
        callLogicFuncInThread(
            () -> { cardLogic.setTagsToCard(card, set); return true; }, 
            "", "",
            "settagstocarderror", "Beim Setzen der Tags für die Karte mit der UUID "+cardString+" ist ein Fehler $ aufgetreten",
            callback,"");
    }

    /**
     * Wird verwendet, um Data für eine Karte zu aktualisieren. Wird an die CardLogic weitergegeben.
     *
     * @param cardToChange die Karte zu aktualisieren
     * @param neu          Ob, die Karte neue oder nicht ist zu verstehen
     * @param callback     Bei Success passiert nichts, bei Failure wird Exception an GUI weitergegeben.
     */
    public void updateCardData(Card cardToChange, boolean neu, SingleDataCallback<Boolean> callback) 
    {
        String uuid = cardToChange != null ? cardToChange.getUuid() : "";
        callLogicFuncInThread(
            () -> { cardLogic.updateCardData(cardToChange, neu); return true; }, 
            "", "",
            "updatecreatecarderror", "Beim Updaten/Speichern der Karte "+uuid+" mit der ist ein Fehler $ aufgetreten",
            callback,"");
    }


    /**
     * Wird aufgerufen, um ausgewählte Karten zu exportieren. Wird an die CardLogic weitergereicht.
     *
     * @param cards    Set an Karten, die exportiert werden sollen
     * @param filetype Exporttyp der Karten
     * @param callback Bei Success passiert nichts, bei Failure wird Exception an GUI weitergegeben.
     */
    public void exportCards(List<CardOverview> cards, String destination, ExportFileType filetype, SingleDataCallback<Boolean> callback) 
    {
        //Special case
        try { success(callback, cardLogic.exportCards(cards, destination, filetype)); }
        catch(final Exception ex) 
        {
            if(ex.getMessage() != null)
                failure("cardexporterror", "Beim Exportieren der Karte(n) gab es einen Fehler " + ex.getMessage(), callback, "");
        }
    }
}


