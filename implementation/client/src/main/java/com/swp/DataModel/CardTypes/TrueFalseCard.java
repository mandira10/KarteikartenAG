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
     * @param question: Frage der Karte
     * @param answer: Antwort der Karte
     * @param visible: Sichtbarkeit der Karte
     */
    public TrueFalseCard(String question, boolean answer, String title, boolean visible)
    {
        super(CardType.TRUEFALSE);
        setTitle(title);
        sQuestion = question;
        bAnswer = answer;
        bVisibility = visible;
        sContent = getContent();
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

    @Override
    public String getContent(){
        StringBuilder string = new StringBuilder();
        return string.append(sTitle+" "+sQuestion).toString();
    }
}
