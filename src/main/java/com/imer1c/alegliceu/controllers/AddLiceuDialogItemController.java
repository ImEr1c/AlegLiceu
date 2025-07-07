package com.imer1c.alegliceu.controllers;

import com.imer1c.alegliceu.AlegLiceu;
import com.imer1c.alegliceu.manager.Profil;
import com.imer1c.alegliceu.manager.UserListManager;
import javafx.css.PseudoClass;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;

public class AddLiceuDialogItemController implements ArgInitializer {

    private static final PseudoClass RED = PseudoClass.getPseudoClass("red");

    public Label profilLabel;
    public Button add;
    public HBox root;

    @Override
    public void initialize(Object... args)
    {
        Profil profil = (Profil) args[0];
        AlegLiceu appInstance = (AlegLiceu) args[1];

        profilLabel.setText(profil.getSpecializare());

        profilLabel.prefWidthProperty().bind(root.widthProperty().multiply(0.8));

        UserListManager userListManager = appInstance.getUserListManager();

        if (userListManager.contains(profil))
        {
            add.pseudoClassStateChanged(RED, true);
            add.setText("Șterge");
        }

        add.setOnMouseClicked(e -> {
            if (add.getPseudoClassStates().contains(RED))
            {
                add.setText("Adaugă");
                add.pseudoClassStateChanged(RED, false);
                userListManager.remove(profil);
            }
            else
            {
                add.setText("Șterge");
                add.pseudoClassStateChanged(RED, true);
                userListManager.add(profil);
            }
        });
    }
}
