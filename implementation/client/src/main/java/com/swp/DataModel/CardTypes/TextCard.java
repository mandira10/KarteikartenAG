package com.swp.DataModel.CardTypes;

import com.swp.DataModel.Card;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;


/**
 * Klasse zum Erstellen von Karteikarten des Typs TextCard
 * Erbt die Grundeigenschaften der Klasse Card
 *
 * @author Mert As, Efe Carkcioglu, Tom Beuke, Ole-Niklas Mahlstädt, Nadja Cordes
 */

@Getter
@Setter
@Entity
@DiscriminatorValue("TEXT")
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
       this("", "", "");
    }

    /**
     * Konstruktor der Klasse TextCard
     * @param question: Frage der Karte
     * @param answer: Antwort der Karte
     * @param title: Optionaler Titel der Karte
     */
    public TextCard(String question, String answer, String title)
    {
        super(CardType.TEXT);
        setTitle(title);
        this.question = question;
        this.answer = answer;
        setContent();
    }


    /**
     * Überschreibt die Grundmethode von Card. Setzt den Content, nach dem gesucht werden soll,
     * wenn ein Suchterm eingegeben wird.
     */
    @Override
    public void setContent(){
        content =  title + "\n" + question + "\n" + answer;
    }

    /**
     * Setter für die Antwort. Prüft zunächst, dass die Karte nicht null oder leer ist.
     * @param answer: zu setzende Antwort
     */
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    /**
     * Überschreibt die Grundmethode von getAnswerString in Card.
     * Gibt die Antwort zurück.
     * @return Antwort der Karte
     */
    @Override
    public String getAnswerString() 
    {
        return answer;
    }
}
