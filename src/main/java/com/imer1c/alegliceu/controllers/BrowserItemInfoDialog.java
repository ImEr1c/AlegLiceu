package com.imer1c.alegliceu.controllers;

import com.imer1c.alegliceu.AlegLiceu;
import com.imer1c.alegliceu.manager.Liceu;
import com.imer1c.alegliceu.manager.LiceuSmallData;
import com.imer1c.alegliceu.manager.Profil;
import com.imer1c.alegliceu.manager.UserListManager;
import com.imer1c.alegliceu.ui.DoubleField;
import com.imer1c.alegliceu.ui.ReadOnlyTextField;
import javafx.beans.property.ReadOnlyStringWrapper;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.util.Callback;

import java.util.Locale;

public class BrowserItemInfoDialog implements ArgInitializer, DialogCloseable {

    public Label title;
    public ReadOnlyTextField telefon;
    public ReadOnlyTextField adresa;
    public TableView<Profil> profileTable;

    public DoubleField distanta;
    public TextField transportDetails;

    private LiceuSmallData liceuData;

    @Override
    public void initialize(Object... args)
    {
        Liceu liceu = (Liceu) args[0];
        AlegLiceu appInstance = (AlegLiceu) args[1];

        title.setText(liceu.getName());

        telefon.setText("Telefon: " + liceu.getTelefon());
        adresa.setText("Adresă: " + liceu.getAdress());

        TableColumn<Profil, String> profilColumn = new TableColumn<>("Profil");
        profilColumn.setCellValueFactory(new PropertyValueFactory<>("specializare"));
        TableColumn<Profil, Integer> locuriColumn = new TableColumn<>("Locuri");
        locuriColumn.setCellValueFactory(new PropertyValueFactory<>("locuri"));
        TableColumn<Profil, String> medieColumn = new TableColumn<>("Medie");
        medieColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Profil, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Profil, String> param)
            {
                return new ReadOnlyStringWrapper(String.format(Locale.US, "%.2f", param.getValue().getMedie()));
            }
        });
        TableColumn<Profil, Integer> codColumn = new TableColumn<>("Cod");
        codColumn.setCellValueFactory(new PropertyValueFactory<>("cod"));
        TableColumn<Profil, String> indexColumn = new TableColumn<>("Index");
        indexColumn.setCellValueFactory(new Callback<TableColumn.CellDataFeatures<Profil, String>, ObservableValue<String>>() {
            @Override
            public ObservableValue<String> call(TableColumn.CellDataFeatures<Profil, String> param)
            {
                int index = param.getValue().getIndex();
                return new ReadOnlyStringWrapper(index == Integer.MAX_VALUE ? "-" : String.valueOf(index));
            }
        });

        profileTable.getColumns().addAll(profilColumn, locuriColumn, medieColumn, codColumn, indexColumn);
        profileTable.setItems(FXCollections.observableArrayList(liceu.getProfile().values()));
        profileTable.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY_ALL_COLUMNS);
        profileTable.setPrefWidth(700);
        profileTable.setPrefHeight(42 + 45 * liceu.getProfile().size());
        profileTable.setFixedCellSize(45);
        profileTable.setFocusTraversable(false);
        profileTable.setFocusModel(null);
        profileTable.setSelectionModel(null);

        profileTable.getColumns().forEach(c -> {
            c.setSortable(false);
            c.setReorderable(false);
        });

        UserListManager manager = appInstance.getUserListManager();
        this.liceuData = manager.getOrCreateLiceuData(liceu.getId());

        if (liceuData != null)
        {
            if (liceuData.getDistance() >= 0)
            {
                distanta.setValue(liceuData.getDistance());
            }

            if (liceuData.getTransportDetails() != null)
            {
                transportDetails.setText(liceuData.getTransportDetails());
            }
        }
    }

    public void onClose()
    {
        liceuData.setDistance(distanta.getValue());
        liceuData.setTransportDetails(transportDetails.getText());
    }
}
