package com.swp.DataModel.CardTypes;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Table
@NoArgsConstructor
public class ImageDescriptionCardAnswer //C++ Struct
{
    @Id
    private String id;
    public String answertext;
    public int xpos;
    public int ypos;

    public ImageDescriptionCardAnswer(String text, int x, int y)
    {
        this.id = UUID.randomUUID().toString();
        this.answertext = text;
        this.xpos = x;
        this.ypos = y;
    }
}