package com.imer1c.alegliceu.controllers;

import com.imer1c.alegliceu.AlegLiceu;
import com.imer1c.alegliceu.manager.DataManager;
import com.imer1c.alegliceu.manager.UserListManager;
import com.imer1c.alegliceu.manager.sort.SortPriority;
import com.imer1c.alegliceu.manager.sort.Sorter;
import com.imer1c.alegliceu.ui.DoubleField;
import com.imer1c.alegliceu.util.Util;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.layout.VBox;

import java.util.HashMap;
import java.util.Map;

public class HomeViewSortDialogController implements ArgInitializer {

    private final Map<SortPriority, Parent> objectToView = new HashMap<>();

    public VBox list;
    public Button add;
    public Button confirm;

    public DoubleField from;
    public DoubleField to;
    public CheckBox all;

    private final Sorter sorter = new Sorter();

    @Override
    public void initialize(Object... args)
    {
        AlegLiceu appInstance = (AlegLiceu) args[0];

        add.setOnMouseClicked(e -> {
            SortPriority item = new SortPriority();
            sorter.addPriority(item);

            Parent view = Util.getView("home_view_sort_dialog_item", appInstance, item, this);
            list.getChildren().add(view);

            objectToView.put(item, view);
        });

        all.selectedProperty().addListener(new ChangeListener<Boolean>() {
            @Override
            public void changed(ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean t1)
            {
                from.setDisable(t1);
                to.setDisable(t1);
            }
        });

        confirm.setOnMouseClicked(e -> {
            UserListManager userListManager = appInstance.getUserListManager();
            DataManager dataManager = appInstance.getDataManager();

            int from = all.isSelected() ? 0 : (int) this.from.getValue() - 1;
            int to = all.isSelected() ? userListManager.getUserList().size() - 1 : (int) this.to.getValue() - 1;

            userListManager.sort(sorter, from, to, dataManager);

            appInstance.closeDialog();
        });
    }

    public void delete(SortPriority item)
    {
        sorter.remove(item);

        Parent view = objectToView.remove(item);
        ((VBox)view.getParent()).getChildren().remove(view);
    }
}
