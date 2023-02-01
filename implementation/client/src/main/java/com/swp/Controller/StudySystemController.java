package com.swp.Controller;

import com.gumse.gui.Locale;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardOverview;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.Logic.StudySystemLogic;
import jakarta.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;


import java.util.List;
import java.util.stream.Collectors;

/**
 * StudySystemController Klasse. Wird in der GUI für alle Funktionen zur StudySystem aufgerufen und
 * gibt Ergebnisse an die GUI weiter.
 */
@Slf4j
public class StudySystemController {
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
    private final ControllerThreadPool threadPool = ControllerThreadPool.getInstance();



    /**
     * Wird nach der Erstellung eines neuen StudySystem verwendet und verschiebt alle Karten für das StudySystem in die erste Box.
     *
     * @param cards              Karten, die StudySystem enthalten soll.
     * @param studySystem        Das StudySystem, das benötigt wird.
     * @param singleDataCallback wird verwendet, um mögliche Fehler abzufangen.
     */
    public void moveAllCardsForDeckToFirstBox(List<Card> cards, StudySystem studySystem, SingleDataCallback<Boolean> singleDataCallback) {
        threadPool.exec(() -> {
            try {
                    studySystemLogic.moveAllCardsForDeckToFirstBox(cards, studySystem);
                    if(singleDataCallback != null)
                    singleDataCallback.callSuccess(true);
                }
            catch(IllegalStateException e){
                log.error(e.getMessage());
                if(singleDataCallback != null)
                    singleDataCallback.callFailure(e.getMessage());
            }
            catch (Exception e) {
                if(singleDataCallback != null)
                    singleDataCallback.callFailure(Locale.getCurrentLocale().getString("moveallcardsfordecktofirstboxerror"));
                log.error(e.getMessage());

            }
        });
    }


    /**
     * Wird verwendet, um alle Karten in diesem Studiensystem zu erhalten. Wird an die StudySystemLogic weitergegeben
     *
     * @param studySystem  Das StudySystem, das benötigt wird.
     * @param dataCallback wird verwendet, um mögliche Fehler abzufangen.
     */
    public void getAllCardsInStudySystem(StudySystem studySystem, DataCallback<CardOverview> dataCallback) {
        threadPool.exec(() -> {
            try {
                List<CardOverview> cards = studySystemLogic.getAllCardsInStudySystem(studySystem);
                if (cards.isEmpty())
                      log.info("Es gibt aktuell noch keine Karten für das Deck");

                else
                if(dataCallback != null)
                    dataCallback.callSuccess(cards);

            }
            catch (Exception ex) {
                if(dataCallback != null)
                    dataCallback.callFailure(Locale.getCurrentLocale().getString("getallcardsinstudysystemerror"));
                log.error(ex.getMessage());
            }
        });
    }

    /**
     * Nach Beantwortung einer Frage wird die Antwort übergeben, sodass
     * je nach Antwort die Karte in den Boxen verschoben werden kann. Wird an die StudySystemLogic weitergegeben
     *
     * @param answer             Frage war richtig / falsch beantwortet
     * @param studySystem        Das StudySystem, das benötigt wird.
     * @param singleDataCallback wird verwendet, um mögliche Fehler abzufangen.
     */
    public void giveAnswer(StudySystem studySystem, boolean answer, SingleDataCallback<Boolean> singleDataCallback) {
        threadPool.exec(() -> {
            try {
                studySystemLogic.giveAnswer(studySystem, answer);
                if(singleDataCallback != null)
                    singleDataCallback.callSuccess(true);
            } catch (Exception e) {
                if(singleDataCallback != null)
                    singleDataCallback.callFailure(Locale.getCurrentLocale().getString("giveanswererror"));
                log.error(e.getMessage());
            }
        });
    }

    /**
     * Wird verwendet, um eine Bewertung vom Benutzer für VoteStudySystem zu bekommen.
     * Wird an die StudySystemLogic weitergegeben
     *
     * @param studySystem        Das StudySystem, das benötigt wird.
     * @param card               Die bewertete Karte
     * @param rating             Bewertung von GUI
     * @param singleDataCallback wird verwendet, um mögliche Fehler abzufangen.
     */
    public void giveRating(StudySystem studySystem, Card card, int rating, SingleDataCallback<Boolean> singleDataCallback) {
        threadPool.exec(() -> {
            try {
                studySystemLogic.giveRating(studySystem, card, rating);
                if(singleDataCallback != null)
                    singleDataCallback.callSuccess(true);
            } catch (Exception e) {
                if(singleDataCallback != null)
                    singleDataCallback.callFailure(Locale.getCurrentLocale().getString("giveratingerror"));
                log.error(e.getMessage());
            }
        });
    }

    /**
     * Wird verwendet, um eine Antwortzeit vom Benutzer für TimingStudySystem zu bekommen.
     * Wird an die StudySystemLogic weitergegeben
     *
     * @param studySystem        Das StudySystem, das benötigt wird.
     * @param seconds            Antwortzeit vom Benutzer für die Frage
     * @param singleDataCallback wird verwendet, um mögliche Fehler abzufangen.
     */
    public void giveTime(StudySystem studySystem, float seconds, SingleDataCallback<Boolean> singleDataCallback) {
        threadPool.exec(() -> {
            try {
                studySystemLogic.giveTime(studySystem, seconds);
                if(singleDataCallback != null)
                    singleDataCallback.callSuccess(true);
            } catch (Exception e) {
                if(singleDataCallback != null)
                    singleDataCallback.callFailure(Locale.getCurrentLocale().getString("givetimeerror"));
                log.error(e.getMessage());
            }
        });
    }

    /**
     * Wird verwendet, um den Test zu beenden und Ergebnispunkt zu berechnen
     * wird an die StudySystemLogic weitergegeben
     *
     * @param studySystem        Das StudySystem, das benötigt wird.
     * @param singleDataCallback wird verwendet, um mögliche Fehler abzufangen.
     */
    public void finishTestAndGetResult(StudySystem studySystem, SingleDataCallback<Integer> singleDataCallback) {
        threadPool.exec(() -> {
            try {
                if(singleDataCallback != null)
                    singleDataCallback.callSuccess(studySystemLogic.finishTestAndGetResult(studySystem));
            } catch (Exception e) {
                if(singleDataCallback != null)
                    singleDataCallback.callFailure(Locale.getCurrentLocale().getString("finishtestandgetresulterror"));
                log.error(e.getMessage());
            }
        });
    }


    /**
     * Wird verwendet, um die nächste Frage zu bekommen,
     * wird an die StudySystemLogic weitergegeben
     *
     * @param studySystem        Das StudySystem, das benötigt wird.
     * @param singleDataCallback wird verwendet, um mögliche Fehler abzufangen.
     *                           //TODO  javadoc, ich würde sagen wir brauchen hier die Box noch, damit wir wissen, woraus wir die nächste Karte ziehen
     */
    public void getNextCard(StudySystem studySystem, SingleDataCallback<Card> singleDataCallback) {
        threadPool.exec(() -> {
            try {
                if(singleDataCallback != null)
                    singleDataCallback.callSuccess(studySystemLogic.getNextCard(studySystem));
            }catch (NoResultException e) {
                if(singleDataCallback != null)
                    singleDataCallback.callFailure(e.getMessage());
                log.error(e.getMessage());
            }
            catch (Exception e) {
                singleDataCallback.callFailure(Locale.getCurrentLocale().getString("getnextcarderror"));
                log.error(e.getMessage());
            }
        });
    }

    /**
     * Wird verwendet, um die Anzahl der Karten in einem studySystem zu bekommen. Wird an die StudySystemLogic weitergegeben
     *
     * @param studySystem         studySystem, um die Anzahl der Karten darin zu suchen
     * @param singleDataCallback  wird verwendet, um mögliche Fehler abzufangen.
     */
    public void numCardsInDeck(StudySystem studySystem, SingleDataCallback<Integer> singleDataCallback) {
        threadPool.exec(() -> {
            try {
                if(singleDataCallback != null)
                    singleDataCallback.callSuccess(studySystemLogic.numCardsInDeck(studySystem));
            } catch (Exception ex) {
                if(singleDataCallback != null)
                    singleDataCallback.callFailure(Locale.getCurrentLocale().getString("numcardsindeckerror"));
                log.error(ex.getMessage());
            }
        });
    }

    /**
     * Wird verwendet, um ein StudySystem nach UUID zu bekommen. Wird an die StudySystemLogic weitergegeben
     *
     * @param uuid   UUID zu suchen
     * @param singleDataCallback  wird verwendet, um mögliche Fehler abzufangen.
     */
    public void getStudySystemByUUID(String uuid, SingleDataCallback<StudySystem> singleDataCallback) {
        threadPool.exec(() -> {
            try {
                if(singleDataCallback != null)
                    singleDataCallback.callSuccess(studySystemLogic.getStudySystemByUUID(uuid));
            } catch (IllegalStateException ex) {//übergebener Wert ist leer
                log.error(Locale.getCurrentLocale().getString("getstudysystembyuuidempty"));
                if(singleDataCallback != null)
                    singleDataCallback.callFailure(ex.getMessage());
            } catch (NoResultException ex) {
                log.error(Locale.getCurrentLocale().getString("getstudysystembyuuidnoresult"));
                if(singleDataCallback != null)
                    singleDataCallback.onFailure(ex.getMessage());
            } catch (Exception ex) {
                if(singleDataCallback != null)
                    singleDataCallback.callFailure(Locale.getCurrentLocale().getString("getstudysystembyuuiderror"));
                log.error(ex.getMessage());
            }
        });
    }

    /**
     * Wird verwendet, um eine Liste von Karten in einem StudySystem zu löschen. Wird an die StudySystemLogic weitergegeben.
     *
     * @param singleDataCallback  wird verwendet, um mögliche Fehler abzufangen.
     * @param studySystem         Das StudySystem, das benötigt wird.
     * @param list                die Liste der Karten zu löschen
     */
    public void removeCardsFromStudySystem(List<CardOverview> list, StudySystem studySystem, SingleDataCallback<Boolean> singleDataCallback) {
        threadPool.exec(() -> {
            try {
                studySystemLogic.removeCardsFromStudySystem(list, studySystem);
                if(singleDataCallback != null)
                    singleDataCallback.callSuccess(true);
            } catch (IllegalStateException ex) {
                log.error("Null Variable gegeben");
                if(singleDataCallback != null)
                    singleDataCallback.onFailure(ex.getMessage());
            } catch (Exception ex) {
                if(singleDataCallback != null)
                    singleDataCallback.callFailure(Locale.getCurrentLocale().getString("removecardsfromstudysystemerror"));
                log.error(ex.getMessage());
            }
        });
    }


    /**
     * Wird verwendet, um ein StudySystem zu löschen. Wird an die StudySystemLogic weitergegeben.
     *
     * @param singleDataCallback  wird verwendet, um mögliche Fehler abzufangen.
     * @param studySystem   StudySystem zu löschen.
     */
    public void deleteStudySystem(StudySystem studySystem, SingleDataCallback<Boolean> singleDataCallback) {
        threadPool.exec(() -> {
            try {
                studySystemLogic.deleteStudySystem(studySystem);
                if(singleDataCallback != null)
                    singleDataCallback.callSuccess(true);
            } catch (IllegalStateException ex) {
                if(singleDataCallback != null)
                    singleDataCallback.callFailure(ex.getMessage());
                log.error("Null Variable gegeben");
            } catch (Exception ex) {
                if(singleDataCallback != null)
                    singleDataCallback.callFailure(Locale.getCurrentLocale().getString("deletestudysystemerror"));
                log.error(ex.getMessage());
            }
        });
    }


    /**
     * Wird verwendet, um eine Liste von studySystems zu löschen. Wird an die StudySystemLogic weitergegeben.
     *
     * @param singleDataCallback  wird verwendet, um mögliche Fehler abzufangen.
     */
    public void deleteDecks(StudySystem[] studySystems, SingleDataCallback<Boolean> singleDataCallback) {
        threadPool.exec(() -> {
            try {
                studySystemLogic.deleteStudySystem(studySystems);
                if(singleDataCallback != null)
                    singleDataCallback.callSuccess(true);
            } catch (Exception ex) {
                if(singleDataCallback != null)
                    singleDataCallback.callFailure(Locale.getCurrentLocale().getString("deletedeckserror"));
                log.error(ex.getMessage());
            }
        });
    }

    /**
     * Wird verwendet, um StudySystem zu bekommen
     *
     * @param dataCallback  wird verwendet, um mögliche Fehler abzufangen.
     */
    public void getStudySystems(DataCallback<StudySystem> dataCallback) {
        threadPool.exec(() -> {
            try {
                List<StudySystem> studySystems = studySystemLogic.getStudySystems();
                if (studySystems.isEmpty()) {
                    log.info("Es gibt noch keine Studysystems gefunden");
                }
                if(dataCallback != null)
                    dataCallback.callSuccess(studySystems);
            }
            catch (Exception ex) {
                if(dataCallback != null)
                    dataCallback.callFailure(Locale.getCurrentLocale().getString("getstudysystemserror"));
                log.error(ex.getMessage());
            }
        });
    }

    /**
     * Wird verwendet, um eine Suche für StudySystem nach einem Suchbegriff machen.
     *
     * @param searchterm   Suchbegriff um nachzusuchen.
     * @param dataCallback  wird verwendet, um mögliche Fehler abzufangen.
     */
    public void getStudySystemBySearchTerms(String searchterm, DataCallback<StudySystem> dataCallback) {
        threadPool.exec(() -> {
            try {
                List<StudySystem> studySystems = studySystemLogic.getStudySystemsBySearchterm(searchterm);
                if (studySystems.isEmpty()) {
                    log.info("Es wurden keine zugehörigen StudySystems für den Suchterm gefunden gefunden");
                    if(dataCallback != null)
                        dataCallback.callInfo(Locale.getCurrentLocale().getString("getstudysystembysearchtermsempty"));
                }
                if(dataCallback != null)
                    dataCallback.callSuccess(studySystems);
            }
            catch (IllegalArgumentException ex){
                if(dataCallback != null)
                    dataCallback.callFailure(ex.getMessage());
                log.error(ex.getMessage());
            }
            catch (Exception ex) {
                if(dataCallback != null)
                    dataCallback.callFailure(Locale.getCurrentLocale().getString("getstudysystembysearchtermserror"));
                log.error(ex.getMessage());
            }
        });
    }


    /**
     * Für nachträgliches Hinzufügen von Karten. Wird an die StudySystemLogic weitergegeben
     *
     * @param cards                 die Liste von Karten, um hinzufügen
     * @param studySystem           Das StudySystem, das benötigt wird.
     * @param singleDataCallback    wird verwendet, um mögliche Fehler abzufangen. 
     */
    public void addCardsToStudySystem(List<CardOverview> cards, StudySystem studySystem, SingleDataCallback<String> singleDataCallback) {
        threadPool.exec(() -> {
            try {
                List<Card> existingCards = studySystemLogic.addCardsToDeck(cards, studySystem);
                if(!existingCards.isEmpty()){
                    String result = existingCards.stream().map(Card::getTitle)
                            .collect(Collectors.joining(","));
                    if(singleDataCallback != null)
                        singleDataCallback.callSuccess("Karten bereits in Deck enthalten, nicht hinzugefügt:" + result);
                }
                else {
                    if(singleDataCallback != null)
                        singleDataCallback.callSuccess("");
                }
            } catch (Exception ex) {
                log.error(ex.getMessage());
                if(singleDataCallback != null)
                    singleDataCallback.callFailure(Locale.getCurrentLocale().getString("addcardstostudysystemerror"));
            }
        });
    }

    /**
     * Wird verwendet, um das StudySystem zu updaten. Wird an die StudySystemLogic weitergegeben.
     *
     * @param oldStudySystem     StudySystem im vorherigen Zustand, benötigt, um festzustellen, ob das StudySystem gewechselt wurde und Handling
     * @param newStudySystem     Neue StudySystem Eigenschaften
     * @param neu                Ist true, wenn das StudySystem neu angelegt wurde
     * @param singleDataCallback wird verwendet, um mögliche Fehler abzufangen.
     */
    public void updateStudySystemData(StudySystem oldStudySystem, StudySystem newStudySystem, boolean neu, SingleDataCallback<Boolean> singleDataCallback) 
    {
        threadPool.exec(() -> {
            try {
                studySystemLogic.updateStudySystemData(oldStudySystem, newStudySystem, neu);
                if(singleDataCallback != null)
                    singleDataCallback.callSuccess(true);
            } catch (Exception ex) {
                if(singleDataCallback != null)
                    singleDataCallback.callFailure(Locale.getCurrentLocale().getString("updatedeckdataerror"));
                log.error(ex.getMessage());
            }
        });
    }

    /**
     * Mithilfe der Methode werden alle Karten aus dem StudySystem zurück auf "New" gesetzt und der Progress zurückgesetzt.
     * Sollten sich Karten in Box 1 (bei Timing/Vote) oder 2-n befinden (bei Leitner) werden diese zurück in Box 0 verschoben
     *
     * @param studySystem Das zurückzusetzende StudySystem
     * @param singleDataCallback wird verwendet, um mögliche Fehler abzufangen.
     */
    public void resetLearnStatus(StudySystem studySystem, SingleDataCallback<Boolean> singleDataCallback) {
        threadPool.exec(() -> {
            try {
               //TODO
            } catch (Exception ex) {
                singleDataCallback.callFailure(Locale.getCurrentLocale().getString("TODO"));
                log.error(ex.getMessage());
            }
        });
    }



}

