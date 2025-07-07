package com.imer1c.alegliceu.controllers;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.imer1c.alegliceu.manager.PrintSettings;
import com.imer1c.alegliceu.util.Util;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.image.WritableImage;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;

public class PrintSendToPhoneDialogController implements ArgInitializer {

    public ProgressIndicator indicator;
    private PrintSettings settings;

    private HttpServer server;

    public ImageView code;

    @Override
    public void initialize(Object... args)
    {
        this.settings = (PrintSettings) args[0];

        try
        {
            startServer();
        }
        catch (IOException | WriterException e)
        {
            throw new RuntimeException(e);
        }
    }

    private void createQRCode(String address) throws WriterException
    {
        QRCodeWriter writer = new QRCodeWriter();
        BitMatrix matrix = writer.encode(address, BarcodeFormat.QR_CODE, 300, 300);
        BufferedImage bufferedImage = MatrixToImageWriter.toBufferedImage(matrix);
        WritableImage image = SwingFXUtils.toFXImage(bufferedImage, null);

        this.code.setImage(image);
    }

    private void startServer() throws IOException, WriterException
    {
        this.server = HttpServer.create();
        this.server.bind(new InetSocketAddress(0), 0);

        this.server.createContext("/lista", this::onDownload);
        this.server.start();

        String address = String.format("http://%s:%s/lista", Util.getLocalIp(), server.getAddress().getPort());
        createQRCode(address);
    }

    private void onDownload(HttpExchange exchange) throws IOException
    {
        indicator.setVisible(true);

        Headers responseHeaders = exchange.getResponseHeaders();
        responseHeaders.add("Content-Type", "application/pdf");
        responseHeaders.add("Content-Disposition", "attachment; filename=\"lista%20licee.pdf\"");

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        settings.createPDF(outputStream);

        byte[] byteArray = outputStream.toByteArray();

        exchange.sendResponseHeaders(200, byteArray.length);

        OutputStream responseBody = exchange.getResponseBody();
        responseBody.write(byteArray);
        responseBody.close();

        server.stop(0);

        indicator.setVisible(false);
        code.setImage(new Image(getClass().getClassLoader().getResource("checkmark_green.png").toString()));
    }
}
