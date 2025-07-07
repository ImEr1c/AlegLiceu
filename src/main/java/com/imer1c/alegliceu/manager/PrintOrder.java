package com.imer1c.alegliceu.manager;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.Function;

public class PrintOrder {

    public static final Section LICEU_NAME_SECTION = new Section("Nume Liceu", i -> i.getProfil().getLiceuName());
    public static final Section SPECIALIZARE_SECTION = new Section("Nume Profil", i -> i.getProfil().getSpecializare());
    public static final Section LOCURI_SECTION = new Section("Nr. Locuri", i -> String.valueOf(i.getProfil().getLocuri()));
    public static final Section MEDIE_SECTION = new Section("Medie", i -> String.format(Locale.US, "%.2f", i.getProfil().getMedie()));
    public static final Section COD_SECTION = new Section("Cod", i -> "Cod: " + i.getProfil().getCod());
    public static final Section DISTANCE_SECTION = new Section("Distanță Liceu", i -> {
        double distance = i.getLiceuData().getDistance();

        if (distance <= 0)
        {
            return null;
        }

        return String.valueOf(distance == (int) distance ? (int) distance : distance);
    });

    private final List<Section> sections = new ArrayList<>();
    private String unitOfMeasure;

    public void setUnitOfMeasure(String unitOfMeasure)
    {
        this.unitOfMeasure = unitOfMeasure;
    }

    public String updatePreview()
    {
        if (sections.isEmpty())
        {
            return "";
        }

        StringBuilder builder = new StringBuilder();

        for (Section section : sections)
        {
            builder.append(section.id).append("-");
        }
        builder.deleteCharAt(builder.length() - 1);

        return builder.toString();
    }

    public void toggleSection(Section section, boolean added)
    {
        if (added)
        {
            sections.add(section);
        }
        else
        {
            sections.remove(section);
        }
    }

    public String createLine(int index, PrintSettings.Item item)
    {
        StringBuilder builder = new StringBuilder(String.valueOf(index + 1)).append(". ");

        for (Section section : sections)
        {
            String data = section.getData(item);

            if (section == DISTANCE_SECTION && data != null)
            {
                builder.append(data).append(" ").append(unitOfMeasure);
            }
            else
            {
                builder.append(data).append(" - ");
            }

        }

        return builder.substring(0, builder.length() - 3);
    }

    public static class Section {
        private final String id;
        private final Function<PrintSettings.Item, String> getter;

        public Section(String id, Function<PrintSettings.Item, String> getter)
        {
            this.id = id;
            this.getter = getter;
        }

        public String getId()
        {
            return id;
        }

        public String getData(PrintSettings.Item item)
        {
            return getter.apply(item);
        }
    }
}
