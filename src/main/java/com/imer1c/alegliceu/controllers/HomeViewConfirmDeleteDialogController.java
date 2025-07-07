package com.imer1c.alegliceu.controllers;

import com.imer1c.alegliceu.AlegLiceu;
import com.imer1c.alegliceu.manager.Profil;
import com.imer1c.alegliceu.manager.UserListManager;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class HomeViewConfirmDeleteDialogController implements ArgInitializer {

    public Label label;
    public Button no;
    public Button yes;

    @Override
    public void initialize(Object... args)
    {
        UserListManager manager = (UserListManager) args[0];
        Profil profil = (Profil) args[1];
        AlegLiceu appInstance = (AlegLiceu) args[2];
        HomeLiceuItemController itemController = (HomeLiceuItemController) args[3];

        label.setText(String.format("Ești sigur/sigură că dorești să ștergi profilul %s-%s ?", profil.getLiceuName(), profil.getSpecializare()));

        no.setOnMouseClicked(e -> {
            appInstance.closeDialog();
        });

        yes.setOnMouseClicked(e -> {
            itemController.delete(() -> manager.remove(profil));

            appInstance.closeDialog();
        });
    }
}
