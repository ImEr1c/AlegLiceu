package com.imer1c.alegliceu.controllers;

import com.imer1c.alegliceu.AlegLiceu;
import com.imer1c.alegliceu.manager.Liceu;
import com.imer1c.alegliceu.util.Util;
import javafx.scene.Parent;
import javafx.scene.layout.VBox;

public class AddLiceuDialogController implements ArgInitializer {
    public VBox root;

    @Override
    public void initialize(Object... args)
    {
        Liceu liceu = (Liceu) args[0];
        AlegLiceu appInstance = (AlegLiceu) args[1];

        liceu.getProfile().forEach(((s, profil) -> {
            Parent view = Util.getView("add_liceu_dialog_item", profil, appInstance);

            root.getChildren().add(view);
        }));

        root.getChildren().getLast().setStyle("-fx-border-width: 0");

    }
}
