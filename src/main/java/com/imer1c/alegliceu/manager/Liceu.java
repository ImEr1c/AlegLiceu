package com.imer1c.alegliceu.manager;

import java.util.HashMap;
import java.util.Map;

public class Liceu {

    private final Map<String, Profil> profile;
    private final String name;
    private final String adress;
    private final long id;
    private final String telefon;
    private int locuri;
    private double maxMedie;
    private double distance;
    private String detaliiUser;

    public Liceu(String name, String adress, long id, int profileCount, String telefon)
    {
        this.name = name;
        this.adress = adress;
        this.id = id;
        this.profile = new HashMap<>(profileCount);
        this.telefon = telefon;
    }

    public double getDistance()
    {
        return distance;
    }

    public void setDistance(double distance)
    {
        this.distance = distance;
    }

    public String getDetaliiUser()
    {
        return detaliiUser;
    }

    public void setDetaliiUser(String detaliiUser)
    {
        this.detaliiUser = detaliiUser;
    }

    public void addProfile(Profil profil)
    {
        this.profile.put(profil.getSpecializare(), profil);
        this.locuri += profil.getLocuri();
    }

    public Profil getProfil(String sp)
    {
        return this.profile.get(sp);
    }

    public double getMedieForProfil(String sp)
    {
        return this.profile.containsKey(sp) ? this.profile.get(sp).getMedie() : -1;
    }

    public boolean containsBasicSp(String basicSp)
    {
        for (String s : this.profile.keySet())
        {
            if (s.equals(basicSp))
            {
                return true;
            }
        }

        return false;
    }

    public boolean contains(String sp)
    {
        return this.profile.containsKey(sp);
    }

    public Map<String, Profil> getProfile()
    {
        return profile;
    }

    public String getName()
    {
        return name;
    }

    public String getAdress()
    {
        return adress;
    }

    public long getId()
    {
        return id;
    }

    public double getMaxMedie()
    {
        return maxMedie;
    }

    public int getLocuri()
    {
        return locuri;
    }

    public String getTelefon()
    {
        return telefon;
    }

    public void computeValues()
    {
        maxMedie = 0;

        for (Profil value : profile.values())
        {
            if (value.getMedie() > maxMedie)
            {
                maxMedie = value.getMedie();
            }
        }
    }
}
