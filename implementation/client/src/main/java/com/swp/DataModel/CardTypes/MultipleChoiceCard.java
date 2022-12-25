package com.swp.DataModel.CardTypes;

import com.swp.DataModel.Card;
import lombok.Getter;
import lombok.Setter;

/**
 * Klasse zum Erstellen von Karteikarten des Typs MultipleChoiceCard
 * Erbt die Grundeigenschaften der Klasse Card
 */

@Getter
@Setter
public class MultipleChoiceCard extends Card 
{
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
        setSTitle("MultipleChoiceCard");
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
        setSTitle(title);
        sQuestion = question;
        saAnswers = answers;
        iaCorrectAnswers = correctAnswers;
        bVisibility = visible;
        sContent = getSContent();
    }


    @Override
    public String getSContent(){
        return sTitle + "\n" + sQuestion + "\n" + saAnswers;
    }
}
