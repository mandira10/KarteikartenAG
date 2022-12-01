package com.swp.Controller;

import com.swp.DataModel.*;
import com.swp.Logic.CardLogic;
import com.swp.Logic.CategoryLogic;
import com.swp.Persistence.Exporter.ExportFileType;

import java.util.List;

public class CategoryController 
{
    private CategoryController() {}

    // CATEGORY
    public static void updateCategoryData(Category oldcategory, Category newcategory) 
    {
        CategoryLogic.updateCategoryData(oldcategory, newcategory);
    }

    public static void createCardToCategory(Card card, Category category) {
        CardLogic.createCardToCategory(card, category);
    }
    public static void deleteCategory(Category category) { CategoryLogic.deleteCategory(category); }
    public static void getCategories() { CategoryLogic.getCategories(); }
    public static void getAllInfosFor(Category category) { CategoryLogic.getAllInfosFor(category); }
    public static void getCountOfCardsFor(Category category) { CardLogic.getCardsByCategory(category); }
    public static void exportCards(Card[] cards, ExportFileType filetype)
    {
        CardLogic.exportCards(cards, filetype);
    }
}
