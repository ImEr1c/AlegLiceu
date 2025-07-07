package com.imer1c.alegliceu.manager.sort;

import com.imer1c.alegliceu.AlegLiceu;
import com.imer1c.alegliceu.manager.PrintSettings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ProfilPriorityType implements SortPriorityType {
    @Override
    public String getName()
    {
        return "Profil";
    }

    @Override
    public ObservableList<String> getOptions(AlegLiceu appInstance)
    {
        return FXCollections.observableArrayList(appInstance.getDataManager().getSpecializari());
    }

    @Override
    public int compare(PrintSettings.Item o1, PrintSettings.Item o2, String option)
    {
        boolean o1profil = o1.getProfil().getSpecializare().equals(option);
        boolean o2profil = o2.getProfil().getSpecializare().equals(option);

        if (o1profil == o2profil)
        {
            return 0;
        }

        return o1profil ? -1 : 1;
    }

    @Override
    public String toString()
    {
        return getName();
    }
}
