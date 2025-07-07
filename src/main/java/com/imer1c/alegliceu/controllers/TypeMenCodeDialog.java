package com.imer1c.alegliceu.controllers;

import com.imer1c.alegliceu.AlegLiceu;
import com.imer1c.alegliceu.manager.CandidateInfo;
import com.imer1c.alegliceu.manager.UserListManager;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;

public class TypeMenCodeDialog implements ArgInitializer {
    public TextField field;
    public Button confirm;

    @Override
    public void initialize(Object[] args)
    {
        AlegLiceu appInstance = (AlegLiceu) args[0];
        UserListManager userListManager = appInstance.getUserListManager();

        confirm.setOnMouseClicked(e -> {
            String text = field.getText();

            if (text.isBlank())
            {
                return;
            }

            userListManager.setCodMen(text);
            appInstance.openDialog(MainController.FXMLData.of("loading"), 532, 400);

            CandidateInfo candidateInfo = userListManager.getCandidateInfo();
            appInstance.getDataManager().searchForCandidateData(candidateInfo, () -> {
                userListManager.runDifSimulation(appInstance.getDataManager(), candidateInfo.getCodMen(), candidateInfo.getIndex(), () -> {
                    userListManager.runComparationSimulation(appInstance.getDataManager(), candidateInfo.getIndex(), () -> {
                        Platform.runLater(() -> {
                            appInstance.openDialog(MainController.FXMLData.of("grades_and_repartition_dialog", appInstance), 800, 600);
                        });
                    });
                });
            });
        });
    }
}
