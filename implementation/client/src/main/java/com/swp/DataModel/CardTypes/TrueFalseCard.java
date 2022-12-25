package com.swp.DataModel.CardTypes;

import com.swp.DataModel.Card;
import lombok.Getter;
import lombok.Setter;

/**
 * Klasse zum Erstellen von Karteikarten des Typs TrueFalseCard
 * Erbt die Grundeigenschaften der Klasse Card
 */

@Getter
@Setter
public class TrueFalseCard extends Card 
{

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
        setSTitle("TrueFalseCard");
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
        setSTitle(title);
        sQuestion = question;
        bAnswer = answer;
        bVisibility = visible;
        sContent = getSContent();
    }


    @Override
    public String getSContent(){
        return sTitle + "\n" + sQuestion;
    }
}
