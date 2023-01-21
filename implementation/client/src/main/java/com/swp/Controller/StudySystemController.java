package com.swp.Controller;

import com.swp.DataModel.Card;
import com.swp.DataModel.CardOverview;
import com.swp.DataModel.Category;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.DataModel.StudySystem.StudySystemBox;
import com.swp.Logic.StudySystemLogic;
import jakarta.persistence.NoResultException;
import lombok.extern.slf4j.Slf4j;


import java.util.ArrayList;
import java.util.List;

@Slf4j
public class StudySystemController{

    private static StudySystemController studySystemController;
    public static StudySystemController getInstance() {
        if (studySystemController == null)
            studySystemController = new StudySystemController();
        return studySystemController;
    }
    StudySystemLogic studySystemLogic = StudySystemLogic.getInstance();

    /**
     * Verschiebt spezifische Karte in eine Box des StudySystems
     * @param card: Zu verschiebene Karte
     * @param box: Index der Box, in den die Karte verschoben werden soll
     * @TODO: MERT
     */
    public void moveCardToSpecificBox(Card card, int box, StudySystem studySystem, SingleDataCallback<Boolean> singleDataCallback)
    {
        try {
            studySystemLogic.moveCardToBox(card,box, studySystem);
        }
        catch (Exception exception){
            singleDataCallback.onFailure(exception.getMessage());
        }
    }

    /**
     * Used after creation of a new StudySystemDeck, moves all cards for the studySystem to the first box.
     * @param cards Karten, die StudySystem enthalten soll.
     * @param studySystem Das StudySystem, das benötigt wird.
     * @param singleDataCallback wird verwendet, um mögliche Fehler abzufangen.
     */
    public void moveAllCardsForDeckToFirstBox(List<Card> cards,StudySystem studySystem, SingleDataCallback<Boolean> singleDataCallback) {

        try{
            if(cards.isEmpty()){
                singleDataCallback.onFailure("Es gibt keine Karten");
            }
            else{
                studySystemLogic.moveAllCardsForDeckToFirstBox(cards,studySystem);
            }
        }
        catch (Exception e){
            singleDataCallback.onFailure(e.getMessage());
        }
    }


    /**
     * @TODO: MERT
     * @param studySystem
     * @param dataCallback
     */
    public void getAllCardsInStudySystem(StudySystem studySystem, DataCallback<CardOverview> dataCallback) {

        try {
            List<CardOverview> cards = studySystemLogic.getAllCardsInStudySystem(studySystem);

            if (cards.isEmpty()) {
                dataCallback.onInfo("Es gibt keine Karten zu diesem StudySystem");
            }
            else {
                dataCallback.onSuccess(cards);
            }
        }
        catch(Exception ex){
            dataCallback.onFailure(ex.getMessage());
        }
    }

    /**
     * Nach Beantwortung einer Frage wird die Antwort übergeben, so dass
     * je nach Antwort die Karte in den Boxen verschoben werden kann
     * @param answer: Frage war richtig / falsch beantwortet
     * @TODO: MERT
     */
    public void giveAnswer(StudySystem studySystem,boolean answer, SingleDataCallback singleDataCallback) {
        try{
            studySystemLogic.giveAnswer(studySystem,answer);
        }
        catch (Exception e){
            singleDataCallback.onFailure(e.getMessage());
        }
    }

    /**
     * @TODO: MERT
     * @param studySystem
     * @param rating
     * @param singleDataCallback
     */
    public void giveRating(StudySystem studySystem,int rating,SingleDataCallback singleDataCallback) {
        try{
            studySystemLogic.giveRating(studySystem,rating);
        }
        catch (Exception e){
            singleDataCallback.onFailure(e.getMessage());
        }
    };

    /**
     * @TODO: MERT
     * @param studySystem
     * @param seconds
     * @param singleDataCallback
     */
    public void giveTime(StudySystem studySystem,float seconds,SingleDataCallback singleDataCallback) {
        try{
            studySystemLogic.giveTime(studySystem,seconds);
        }
        catch (Exception e){
            singleDataCallback.onFailure(e.getMessage());
        }
    };

    /**
     * @TODO: MERT
     * @param studySystem
     * @param singleDataCallback
     */
    public void finishTestAndGetResult(StudySystem studySystem,SingleDataCallback<Integer> singleDataCallback) {
        try{
            singleDataCallback.onSuccess(studySystemLogic.finishTestAndGetResult(studySystem));
        }
        catch (Exception e){
            singleDataCallback.onFailure(e.getMessage());
        }
    }


    /**
     * @TODO: MERT
     * Gibt die nächste Karte zum Lernen zurück
     * @return Karte die als nächstes gelernt werden soll
     * //TODO: javadoc, ich würde sagen wir brauchen hier die Box noch, damit wir wissen, woraus wir die nächste Karte ziehen
     */
    public void getNextCard(StudySystem studySystem, SingleDataCallback<Card> singleDataCallback)
    {
        try{
            singleDataCallback.onSuccess(studySystemLogic.getNextCard(studySystem));
        }
        catch (Exception e){
            singleDataCallback.onFailure(e.getMessage());
        }
    }

    /**
     * @TODO: MERT
     * @param studySystem
     * @param singleDataCallback
     */
    public void getProgress(StudySystem studySystem,SingleDataCallback<Float> singleDataCallback)
    {
        try{
            singleDataCallback.onSuccess(studySystemLogic.getProgress(studySystem));
        }
        catch (Exception e){
            singleDataCallback.onFailure(e.getMessage());
        }
    }

    /**
     * Wird verwendet, um die Anzahl der Karten in einem studySystem zu bekommen. Wird an die StudySystemLogic weitergegeben
     * @param studySystem: studySystem, um die Anzahl der Karten darin zu suchen
     */
    public void numCardsInDeck(StudySystem studySystem, SingleDataCallback<Integer> singleDataCallback) {
        try{
            singleDataCallback.onSuccess(studySystemLogic.numCardsInDeck(studySystem));
        }
        catch(Exception ex){
            log.error("Beim Abrufen von der Anzahl der studySystems ist ein Fehler aufgetreten");
        }
    }

    /**
     * Wird verwendet, um ein StudySystem nach UUID zu bekommen. Wird an die StudySystemLogic weitergegeben
     * @param uuid: UUID zu suchen
     */
    public void getStudySystemByUUID(String uuid, SingleDataCallback<StudySystem> singleDataCallback) {
        try{
            singleDataCallback.onSuccess(studySystemLogic.getStudySystemByUUID(uuid));
        }
        catch (IllegalArgumentException ex) {//übergebener Wert ist leer
            singleDataCallback.onFailure(ex.getMessage());
        }
        catch(NoResultException ex){
            singleDataCallback.onFailure("Es konnte kein studySystem zur UUID gefunden werden");
        }
        catch(Exception ex){
            singleDataCallback.onFailure("Beim Abrufen des studySystems ist ein Fehler aufgetreten");
        }
    }

    //TODO DOC
    public void removeCardsFromStudySystem(ArrayList<Card> selection, StudySystem pDeck, SingleDataCallback<Boolean> singleDataCallback) {
    }

    
    //TODO DOC Wird verwendet, um ein StudySystem zu löschen. Wird an die ... weitergegeben.
    public void deleteStudySystem(StudySystem studySystem, SingleDataCallback<Boolean> singleDataCallback) {
        try{studySystemLogic.deleteStudySystem(studySystem);}
        catch(IllegalStateException ex){
            singleDataCallback.onFailure(ex.getMessage());
        }
        catch(Exception ex){
            singleDataCallback.onFailure(ex.getMessage());
        }
    }


    /**
     * Wird verwendet, um eine Liste von studySystems zu löschen. Wird an die StudySystemLogic weitergegeben.
     * @param studySystems: die Liste der zu löschenden StudySystems
     */
    public void deleteDecks(StudySystem[] studySystems, SingleDataCallback<Boolean> singleDataCallback) {

        try{studySystemLogic.deleteStudySystem(studySystems);}
        catch(Exception ex){
            singleDataCallback.onFailure(ex.getMessage());
        }
    }

    /**
     * TODO MERT
     * @param dataCallback
     */
    public void getStudySystems(DataCallback<StudySystem> dataCallback) {

        try {
            List<StudySystem> studySystems = studySystemLogic.getStudySystems();
            if (studySystems.isEmpty()) {
                dataCallback.onInfo("Es gibt derzeit studySystems");
            }

            dataCallback.onSuccess(studySystems);
        }
        catch(Exception ex){
            dataCallback.onFailure(ex.getMessage());
        }
    }

    /**
     * TODO MERT
     * @param searchterm
     * @param dataCallback
     */
    public void getStudySystemBySearchTerms(String searchterm, DataCallback<StudySystem> dataCallback) {
        try{
            List<StudySystem> studySystems = studySystemLogic.getStudySystemsBySearchterm(searchterm);
            if(studySystems.isEmpty()){
                dataCallback.onInfo("Es gibt keine studySystems zu diesem Suchbegriff");
            }
            dataCallback.onSuccess(studySystems);
        }
        catch(IllegalArgumentException ex){
            dataCallback.onFailure(ex.getMessage());
        }
        catch(Exception ex){
            dataCallback.onFailure(ex.getMessage());
        }
    }


    /**
     * Wird benutzt, um einzelne Karten aus dem studySystem zu löschen. Wird an die StudySystemLogic weitergegeben
     * @param cards
     * @param studySystem
     */
    public void removeCardsFromStudySystem(List<Card> cards, StudySystem studySystem, SingleDataCallback<Boolean> singleDataCallback) {
        try {
            studySystemLogic.removeCardsFromStudySystem(cards, studySystem);
        }
        catch(Exception ex){
            singleDataCallback.onFailure(ex.getMessage());
        }
    }


    /**
     * Für nachträgliches Hinzufügen von Karten. Wird an die StudySystemLogic weitergegeben
     * @param cards
     * @param studySystem
     */
    public void addCardsToStudySystem(List<Card> cards, StudySystem studySystem, SingleDataCallback<Boolean> singleDataCallback){
        try{studySystemLogic.addCardsToDeck(cards,studySystem);}
        catch(Exception ex){
            singleDataCallback.onFailure(ex.getMessage());
        }

    }

    /**
     * Wird verwendet, um das StudySystem zu updaten. Wird an die StudySystemLogic weitergegeben.
     * @param oldStudySystem StudySystem im vorherigen Zustand, benötigt, um festzustellen, ob das StudySystem gewechselt wurde und Handling
     * @param newStudySystem Neue StudySystem Eigenschaften
     * @param neu Ist true, wenn das StudySystem neu angelegt wurde
     * //TODO MERT
     */
    public void updateDeckData(StudySystem oldStudySystem, StudySystem newStudySystem, boolean neu, SingleDataCallback<Boolean> singleDataCallback) {
        try{studySystemLogic.updateStudySystemData(oldStudySystem, newStudySystem,neu);}
        catch(Exception ex){
            singleDataCallback.onFailure(ex.getMessage());
        }
    }

    /**
     * Wird verwendet, um ein komplett neues StudySystem anzulegen. Wird an die StudySystemLogic weitergegeben.
     * @param type Typ des StudySystems
     * @return true, wenn erfolgreich
     */
    public void addStudySystemTypeAndUpdate(StudySystem.StudySystemType type, SingleDataCallback<Boolean> singleDataCallback) {
        try{studySystemLogic.addStudySystemTypeAndUpdate(type);}
        catch(Exception ex){
            singleDataCallback.onFailure(ex.getMessage());
        }
    }


    /**
     * Wird verwendet, um eine Karte in einem studySystem mit einer bestimmten Kategorie zu erstellen. Wird an die StudySystemLogic weitergegeben
     * @param category: die Kategorie für die Karte
     * @param studySystem: studySystem, um die Karte darin zu speichern
     */
    public void createBoxToCardForCategory(Category category, StudySystem studySystem, SingleDataCallback<Boolean> singleDataCallback) {
        try{studySystemLogic.createBoxToCardForCategory(category, studySystem);}
        catch(Exception ex){
            singleDataCallback.onFailure(ex.getMessage());
        }
    }

//    /**
//     * Wird verwendet, um Karten in einem studySystem zu bekommen. Wird an die StudySystemLogic weitergegeben
//     * @param studySystem: studySystem, um Karten darin zu suchen
//     */
//    public void getCardsInDeck(studySystem studySystem,DataCallback<Card> dataCallback) {
//        try{
//            List<Card> cards = StudySystemLogic.getCardsByDeck(studySystem);
//            if(cards.isEmpty()){
//                dataCallback.onInfo("Es gibt keine Karten für dieses studySystem");
//            }
//            dataCallback.onSuccess(cards);
//        }
//        catch(Exception ex){
//            dataCallback.onFailure(ex.getMessage());
//        }
//    }
    

}

