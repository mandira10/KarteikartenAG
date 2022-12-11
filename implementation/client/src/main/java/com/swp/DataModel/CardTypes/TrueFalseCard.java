package com.swp.DataModel.CardTypes;

import com.swp.DataModel.Card;

/**
 * Klasse zum Erstellen von Karteikarten des Typs TrueFalseCard
 * Erbt die Grundeigenschaften der Klasse Card
 */
public class TrueFalseCard extends Card 
{
    /**
     * Frage der Karte
     */
    private String sQuestion;

    /**
     * Antwort der Karte
     */
    private boolean bAnswer;

    /**
     * Leerer Konstruktor der Klasse TrueFalseCard
     */
    public TrueFalseCard()
    {
        super(CardType.TRUEFALSE);
        setTitle("TrueFalseCard");
    }

    /**
     * Konstruktor der Klasse TrueFalseCard
     * @param question
     * @param answer
     * @param visible
     */
    public TrueFalseCard(String question, boolean answer, boolean visible)
    {
        super(CardType.TRUEFALSE);
        setTitle("TrueFalseCard");
        sQuestion = question;
        bAnswer = answer;
        bVisibility = visible;
    }

    public String getQuestion() {
        return sQuestion;
    }

    public boolean isAnswer() {
        return bAnswer;
    }
    public void setQuestion(String sQuestion) {
        this.sQuestion = sQuestion;
    }

    public void setAnswer(boolean bAnswer) {
        this.bAnswer = bAnswer;
    }
}
