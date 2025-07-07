package com.imer1c.alegliceu.ui;

import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.StringConverter;

import java.util.function.UnaryOperator;
import java.util.regex.Pattern;

public class DoubleField extends TextField {

    private static final Pattern doublePattern = Pattern.compile("-?(([1-9][0-9]*)|0)?(\\.[0-9]*)?");

    public DoubleField()
    {
        UnaryOperator<TextFormatter.Change> operator = c -> {
            String text = c.getControlNewText();
            if (doublePattern.matcher(text).matches()) {
                return c;
            } else {
                return null ;
            }
        };

        setTextFormatter(new TextFormatter<>(new StringConverter<Double>() {
            @Override
            public String toString(Double object)
            {
                if (object == null)
                {
                    return "";
                }

                double v = object.doubleValue();
                return (v == (int) v) ? String.valueOf((int) v) : object.toString();
            }

            @Override
            public Double fromString(String string)
            {
                string = string.trim();

                if (string.isBlank())
                {
                    return -1.0;
                }

                return Double.parseDouble(string);
            }
        }, 0D, operator));
        setText(null);

    }

    public double getValue()
    {
        String text = getText();

        return text.isEmpty() ? -1 : Double.parseDouble(text);
    }

    public void setValue(double value)
    {
        ((TextFormatter<Double>) getTextFormatter()).setValue(value);
    }
}
