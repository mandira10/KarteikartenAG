package com.swp.DataModel.CardTypes;

import com.gumse.gui.Locale;
import com.swp.DataModel.Card;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

import static com.swp.Validator.checkNotNullOrBlank;


/**
 * Klasse zum Erstellen von Karteikarten des Typs ImageTestCard
 * Erbt die Grundeigenschaften der Klasse Card
 */

@Getter
@Setter
@Entity
@DiscriminatorValue("IMAGETEST")
public class ImageTestCard extends Card 
{

    /**
     * Bild für die Karteikarte
     */
    @Column
    private byte[] image;

    /**
     * Zusätzliche Beschreibung zum Bild als Antwort
     */
    @Column
    private String answer;

    /**
     * Tauscht die Frage/Antwort Option, sodass das Bild sowohl
     * als Frage als auch als Antwort verwendet werden kann.
     */
    @Column
    private boolean swapQA;

    /**
     * Leerer Konstruktor der Klasse ImageTestCard
     */
    public ImageTestCard()
    {
        this.question = "";
        this.image = null;
        this.answer = null;
        this.title = "";
    }

    /**
     * Konstruktor der Klasse ImageTestCard
     * @param question: Textuelle Beschreibung
     * @param answer: Text zum Bild
     * @param image: Bild zur Antwort
     * @param swapQA: Wechsel Frage/Antwort
     * @param title Optionaler Titel der Karte
     */
    public ImageTestCard(String question, String answer, byte[] image, String title, boolean swapQA)
    {
        super(CardType.IMAGETEST);
        setTitle(title);
        this.question = question;
        this.answer = answer;
        this.image = image;
        this.swapQA = swapQA;
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
