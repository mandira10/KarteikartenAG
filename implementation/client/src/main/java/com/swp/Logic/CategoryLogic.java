package com.swp.Logic;

import java.util.List;

import com.swp.DataModel.Category;
import com.swp.Persistence.CategoryRepository;

public class CategoryLogic
{
    public static List<Category> getCategories() 
    { 
        List<Category> catList = CategoryRepository.getCategories();

        //Convert parentuuid entries to actual category references

        return catList;
    }

    public static void updateCategoryData(Category oldcategory, Category newcategory) 
    {
        if(newcategory.getUUID().isEmpty())
            CategoryRepository.saveCategory(newcategory);
        else
            CategoryRepository.updateCategory(oldcategory, newcategory);
    }

    public static void deleteCategory(Category category)                  
    { 
        CategoryRepository.deleteCategory(category); 
    }

    public static void deleteCategories(Category[] categories)                  
    { 
        for(Category c : categories)
            CategoryRepository.deleteCategory(c); 
    }
    public static void getAllInfosFor(Category category)
    { 
        /*CategoryRepository.()*/ 
    }
}