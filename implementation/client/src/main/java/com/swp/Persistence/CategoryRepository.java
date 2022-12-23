package com.swp.Persistence;

import java.util.Set;

import com.swp.DataModel.Card;
import com.swp.DataModel.CardToCategory;
import com.swp.DataModel.Category;

public class CategoryRepository
{
    /**
     *
     * @param oldcategory
     * @param newcategory
     */
    public static boolean updateCategory(Category oldcategory, Category newcategory)
    {

        //server.send("/updatecategorydata", jsonString);
        return false;
    }

    public static boolean saveCategory(Category category)
    {
        //server.send("/createcategory", jsonString);
        return false;
    }

    public static boolean saveCardToCategory(Card card, Category category)
    {
        //server.send("/createcardtocategory", jsonString);
        return false;
    }

    public static boolean deleteCategory(Category card)
    {
        //server.send("/deletecategory", jsonString);
        return false;
    }

    public static Set<Category> getCategories()
    {
        Set<Category> cats = Cache.getInstance().getCategories();
        if(!cats.isEmpty())
            return cats;

        //server.send("/getcategories", jsonString);
        return null;
    }

    public static Set<CardToCategory> getCardToCategories()
    {
        Set<CardToCategory> cats = Cache.getInstance().getCardToCategories();
        if(!cats.isEmpty())
            return cats;

        //server.send("/getcardtocategories", jsonString);
        return null;
    }
}
