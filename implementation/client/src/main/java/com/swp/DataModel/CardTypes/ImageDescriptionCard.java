package com.swp.DataModel.CardTypes;

import com.gumse.textures.Texture;
import com.swp.DataModel.Card;

/**
 * Klasse zum Erstellen von Karteikarten des Typs ImageDescriptionCard
 * Erbt die Grundeigenschaften der Klasse Card
 */
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
        setTitle("ImageDescriptionCard");
    }

    /**
     * Konstruktor der Klasse ImageDescriptionCard
     * @param answer: Antwort
     * @param image: Bild für die Karte
     * @param visible: Sichtbarkeit der Karte
     */
    public ImageDescriptionCard(String answer, Texture image, boolean visible)
    {
        super(CardType.IMAGEDESC);
        setTitle("ImageDescriptionCard");
        oImage = image;
        sAnswer = answer;
        bVisibility = visible;
    }

    public Texture getImage() {
        return oImage;
    }

    public String getAnswer() {
        return sAnswer;
    }

    public void setAnswer(String sAnswer) {
        this.sAnswer = sAnswer;
    }

    public void setImage(Texture oImage) {
        this.oImage = oImage;
    }
}
