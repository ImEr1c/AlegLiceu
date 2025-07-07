package com.imer1c.alegliceu.controllers;

import com.imer1c.alegliceu.AlegLiceu;
import com.imer1c.alegliceu.manager.DataManager;
import com.imer1c.alegliceu.manager.ShowSimulationData;
import com.imer1c.alegliceu.manager.UserListManager;
import javafx.application.Platform;
import javafx.scene.control.Button;

public class ChooseSimulationDialogController implements ArgInitializer {
    public Button dif;
    public Button comparation;

    @Override
    public void initialize(Object... args)
    {
        HomeViewController controller = (HomeViewController) args[0];
        int index = (int) args[1];
        AlegLiceu appInstance = (AlegLiceu) args[2];

        DataManager dataManager = appInstance.getDataManager();
        UserListManager userListManager = appInstance.getUserListManager();

        dif.setOnMouseClicked(e -> {
            if (userListManager.hasCandidateInfo())
            {
                controller.afterSimulation(ShowSimulationData.DIF);
            }
            else
            {
                controller.runDifSimulation(index);
            }
        });

        comparation.setOnMouseClicked(e -> {
            if (userListManager.hasCandidateInfo())
            {
                controller.afterSimulation(ShowSimulationData.COMPARATION);
            }
            else
            {
                appInstance.openDialog(MainController.FXMLData.of("loading"), 532, 400);
                userListManager.runComparationSimulation(dataManager, index, () -> {
                    Platform.runLater(appInstance::closeDialog);
                });
            }
        });
    }
}
