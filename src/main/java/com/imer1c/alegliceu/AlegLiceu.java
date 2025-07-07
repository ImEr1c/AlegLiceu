package com.imer1c.alegliceu;

import com.imer1c.alegliceu.controllers.DialogCloseable;
import com.imer1c.alegliceu.controllers.MainController;
import com.imer1c.alegliceu.manager.DataManager;
import com.imer1c.alegliceu.manager.UserListManager;
import com.imer1c.alegliceu.util.Util;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.Region;
import javafx.stage.Stage;

import java.io.IOException;

public class AlegLiceu extends Application {

    private static final AlegLiceu instance = new AlegLiceu();

    public static AlegLiceu getInstance()
    {
        return instance;
    }

    private final DataManager dataManager = new DataManager();
    private final UserListManager userListManager = new UserListManager();
    private MainController mainController;
    private Stage stage;

    @Override
    public void start(Stage stage) throws IOException
    {
        Util.disableSSLCheck();

        userListManager.load();
        boolean loadedData = dataManager.load();

        this.stage = stage;

        Runtime.getRuntime().addShutdownHook(new Thread(userListManager::save));
        Thread.setDefaultUncaughtExceptionHandler((t, e) -> {
            e.printStackTrace();
            Stage s = new Stage();
            s.setScene(new Scene(Util.getView("crash", e), 800, 600));
            s.setTitle("Eroare");
            s.setOnCloseRequest(l -> {
                userListManager.save();
                System.exit(0);
            });
            s.show();
        });

        Scene scene = new Scene(Util.getView("main", this), 1280, 720);
        stage.setTitle("Aleg Liceu");
        stage.setScene(scene);
        stage.show();

        if (loadedData)
        {
            setView(Util.getView("home_view", this));
        }
        else
        {
            setView(Util.getView("loading"));

            new Thread(() -> {
                try
                {
                    dataManager.reloadCachedData();

                    Platform.runLater(() -> setView(Util.getView("home_view", this)));
                } catch (Exception e) {e.printStackTrace();}
            }).start();
        }
    }

    public void setMainController(MainController mainController)
    {
        this.mainController = mainController;
    }

    public UserListManager getUserListManager()
    {
        return userListManager;
    }

    public DataManager getDataManager()
    {
        return dataManager;
    }

    public void setView(Region view)
    {
        this.mainController.setMainView(view);
    }

    public void openDialog(MainController.FXMLData data, int maxWidth, int maxHeight)
    {
        this.mainController.displayDialog(data, maxWidth, maxHeight);
    }

    public void setOnDialogClose(DialogCloseable dialogCloseable)
    {
        this.mainController.setOnDialogClose(dialogCloseable);
    }

    public void closeDialog()
    {
        this.mainController.closeDialog(false);
    }

    public Stage getStage()
    {
        return stage;
    }

    public static void main(String[] args)
    {
        launch();
    }
}