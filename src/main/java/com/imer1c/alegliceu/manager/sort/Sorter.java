package com.imer1c.alegliceu.manager.sort;

import com.imer1c.alegliceu.manager.PrintSettings;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.util.Comparator;

public class Sorter {

    private final ObservableList<SortPriority> priorities = FXCollections.observableArrayList();

    public void addPriority(SortPriority priority)
    {
        this.priorities.add(priority);
    }

    public void remove(SortPriority item)
    {
        this.priorities.remove(item);
    }

    public ObservableList<SortPriority> getPriorities()
    {
        return priorities;
    }

    public Comparator<PrintSettings.Item> buildComparator()
    {
        return (o1, o2) -> {
            for (SortPriority priority : priorities)
            {
                int result = priority.compare(o1, o2);

                if (result != 0)
                {
                    return result;
                }
            }

            return 0;
        };
    }
}
