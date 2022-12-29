package com.swp.DataModel.CardTypes;

import com.swp.DataModel.Card;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

/**
 * Klasse zum Erstellen von Karteikarten des Typs TrueFalseCard
 * Erbt die Grundeigenschaften der Klasse Card
 */

@Getter
@Setter
@Entity
public class TrueFalseCard extends Card 
{

    /**
     * Antwort der Karte
     */
    @Column
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
        this.question = question;
        answer = answer;
        visibility = visible;
        content = getContent();
    }


    @Override
    public String getContent(){
        return title + "\n" + question;
    }
}
