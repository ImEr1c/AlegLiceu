package com.imer1c.alegliceu.manager.sort;

import com.imer1c.alegliceu.AlegLiceu;
import com.imer1c.alegliceu.manager.PrintSettings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class DistantaPriorityType implements SortPriorityType {
    @Override
    public String getName()
    {
        return "Distanța";
    }

    @Override
    public ObservableList<String> getOptions(AlegLiceu appInstance)
    {
        return FXCollections.observableArrayList("Crescător", "Descrescător");
    }

    @Override
    public int compare(PrintSettings.Item o1, PrintSettings.Item o2, String option)
    {
        if (option.equals("Crescător"))
        {
            return Double.compare(o1.getLiceuData().getDistance(), o2.getLiceuData().getDistance());
        }
        else
        {
            return Double.compare(o2.getLiceuData().getDistance(), o1.getLiceuData().getDistance());
        }
    }

    @Override
    public String toString()
    {
        return getName();
    }
}
