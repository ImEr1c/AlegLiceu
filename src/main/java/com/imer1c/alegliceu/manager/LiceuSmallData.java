package com.imer1c.alegliceu.manager;

import com.imer1c.alegliceu.util.RepartitionType;

import java.util.HashMap;
import java.util.Map;

public class LiceuSmallData {

    private final Map<String, String> profileToRepartitionColor = new HashMap<>();
    private double distance = -1;
    private RepartitionType repartitionType;
    private String transportDetails;

    public String getTransportDetails()
    {
        return transportDetails;
    }

    public void setTransportDetails(String transportDetails)
    {
        this.transportDetails = transportDetails;
    }

    public double getDistance()
    {
        return distance;
    }

    public RepartitionType getRepartitionType()
    {
        return repartitionType;
    }

    public void setRepartitionType(RepartitionType repartitionType)
    {
        this.repartitionType = repartitionType;
    }

    public void setDistance(double distance)
    {
        this.distance = distance;
    }

    public void putProfilColor(String profil, String color)
    {
        this.profileToRepartitionColor.put(profil, color);
    }

    public String getProfilColor(String profil)
    {
        return this.profileToRepartitionColor.get(profil);
    }

}
