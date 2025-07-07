package com.imer1c.alegliceu.controllers;

import com.imer1c.alegliceu.AlegLiceu;
import com.imer1c.alegliceu.manager.Profil;
import com.imer1c.alegliceu.manager.ShowSimulationData;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.css.PseudoClass;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.Locale;

public class HomeLiceuItemController implements ArgInitializer {

    private static final PseudoClass CLOSE = PseudoClass.getPseudoClass("close");

    public Label medie;
    public Label name;
    public Label profil;
    public Label cod;

    public HBox root;
    public VBox nameProfileGroup;
    public Label index;
    public Button delete;
    public Button move;

    private boolean paste;

    private HomeViewController homeViewController;
    private int indexInt;

    @Override
    public void initialize(Object... args)
    {
        Profil profil = (Profil) args[0];
        AlegLiceu appInstance = (AlegLiceu) args[1];
        this.indexInt = (int) args[2];
        HomeViewController controller = (HomeViewController) args[3];
        boolean onCopyBar = (boolean) args[4];
        ShowSimulationData showingSimData = (ShowSimulationData) args[5];

        this.homeViewController = controller;

        if (onCopyBar)
        {
            this.index.setVisible(false);
            this.index.setManaged(false);

            move.setVisible(false);
            move.setManaged(false);

            controller.setMovingController(this);

            playScale(true, root, null);

            delete.pseudoClassStateChanged(CLOSE, true);
        }
        else
        {
            this.index.setText((indexInt + 1) + ".");

            if (controller != null)
            {
                controller.registerController(indexInt, this);
            }
        }

        if (controller != null && controller.hasContentInCopyBar())
        {
            setCopyingStatus(true);
        }

        this.medie.setText(String.format(Locale.US, "Medie: %.2f", profil.getMedie()));
        this.name.setText(profil.getLiceuName());
        this.profil.setText(profil.getSpecializare());
        this.profil.setTextFill(Color.web(appInstance.getDataManager().getColor(profil.getSpecializare())));
        this.cod.setText("Cod: " + profil.getCod());

        if (showingSimData == ShowSimulationData.COMPARATION && profil.getRepartitionColor() != null)
        {
            this.name.setBackground(new Background(new BackgroundFill(Color.web(profil.getRepartitionColor()), CornerRadii.EMPTY, Insets.EMPTY)));
        }

        this.move.setOnMouseClicked(e -> {
            if (!paste)
            {
                playScale(false, root, l -> controller.setMovingIndex(indexInt));
            }
        });

        this.delete.setOnMouseClicked(e -> {
            if (onCopyBar)
            {
                playScale(false, root, l -> controller.closeMovingIndex());
            }
            else
            {
                appInstance.openDialog(MainController.FXMLData.of("home_view_confirm_delete_dialog", appInstance.getUserListManager(), profil, appInstance, this), 532, 200);
            }
        });
    }

    public void delete(Runnable after)
    {
        FadeTransition transition = new FadeTransition(Duration.millis(500), root);
        transition.setToValue(0);
        transition.play();

        TranslateTransition translateTransition = homeViewController.moveAllCellsUpForDelete(indexInt + 1);

        if (translateTransition != null)
        {
            translateTransition.setOnFinished(e -> {
                after.run();
            });
        }
    }

    public void closedCopyBar(Runnable after)
    {
        playScale(false, root, l -> after.run());
    }

    public void appear()
    {
        playScale(true, root, null);
    }

    public void setCopyingStatus(boolean paste)
    {
        this.move.setVisible(!paste);
        this.move.setMouseTransparent(paste);
        this.paste = paste;
    }

    private static void playScale(boolean in, Parent root, EventHandler<ActionEvent> handler)
    {
        if (in)
        {
            root.setOpacity(0.5);
            root.setScaleX(0);
            root.setScaleY(0);
        }

        FadeTransition fade = new FadeTransition(Duration.millis(500), root);
        fade.setToValue(in ? 1 : 0);

        ScaleTransition scale = new ScaleTransition(Duration.millis(500), root);
        scale.setToX(in ? 1 : 0.5);
        scale.setToY(in ? 1 : 0.5);

        ParallelTransition transition = new ParallelTransition(fade, scale);
        transition.setOnFinished(handler);
        transition.play();
    }
}
