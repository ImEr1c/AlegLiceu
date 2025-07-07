package com.imer1c.alegliceu.manager.sort;

import com.imer1c.alegliceu.AlegLiceu;
import com.imer1c.alegliceu.manager.PrintSettings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class LocuriPriorityType implements SortPriorityType {
    @Override
    public String getName()
    {
        return "Locuri";
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
            return Integer.compare(o1.getProfil().getLocuri(), o2.getProfil().getLocuri());
        }
        else
        {
            return Integer.compare(o2.getProfil().getLocuri(), o1.getProfil().getLocuri());
        }
    }

    @Override
    public String toString()
    {
        return getName();
    }
}
