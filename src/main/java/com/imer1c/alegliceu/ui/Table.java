package com.imer1c.alegliceu.ui;

import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

public class Table<T> extends GridPane {

    private final List<Column<T>> columns = new ArrayList<>();
    private List<T> items;

    public Table()
    {
        super(10, 5);
        getStyleClass().add("table");
    }

    public void addColumn(Column<T> column)
    {
        this.columns.add(column);
    }

    public void setItems(List<T> list)
    {
        this.items = list;
        update();
    }

    public void update()
    {
        getChildren().clear();

        HBox header = new HBox();
        header.getStyleClass().add("table-header");
        add(header, 0, 0);
        setColumnSpan(header, 4);

        for (int i = 0; i < columns.size(); i++)
        {
            Column<T> column = columns.get(i);

            Label label = new Label(column.getName());
            label.getStyleClass().addAll("table-label");

            add(label, i, 0);
        }

        for (int row = 0; row < items.size(); row++)
        {
            for (int column = 0; column < columns.size(); column++)
            {
                T item = items.get(row);
                Column<T> col = columns.get(column);

                Label label = new Label(col.getValue(item));
                label.getStyleClass().add("table-label");

                add(label, column, row + 1);
            }
        }
    }

    public static class Column<T> {
        private final String name;
        private final Function<T, String> getter;

        public Column(String name, Function<T, String> getter)
        {
            this.name = name;
            this.getter = getter;
        }

        public String getName()
        {
            return name;
        }

        public String getValue(T t)
        {
            return getter.apply(t);
        }
    }



}
