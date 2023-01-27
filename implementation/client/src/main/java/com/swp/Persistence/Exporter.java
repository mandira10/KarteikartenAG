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

    public boolean export(List<Card> cards, String destination)
    {
        switch(this.iType)
        {
            case EXPORT_PDF:  return PDFExporter.export(cards, destination);
            case EXPORT_XML:  return XMLExporter.export(cards, destination);
            case EXPORT_JSON: return JSONExporter.export(cards, destination);
        }
        
        return false;
    }
}