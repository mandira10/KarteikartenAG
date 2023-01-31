package com.swp.Persistence;

import java.util.List;

import com.swp.DataModel.Card;

public class Exporter 
{
    public enum ExportFileType
    {
        EXPORT_PDF,
        EXPORT_XML,
        EXPORT_JSON,
    }

    ExportFileType iType;

    public Exporter(ExportFileType filetype)
    {
        this.iType = filetype;
    }

    private String prepareDestination(String givendest, String ext)
    {
        String retStr = givendest;
        if(!givendest.substring(givendest.length() - ext.length(), givendest.length()).equals(ext))
            retStr += ext;
        return retStr;
    }

    public boolean export(List<Card> cards, String destination)
    {
        switch(this.iType)
        {
            case EXPORT_PDF:  return PDFExporter.export(cards,  prepareDestination(destination, ".pdf"));
            case EXPORT_XML:  return XMLExporter.export(cards,  prepareDestination(destination, ".xml"));
            case EXPORT_JSON: return JSONExporter.export(cards, prepareDestination(destination, ".json"));
        }
        
        return false;
    }
}