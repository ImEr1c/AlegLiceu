package com.imer1c.alegliceu.controllers;

import com.imer1c.alegliceu.AlegLiceu;
import com.imer1c.alegliceu.manager.PrintSettings;
import javafx.scene.control.Button;
import javafx.stage.FileChooser;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class PrintChooseTypeDialogController implements ArgInitializer {
    public Button save;
    public Button phone;

    private PrintSettings settings;
    private AlegLiceu appInstance;

    @Override
    public void initialize(Object... args)
    {
        this.settings = (PrintSettings) args[0];
        this.appInstance = (AlegLiceu) args[1];

        save.setOnMouseClicked(e -> {
            saveFile();
        });
        phone.setOnMouseClicked(e -> {
            appInstance.openDialog(MainController.FXMLData.of("print_send_to_phone_dialog", settings), 800, 600);
        });
    }

    private void saveFile()
    {
        FileChooser chooser = new FileChooser();
        chooser.setTitle("Salvează Lista de licee");
        chooser.setInitialFileName("lista licee.pdf");
        File file = chooser.showSaveDialog(appInstance.getStage());

        if (file == null)
        {
            return;
        }

        try (FileOutputStream outputStream = new FileOutputStream(file))
        {
            settings.createPDF(outputStream);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        appInstance.openDialog(MainController.FXMLData.of("print_save_success_dialog"), 532, 200);
    }
}
