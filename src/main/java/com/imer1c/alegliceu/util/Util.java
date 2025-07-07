package com.imer1c.alegliceu.util;

import com.imer1c.alegliceu.AlegLiceu;
import com.imer1c.alegliceu.controllers.ArgInitializer;
import com.imer1c.alegliceu.controllers.Initializable;
import javafx.fxml.FXMLLoader;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import java.io.IOException;
import java.net.*;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;
import java.util.Enumeration;
import java.util.Iterator;

public class Util {
    public static<T> T getView(String path)
    {
        URL url = Util.class.getClassLoader().getResource("fxml/" + path + ".fxml");

        try
        {
            return FXMLLoader.load(url);
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static<T, C> T getView(String path, AlegLiceu appInstance)
    {
        URL url = Util.class.getClassLoader().getResource("fxml/" + path + ".fxml");

        try
        {
            FXMLLoader loader = new FXMLLoader(url);
            T load = loader.load();
            C controller = loader.getController();

            if (controller instanceof Initializable i)
            {
                i.initialize(appInstance);
            }

            return load;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static<T, C> T getView(String path, Object... args)
    {
        URL url = Util.class.getClassLoader().getResource("fxml/" + path + ".fxml");

        try
        {
            FXMLLoader loader = new FXMLLoader(url);
            T load = loader.load();
            C controller = loader.getController();

            if (controller instanceof ArgInitializer i)
            {
                i.initialize(args);
            }

            return load;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void disableSSLCheck()
    {
        TrustManager[] trustAllCerts = new TrustManager[]
                {
                        new X509TrustManager() {
                            @Override
                            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException
                            {

                            }

                            @Override
                            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException
                            {

                            }

                            @Override
                            public X509Certificate[] getAcceptedIssuers()
                            {
                                return null;
                            }
                        }
                };

        try
        {
            SSLContext sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, trustAllCerts, new SecureRandom());

            HttpsURLConnection.setDefaultSSLSocketFactory(sslContext.getSocketFactory());
        }
        catch (NoSuchAlgorithmException | KeyManagementException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static String getLocalIp()
    {
        try
        {
            Iterator<NetworkInterface> iterator = NetworkInterface.getNetworkInterfaces().asIterator();

            while (iterator.hasNext())
            {
                NetworkInterface networkInterface = iterator.next();

                if (networkInterface.isLoopback() || !networkInterface.isUp())
                {
                    continue;
                }

                Enumeration<InetAddress> inetAddresses = networkInterface.getInetAddresses();

                while (inetAddresses.hasMoreElements())
                {
                    InetAddress address = inetAddresses.nextElement();

                    if (address instanceof Inet4Address addr && !addr.isLoopbackAddress())
                    {
                        return addr.getHostAddress();
                    }
                }
            }
        }
        catch (SocketException e)
        {
            throw new RuntimeException(e);
        }

        return "not found";
    }
}
