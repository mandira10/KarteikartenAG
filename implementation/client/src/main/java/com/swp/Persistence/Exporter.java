package com.swp.Persistence;

import com.swp.DataModel.Card;

import java.util.List;

/**
 * Erstellt die jeweiligen Exporter für PDF, XML und JSON Dateien
 * @author Tom Beuke
 */
public class Exporter
{
    /**
     * Die Export-Dateiformate
     */
    public enum ExportFileType
    {
        /** PDF */  EXPORT_PDF,
        /** XML */  EXPORT_XML,
        /** JSON */ EXPORT_JSON,
    }

    ExportFileType iType;

    /**
     * Der Hauptkonstruktor der Klasse Exporter
     *
     * @param filetype Der dateityp
     */
    public Exporter(ExportFileType filetype)
    {
        this.iType = filetype;
    }

    private String prepareDestination(String givendest, String ext)
    {
        String retStr = givendest;
        if(!givendest.startsWith(ext, givendest.length() - ext.length()))
            retStr += ext;
        return retStr;
    }

    /**
     * Exportiert Karten in die angegebene Datei
     *
     * @param cards       Die zu exportierenden Karten
     * @param destination Die Ausgabedatei
     * @return Ob das Exportieren erfolgreich war
     */
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