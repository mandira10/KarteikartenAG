package com.swp.GUI.Cards.EditCardPages;

import com.gumse.gui.Primitives.RenderGUI;

import com.swp.DataModel.Card;

/**
 * Die Oberklasse für alle EditCardPages
 */
public abstract class EditCardGUI extends RenderGUI
{
    public abstract void setCard(Card card);

    /**
     * Überprüft ob Pflichtfleder ausgefüllt worden
     *
     * @return true, wenn alle Pflichtfelder gefüllt sind
     */
    public abstract boolean checkMandatory();
}