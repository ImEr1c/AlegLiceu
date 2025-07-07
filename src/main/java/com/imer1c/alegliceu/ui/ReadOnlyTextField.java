package com.imer1c.alegliceu.ui;

import javafx.scene.control.TextField;

public class ReadOnlyTextField extends TextField {

    public ReadOnlyTextField()
    {
        setEditable(false);
        setFocusTraversable(false);
        setFocused(false);
        getStyleClass().add("read-only-text-field");
    }
}
