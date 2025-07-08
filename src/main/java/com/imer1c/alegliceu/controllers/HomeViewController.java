package com.imer1c.alegliceu.controllers;

import com.imer1c.alegliceu.AlegLiceu;
import com.imer1c.alegliceu.manager.*;
import com.imer1c.alegliceu.util.Util;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Parent;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.Button;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.skin.ListViewSkin;
import javafx.scene.control.skin.VirtualFlow;
import javafx.scene.image.WritableImage;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.util.HashMap;
import java.util.Map;

public class HomeViewController implements Initializable {

    private final Map<Integer, HomeLiceuItemController> controllerMap = new HashMap<>();

    public ListView<ProfilSmallData> list;
    public VBox root;
    public Button add;
    public HBox copyBar;

    public Button gradesInfoScreen;
    public Button print;
    public Button simulation;
    public Button sort;

    private AlegLiceu appInstance;
    private UserListManager userListManager;
    private DataManager dataManager;

    private int movingIndex = -1;
    private HomeLiceuItemController movingController;

    private ShowSimulationData showingSimulationData = ShowSimulationData.OFF;

    @Override
    public void initialize(AlegLiceu appInstance)
    {
        list.maxWidthProperty().bind(root.widthProperty().multiply(0.8));
        list.maxHeightProperty().bind(root.heightProperty().multiply(0.9));

        copyBar.maxWidthProperty().bind(list.widthProperty().subtract(20));

        add.setOnMouseClicked(e -> appInstance.setView(Util.getView("browse_view", appInstance)));
        gradesInfoScreen.setOnMouseClicked(e -> {
            if (userListManager.getCandidateInfo() == null)
            {
                appInstance.openDialog(MainController.FXMLData.of("type_men_code_dialog", appInstance), 532, 400);
            }
            else
            {
                appInstance.openDialog(MainController.FXMLData.of("grades_and_repartition_dialog", appInstance), 800, 600);
            }
        });
        simulation.setOnMouseClicked(e -> {
            if (showingSimulationData != ShowSimulationData.OFF)
            {
                this.showingSimulationData = ShowSimulationData.OFF;
                this.simulation.setText("Pornește Simularea");

                updateList();
            }
            else
            {
                CandidateInfo candidateInfo = userListManager.getCandidateInfo();

                if (!userListManager.hasCandidateInfo())
                {
                    appInstance.openDialog(MainController.FXMLData.of("choose_simulation_data_dialog", this, appInstance), 532, 400);
                }
                else
                {
                    appInstance.openDialog(MainController.FXMLData.of("choose_simulation_dialog", this, candidateInfo.getIndex(), appInstance),
                            532, 400);
                }
            }
        });
        print.setOnMouseClicked(e -> {
            appInstance.openDialog(MainController.FXMLData.of("home_view_print_select_dialog", appInstance), 800, 700);
        });
        sort.setOnMouseClicked(e -> {
            appInstance.openDialog(MainController.FXMLData.of("home_view_sort_dialog", appInstance), 800, 600);
        });

        this.appInstance = appInstance;

        this.userListManager = appInstance.getUserListManager();
        ObservableList<ProfilSmallData> userList = userListManager.getUserList();

        this.dataManager = appInstance.getDataManager();

        list.setItems(userList);
        list.setSelectionModel(null);
        list.setFocusTraversable(false);
        list.setCellFactory(listView -> new ListCell<>() {

            @Override
            protected void updateItem(ProfilSmallData item, boolean empty)
            {
                super.updateItem(item, empty);

                if (item == null || empty)
                {
                    setGraphic(null);
                }
                else
                {
                    Profil profil = dataManager.getProfil(item);
                    Parent view = createItem(profil, getIndex(), false);

                    LiceuSmallData liceuData = userListManager.getOrCreateLiceuData(profil.getLiceuId());

                    if (showingSimulationData == ShowSimulationData.DIF && liceuData.getRepartitionType() != null)
                    {
                        view.setStyle(liceuData.getRepartitionType().style);
                    }

                    if (showingSimulationData == ShowSimulationData.COMPARATION && liceuData.getProfilColor(profil.getSpecializare()) != null)
                    {
                        view.setStyle("-fx-background-color: " + liceuData.getProfilColor(profil.getSpecializare()));
                    }

                    prefWidthProperty().bind(list.widthProperty().subtract(20));

                    setPadding(new Insets(10, 5, 10, 5));


                    setGraphic(view);
                }
            }
        });
    }

    private Parent createItem(Profil profil, int index, boolean onCopyBar)
    {
        Parent view = Util.getView("home_liceu_item", profil, appInstance, index, this, onCopyBar, showingSimulationData);

        view.setOnDragDetected(e -> {
            Dragboard db = view.startDragAndDrop(TransferMode.MOVE);
            ClipboardContent content = new ClipboardContent();
            content.putString(String.valueOf(index));
            db.setContent(content);

            SnapshotParameters parameters = new SnapshotParameters();
            WritableImage snapshot = view.snapshot(parameters, null);
            db.setDragView(snapshot);

            FadeTransition transition = new FadeTransition(Duration.millis(500), view);
            transition.setToValue(0.5);
            transition.play();

            e.consume();
        });

        view.setOnDragDone(e -> {
            if (e.getTransferMode() == null)
            {
                FadeTransition transition = new FadeTransition(Duration.millis(500), view);
                transition.setToValue(1);
                transition.play();
            }
        });

        if (!onCopyBar)
        {
            view.setOnDragOver(e -> {
                if (e.getGestureSource() != this && e.getDragboard().hasString() && Integer.parseInt(e.getDragboard().getString()) != index)
                {
                    e.acceptTransferModes(TransferMode.MOVE);

                    view.setStyle("-fx-background-color: yellow");
                }

                e.consume();
            });

            view.setOnDragExited(e -> {
                if (e.getGestureSource() != this && e.getDragboard().hasString())
                {
                    view.setStyle(null);
                }
            });

            view.setOnDragDropped(e -> {
                Dragboard db = e.getDragboard();

                if (db.hasString())
                {
                    int from = Integer.parseInt(db.getString());
                    int to = index;

                    ListViewSkin skin = (ListViewSkin) list.getSkin();
                    VirtualFlow<ListCell<Profil>> flow = (VirtualFlow<ListCell<Profil>>) skin.getChildren().getFirst();

                    TranslateTransition translateTransition = null;

                    if (to > from)
                    {
                        int animateFrom = from + 1;
                        int animateTo = to;

                        for (int i = animateFrom; i <= animateTo; i++)
                        {
                            ListCell<Profil> cell = flow.getCell(i);
                            translateTransition = new TranslateTransition(Duration.millis(500), cell.getGraphic());
                            translateTransition.setToY(-cell.getHeight());
                            translateTransition.play();
                        }
                    }
                    else
                    {
                        int animateFrom = to;
                        int animateTo = from - 1;

                        for (int i = animateFrom; i <= animateTo; i++)
                        {
                            ListCell<Profil> cell = flow.getCell(i);
                            translateTransition = new TranslateTransition(Duration.millis(500), cell.getGraphic());
                            translateTransition.setToY(cell.getHeight());
                            translateTransition.play();
                        }
                    }

                    if (translateTransition != null)
                    {
                        translateTransition.setOnFinished(l -> {
                            userListManager.move(from, to);

                            if (from == movingIndex)
                            {
                                movingIndex = to;
                                closeMovingIndex();
                            }
                        });
                    }

                    e.setDropCompleted(true);
                }

                e.consume();
            });
        }

        return view;
    }

    public TranslateTransition moveAllCellsUpForDelete(int from)
    {
        ListViewSkin skin = (ListViewSkin) list.getSkin();
        VirtualFlow<ListCell<Profil>> flow = (VirtualFlow<ListCell<Profil>>) skin.getChildren().getFirst();

        TranslateTransition translateTransition = null;

        for (int i = from; i <= flow.getLastVisibleCell().getIndex(); i++)
        {
            ListCell<Profil> cell = flow.getCell(i);
            translateTransition = new TranslateTransition(Duration.millis(500), cell.getGraphic());
            translateTransition.setToY(-cell.getHeight());
            translateTransition.play();
        }

        return translateTransition;
    }

    public void setMovingIndex(int movingIndex)
    {
        this.copyBar.setVisible(true);
        this.copyBar.setMouseTransparent(false);

        ProfilSmallData smallData = userListManager.get(movingIndex);
        Profil profil = dataManager.getProfil(smallData);
        this.movingIndex = movingIndex;

        Parent view = createItem(profil, movingIndex, true);
        this.copyBar.getChildren().add(view);

        setCopyingStatus(true);
    }

    public void closeMovingIndex()
    {
        this.controllerMap.get(movingIndex).appear();

        this.movingIndex = -1;

        this.movingController.closedCopyBar(() -> {
            this.copyBar.getChildren().clear();

            this.copyBar.setVisible(false);
            this.copyBar.setMouseTransparent(true);

            this.movingController = null;
        });

        setCopyingStatus(false);
    }

    private void setCopyingStatus(boolean paste)
    {
        for (HomeLiceuItemController value : this.controllerMap.values())
        {
            value.setCopyingStatus(paste);
        }
    }

    public boolean hasContentInCopyBar()
    {
        return movingIndex != -1;
    }

    public void setMovingController(HomeLiceuItemController movingController)
    {
        this.movingController = movingController;
    }

    public void registerController(int index, HomeLiceuItemController controller)
    {
        this.controllerMap.put(index, controller);
    }

    public int getMovingIndex()
    {
        return movingIndex;
    }

    public void runDifSimulation(int index)
    {
        appInstance.openDialog(MainController.FXMLData.of("loading"), 532, 400);

        new Thread(() -> {
            appInstance.getUserListManager().runDifSimulation(dataManager, null, index, () -> afterSimulation(ShowSimulationData.DIF));
        }).start();
    }

    public void afterSimulation(ShowSimulationData showingSimulationData)
    {
        Platform.runLater(() -> {
            this.showingSimulationData = showingSimulationData;

            this.simulation.setText("Oprește Simularea");

            updateList();
            appInstance.closeDialog();
        });
    }

    private void updateList()
    {
        ObservableList<ProfilSmallData> items = list.getItems();
        list.setItems(null);
        list.setItems(items);
    }
}
