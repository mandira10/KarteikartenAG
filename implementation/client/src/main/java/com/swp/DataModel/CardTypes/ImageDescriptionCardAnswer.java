package com.swp.DataModel.CardTypes;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.UUID;

/**
 * Klasse für die ImageDescriptionCard.
 */
@Entity
@Table
@NoArgsConstructor
public class ImageDescriptionCardAnswer //C++ Struct
{
    /**
     * Id des Antwort für die ImageDescriptionCard
     */
    @Id
    private String id;

    /**
     * Zugehörige ImageDescriptionCard
     */
    //@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID")
    private ImageDescriptionCard parent;

    /**
     * Antworttext
     */
    @Column
    public String answertext;

    /**
     * Position x
     */
    @Column
    public int xpos;

    /**
     * Position y
     */
    @Column
    public int ypos;

    /**
     * Konstruktor der ImageDescriptionAnswer
     * @param text  Text der Antwort
     * @param x Position x
     * @param y Position y
     */
    public ImageDescriptionCardAnswer(String text, int x, int y)
    {
        this.id = UUID.randomUUID().toString();
        this.answertext = text;
        this.xpos = x;
        this.ypos = y;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ImageDescriptionCardAnswer that = (ImageDescriptionCardAnswer) o;
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }

    @Override
    public String toString() {
        return "ImageDescriptionCardAnswer{" +
                "id='" + id + '\'' +
                ", answertext='" + answertext + '\'' +
                ", xpos=" + xpos +
                ", ypos=" + ypos +
                '}';
    }
}