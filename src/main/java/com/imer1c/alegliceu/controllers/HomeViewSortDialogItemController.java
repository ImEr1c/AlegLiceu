package com.imer1c.alegliceu.controllers;

import com.imer1c.alegliceu.AlegLiceu;
import com.imer1c.alegliceu.manager.sort.SortPriority;
import com.imer1c.alegliceu.manager.sort.SortPriorityType;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;

public class HomeViewSortDialogItemController implements ArgInitializer {
    public ComboBox<SortPriorityType> priority;
    public ComboBox<String> options;
    public Button delete;

    @Override
    public void initialize(Object... args)
    {
        AlegLiceu appInstance = (AlegLiceu) args[0];
        SortPriority sortPriority = (SortPriority) args[1];
        HomeViewSortDialogController controller = (HomeViewSortDialogController) args[2];

        this.priority.setItems(FXCollections.observableArrayList(SortPriorityType.values));
        this.priority.valueProperty().addListener(new ChangeListener<SortPriorityType>() {
            @Override
            public void changed(ObservableValue<? extends SortPriorityType> observableValue, SortPriorityType sortPriorityType, SortPriorityType t1)
            {
                sortPriority.setType(t1);

                options.setItems(t1.getOptions(appInstance));
            }
        });

        this.options.valueProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String t1)
            {
                sortPriority.setOption(t1);
            }
        });

        this.delete.setOnMouseClicked(e -> {
            controller.delete(sortPriority);
        });
    }
}
