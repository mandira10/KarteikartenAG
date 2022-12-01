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

    public Exporter(Card[] cards, ExportFileType filetype)
    {
        this.iType = filetype;
        switch(this.iType)
        {
            case EXPORT_PDF:  PDFExporter.export(cards);  break;
            case EXPORT_XML:  XMLExporter.export(cards);  break;
            case EXPORT_JSON: JSONExporter.export(cards); break;
        }
    }
}