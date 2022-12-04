package com.swp.Controller;

import com.swp.DataModel.*;
import com.swp.Logic.CategoryLogic;

import java.util.Set;

public class CategoryController
{
    private CategoryController() {}

    public static boolean updateCategoryData(Category oldcategory, Category newcategory) { return CategoryLogic.updateCategoryData(oldcategory, newcategory); }
    public static boolean createCardToCategory(Card card, Category category)             { return CategoryLogic.createCardToCategory(card, category); }
    public static boolean deleteCategory(Category category)                              { return CategoryLogic.deleteCategory(category); }
    public static boolean deleteCardToCategory(CardToCategory c2d)                       { return CategoryLogic.deleteCardToCategory(c2d); }
    public static Set<Category> getCategories()                                          { return CategoryLogic.getCategories(); }
    public static Category getCategoryByUUID(String uuid)                                { return CategoryLogic.getCategoryByUUID(uuid); }
    public static Set<Card> getCardsInCategory(Category category)                        { return CategoryLogic.getCardsByCategory(category); }
    public static int numCardsInCategory(Category category)                              { return CategoryLogic.numCardsInCategory(category); }
}