package com.swp.Persistence;

import com.gumse.textures.Texture;
import com.gumse.tools.Output;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;
import com.swp.DataModel.Card;
import com.swp.GUI.Cards.CardRenderer.CardRenderer;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.List;

/**
 * Exportiert eine liste von Karten als PDF Datei
 * @author Tom Beuke
 */
public class PDFExporter
{
    private PDFExporter()
    {

    }

    private static boolean writeElement(Document doc, Element elem)
    {
        try { doc.add(elem); } 
        catch (DocumentException e)
        {
            Output.error("Failed to write element to PDF");
            return false;
        }
        return true;
    }

    /**
     * Exportiert die Karten in eine Ausgabedatei
     *
     * @param cards       Die Karten, welche exportiert werden sollen
     * @param destination Die Ausgabedatei
     * @return Gibt true bei erfolgreichem Exportieren wieder
     */
    public static boolean export(List<Card> cards, String destination)
    {
        Document document = new Document();
        try { PdfWriter.getInstance(document, new FileOutputStream(destination)); } 
        catch(FileNotFoundException | DocumentException e) 
        {
            Output.error("Failed to open file " + destination + ": " + e.getMessage());
            return false;
        }
        document.setPageSize(new Rectangle(595, 842)); // A4 at 72 PPI
        document.open();

        Font font = FontFactory.getFont(FontFactory.COURIER, 16, BaseColor.BLACK);
        CardRenderer renderer = new CardRenderer();
        
        //Write to PDF
        for(Card card : cards)
        {
            Chunk chunk = new Chunk(card.getTitle(), font);
            if(!writeElement(document, chunk)) { return false; }

            renderer.setCard(card);
            renderer.renderToTexture();
            Texture front = renderer.getFrontTexture();
            Texture back = renderer.getBackTexture();

            int bordersize = 60;

            try 
            {
                //Frontside
                byte[] pixels = new byte[front.getData(true).remaining()];
                front.getData().get(pixels);
                Image frontImage = Image.getInstance(front.getSize().x, front.getSize().y, 3, 8, pixels);
                float fitWidth = document.getPageSize().getWidth() - 2 * bordersize;
                float percentX = fitWidth * 100 / frontImage.getScaledWidth();
                frontImage.scalePercent(percentX, -percentX);
                frontImage.setWidthPercentage(0);
                frontImage.setAbsolutePosition(bordersize, 800);
                if(!writeElement(document, frontImage)) { return false; }

                //Backside
                pixels = new byte[back.getData(true).remaining()];
                back.getData().get(pixels);
                Image backImage = Image.getInstance(back.getSize().x, back.getSize().y, 3, 8, pixels);
                fitWidth = document.getPageSize().getWidth() - 2 * bordersize;
                percentX = fitWidth * 100 / backImage.getScaledWidth();
                backImage.scalePercent(percentX, -percentX);
                backImage.setWidthPercentage(0);
                backImage.setAbsolutePosition(bordersize, 410);
                if(!writeElement(document, backImage)) { return false; }


                document.newPage();
            } 
            catch (BadElementException e) 
            {
                Output.error("Failed to read imagedata: " + e.getMessage());
                return false;
            }
        }

        document.close();
        return true;
    }
}