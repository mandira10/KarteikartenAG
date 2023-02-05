package com.swp.Controller;

import com.gumse.gui.Locale;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardOverview;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.Logic.StudySystemLogic;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.stream.Collectors;

/**
 * StudySystemController Klasse. Wird in der GUI für alle Funktionen zur StudySystem aufgerufen und
 * gibt Ergebnisse an die GUI weiter.
 *
 * @author Nadja Cordes, Mert As
 */
@Slf4j
public class StudySystemController extends Controller
{
    /**
     * Instanz von studySystemContoller
     */
    private static StudySystemController studySystemController;

    /**
     * Hilfsmethode. Wird genutzt, damit nicht mehrere Instanzen vom StudySystemController entstehen.
     *
     * @return studySystemController Instanz, die benutzt werden kann.
     */
    public static StudySystemController getInstance()
    {
        if (studySystemController == null)
            studySystemController = new StudySystemController();
        return studySystemController;
    }

    /**
     * Benutze Logiken, auf die zugegriffen wird.
     */
    private final StudySystemLogic studySystemLogic = StudySystemLogic.getInstance();



    /**
     * Wird nach der Erstellung eines neuen StudySystem verwendet und verschiebt alle Karten für das StudySystem in die erste Box.
     *
     * @param cards       Karten, die StudySystem enthalten soll.
     * @param studySystem Das StudySystem, das benötigt wird.
     * @param callback    wird verwendet, um mögliche Fehler abzufangen.
     */
    public void moveAllCardsForDeckToFirstBox(List<Card> cards, StudySystem studySystem, SingleDataCallback<Boolean> callback) 
    {
        callLogicFuncInThread(
            () -> { studySystemLogic.moveAllCardsForDeckToFirstBox(cards, studySystem); return true; }, 

            "moveallcardsfordecktofirstboxerror", "",
            callback, "");
    }


    /**
     * Wird verwendet, um alle Karten in diesem Studiensystem zu erhalten. Wird an die StudySystemLogic weitergegeben
     *
     * @param studySystem  Das StudySystem, das benötigt wird.
     * @param callback     wird verwendet, um mögliche Fehler abzufangen.
     */
    public void getAllCardsInStudySystem(StudySystem studySystem, DataCallback<CardOverview> callback)
    {
        callLogicFuncInThread(
            () -> { return studySystemLogic.getAllCardsInStudySystem(studySystem); },
            "", "Es gibt aktuell noch keine Karten für das Deck",
            "getallcardsinstudysystemerror", "",
            callback, "");
    }

    /**
     * Nach Beantwortung einer Frage wird die Antwort übergeben, sodass
     * je nach Antwort die Karte in den Boxen verschoben werden kann. Wird an die StudySystemLogic weitergegeben
     *
     * @param answer      Frage war richtig / falsch beantwortet
     * @param studySystem Das StudySystem, das benötigt wird.
     * @param callback    wird verwendet, um mögliche Fehler abzufangen.
     */
    public void giveAnswer(StudySystem studySystem, boolean answer, SingleDataCallback<Boolean> callback)
    {
        callLogicFuncInThread(
            () -> { studySystemLogic.giveAnswer(studySystem, answer); return true; },

            "giveanswererror", "",
            callback, "");
    }

    /**
     * Wird verwendet, um eine Bewertung vom Benutzer für VoteStudySystem zu bekommen.
     * Wird an die StudySystemLogic weitergegeben
     *
     * @param studySystem Das StudySystem, das benötigt wird.
     * @param card        Die bewertete Karte
     * @param rating      Bewertung von GUI
     * @param callback    wird verwendet, um mögliche Fehler abzufangen.
     */
    public void giveRating(StudySystem studySystem, Card card, int rating, SingleDataCallback<Boolean> callback)
    {
        callLogicFuncInThread(
            () -> { studySystemLogic.giveRating(studySystem, card, rating); return true; },

            "giveratingerror", "",
            callback, "");
    }

    /**
     * Wird verwendet, um den Test zu beenden und Ergebnispunkt zu berechnen
     * wird an die StudySystemLogic weitergegeben
     *
     * @param studySystem Das StudySystem, das benötigt wird.
     * @param callback    wird verwendet, um mögliche Fehler abzufangen.
     */
    public void finishTestAndGetResult(StudySystem studySystem, SingleDataCallback<Integer> callback)
    {
        callLogicFuncInThread(
            () -> { return studySystemLogic.finishTestAndGetResult(studySystem); },

            "finishtestandgetresulterror", "",
            callback, "");
    }


    /**
     * Wird verwendet, um die nächste Frage zu bekommen,
     * wird an die StudySystemLogic weitergegeben
     *
     * @param studySystem Das StudySystem, das benötigt wird.
     * @param callback    wird verwendet, um mögliche Fehler abzufangen.
       */
    public void getNextCard(StudySystem studySystem, SingleDataCallback<Card> callback)
    {
        callLogicFuncInThread(
            () -> { return studySystemLogic.getNextCard(studySystem); },

            "getnextcarderror", "",
            callback, "");
    }

    /**
     * Wird verwendet, um die Anzahl der Karten in einem studySystem zu bekommen. Wird an die StudySystemLogic weitergegeben
     *
     * @param studySystem studySystem, um die Anzahl der Karten darin zu suchen
     * @param callback    wird verwendet, um mögliche Fehler abzufangen.
     */
    public void numCardsInDeck(StudySystem studySystem, SingleDataCallback<Integer> callback)
    {
        callLogicFuncInThread(
            () -> { return studySystemLogic.numCardsInDeck(studySystem); },

            "numcardsindeckerror", "",
            callback, "");
    }

    /**
     * Wird verwendet, um ein StudySystem nach UUID zu bekommen. Wird an die StudySystemLogic weitergegeben
     *
     * @param uuid     UUID zu suchen
     * @param callback wird verwendet, um mögliche Fehler abzufangen.
     */
    public void getStudySystemByUUID(String uuid, SingleDataCallback<StudySystem> callback)
    {
        callLogicFuncInThread(
            () -> { return studySystemLogic.getStudySystemByUUID(uuid); },

            "getstudysystembyuuiderror", "",
            callback, "UUID");
    }

    /**
     * Wird verwendet, um eine Liste von Karten in einem StudySystem zu löschen. Wird an die StudySystemLogic weitergegeben.
     *
     * @param callback    wird verwendet, um mögliche Fehler abzufangen.
     * @param studySystem Das StudySystem, das benötigt wird.
     * @param list        die Liste der Karten zu löschen
     */
    public void removeCardsFromStudySystem(List<CardOverview> list, StudySystem studySystem, SingleDataCallback<Boolean> callback)
    {
        callLogicFuncInThread(
            () -> { studySystemLogic.removeCardsFromStudySystem(list, studySystem); return true; },

            "removecardsfromstudysystemerror", "",
            callback, "");
    }


    /**
     * Wird verwendet, um ein StudySystem zu löschen. Wird an die StudySystemLogic weitergegeben.
     *
     * @param callback    wird verwendet, um mögliche Fehler abzufangen.
     * @param studySystem StudySystem zu löschen.
     */
    public void deleteStudySystem(StudySystem studySystem, SingleDataCallback<Boolean> callback)
    {
        callLogicFuncInThread(
            () -> { studySystemLogic.deleteStudySystem(studySystem); return true; },
            "deletestudysystemerror", "",
            callback, "");
    }


    /**
     * Wird verwendet, um eine Liste von studySystems zu löschen. Wird an die StudySystemLogic weitergegeben.
     *
     * @param studySystems Die zu löschenden Decks
     * @param callback     wird verwendet, um mögliche Fehler abzufangen.
     */
    public void deleteDecks(StudySystem[] studySystems, SingleDataCallback<Boolean> callback)
    {
        callLogicFuncInThread(
            () -> { studySystemLogic.deleteStudySystem(studySystems); return true; },

            "deletedeckserror", "",
            callback, "");
    }

    /**
     * Wird verwendet, um StudySystem zu bekommen
     *
     * @param callback wird verwendet, um mögliche Fehler abzufangen.
     */
    public void getStudySystems(DataCallback<StudySystem> callback)
    {
        callLogicFuncInThread(
            () -> { return studySystemLogic.getStudySystems(); },
            "", "Es gibt noch keine Studysystems",
            "getstudysystemserror", "",
            callback, "");
    }

    /**
     * Wird verwendet, um eine Suche für StudySystem nach einem Suchbegriff machen.
     *
     * @param searchterm Suchbegriff um nachzusuchen.
     * @param callback   wird verwendet, um mögliche Fehler abzufangen.
     */
    public void getStudySystemBySearchTerms(String searchterm, DataCallback<StudySystem> callback)
    {
        callLogicFuncInThread(
            () -> { return studySystemLogic.getStudySystemsBySearchterm(searchterm); },
            "getstudysystembysearchtermsempty", "Es wurden keine zugehörigen StudySystems für den Suchterm gefunden gefunden",
            "getstudysystembysearchtermserror", "",
            callback, searchterm);
    }


    /**
     * Für nachträgliches Hinzufügen von Karten. Wird an die StudySystemLogic weitergegeben
     *
     * @param cards       Die Liste von Karten, um hinzufügen
     * @param studySystem Das StudySystem, das benötigt wird.
     * @param callback    wird verwendet, um mögliche Fehler abzufangen.
     */
    public void addCardsToStudySystem(List<CardOverview> cards, StudySystem studySystem, SingleDataCallback<String> callback)
    {
        callLogicFuncInThread(
            () -> {
                List<Card> existingCards = studySystemLogic.addCardsToDeck(cards, studySystem);
                String result = "";
                if(!existingCards.isEmpty())
                    result = Locale.getCurrentLocale().getString("studysystemaddcardsinfo")
                           + existingCards.stream().map(Card::getTitle).collect(Collectors.joining(","));
                return result;
            },

            "addcardstostudysystemerror", "",
            callback, "");
    }

    /**
     * Wird verwendet, um das StudySystem zu updaten. Wird an die StudySystemLogic weitergegeben.
     *
     * @param oldStudySystem StudySystem im vorherigen Zustand, benötigt, um festzustellen, ob das StudySystem gewechselt wurde und Handling
     * @param newStudySystem Neue StudySystem Eigenschaften
     * @param neu            Ist true, wenn das StudySystem neu angelegt wurde
     * @param changedBoxes   Haben sich die Boxen verändert?
     * @param callback       wird verwendet, um mögliche Fehler abzufangen.
     */
    public void updateStudySystemData(StudySystem oldStudySystem, StudySystem newStudySystem, boolean neu, boolean changedBoxes, SingleDataCallback<Boolean> callback)
    {
        callLogicFuncInThread(
            () -> { studySystemLogic.updateStudySystemData(oldStudySystem, newStudySystem, neu, changedBoxes); return true; },

            "updatedeckdataerror", "",
            callback, "");
    }

    /**
     * Mithilfe der Methode werden alle Karten aus dem StudySystem zurück auf "New" gesetzt und der Progress zurückgesetzt.
     * Sollten sich Karten in Box 1 (bei Timing/Vote) oder 2-n befinden (bei Leitner) werden diese zurück in Box 0 verschoben
     *
     * @param studySystem Das zurückzusetzende StudySystem
     * @param callback    wird verwendet, um mögliche Fehler abzufangen.
     */
    public void resetLearnStatus(StudySystem studySystem, SingleDataCallback<Boolean> callback)
    {
        callLogicFuncInThread(
            () -> { studySystemLogic.resetLearnStatus(studySystem); return true; },
            "resetlearnstatuserror", "",
            callback, "");
    }
}