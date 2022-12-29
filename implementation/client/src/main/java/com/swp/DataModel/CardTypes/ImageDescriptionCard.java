package com.swp.DataModel.CardTypes;

import com.gumse.textures.Texture;
import com.swp.DataModel.Card;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import lombok.Getter;
import lombok.Setter;

/**
 * Klasse zum Erstellen von Karteikarten des Typs ImageDescriptionCard
 * Erbt die Grundeigenschaften der Klasse Card
 */

@Getter
@Setter
@Entity
public class ImageDescriptionCard extends Card
{
    /**
     * Bilddatei für die Frage
     */

    @Column
    private String image;  //texture

    /**
     * Antwort der Frage
     */
    @Column
    private String answer;

    /**
     * Leerer Konstruktor der Klasse ImageDescriptionCard
     */
    public ImageDescriptionCard()
    {
        super(CardType.IMAGEDESC);
        setTitle("ImageDescriptionCard");
    }

    /**
     * Konstruktor der Klasse ImageDescriptionCard
     * @param question Textuelle Frage zum Bild
     * @param answer: Antwort
     * @param image: Bild für die Karte
     * @param title Optionaler Titel der Karte
     * @param visible Sichtbarkeit der Karte
     */
    public ImageDescriptionCard(String question, String answer, String title, String image, boolean visible)
    {
        super(CardType.IMAGEDESC);
        setTitle(title);
        this.question = question;
        this.image = image;
        this.answer = answer;
        this.visibility = visible;
        this.content = getContent();
    }

    @Override
    public String getContent(){
        return title + "\n" + question + "\n" + answer;
    }
}