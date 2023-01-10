package com.swp.DataModel.Language;

import java.util.Map;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public abstract class Language 
{
    /**
     * Enum für die verfügbaren Sprachen.
     */
    public enum AvailableLanguages
    {
        ENGLISH,
        GERMAN,
    };

    public enum LanguageEntry
    {

    };

    protected String name;
    protected String shortName;
    protected Map<LanguageEntry, String> entries;

    protected Language()
    {
    }

    public String getEntry(LanguageEntry entry)
    {
        return entries.get(entry);
    }
}