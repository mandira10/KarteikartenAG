package com.swp.DataModel.CardTypes;

import com.gumse.textures.Texture;
import com.swp.DataModel.Card;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;


/**
 * Klasse zum Erstellen von Karteikarten des Typs ImageTestCard
 * Erbt die Grundeigenschaften der Klasse Card
 */

@Getter
@Setter
@Entity
public class ImageTestCard extends Card 
{

    /**
     * Bild für die Karteikarte
     */

    @Column
    private String image; //Texture

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
        super(CardType.IMAGETEST);
        setTitle("ImageTestCard");
    }

    /**
     * Konstruktor der Klasse ImageTestCard
     * @param question: Textuelle Beschreibung
     * @param answer: Text zum Bild
     * @param image: Bild zur Antwort
     * @param swapQA: Wechsel Frage/Antwort
     * @param title Optionaler Titel der Karte
     * @param visible Sichtbarkeit der Karte
     */
    public ImageTestCard(String question, String answer, String image, String title, boolean swapQA, boolean visible)
    {
        super(CardType.IMAGETEST);
        setTitle(title);
        this.question = question;
        this.answer = answer;
        this.image = image;
        this.swapQA = swapQA;
        this.visibility = visible;
        this.content = getContent();
    }



    @Override
    public String getContent(){
        return title + "\n" + question + "\n" + answer;
    }
}
