package com.imer1c.alegliceu.controllers;

import com.imer1c.alegliceu.AlegLiceu;
import javafx.fxml.FXML;
import javafx.scene.control.Button;

public class ChooseSimulationDataDialogController implements ArgInitializer {

    public Button index;
    public Button medie;

    @Override
    public void initialize(Object... args)
    {
        HomeViewController controller = (HomeViewController) args[0];
        AlegLiceu appInstance = (AlegLiceu) args[1];

        index.setOnMouseClicked(e -> {
            appInstance.openDialog(MainController.FXMLData.of("run_simulation_index_dialog", controller, appInstance), 532, 400);
        });

        medie.setOnMouseClicked(e -> {
            appInstance.openDialog(MainController.FXMLData.of("run_simulation_medie_dialog", controller, appInstance), 532, 400);
        });
    }
}
