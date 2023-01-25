package com.swp.GUI.Cards.EditCardPages;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import com.gumse.gui.Basics.Button;
import com.gumse.gui.Basics.Scroller;
import com.gumse.gui.Font.FontManager;
import com.gumse.gui.Primitives.RenderGUI;
import com.gumse.gui.XML.XMLGUI;
import com.gumse.maths.ivec2;
import com.gumse.tools.Output;
import com.swp.DataModel.CardTypes.MultipleChoiceCard;
import com.swp.GUI.Cards.EditCardPages.EditMultipleChoiceCardAnswerEntry.AnswerEntryCallback;

public class EditMultipleChoiceCard extends RenderGUI
{
    private Button pAddEntryButton;
    private Scroller pContextScroller;
    private MultipleChoiceCard pCard;

    public EditMultipleChoiceCard()
    {
        this.vSize = new ivec2(100,100);

        addGUI(XMLGUI.loadFile("guis/cards/edit/editmultiplechoicecardpage.xml"));
        pContextScroller = (Scroller)findChildByID("scroller");

        createAddButton();

        this.setSizeInPercent(true, true);
        reposition();
    }

    private void reallignEntries()
    {
        int yoffset = -40; // First child is pAddEntryButton
        for(RenderGUI child : pContextScroller.getChildren())
        {
            child.setPosition(new ivec2(0, yoffset));
            yoffset += 40;
        }
        pAddEntryButton.setPosition(new ivec2(100, yoffset));
    }

    public void addEntry(String answer, boolean iscorrect)
    {
        EditMultipleChoiceCardAnswerEntry entry = new EditMultipleChoiceCardAnswerEntry(answer, iscorrect, new AnswerEntryCallback() {
            @Override public void onRemove(EditMultipleChoiceCardAnswerEntry entry) 
            {
                pContextScroller.removeChild(entry);
                reallignEntries();
                overrideCarddata();
            }

            @Override public void onChange(EditMultipleChoiceCardAnswerEntry entry) 
            {
                overrideCarddata();
            }
        });

        pContextScroller.addGUI(entry);
        reallignEntries();
    }

    private void createAddButton()
    {
        pAddEntryButton = new Button(new ivec2(100, 40), new ivec2(30), "+", FontManager.getInstance().getDefaultFont());
        pAddEntryButton.setPositionInPercent(true, false);
        pAddEntryButton.getBox().setTextSize(28);
        pAddEntryButton.setOrigin(new ivec2(30, 0));
        pAddEntryButton.onClick(new GUICallback() {
            @Override public void run(RenderGUI gui) 
            {
                addEntry("", false);
            }
        });
        pContextScroller.addGUI(pAddEntryButton);
    }

    public void setCard(MultipleChoiceCard card)
    {
        this.pCard = card;
        pContextScroller.destroyChildren();
        createAddButton();

        List<Integer> correctAnswers = Arrays.stream(pCard.getCorrectAnswers()).boxed().toList();
        for(int i = 0; i < pCard.getAnswers().length; i++)
        {
            addEntry(pCard.getAnswers()[i], correctAnswers.contains(i));
        }
    }

    private void overrideCarddata()
    {
        List<String> answers = new ArrayList<>();
        List<Integer> correctanswers = new ArrayList<>();

        int i = 0;
        for(RenderGUI child : pContextScroller.getChildren())
        {
            if(!child.getType().equals("EditMultipleChoiceCardAnswerEntry"))
                continue;

            EditMultipleChoiceCardAnswerEntry entry = (EditMultipleChoiceCardAnswerEntry)child;
            answers.add(entry.getAnswerString());
            Output.info(entry.getAnswerString());
            if(entry.isCorrect())
                correctanswers.add(i);
            i++;
        }

        pCard.setAnswers(answers.toArray(new String[0])); //https://shipilev.net/blog/2016/arrays-wisdom-ancients/
        pCard.setCorrectAnswers(correctanswers.stream().filter(Objects::nonNull).mapToInt(Integer::intValue).toArray());
    }
}