package com.swp.DataModel.CardTypes;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import java.util.UUID;

@Entity
@Table
@NoArgsConstructor
@Embeddable
public class ImageDescriptionCardAnswer //C++ Struct
{
    @Id
    private String id;
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
}