package com.swp.Logic.CategoryLogicTest;

import com.swp.DataModel.Category;
import com.swp.Logic.CardLogic;
import com.swp.Logic.CategoryLogic;
import com.swp.Persistence.CardRepository;
import com.swp.Persistence.CategoryRepository;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.Test;

import org.junit.jupiter.api.BeforeEach;

import java.util.Arrays;
import java.util.List;

import static org.joor.Reflect.on;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

public class updateSaveDeleteCategoryTest {

    private CardRepository cardRepMock;
    private CategoryRepository categoryRepMock;
    private CardLogic cardLogic = CardLogic.getInstance();
    private CategoryLogic categoryLogic = CategoryLogic.getInstance();


    @BeforeEach
    public void beforeEach(){
        cardRepMock = mock(CardRepository.class);
        categoryRepMock = mock(CategoryRepository.class);
        on(categoryLogic).set("categoryRepository",categoryRepMock);

    }

    @Test
    public void testExceptionIfCategoryToDeleteIsNull() {
        final String expected = "Kategorie existiert nicht";
        final IllegalStateException exception =
                assertThrows(IllegalStateException.class, () -> categoryLogic.deleteCategory(null));
        assertEquals(expected, exception.getMessage());
    }

    @Test
    public void testDeleteCategoryFunction(){
        Category category  = new Category("Test");
        doNothing().when(categoryRepMock).delete(category);
        categoryLogic.deleteCategory(category);
    }

    @Test
    public void testDeleteFunctionForManyCategories(){
       Category cat1 = new Category("Test");
       Category cat2 = new Category("Test2");
        List<Category> cats = Arrays.asList(new Category[]{cat2,cat1});
        doNothing().when(categoryRepMock).delete(any(Category.class));
       categoryLogic.deleteCategories(cats);
    }

    @Test
    public void testExceptionIfCategoryToUpdateIsNull() {
        final String expected = "Kategorie existiert nicht";
        final IllegalStateException exception =
                assertThrows(IllegalStateException.class, () -> categoryLogic.updateCategoryData(null,true,false));
        assertEquals(expected, exception.getMessage());
    }

    @Test
    public void testExceptionIfCategoryNameAlreadyExists1() {
        final String expected = "Kategorie mit dem Namen existiert bereits!";
        Category catToAdd = new Category("Test");
        Category existingCat = new Category("Test");
        when(categoryRepMock.find("Test")).thenReturn(existingCat);
        final IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> categoryLogic.updateCategoryData(catToAdd,true,false));
        assertEquals(expected, exception.getMessage());
    }

    @Test
    public void testExceptionIfCategoryNameAlreadyExists2() {
        final String expected = "Kategorie mit dem Namen existiert bereits!";
        Category catToChange = new Category("Test");
        Category existingCat = new Category("Test");
        when(categoryRepMock.find("Test")).thenReturn(existingCat);
        final IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> categoryLogic.updateCategoryData(catToChange,false,true));
        assertEquals(expected, exception.getMessage());
    }

    @Test
    public void testSaveFunction() {
        Category catToAdd = new Category("Test");
        when(categoryRepMock.find("Test")).thenThrow(NoResultException.class);
        when(categoryRepMock.save(catToAdd)).thenReturn(catToAdd);
        categoryLogic.updateCategoryData(catToAdd,true,false);
    }
    @Test
    public void testUpdateFunction() {
        Category catToAdd = new Category("Test");
        when(categoryRepMock.update(catToAdd)).thenReturn(catToAdd);
        categoryLogic.updateCategoryData(catToAdd,false,true);
    }

    @Test
    public void testGetCategoryByUUIDNullException(){
        final String expected = "UUID darf nicht null sein!";
        final IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> categoryLogic.getCategoryByUUID(null));
        assertEquals(expected, exception.getMessage());
    }

    @Test
    public void testGetCategoryByUUIDEmptyException(){
        final String expected = "UUID darf nicht leer sein!";
        final IllegalArgumentException exception =
                assertThrows(IllegalArgumentException.class, () -> categoryLogic.getCategoryByUUID(""));
        assertEquals(expected, exception.getMessage());
    }

    @Test
    public void testGetCategoryByUUID(){
        Category category = new Category("Test");
        when(categoryRepMock.findByUUID(any(String.class))).thenReturn(category);
        Category category1 = categoryLogic.getCategoryByUUID("Test");
        assertEquals(category,category1);
    }

}
