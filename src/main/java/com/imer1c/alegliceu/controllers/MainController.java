package com.imer1c.alegliceu.controllers;

import com.imer1c.alegliceu.AlegLiceu;
import com.imer1c.alegliceu.SpringInterpolator;
import com.imer1c.alegliceu.util.Util;
import javafx.animation.FadeTransition;
import javafx.animation.ParallelTransition;
import javafx.animation.ScaleTransition;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.layout.Region;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.io.IOException;
import java.net.URL;

public class MainController implements Initializable {
    public StackPane mainView;
    public Region blackOverlay;
    public StackPane popupContainer;

    private DialogCloseable dialogCloseable;

    @Override
    public void initialize(AlegLiceu appInstance)
    {
        appInstance.setMainController(this);

    }

    public void setMainView(Region region)
    {
        FadeTransition fade = fade(Duration.millis(300), mainView, 1, 0);
        fade.setOnFinished(e -> {
            mainView.getChildren().clear();
            mainView.getChildren().add(region);

            fade(Duration.millis(300), mainView, 0, 1).play();
        });
        fade.play();
    }

    public void displayDialog(FXMLData data, int maxWidth, int maxHeight)
    {
        if (!popupContainer.getChildren().isEmpty())
        {
            closeDialog(true);
        }

        Region region;

        try
        {
            URL url = Util.class.getClassLoader().getResource("fxml/" + data.getPath() + ".fxml");
            FXMLLoader loader = new FXMLLoader(url);
            region = loader.load();

            Object controller = loader.getController();

            if (controller instanceof ArgInitializer i)
            {
                i.initialize(data.getArgs());
            }

            if (controller instanceof DialogCloseable c)
            {
                this.dialogCloseable = c;
            }
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }

        this.blackOverlay.setVisible(true);
        this.blackOverlay.setMouseTransparent(false);

        this.popupContainer.setVisible(true);
        this.popupContainer.setMouseTransparent(false);

        this.popupContainer.getChildren().add(region);

        this.popupContainer.setMaxWidth(maxWidth);
        this.popupContainer.setMaxHeight(maxHeight);

        ParallelTransition transition = new ParallelTransition(
                fade(Duration.millis(500), blackOverlay, -1, 1),
                fade(Duration.millis(500), popupContainer, 0, 1),
                scale(Duration.millis(500), popupContainer, 0.9, 0.9, 1, 1)
        );
        transition.setInterpolator(SpringInterpolator.INTERPOLATOR_0);
        if (region.getId() == null || !region.getId().equals("loading"))
        {
            transition.setOnFinished(e -> {
                blackOverlay.setOnMouseClicked(l -> closeDialog(false));
            });
        }
        transition.play();
    }

    public void closeDialog(boolean openAnother)
    {
        if (dialogCloseable != null)
        {
            this.dialogCloseable.onClose();
            this.dialogCloseable = null;
        }

        this.blackOverlay.setOnMouseClicked(null);

        if (openAnother)
        {
            this.popupContainer.getChildren().clear();
            return;
        }

        ParallelTransition transition = new ParallelTransition(
                fade(Duration.millis(500), blackOverlay, 1, 0),
                fade(Duration.millis(500), popupContainer, 1, 0),
                scale(Duration.millis(500), popupContainer, 1, 1, 0.8, 0.8)
        );
        transition.setInterpolator(SpringInterpolator.INTERPOLATOR_0);
        transition.setOnFinished(e -> {
            this.popupContainer.getChildren().clear();

            this.blackOverlay.setVisible(false);
            this.blackOverlay.setMouseTransparent(true);

            this.popupContainer.setVisible(false);
            this.popupContainer.setMouseTransparent(true);
        });
        transition.play();
    }

    public void setOnDialogClose(DialogCloseable dialogCloseable)
    {
        this.dialogCloseable = dialogCloseable;
    }

    private FadeTransition fade(Duration duration, Node node, double from, double to)
    {
        if (from != -1)
        {
            node.setOpacity(from);
        }

        FadeTransition transition = new FadeTransition(duration, node);
        transition.setToValue(to);

        return transition;
    }

    private ScaleTransition scale(Duration duration, Node node, double fromX, double fromY, double toX, double toY)
    {
        node.setScaleX(fromX);
        node.setScaleY(fromY);

        ScaleTransition transition = new ScaleTransition(duration, node);
        transition.setToX(toX);
        transition.setToY(toY);

        return transition;
    }

    public static class FXMLData {
        private final String path;
        private final Object[] args;

        public FXMLData(String path, Object[] args)
        {
            this.path = path;
            this.args = args;
        }

        public static FXMLData of(String path, Object... args)
        {
            return new FXMLData(path, args);
        }

        public Object[] getArgs()
        {
            return args;
        }

        public String getPath()
        {
            return path;
        }
    }
}
