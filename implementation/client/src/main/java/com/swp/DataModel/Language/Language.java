package com.swp.DataModel.Language;

import java.util.Map;

import com.gumse.gui.Locale;
import com.gumse.tools.Output;
import com.gumse.tools.Toolbox;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Language 
{
    protected Locale locale;
    private int i;

    protected void loadLocale(String resourceName)
    {
        String filecontent = Toolbox.loadResourceAsString(resourceName, getClass());

        i = 0;
        filecontent.lines().forEach((String line) -> {
            i++;
            if(line.replaceAll("\\s","").isEmpty() || line.charAt(0) == '#')
                return;
            
            String[] args = line.split("= ");
            if(args.length < 1)
                Output.error("Locale resource for language " + locale.getLanguage() + " is missing a definition at line " + i);
            String id = args[0].replaceAll("\\s","");
            String value = args[1];
            locale.setString(id, value);
        });
    }

    private String combineStrings(String[] strs)
    {
        return ""; //TODO
    }

    public void activate()
    {
        Locale.setCurrentLocale(locale);
    }
}