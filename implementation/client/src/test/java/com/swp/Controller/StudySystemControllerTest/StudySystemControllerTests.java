package com.swp.Controller.StudySystemControllerTest;


import com.swp.Controller.CardController;
import com.swp.Controller.DataCallback;
import com.swp.Controller.SingleDataCallback;
import com.swp.Controller.StudySystemController;
import com.swp.DataModel.Card;
import com.swp.DataModel.CardOverview;
import com.swp.DataModel.CardTypes.MultipleChoiceCard;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.DataModel.CardTypes.TrueFalseCard;
import com.swp.DataModel.StudySystem.StudySystem;
import com.swp.DataModel.Tag;
import com.swp.Logic.CardLogic;
import com.swp.Logic.StudySystemLogic;
import com.swp.Persistence.Exporter;
import com.swp.TestData;
import jakarta.persistence.NoResultException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.joor.Reflect.on;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

/**
 * Testet die Controller Funktionen
 */
public class StudySystemControllerTests {

    private StudySystemLogic studySystemLogic = StudySystemLogic.getInstance();
    private StudySystemController studySystemController = StudySystemController.getInstance();

    @BeforeEach
    public void beforeEach(){
        studySystemLogic = mock(StudySystemLogic.class);
        on(studySystemController).set("studysystemLogic",studySystemLogic);
    }



    @Test
    public void getAllCardsInStudySystemTestEmptySet(){

        List<CardOverview> list = new ArrayList<>();
        when(studySystemLogic.getAllCardsInStudySystem(any(StudySystem.class))).thenReturn(list);
        final String expected = "Es gibt keine Karten zu diesem StudySystem";
        final String[] actual = new String[1];

        studySystemController.getAllCardsInStudySystem(any(StudySystem.class), new DataCallback<CardOverview>() {
            @Override
            public void onSuccess(List<CardOverview> data) {

            }

            @Override
            public void onFailure(String msg) {

            }

            @Override
            public void onInfo(String msg) {
                actual[0] = msg;
            }
        });

        assertEquals(expected,actual[0]);


    }

    @Test
    public void getAllCardsInStudySystemTestWithList(){
        final List<CardOverview> list = Arrays.asList(new CardOverview(), new CardOverview());
        when(studySystemLogic.getAllCardsInStudySystem(any(StudySystem.class))).thenReturn(list);
        final List<CardOverview> expected = list;
        final List<CardOverview>[] actual = new List[1];
        studySystemController.getAllCardsInStudySystem(any(StudySystem.class),new DataCallback<CardOverview>() {
            @Override
            public void onSuccess(List<CardOverview> data) {
                actual[0] = data;
            }

            @Override
            public void onFailure(String msg) {
            }

            @Override
            public void onInfo(String msg) {
            }
        });
        assertEquals(expected, actual[0]);
    }

    @Test
    public void getAllCardsInStudySystemTestException(){
        when(studySystemLogic.getAllCardsInStudySystem(any(StudySystem.class))).thenThrow(RuntimeException.class);
        final String expected = "Ein Fehler ist aufgetreten";
        final String[] actual = new String[1];
        studySystemController.getAllCardsInStudySystem(any(StudySystem.class), new DataCallback<CardOverview>() {
            @Override
            public void onSuccess(List<CardOverview> data) {
            }

            @Override
            public void onFailure(String msg) {
                actual[0] = msg;
            }

            @Override
            public void onInfo(String msg) {
            }
        });
        assertEquals(expected,actual[0]);

    }







}
