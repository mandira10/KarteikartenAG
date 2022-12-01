package com.swp.Persistence;

import java.util.List;

import com.swp.DataModel.Category;

public class CategoryRepository 
{
    /**
     * 
     * @param oldcategory
     * @param newcategory
     */
    public static void updateCategory(Category oldcategory, Category newcategory)
    {

        //server.send("/updatecategorydata", jsonString);
    }

    public static void saveCategory(Category category)
    {
        //server.send("/createcategory", jsonString);
    }

    public static void deleteCategory(Category card)
    {
        //server.send("/deletecategory", jsonString);
    }

    public static List<Category> getCategories()  { return null; }
    public static Category getCategory(int index) { return null; }
}
