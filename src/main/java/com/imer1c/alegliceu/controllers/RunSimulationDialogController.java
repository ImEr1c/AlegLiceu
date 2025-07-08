package com.imer1c.alegliceu.controllers;

import com.imer1c.alegliceu.AlegLiceu;
import com.imer1c.alegliceu.ui.DoubleField;
import javafx.scene.control.Button;

public class RunSimulationDialogController implements ArgInitializer {

    public DoubleField field;
    public Button confirm;

    @Override
    public void initialize(Object... args)
    {
        HomeViewController controller = (HomeViewController) args[0];
        AlegLiceu appInstance = (AlegLiceu) args[1];

        confirm.setOnMouseClicked(e -> {
            double value = field.getValue();

            if (value <= 0)
            {
                return;
            }

            boolean index = field.getPromptText().equals("Poziție");
            int estimatedIndex;

            if (index)
            {
                estimatedIndex = (int) field.getValue();
            }
            else
            {
                appInstance.openDialog(MainController.FXMLData.of("loading"), 532, 400);
                estimatedIndex = appInstance.getDataManager().getEstimatedIndex(value);
            }

            appInstance.openDialog(MainController.FXMLData.of("choose_simulation_dialog", controller, estimatedIndex, appInstance), 532, 400);
        });
    }
}
