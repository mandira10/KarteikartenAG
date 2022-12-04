package com.swp.Logic;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.swp.DataModel.Card;
import com.swp.DataModel.CardToCategory;
import com.swp.DataModel.Category;
import com.swp.Persistence.CategoryRepository;

public class CategoryLogic
{    
    public static boolean createCardToCategory(Card card, Category category) 
    {
        if(getCategoriesByCard(card).contains(category))
            return true;
        
        return CategoryRepository.saveCardToCategory(card, category);
    }

    public static Set<Category> getCategories()
    {
        Set<Category> catList = CategoryRepository.getCategories();

        //Convert parentuuid entries to actual category references

        return catList;
    }

    public static Category getCategory(String uuid) 
    {
        Set<Category> cats = CategoryRepository.getCategories();
        for(Category c : cats)
        {
            if(c.getUUID() == uuid)
                return c;
        }
        return null; 
    }

    public static Category getCategoryByUUID(String uuid)
    { 
        return null;
    }

    public static Set<Category> getCategoriesByCard(Card card)
    { 
        Set<Category> retArr = new HashSet<>();
        for(CardToCategory c2c : CategoryRepository.getCardToCategories())
        {
            if(c2c.getCard() == card)
                retArr.add(c2c.getCategory());
        }
        return retArr;
    }

    public static Set<Card> getCardsByCategory(Category category)
    { 
        Set<Card> retArr = new HashSet<>();
        for(CardToCategory c2c : CategoryRepository.getCardToCategories())
        {
            if(c2c.getCategory() == category)
                retArr.add(c2c.getCard());
        }
        return retArr;
    }


    public static int numCardsInCategory(Category category)
    { 
        return getCardsByCategory(category).size(); 
    }

    public static boolean updateCategoryData(Category oldcategory, Category newcategory)
    {
        if(newcategory.getUUID().isEmpty())
            return CategoryRepository.saveCategory(newcategory);
        else
            return CategoryRepository.updateCategory(oldcategory, newcategory);
    }

    public static boolean deleteCategory(Category category)
    {
        return CategoryRepository.deleteCategory(category);
    }

    public static boolean deleteCategories(Category[] categories)
    {
        boolean ret = true;
        for(Category c : categories)
        {
            if(!CategoryRepository.deleteCategory(c))
                ret = false;
        }
        
        return ret;
    }

    public static boolean deleteCardToCategory(CardToCategory c2d) 
    {
        return false;
    }
}