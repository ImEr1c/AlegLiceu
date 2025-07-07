module com.imer1c.alegliceu {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.desktop;
    requires java.management;
    requires org.apache.pdfbox;
    requires jdk.httpserver;
    requires com.google.zxing;
    requires com.google.zxing.javase;
    requires javafx.swing;
    requires org.apache.fontbox;
    requires jcommander;


    opens com.imer1c.alegliceu to javafx.fxml;
    exports com.imer1c.alegliceu;
}