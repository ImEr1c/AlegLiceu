package com.imer1c.alegliceu.manager;

public class Profil {

    private final String liceuName;
    private final long liceuId;
    private final String specializare;
    private final String specializareRaw;
    private final String limbaMatern;
    private final String limbaBilingv;
    private final int locuri;
    private final double medie;
    private final int cod;
    private int index = Integer.MAX_VALUE;
    private String repartitionColor;

    public Profil(String liceuName, long liceuId, String specializare, String specializareRaw, String limbaMatern, String limbaBilingv, int locuri, double medie, int cod)
    {
        this.liceuName = liceuName;
        this.liceuId = liceuId;
        this.specializare = specializare;
        this.specializareRaw = specializareRaw;
        this.limbaMatern = limbaMatern;
        this.limbaBilingv = limbaBilingv;
        this.locuri = locuri;
        this.cod = cod;
        this.medie = medie;
    }

    public String getSpecializareRaw()
    {
        return specializareRaw;
    }

    public String getLimbaMatern()
    {
        return limbaMatern;
    }

    public String getLimbaBilingv()
    {
        return limbaBilingv;
    }

    public int getIndex()
    {
        return index;
    }

    public void setIndex(int index)
    {
        this.index = index;
    }

    public String getRepartitionColor()
    {
        return repartitionColor;
    }

    public void setRepartitionColor(String repartitionColor)
    {
        this.repartitionColor = repartitionColor;
    }

    public String getLiceuName()
    {
        return liceuName;
    }

    public long getLiceuId()
    {
        return liceuId;
    }

    public String getSpecializare()
    {
        return specializare;
    }

    public int getLocuri()
    {
        return locuri;
    }

    public int getCod()
    {
        return cod;
    }

    public double getMedie()
    {
        return medie;
    }

    public ProfilSmallData createSmallData()
    {
        return new ProfilSmallData(liceuId, specializare);
    }

    public boolean isProfile(String p)
    {
        String end = p.split("<br/>")[1];
        String profil = p.split("<b>")[1].split("</b>")[0];

        boolean b;

        if (end.contains("/"))
        {
            String[] langs = end.split("/");

            b = langs[0].equals(limbaMatern) && langs[1].equals(limbaBilingv);
        }
        else
        {
            b = (limbaBilingv == null || limbaBilingv.equals("-")) && end.equals(limbaMatern);
        }

        return profil.contains(specializareRaw) && b;
    }
}
