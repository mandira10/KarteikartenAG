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


    @Override
    public String getContent(){
        return sTitle + "\n" + sQuestion;
    }
}
