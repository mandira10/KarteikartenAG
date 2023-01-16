package com.swp.DataModel.StudySystem;

import com.swp.DataModel.Card;
import com.swp.DataModel.Deck;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import java.awt.event.MouseListener;
import java.util.*;

/**
 * Klasse für das VoteSystem. Erbt alle Attribute vom StudySystem
 */
@Entity
@DiscriminatorValue("Vote")
public class VoteSystem extends StudySystem implements MouseListener
{
    int questionCount = 0;
    int trueAnswerCount = 0;
    int pointQuestion = 100;
    int resultPoint = 0;
    HashMap<Card, Integer> unsortedCards = new HashMap<>();
    ArrayList<Card> sortedCards = new ArrayList<>();

    /**
     * Konstruktor der Klasse VoteSystem.
     * @param deck: Das Deck für das Lernsystem
     */
    public VoteSystem(Deck deck)
    {
        super(deck, new StudySystemType(StudySystemType.KNOWN_TYPES.VOTE), 5);

    }

    @Override
    public void giveAnswer(boolean answer) {
        if(answer) {
            trueAnswerCount++;
        }
    }

    @Override
    public void giveRating(int rating) {
        unsortedCards.put(getAllCardsInStudySystem().get(questionCount),rating);
    }

    @Override
    public void finishTest() {
        if(questionCount++ == getAllCardsInStudySystem().size()){
            sortByValue(unsortedCards);
            // Deck Update by rating answers
            if(trueAnswerCount == 0){
                resultPoint = 0;
            }
            else{
                pointQuestion = pointQuestion / getAllCardsInStudySystem().size();
                resultPoint = pointQuestion * trueAnswerCount;
            }
        }
    }

    public  void sortByValue(HashMap<Card, Integer> hm)
    {

        List<Map.Entry<Card, Integer> > list = new LinkedList<Map.Entry<Card, Integer> >(hm.entrySet());


        Collections.sort(list, Map.Entry.comparingByValue());


        for (Map.Entry<Card, Integer> aa : list) {
            sortedCards.add(aa.getKey());
        }

    }

    @Override
    public int getResult() {
        return resultPoint;
    }

    @Override
    public Card getNextCard(int index) {
        if(questionCount++ == getAllCardsInStudySystem().size()){
            return null;
        }
        else{
            questionCount++;
            return getNextCard(questionCount);
        }
    }

    @Override
    public float getProgress() {
        if(getAllCardsInStudySystem().size() == 0){
            return 0;
        }
        else{
            return trueAnswerCount / getAllCardsInStudySystem().size();
        }
    }
}