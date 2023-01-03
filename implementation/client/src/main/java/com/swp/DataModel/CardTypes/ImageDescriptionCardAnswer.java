package com.swp.DataModel.CardTypes;


public class ImageDescriptionCardAnswer //C++ Struct
{
    public String answertext;
    public int xpos;
    public int ypos;

    public ImageDescriptionCardAnswer(String text, int x, int y)
    {
        this.answertext = text;
        this.xpos = x;
        this.ypos = y;
    }
}