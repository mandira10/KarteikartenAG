package com.swp.Controller;

import com.swp.DataModel.Card;
import com.swp.DataModel.StudySystem.StudySystem;
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
     * @param boxindex: Index der Box, in den die Karte verschoben werden soll
     */
    public void moveCardToBox(StudySystem studySystem, Card card, int boxindex, SingleDataCallback<Boolean> singleDataCallback)
    {
        try {
            studySystemLogic.moveCardToBox(studySystem,card,boxindex);
        }
        catch (Exception exception){
            singleDataCallback.onFailure(exception.getMessage());
        }
    }

    public void moveAllCardsForDeckToFirstBox(List<Card> cards, SingleDataCallback<Boolean> singleDataCallback) {

        try{
            if(cards.isEmpty()){
                singleDataCallback.onFailure("Es gibt keine Karten");
            }
            else{
                studySystemLogic.moveAllCardsForDeckToFirstBox(cards);
            }
        }
        catch (Exception e){
            singleDataCallback.onFailure(e.getMessage());
        }
    }

    public void getAllCardsInStudySystem(DataCallback<Card> dataCallback) {

        try {
            List<Card> cards = studySystemLogic.getAllCardsInStudySystem();

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
    public void giveAnswer(boolean answer,SingleDataCallback singleDataCallback) {
        try{
            studySystemLogic.giveAnswer(answer);
        }
        catch (Exception e){
            singleDataCallback.onFailure(e.getMessage());
        }
    }

    //TO IMPLEMENT
    public void giveRating(int rating,SingleDataCallback singleDataCallback) {
        try{
            studySystemLogic.giveRating(rating);
        }
        catch (Exception e){
            singleDataCallback.onFailure(e.getMessage());
        }
    };

    //TO IMPLEMENT
    public void giveTime(float seconds,SingleDataCallback singleDataCallback) {
        try{
            studySystemLogic.giveTime(seconds);
        }
        catch (Exception e){
            singleDataCallback.onFailure(e.getMessage());
        }
    };

    //TO IMPLEMENT (is also called when the test has been canceled)
    public void finishTest(SingleDataCallback singleDataCallback) {
        try{
            studySystemLogic.finishTest();
        }
        catch (Exception e){
            singleDataCallback.onFailure(e.getMessage());
        }
    }

    //TO IMPLEMENT (returns final score calculated in finishTest)
    public void getResult(SingleDataCallback<Integer> singleDataCallback) {
        try{
            singleDataCallback.onSuccess(studySystemLogic.getResult());
        }catch (Exception ex){
            singleDataCallback.onFailure("");
        }
        }

    /**
     * Gibt die nächste Karte zum Lernen zurück
     * @return Karte die als nächstes gelernt werden soll
     */
    public void getNextCard(int index,SingleDataCallback singleDataCallback)
    {
        try{
            Card card = studySystemLogic.getNextCard(index);
            singleDataCallback.onSuccess(card);
        }
        catch (Exception e){
            singleDataCallback.onFailure(e.getMessage());
        }
    }

    //NEEDS TO BE IMPLEMENTED
    public void getProgress(SingleDataCallback singleDataCallback)
    {
        try{
            singleDataCallback.onSuccess(studySystemLogic.getProgress());
        }
        catch (Exception e){
            singleDataCallback.onFailure(e.getMessage());
        }
    }
}

