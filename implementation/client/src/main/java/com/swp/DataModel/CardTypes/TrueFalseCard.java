package com.swp.DataModel.CardTypes;

import com.swp.DataModel.BooleanConverter;
import com.swp.DataModel.Card;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

/**
 * Klasse zum Erstellen von Karteikarten des Typs TrueFalseCard
 * Erbt die Grundeigenschaften der Klasse Card
 *
 * @author Mert As, Efe Carkcioglu, Tom Beuke, Ole-Niklas Mahlstädt, Nadja Cordes
 */
@Getter
@Setter
@Entity
@DiscriminatorValue("TRUEFALSE")
public class TrueFalseCard extends Card 
{

    /**
     * Antwort der Karte, wird in Datenbank als String gespeichert.
     */
    @Column
    @Convert(converter = BooleanConverter.class)
    private boolean answer;

    /**
     * Leerer Konstruktor der Klasse TrueFalseCard
     */
    public TrueFalseCard()
    {
       this("", false, "");
    }

    /**
     * Konstruktor der Klasse TrueFalseCard
     * @param question Frage der Karte
     * @param answer   Antwort der Karte
     * @param title    Der Titel der Karte
     */
    public TrueFalseCard(String question, boolean answer, String title)
    {
        super(CardType.TRUEFALSE);
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
        content =  title + "\n" + question;
    }

    /**
     * Überschreibt die Grundmethode von getAnswerString in Card.
     * Gibt die Antwort zurück, konvertiert Boolean zu String.
     * @return Antwort der Karte
     */
    @Override
    public String getAnswerString() 
    {
        return answer ? "True" : "False";
    }
}
