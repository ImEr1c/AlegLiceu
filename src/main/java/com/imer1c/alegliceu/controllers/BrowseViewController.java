package com.imer1c.alegliceu.controllers;

import com.imer1c.alegliceu.AlegLiceu;
import com.imer1c.alegliceu.manager.Liceu;
import com.imer1c.alegliceu.ui.GridListView;
import com.imer1c.alegliceu.util.BrowseFilter;
import com.imer1c.alegliceu.util.Sort;
import com.imer1c.alegliceu.util.Util;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.effect.BlurType;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class BrowseViewController implements Initializable {

    public GridListView<Liceu> list;
    public AnchorPane root;

    public TextField search;
    public Button home;
    public Button filterButton;
    public ComboBox<Sort> sort;
    public HBox topBox;

    private AlegLiceu appInstance;

    private List<Liceu> liceeFull;

    private final BrowseFilter filter = new BrowseFilter();

    @Override
    public void initialize(AlegLiceu appInstance)
    {
        list.maxWidthProperty().bind(root.widthProperty().multiply(0.8));
        list.maxHeightProperty().bind(root.heightProperty().multiply(0.8));

        topBox.prefWidthProperty().bind(list.widthProperty());

        home.setOnMouseClicked(e -> appInstance.setView(Util.getView("home_view", appInstance)));

        this.appInstance = appInstance;

        this.liceeFull = new ArrayList<>(appInstance.getDataManager().getLicee().values());

        this.list.setUiBuilder(this::createItem);
        this.list.setContent(liceeFull);

        search.setOnAction(e -> {
            filterList();
        });
        DropShadow shadow = new DropShadow(BlurType.ONE_PASS_BOX, Color.GRAY, 30, 0.25, 0, 0);
        search.setEffect(shadow);

        search.focusedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observableValue, Boolean aBoolean, Boolean t1)
            {
                new Timeline(
                        new KeyFrame(Duration.millis(300), new KeyValue(shadow.spreadProperty(), t1 ? 0.25 : 0))
                ).play();
            }
        });

        filterButton.setOnMouseClicked(e -> {
            appInstance.openDialog(MainController.FXMLData.of("browse_filter_dialog", appInstance, filter), 532, 400);
            appInstance.setOnDialogClose(this::filterList);
        });

        this.sort.setItems(FXCollections.observableArrayList(Sort.values()));
        this.sort.setOnAction(e -> filterList());

    }

    private Parent createItem(Liceu liceu)
    {
        return Util.getView("browse_item", liceu, appInstance, filter.getProfil());
    }

    private void filterList()
    {
        new Thread(() -> {
            Predicate<Liceu> pred = filter.buildPredicate(search.getText());

            Stream<Liceu> stream = liceeFull
                    .stream()
                    .filter(pred);

            if (sort.getValue() != null && sort.getValue() != Sort.NULL)
            {
                stream = stream.sorted(sort.getValue().build(filter.getProfil()));
            }

            List<Liceu> list = stream.toList();
            this.list.setContent(list);
        }).start();
    }


}
