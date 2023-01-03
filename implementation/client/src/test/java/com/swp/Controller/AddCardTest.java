package com.swp.Controller;


import com.swp.DataModel.Card;
import com.swp.DataModel.CardTypes.MultipleChoiceCard;
import com.swp.DataModel.CardTypes.TextCard;
import com.swp.Persistence.CardRepository;
import org.junit.Assert;
import org.junit.Test;


import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Optional;


import static org.junit.Assert.*;

public class AddCardTest {


      /**
       * Testing the working of BeanUtils for different CardTypes
       * //TODO: incorporate image and audiofile as soon as incorporated
       * @throws InvocationTargetException todo
       * @throws IllegalAccessException todo
       * @throws NoSuchMethodException todo
       * @throws InstantiationException todo
       */
      @Test
  public void testCardCreateAndUpdate() throws InvocationTargetException, IllegalAccessException, NoSuchMethodException, InstantiationException {
        //TEXT
            HashMap<String, Object> txmap = new HashMap<>();
            txmap.put("answer", "Testantwort");
            txmap.put("question","Testfrage");
            txmap.put("title","Testtitel");
            txmap.put("visibility",false);
            assertTrue(CardController.updateCardData(null, "TEXT", txmap, null,null));
            Optional<Card> optTxCard = CardRepository.findCardByTitle("Testtitel");
            assertNotNull(optTxCard);
            final Card txcard = optTxCard.get();
            TextCard textCard = (TextCard) txcard;
            assertNotNull(textCard);
            assertEquals("Testtitel",textCard.getTitle());
            assertEquals("Testfrage",textCard.getQuestion());
            assertEquals("Testantwort",textCard.getAnswer());
            assertEquals(false,textCard.isVisibility());
            assertEquals(textCard.getTitle() + "\n" + textCard.getQuestion() + "\n" + textCard.getAnswer(),textCard.getContent());
        //MULTIPLE CHOICE
            String[] answers = {"Testantwort 1","Testantwort2","Testantwort 3"};
            int[] correctAnswers = {1,3};
            HashMap<String, Object> mcmap = new HashMap<>();
            mcmap.put("answers", answers) ;
            mcmap.put("question","Testfrage");
            mcmap.put("correctAnswers",correctAnswers);
            mcmap.put("title","Testtitel1");
            mcmap.put("visibility",false);
            assertTrue(CardController.updateCardData(null, "MULTIPLECHOICE", mcmap, null,null));
            Optional<Card> optMcCard = CardRepository.findCardByTitle("Testtitel1");
            assertNotNull(optMcCard);
            final Card card = optMcCard.get();
            MultipleChoiceCard mcCard = (MultipleChoiceCard) card;
            assertNotNull(mcCard);
            assertEquals("Testtitel1",mcCard.getTitle());
            assertEquals("Testfrage",mcCard.getQuestion());
            Assert.assertArrayEquals(answers, mcCard.getAnswers());
            Assert.assertArrayEquals(correctAnswers, mcCard.getCorrectAnswers());
            assertEquals(false,mcCard.isVisibility());
            assertEquals(mcCard.getTitle() + "\n" + mcCard.getQuestion(),mcCard.getContent());

    }


}

