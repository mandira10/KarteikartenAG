package com.swp.DataModel.CardTypes;

import com.swp.DataModel.Card;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import static com.swp.Validator.checkNotNullOrBlank;


/**
 * Klasse zum Erstellen von Karteikarten des Typs TextCard
 * Erbt die Grundeigenschaften der Klasse Card
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
       this("", "", "", false);
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
        setContent();
    }



    @Override
    public void setContent(){
        content =  title + "\n" + question + "\n" + answer;
    }

    public void setAnswer(String answer) {
        this.answer = checkNotNullOrBlank("Antwort", answer);
    }

    @Override
    public String getAnswerString() 
    {
        return answer;
    }
}
