package com.swp.DataModel.CardTypes;

import com.swp.DataModel.Card;

/**
 * Klasse zum Erstellen von Karteikarten des Typs MultipleChoiceCard
 * Erbt die Grundeigenschaften der Klasse Card
 */
public class MultipleChoiceCard extends Card 
{
    /**
     * Frage der Karte
     */
    private String sQuestion;

    /**
     * Antwortmöglichkeiten der Karte
     */
    private String[] saAnswers;

    /**
     * Array mit den korrekten Antworten
     */
    private int[] iaCorrectAnswers;

    /**
     * Leerer Konstruktor der Klasse MultipleChoiceCard
     */
    public MultipleChoiceCard()
    {
        super(CardType.MULITPLECHOICE);
        setTitle("MultipleChoiceCard");
    }

    /**
     * Konstruktor der Klasse MultipleChoiceCard
     * @param question Frage der Karte
     * @param answers Antwortmöglichkeiten der Karte
     * @param correctAnswers Korrekte Antworten für die Karte
     * @param title Optionaler Titel der Karte
     * @param visible Sichtbarkeit der Karte
     */
    public MultipleChoiceCard(String question, String[] answers, int[] correctAnswers, String title, boolean visible){
        super(CardType.MULITPLECHOICE);
        setTitle(title);
        sQuestion = question;
        saAnswers = answers;
        iaCorrectAnswers = correctAnswers;
        bVisibility = visible;
    }

    public String getQuestion() {
        return sQuestion;
    }

    public String[] getAnswers() {
        return saAnswers;
    }

    public int[] getCorrectAnswers() {
        return iaCorrectAnswers;
    }
    public void setQuestion(String sQuestion) {
        this.sQuestion = sQuestion;
    }

    public void setAnswers(String[] saAnswers) {
        this.saAnswers = saAnswers;
    }

    public void setCorrectAnswers(int[] iaCorrectAnswers) {
        this.iaCorrectAnswers = iaCorrectAnswers;
    }
}
