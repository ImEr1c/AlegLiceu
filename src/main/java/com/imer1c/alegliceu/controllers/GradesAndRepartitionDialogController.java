package com.imer1c.alegliceu.controllers;

import com.imer1c.alegliceu.AlegLiceu;
import com.imer1c.alegliceu.manager.CandidateInfo;
import com.imer1c.alegliceu.manager.UserListManager;
import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

import java.util.Locale;

public class GradesAndRepartitionDialogController implements ArgInitializer {
    public Label cod;
    public Label scoala;
    public Label romana;
    public Label matematica;
    public Label medie;

    public Button change;
    public Button refresh;

    public Label medieGenerala;
    public Label liceu;
    public Label specializare;

    @Override
    public void initialize(Object[] args)
    {
        AlegLiceu appInstance = (AlegLiceu) args[0];
        UserListManager userListManager = appInstance.getUserListManager();
        CandidateInfo candidateInfo = userListManager.getCandidateInfo();

        cod.setText("Cod: " + candidateInfo.getCodMen());
        scoala.setText("Școala: " + format(candidateInfo.getSchool()));
        romana.setText("Notă Limba Română: " + format(candidateInfo.getRomana()));
        matematica.setText("Nota Matematică: " + format(candidateInfo.getMatematica()));
        medie.setText("Medie Evaluare Națională: " + format(candidateInfo.getMedie()));

        medieGenerala.setText("Medie Generală 5-8: " + format(candidateInfo.getMedieGenerala()));
        liceu.setText("Liceu repartizat: " + format(candidateInfo.getLiceu()));
        specializare.setText("Specializare repartizată: " + format(candidateInfo.getSpecializare()));

        change.setOnMouseClicked(e -> {
            userListManager.resetCandidateInfo();
            appInstance.openDialog(MainController.FXMLData.of("type_men_code_dialog", appInstance), 532, 400);
        });

        refresh.setOnMouseClicked(e -> {
            appInstance.openDialog(MainController.FXMLData.of("loading"), 532, 400);

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

    private String format(Object o)
    {
        if (o == null)
        {
            return "Nu există date";
        }

        if (o instanceof Double d)
        {
            if (d == 0)
            {
                return "Nu există date";
            }

            return String.format(Locale.US, "%.2f", d);
        }

        return o.toString();
    }
}
