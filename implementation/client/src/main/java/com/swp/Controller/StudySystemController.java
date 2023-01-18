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
        studySystemLogic.moveCardToBox(studySystem,card,boxindex);
        studySystem.setPointQuestion();
    }

    public void moveAllCardsForDeckToFirstBox(List<Card> cards) {

    }

    public void getAllCardsInStudySystem(DataCallback<Card> dataCallback) {

        try {
            List<Card> cards = studySystemLogic.getAllCardsInStudySystem();

            if (cards.isEmpty()) {
                dataCallback.onInfo("Es gibt keine Karten zu diesem StudySystem");

                dataCallback.onSuccess(cards);
            }
        }
        catch(Exception ex){

        }
    }

    /**
     * Nach Beantwortung einer Frage wird die Antwort übergeben, so dass
     * je nach Antwort die Karte in den Boxen verschoben werden kann
     * @param answer: Frage war richtig / falsch beantwortet
     */
    public void giveAnswer(boolean answer) { }

    //TO IMPLEMENT
    public void giveRating(int rating) { };

    //TO IMPLEMENT
    public void giveTime(float seconds) { };

    //TO IMPLEMENT (is also called when the test has been canceled)
    public void finishTest() {}

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
    public Card getNextCard(int index)
    {

    }

    //NEEDS TO BE IMPLEMENTED
    public float getProgress()
    {
        return (float)Math.random();  //Should return percentage as: 0.0 ... 1.0
    }
}
}
