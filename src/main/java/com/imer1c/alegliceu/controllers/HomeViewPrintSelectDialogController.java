package com.imer1c.alegliceu.controllers;

import com.imer1c.alegliceu.AlegLiceu;
import com.imer1c.alegliceu.manager.DataManager;
import com.imer1c.alegliceu.manager.PrintOrder;
import com.imer1c.alegliceu.manager.PrintSettings;
import com.imer1c.alegliceu.manager.UserListManager;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.util.List;

public class HomeViewPrintSelectDialogController implements ArgInitializer {
    public CheckBox liceuName;
    public CheckBox specializare;
    public CheckBox locuri;
    public CheckBox medie;
    public CheckBox cod;

    public CheckBox distance;
    public TextField unitOfMeasure;

    public Label preview;
    public TextField title;
    public CheckBox warning;

    public Button continueButton;

    private final PrintOrder order = new PrintOrder();

    @Override
    public void initialize(Object... args)
    {
        AlegLiceu appInstance = (AlegLiceu) args[0];

        order.toggleSection(PrintOrder.COD_SECTION, true);

        liceuName.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1)
            {
                order.toggleSection(PrintOrder.LICEU_NAME_SECTION, t1);
                updatePreview();
            }
        });

        specializare.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1)
            {
                order.toggleSection(PrintOrder.SPECIALIZARE_SECTION, t1);
                updatePreview();
            }
        });

        locuri.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1)
            {
                order.toggleSection(PrintOrder.LOCURI_SECTION, t1);
                updatePreview();
            }
        });

        medie.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1)
            {
                order.toggleSection(PrintOrder.MEDIE_SECTION, t1);
                updatePreview();
            }
        });

        cod.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1)
            {
                order.toggleSection(PrintOrder.COD_SECTION, t1);
                updatePreview();
            }
        });

        distance.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1)
            {
                unitOfMeasure.setManaged(t1);
                unitOfMeasure.setVisible(t1);

                order.toggleSection(PrintOrder.DISTANCE_SECTION, t1);
                updatePreview();
            }
        });

        continueButton.setOnMouseClicked(e -> {
            UserListManager userListManager = appInstance.getUserListManager();
            DataManager dataManager = appInstance.getDataManager();

            List<PrintSettings.Item> items = userListManager.getUserList()
                    .stream()
                    .map(data -> new PrintSettings.Item(dataManager.getProfil(data), userListManager.getOrCreateLiceuData(data.getLiceuId())))
                    .toList();

            PrintSettings settings = new PrintSettings(title.getText(), warning.isSelected(), order, items);

            appInstance.openDialog(MainController.FXMLData.of("print_choose_type_dialog", settings, appInstance), 532, 400);
        });

        unitOfMeasure.setOnAction(e -> {
            order.setUnitOfMeasure(unitOfMeasure.getText());
        });

        unitOfMeasure.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean t1)
            {
                if (!t1)
                {
                    order.setUnitOfMeasure(unitOfMeasure.getText());
                }
            }
        });
    }

    private void updatePreview()
    {
        String preview = "poziție. " + order.updatePreview();
        this.preview.setText(preview);
    }
}
