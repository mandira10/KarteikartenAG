package com.swp.DataModel.CardTypes;

import com.swp.DataModel.Card;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;


/**
 * Klasse zum Erstellen von Karteikarten des Typs TextCard
 * Erbt die Grundeigenschaften der Klasse Card
 */

@Getter
@Setter
@Entity
public class TextCard extends Card 
{
    /**
     * Antwort der Karte
     */
    @Column
    private String answer;

    /**
     * Leerer Konstruktor der Klasse TextCard
     */
    public TextCard()
    {
        //super(CardType.TEXT);
        //setSTitle("TextCard");
    }

    /**
     * Konstruktor der Klasse TextCard
     * @param question: Frage der Karte
     * @param answer: Antwort der Karte
     * @param title: Optionaler Titel der Karte
     * @param visible: Sichtbarkeit der Karte
     */
    public TextCard(String question, String answer, String title, boolean visible)
    {
        super(CardType.TEXT);
        setTitle(title);
        this.question = question;
        this.answer = answer;
        this.visibility = visible;
        this.content = getContent();
    }



    @Override
    public String getContent(){
        return title + "\n" + question + "\n" + answer;
    }
}
