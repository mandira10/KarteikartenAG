package com.swp.DataModel.CardTypes;

import com.swp.DataModel.Card;


/**
 * Klasse zum Erstellen von Karteikarten des Typs TextCard
 * Erbt die Grundeigenschaften der Klasse Card
 */
public class TextCard extends Card 
{
    /**
     * Frage der Karte
     */
    private String sQuestion;

    /**
     * Antwort der Karte
     */
    private String sAnswer;

    /**
     * Leerer Konstruktor der Klasse TextCard
     */
    public TextCard()
    {
        super(CardType.TEXT);
        setTitle("TextCard");
    }

    /**
     * Konstruktor der Klasse TextCard
     * @param question Frage der Karte
     * @param answer Antwort der Karte
     */
    public TextCard(String question, String answer, boolean visible)
    {
        super(CardType.TEXT);
        setTitle("TextCard");
        sQuestion = question;
        sAnswer = answer;
        bVisibility = visible;
    }

    public String getQuestion() {
        return sQuestion;
    }

    public String getAnswer() {
        return sAnswer;
    }

    public void setQuestion(String sQuestion) {
        this.sQuestion = sQuestion;
    }

    public void setAnswer(String sAnswer) {
        this.sAnswer = sAnswer;
    }
}
