package com.imer1c.alegliceu.manager.sort;

import com.imer1c.alegliceu.AlegLiceu;
import com.imer1c.alegliceu.manager.PrintSettings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class MediePriorityType implements SortPriorityType {
    @Override
    public String getName()
    {
        return "Medie";
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
            return Double.compare(o1.getProfil().getMedie(), o2.getProfil().getMedie());
        }
        else
        {
            return Double.compare(o2.getProfil().getMedie(), o1.getProfil().getMedie());
        }
    }

    @Override
    public String toString()
    {
        return getName();
    }
}
