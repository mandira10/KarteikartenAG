package com.swp.GUI.Cards.EditCardPages;

import com.gumse.gui.Primitives.RenderGUI;

import com.swp.DataModel.Card;

public abstract class EditCardGUI extends RenderGUI
{
    public abstract void setCard(Card card);
    public abstract boolean checkMandatory();
}