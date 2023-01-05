package com.swp.DataModel.CardTypes;

import java.nio.ByteBuffer;

import com.gumse.textures.Texture;
import com.swp.DataModel.Card;
import jakarta.persistence.Column;
import jakarta.persistence.DiscriminatorValue;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

/**
 * Klasse zum Erstellen von Karteikarten des Typs ImageDescriptionCard
 * Erbt die Grundeigenschaften der Klasse Card
 */

@Getter
@Setter
//@Entity
//@DiscriminatorValue("IMAGEDESC")
public class ImageDescriptionCard extends Card
{
    /**
     * Bilddatei für die Frage
     */
    @Column
    private String image;  //Not optimal

    /**
     * Antwort der Frage
     */
    @Column
    private ImageDescriptionCardAnswer[] answers;

    /**
     * Leerer Konstruktor der Klasse ImageDescriptionCard
     */
    public ImageDescriptionCard()
    {
        this("", new ImageDescriptionCardAnswer[] {}, "", "", false);
    }

    /**
     * Konstruktor der Klasse ImageDescriptionCard
     * @param question Textuelle Frage zum Bild
     * @param answer: Antwort
     * @param image: Bild für die Karte
     * @param title Optionaler Titel der Karte
     * @param visible Sichtbarkeit der Karte
     */
    public ImageDescriptionCard(String question, ImageDescriptionCardAnswer[] answers, String title, String image, boolean visible)
    {
        super(CardType.IMAGEDESC);
        setTitle(title);
        this.question = question;
        this.image = image;
        this.answers = answers;
        this.visibility = visible;
        setContent();
    }

    @Override
    public void setContent()
    {
        content = title + "\n" + question;
        for(ImageDescriptionCardAnswer answer : answers)
            content += "\n" + answer.answertext;
    }

    @Override
    public String getAnswerString() 
    {
        int i = 1;
        String retstr = "";
        for(ImageDescriptionCardAnswer answer : answers)
        {
            retstr += String.valueOf(i++) + " " + answer.answertext + "\n";
        }

        return retstr;
    }
}