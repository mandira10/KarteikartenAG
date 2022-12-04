package com.swp.Persistence;

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

    public boolean export(Card[] cards)
    {
        switch(this.iType)
        {
            case EXPORT_PDF:  return PDFExporter.export(cards);
            case EXPORT_XML:  return XMLExporter.export(cards);
            case EXPORT_JSON: return JSONExporter.export(cards);
        }
        
        return false;
    }
}