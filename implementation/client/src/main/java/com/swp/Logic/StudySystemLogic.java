package com.swp.Logic;

import com.swp.DataModel.Card;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.DataModel.StudySystem.StudySystemBox;
import com.swp.Persistence.StudySystemRepository;
import jakarta.persistence.NoResultException;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

public class StudySystemLogic extends BaseLogic<StudySystem>{


    public StudySystemLogic() {
        super(StudySystemRepository.getInstance());
    }

    private static StudySystemLogic studySystemLogic;
    public static StudySystemLogic getInstance() {
        if (studySystemLogic == null)
            studySystemLogic = new StudySystemLogic();
        return studySystemLogic;
    }

    private final StudySystemRepository studySystemRepository = StudySystemRepository.getInstance();

    /**
     * Verschiebt spezifische Karte in eine Box des StudySystems
     *
     * @param studySystem
     * @param card        : Zu verschiebene Karte
     * @param boxindex    : Index der Box, in den die Karte verschoben werden soll
     */
    public void moveCardToBox(StudySystem studySystem, Card card, int boxindex)
    {
        //studySystem.getType()
               // moveCardToBox


//        if(boxindex >= boxes.size() || boxindex < 0)
//            return;
//
//        for(StudySystemBox box : boxes)
//        {
//            if(box.getBoxContent().contains(card))
//                box.remove(card);
//        }
//
//        boxes.get(boxindex).add(card);
    }

    public void moveAllCardsForDeckToFirstBox(List<Card> cards) {

        //boxes.get(0).add(cards);
    }

    public List<Card> getAllCardsInStudySystem() {
//        List<Card> cardsInStudyS = new ArrayList<>();
//
//        for(StudySystemBox box : boxes){
//            cardsInStudyS.addAll(box.getBoxContent());
//        }
//        return cardsInStudyS;
        return null;
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
    public int getResult() {
    //    studySystemRepository.getResultFor();
        return  0;
    }

    /**
     * Gibt die nächste Karte zum Lernen zurück
     * @return Karte die als nächstes gelernt werden soll
     */
    public Card getNextCard(int index){return null;}
//    {
//        for (StudySystemBox box : boxes) {
//            if (!box.getBoxContent().isEmpty()){
//                return box.getBoxContent().iterator().next();
//            }
//        }
//        throw new NoResultException("No Cards in StudySystem");
//    }

    //NEEDS TO BE IMPLEMENTED
    public float getProgress()
    {
        return (float)Math.random();  //Should return percentage as: 0.0 ... 1.0
    }
}

