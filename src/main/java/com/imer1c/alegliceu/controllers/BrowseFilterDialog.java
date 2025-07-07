package com.imer1c.alegliceu.controllers;

import com.imer1c.alegliceu.AlegLiceu;
import com.imer1c.alegliceu.manager.DataManager;
import com.imer1c.alegliceu.ui.DoubleField;
import com.imer1c.alegliceu.util.BrowseFilter;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.HBox;

public class BrowseFilterDialog implements ArgInitializer {

    public ComboBox<String> profil;
    public DoubleField min;
    public DoubleField max;

    public CheckBox minCheck;
    public CheckBox maxCheck;

    public HBox box1;
    public HBox box2;
    public Button close;

    @Override
    public void initialize(Object... args)
    {
        AlegLiceu appInstance = (AlegLiceu) args[0];
        BrowseFilter filter = (BrowseFilter) args[1];

        DataManager dataManager = appInstance.getDataManager();

        this.box1.prefWidthProperty().bind(profil.widthProperty());
        this.box2.prefWidthProperty().bind(profil.widthProperty());

        this.profil.setItems(FXCollections.observableArrayList(dataManager.getSpecializari()));
        this.profil.setOnAction(e -> {
            filter.setProfil(profil.getSelectionModel().getSelectedItem());
        });

        if (filter.getProfil() != null)
        {
            this.profil.getSelectionModel().select(filter.getProfil());
        }

        this.close.setOnMouseClicked(e -> {
            if (minCheck.isSelected())
            {
                filter.setMedieMin(min.getValue());
            }

            if (maxCheck.isSelected())
            {
                filter.setMedieMax(max.getValue());
            }

            appInstance.closeDialog();
        });

        this.minCheck.setSelected(filter.getMedieMin() != -1);
        this.minCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean val)
            {
                if (!val)
                {
                    filter.setMedieMin(-1);
                }
                else
                {
                    filter.setMedieMin(0);
                }
                min.setDisable(!val);
            }
        });
        this.minCheck.prefWidthProperty().bind(profil.widthProperty().multiply(0.4));
        this.min.setDisable(filter.getMedieMin() == -1);

        if (filter.getMedieMin() != -1)
        {
            this.min.setValue(filter.getMedieMin());
        }

        this.min.setOnAction(e -> {
            filter.setMedieMin(min.getValue());
        });

        this.maxCheck.setSelected(filter.getMedieMax() != -1);
        this.maxCheck.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean val)
            {
                if (!val)
                {
                    filter.setMedieMax(-1);
                }
                else
                {
                    filter.setMedieMax(0);
                }
                max.setDisable(!val);
            }
        });
        this.maxCheck.prefWidthProperty().bind(profil.widthProperty().multiply(0.4));
        this.max.setDisable(filter.getMedieMax() == -1);

        if (filter.getMedieMax() != -1)
        {
            this.max.setValue(filter.getMedieMax());
        }

        this.max.setOnAction(e -> {
            filter.setMedieMax(max.getValue());
        });
    }
}
