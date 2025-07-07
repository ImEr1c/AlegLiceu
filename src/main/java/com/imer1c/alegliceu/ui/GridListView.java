package com.imer1c.alegliceu.ui;

import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.FlowPane;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class GridListView<T> extends ListView<List<T>> {

    private List<T> content;
    private Function<T, Parent> uiBuilder;

    private int itemWidth;
    private int itemsPerRow;

    public GridListView()
    {
        super();
        setSelectionModel(null);
        setFocusTraversable(false);
        widthProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> observableValue, Number number, Number t1)
            {
                listWidthUpdate(t1.doubleValue());
            }
        });

        setCellFactory(listView -> new ListCell<>() {

            @Override
            protected void updateItem(List<T> item, boolean empty)
            {
                super.updateItem(item, empty);

                if (empty || item == null)
                {
                    setGraphic(null);
                }
                else
                {
                    FlowPane hBox = new FlowPane(20, 20);
                    hBox.setAlignment(Pos.CENTER);
                    hBox.setPadding(new Insets(10, 5, 10, 5));

                    ObservableList<Node> children = hBox.getChildren();

                    for (T t : item)
                    {
                        children.add(uiBuilder.apply(t));
                    }

                    setGraphic(hBox);
                }
            }
        });
    }

    public void setUiBuilder(Function<T, Parent> uiBuilder)
    {
        this.uiBuilder = uiBuilder;
    }

    public void setContent(List<T> content)
    {
        this.content = content;

        updateListContent();
    }

    public void setItemWidth(int itemWidth)
    {
        this.itemWidth = itemWidth;
    }

    public int getItemWidth()
    {
        return itemWidth;
    }

    private void listWidthUpdate(double width)
    {
        int itemsPerRow = (int) (width / (itemWidth + 20));

        if (this.itemsPerRow == itemsPerRow)
        {
            return;
        }

        this.itemsPerRow = itemsPerRow;

        updateListContent();
    }

    public void updateListContent()
    {
        if (content == null)
        {
            return;
        }

        double rowsCount = (double)content.size() / itemsPerRow;
        int rowsCountInt = (int) rowsCount;

        if (rowsCount - rowsCountInt > 0)
        {
            rowsCountInt++;
        }

        List<List<T>> rows = new ArrayList<>();

        for (int i = 0; i < rowsCountInt; i++)
        {
            int from = i * itemsPerRow;
            int to = (i + 1) * itemsPerRow;

            List<T> row = content.subList(from, Math.min(to, content.size()));
            rows.add(row);
        }

        Platform.runLater(() -> {
            getItems().clear();
            getItems().addAll(rows);
        });
    }

}
