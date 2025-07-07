package com.imer1c.alegliceu.manager.sort;

import com.imer1c.alegliceu.AlegLiceu;
import com.imer1c.alegliceu.manager.PrintSettings;
import javafx.collections.ObservableList;

public interface SortPriorityType {

    SortPriorityType[] values = new SortPriorityType[]{new ProfilPriorityType(), new MediePriorityType(),
            new LocuriPriorityType(), new DistantaPriorityType()};

    String getName();

    ObservableList<String> getOptions(AlegLiceu appInstance);

    int compare(PrintSettings.Item o1, PrintSettings.Item o2, String option);

}
