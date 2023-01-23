package com.swp.DataModel.CardTypes;

import jakarta.persistence.*;
import lombok.NoArgsConstructor;

import java.util.Objects;
import java.util.UUID;

@Entity
@Table
@NoArgsConstructor
//@Embeddable/
public class ImageDescriptionCardAnswer //C++ Struct
{
    @Id
    private String id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "PARENT_ID")
    private ImageDescriptionCard parent;

    @Column
    public String answertext;
    @Column
    public int xpos;
    @Column
    public int ypos;

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