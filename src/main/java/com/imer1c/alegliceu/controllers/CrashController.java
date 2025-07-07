package com.imer1c.alegliceu.controllers;

import javafx.scene.control.TextArea;

public class CrashController implements ArgInitializer {

    public TextArea text;

    @Override
    public void initialize(Object... args)
    {
        Throwable t = (Throwable) args[0];

        StringBuilder builder = new StringBuilder(t.getMessage()).append("\n");
        for (StackTraceElement stackTraceElement : t.getStackTrace())
        {
            builder.append(stackTraceElement.toString()).append("\n");
        }

        text.setText(builder.toString());
    }
}
