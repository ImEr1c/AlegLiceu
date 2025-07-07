package com.imer1c.alegliceu.util;

public enum RepartitionType {
    FULL("-fx-background-color: red"),
    SOME_REMAIN("-fx-background-color: yellow"),
    EMPTY("-fx-background-color: green");

    public final String style;

    RepartitionType(String style)
    {
        this.style = style;
    }

    public String getStyle()
    {
        return style;
    }
}
