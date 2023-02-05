package com.swp.DataModel.Language;

import com.gumse.gui.Locale;
import com.gumse.tools.Output;
import com.gumse.tools.Toolbox;
import lombok.Getter;
import lombok.Setter;

/**
 * Die Oberklasse aller Sprachklassen
 *  @author Tom Beuke
 */
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
            String value = combineStrings(args);
            locale.setString(id, value);
        });
    }

    private String combineStrings(String[] strs)
    {
        String retstr = "";
        for(int i = 1; i < strs.length; i++)
            retstr += strs[i];
        return retstr;
    }

    /**
     * Aktiviert diese Sprache Global
     */
    public void activate()
    {
        Locale.setCurrentLocale(locale);
    }
}