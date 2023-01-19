package com.swp.Controller;

import com.swp.DataModel.Card;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.DataModel.StudySystem.StudySystemBox;
import com.swp.Logic.StudySystemLogic;
import lombok.extern.slf4j.Slf4j;


import java.util.List;
import java.util.Set;

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
     */
    public void moveCardToBox(Card card, StudySystemBox box, SingleDataCallback<Boolean> singleDataCallback)
    {
        try {
            studySystemLogic.moveCardToBox(card,box);
        }
        catch (Exception exception){
            singleDataCallback.onFailure(exception.getMessage());
        }
    }

    public void moveAllCardsForDeckToFirstBox(List<Card> cards,StudySystemBox box, SingleDataCallback<Boolean> singleDataCallback) {

        try{
            if(cards.isEmpty()){
                singleDataCallback.onFailure("Es gibt keine Karten");
            }
            else{
                studySystemLogic.moveAllCardsForDeckToBox(cards,box);
            }
        }
        catch (Exception e){
            singleDataCallback.onFailure(e.getMessage());
        }
    }

    public void getAllCardsInStudySystem(StudySystem studySystem, DataCallback<Card> dataCallback) {

        try {
            List<Card> cards = studySystemLogic.getAllCardsInStudySystem(studySystem);

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
     */
    public void giveAnswer(StudySystem studySystem,boolean answer,SingleDataCallback singleDataCallback) {
        try{
            studySystemLogic.giveAnswer(studySystem,answer);
        }
        catch (Exception e){
            singleDataCallback.onFailure(e.getMessage());
        }
    }

    //TO IMPLEMENT
    public void giveRating(StudySystem studySystem,int rating,SingleDataCallback singleDataCallback) {
        try{
            studySystemLogic.giveRating(studySystem,rating);
        }
        catch (Exception e){
            singleDataCallback.onFailure(e.getMessage());
        }
    };

    //TO IMPLEMENT
    public void giveTime(StudySystem studySystem,float seconds,SingleDataCallback singleDataCallback) {
        try{
            studySystemLogic.giveTime(studySystem,seconds);
        }
        catch (Exception e){
            singleDataCallback.onFailure(e.getMessage());
        }
    };

    //TO IMPLEMENT (is also called when the test has been canceled)
    public void finishTest(StudySystem studySystem,SingleDataCallback singleDataCallback) {
        try{
            studySystemLogic.finishTest(studySystem);
        }
        catch (Exception e){
            singleDataCallback.onFailure(e.getMessage());
        }
    }

    //TO IMPLEMENT (returns final score calculated in finishTest)
    public void getResult(StudySystem studySystem, SingleDataCallback<Integer> singleDataCallback) {
        try{
            singleDataCallback.onSuccess(studySystemLogic.getResult( studySystem));
        }catch (Exception ex){
            singleDataCallback.onFailure("");
        }
        }

    /**
     * Gibt die nächste Karte zum Lernen zurück
     * @return Karte die als nächstes gelernt werden soll
     */
    public void getNextCard(StudySystem studySystem, SingleDataCallback singleDataCallback)
    {
        try{
            Card card = studySystemLogic.getNextCard(studySystem);
            singleDataCallback.onSuccess(card);
        }
        catch (Exception e){
            singleDataCallback.onFailure(e.getMessage());
        }
    }

    //NEEDS TO BE IMPLEMENTED
    public void getProgress(StudySystem studySystem,SingleDataCallback singleDataCallback)
    {
        try{
            singleDataCallback.onSuccess(studySystemLogic.getProgress(studySystem));
        }
        catch (Exception e){
            singleDataCallback.onFailure(e.getMessage());
        }
    }
}

