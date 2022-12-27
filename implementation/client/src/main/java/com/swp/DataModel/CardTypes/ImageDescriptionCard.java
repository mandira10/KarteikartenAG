package com.swp.DataModel.CardTypes;

import com.gumse.textures.Texture;
import com.swp.DataModel.Card;
import lombok.Getter;
import lombok.Setter;

/**
 * Klasse zum Erstellen von Karteikarten des Typs ImageDescriptionCard
 * Erbt die Grundeigenschaften der Klasse Card
 */

@Getter
@Setter
public class ImageDescriptionCard extends Card
{
    /**
     * Bilddatei für die Frage
     */
    private Texture oImage;

    /**
     * Antwort der Frage
     */
    private String sAnswer;

    /**
     * Leerer Konstruktor der Klasse ImageDescriptionCard
     */
    public ImageDescriptionCard()
    {
        super(CardType.IMAGEDESC);
        setSTitle("ImageDescriptionCard");
    }

    /**
     * Konstruktor der Klasse ImageDescriptionCard
     * @param question Textuelle Frage zum Bild
     * @param answer: Antwort
     * @param image: Bild für die Karte
     * @param title Optionaler Titel der Karte
     * @param visible Sichtbarkeit der Karte
     */
    public ImageDescriptionCard(String question, String answer, String title, Texture image, boolean visible)
    {
        super(CardType.IMAGEDESC);
        setSTitle(title);
        sQuestion = question;
        oImage = image;
        sAnswer = answer;
        bVisibility = visible;
        sContent = getSContent();
    }

    @Override
    public String getSContent(){
        return sTitle + "\n" + sQuestion + "\n" + sAnswer;
    }
}