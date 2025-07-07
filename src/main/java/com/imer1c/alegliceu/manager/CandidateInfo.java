package com.imer1c.alegliceu.manager;

public class CandidateInfo {

    private final String codMen;

    private String school;
    private double romana;
    private double matematica;
    private double medie;
    private int index;

    private double medieGenerala;
    private String liceu;
    private String specializare;

    public CandidateInfo(String codMen)
    {
        this.codMen = codMen;
    }

    public String getCodMen()
    {
        return codMen;
    }

    public String getSchool()
    {
        return school;
    }

    public double getRomana()
    {
        return romana;
    }

    public double getMatematica()
    {
        return matematica;
    }

    public double getMedie()
    {
        return medie;
    }

    public int getIndex()
    {
        return index;
    }

    public double getMedieGenerala()
    {
        return medieGenerala;
    }

    public String getLiceu()
    {
        return liceu;
    }

    public String getSpecializare()
    {
        return specializare;
    }

    public void setEvData(String school, double romana, double matematica, double medie, int index)
    {
        this.school = school;
        this.romana = romana;
        this.matematica = matematica;
        this.medie = medie;
        this.index = index;
    }

    public void setAdmitereData(double medieGenerala, String liceu, String specializare)
    {
        this.medieGenerala= medieGenerala;
        this.liceu = liceu;
        this.specializare = specializare;
    }
}
