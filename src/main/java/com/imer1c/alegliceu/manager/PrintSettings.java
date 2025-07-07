package com.imer1c.alegliceu.manager;

import com.imer1c.alegliceu.controllers.HomeViewController;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.common.PDRectangle;
import org.apache.pdfbox.pdmodel.font.PDType0Font;

import java.awt.*;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;

public class PrintSettings {

    private final String title;
    private final boolean warning;
    private final PrintOrder order;
    private final List<Item> items;

    public PrintSettings(String title, boolean warning, PrintOrder order, List<Item> items)
    {
        this.title = title;
        this.warning = warning;
        this.order = order;
        this.items = items;
    }

    public void createPDF(OutputStream outputStream)
    {
        PDDocument document = new PDDocument();
        PDPage pdPage = new PDPage(PDRectangle.A4);
        document.addPage(pdPage);

        try
        {
            PDPageContentStream stream = new PDPageContentStream(document, pdPage);

            PDType0Font font = PDType0Font.load(document, HomeViewController.class.getClassLoader()
                    .getResourceAsStream("font/FreeSerif.ttf"));
            PDType0Font fontBold = PDType0Font.load(document, HomeViewController.class.getClassLoader()
                    .getResourceAsStream("font/FreeSerifBold.ttf"));

            float y = 750;

            float titleWidth = fontBold.getStringWidth(title) / 1000 * 40;
            float titleX = (PDRectangle.A4.getWidth() - titleWidth) / 2;

            stream.beginText();
            stream.setFont(fontBold, 40);
            stream.setNonStrokingColor(Color.BLACK);
            stream.newLineAtOffset(titleX, y);
            stream.showText(title);
            stream.endText();

            y -= 50;

            if (warning)
            {
                stream.beginText();
                stream.setFont(fontBold, 15);
                stream.setNonStrokingColor(Color.RED);
                stream.newLineAtOffset(50, y);
                stream.showText("! ATENȚIE: Codurile nu au fost verificate și este posibil să nu fie 100% corecte.");
                stream.endText();

                y -= 20;

                stream.beginText();
                stream.setFont(fontBold, 15);
                stream.setNonStrokingColor(Color.RED);
                stream.newLineAtOffset(50, y);
                stream.showText("Verificați folosind broșura de licee primită de la școală pentru a fi siguri.");
                stream.endText();

                y -= 20;
            }

            y -= 15;

            for (int i = 0; i < items.size(); i++)
            {
                Item item = items.get(i);

                stream.beginText();
                stream.setFont(font, 15);
                stream.setNonStrokingColor(Color.BLACK);
                stream.newLineAtOffset(50, y);
                stream.showText(order.createLine(i, item));
                stream.endText();

                y -= 20;

                if (y < 20)
                {
                    y = PDRectangle.A4.getHeight() - 20;
                    stream.close();

                    pdPage = new PDPage(PDRectangle.A4);
                    document.addPage(pdPage);

                    stream = new PDPageContentStream(document, pdPage);
                }
            }

            stream.close();

            document.save(outputStream);
            document.close();

        }
        catch (IOException ex)
        {
            throw new RuntimeException(ex);
        }
    }

    public static class Item {
        private final Profil profil;
        private final LiceuSmallData liceuData;

        public Item(Profil profil, LiceuSmallData liceuData)
        {
            this.profil = profil;
            this.liceuData = liceuData;
        }

        public Profil getProfil()
        {
            return profil;
        }

        public LiceuSmallData getLiceuData()
        {
            return liceuData;
        }
    }
}
