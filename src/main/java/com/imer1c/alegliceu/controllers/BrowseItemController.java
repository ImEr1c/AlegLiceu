package com.imer1c.alegliceu.controllers;

import com.imer1c.alegliceu.AlegLiceu;
import com.imer1c.alegliceu.manager.Liceu;
import com.imer1c.alegliceu.manager.Profil;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import java.util.Locale;
import java.util.Map;

public class BrowseItemController implements ArgInitializer {

    public Label name;
    public ComboBox<String> medieSelector;
    public Label medie;
    public VBox root;
    public HBox medieGroup;
    public HBox buttonsGroup;
    public Button addButton;
    public Button infoButton;

    @Override
    public void initialize(Object... args)
    {
        Liceu liceu = (Liceu) args[0];
        AlegLiceu appInstance = (AlegLiceu) args[1];

        name.prefHeightProperty().bind(root.heightProperty().multiply(0.5));
        medieGroup.prefHeightProperty().bind(root.heightProperty().multiply(0.25));
        buttonsGroup.prefHeightProperty().bind(root.heightProperty().multiply(0.25));

        medie.minWidthProperty().bind(medieGroup.widthProperty().multiply(0.15));

        name.setText(liceu.getName());
        Map<String, Profil> profile = liceu.getProfile();

        medieSelector.setItems(FXCollections.observableArrayList(profile.keySet()));
        medieSelector.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String s, String profileType)
            {
                Profil profil = profile.get(profileType);
                medie.setText((profil == null || profil.getMedie() == -1) ? "Medie Indisponibilă" : String.format(Locale.US, "%.2f", profil.getMedie()));
            }
        });

        if (!medieSelector.getItems().isEmpty())
        {
            Object profil = args[2];

            if (profil != null)
            {
                medieSelector.getSelectionModel().select(args[2].toString());
            }
            else
            {
                medieSelector.getSelectionModel().selectFirst();
            }
        }

        addButton.setOnMouseClicked(e -> {
            appInstance.openDialog(MainController.FXMLData.of("add_liceu_dialog", liceu, appInstance), 800, 80 * profile.size());
        });
        infoButton.setOnMouseClicked(e -> {
            appInstance.openDialog(MainController.FXMLData.of("browser_item_info_dialog", liceu, appInstance), 1024, 600);
        });
    }
}
