package com.imer1c.alegliceu.manager.sort;

import com.imer1c.alegliceu.manager.PrintSettings;

public class SortPriority {
    private SortPriorityType type;
    private String option;

    public SortPriorityType getType()
    {
        return type;
    }

    public void setType(SortPriorityType type)
    {
        this.type = type;
    }

    public String getOption()
    {
        return option;
    }

    public void setOption(String option)
    {
        this.option = option;
    }

    public int compare(PrintSettings.Item o1, PrintSettings.Item o2)
    {
        return type.compare(o1, o2, option);
    }
}
