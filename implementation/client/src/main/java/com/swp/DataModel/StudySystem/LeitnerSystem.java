package com.swp.DataModel.StudySystem;


import com.swp.DataModel.Card;
import com.swp.DataModel.Deck;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Klasse für das LeitnerSystem. Erbt alle Attribute vom StudySystem
 */
@Entity
@DiscriminatorValue("Leitner")
@NoArgsConstructor
@Getter
@Setter
public class LeitnerSystem extends StudySystem
{

        /**
         * Konstruktor der Klasse LeitnerSystem.
         *
         * TODO
         */
        public LeitnerSystem(String name, CardOrder cardOrder, StudySystemType type, int nboxes, boolean visibility)
        {super(name,cardOrder,type,nboxes,visibility);
//                super(deck, StudySystemType.LEITNER, 5);
                 }

                 public LeitnerSystem(LeitnerSystem other){
                 super(other);
                 }
//
//        @Override
//        public void giveAnswer(boolean answer) {
//                if(answer){
//                        trueAnswerCount++;
//                        questionCount++;
//                }
//                else{
//                        questionCount = 0;
//                }
//        }
//
//        @Override
//        public Card getNextCard(int index) {
//                return getAllCardsInStudySystem().stream().toList().get(questionCount);
//                // sollte nicht immer die vorderste Karte `.get(0)` genommen werden
//                // und je nach Antwort wird sie eine Box weitere nach vorne/hinter geschoben
//        }
//
//        @Override
//        public void finishTest() {
//                if(questionCount++ == getAllCardsInStudySystem().size()) {
//                        if (trueAnswerCount == 0) {
//                                resultPoint = 0;
//                        } else {
//                                pointQuestion = pointQuestion / getAllCardsInStudySystem().size();
//                                resultPoint = pointQuestion * trueAnswerCount;
//                        }
//                }
//        }
//
//        @Override
//        public int getResult() {
//                return resultPoint;
//        }
//
//        @Override
//        public float getProgress() {
//                if(getAllCardsInStudySystem().size() == 0){
//                        return 0;
//                }
//                else{
//                        return trueAnswerCount / getAllCardsInStudySystem().size();
//                }
//
//        }
}
