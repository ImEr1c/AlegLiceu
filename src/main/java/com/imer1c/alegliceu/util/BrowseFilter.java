package com.imer1c.alegliceu.util;

import com.imer1c.alegliceu.manager.Liceu;

import java.util.function.Predicate;

public class BrowseFilter {

    private double medieMin = -1;
    private double medieMax = -1;
    private String profil;

    public double getMedieMin()
    {
        return medieMin;
    }

    public void setMedieMin(double medieMin)
    {
        this.medieMin = medieMin;
    }

    public double getMedieMax()
    {
        return medieMax;
    }

    public void setMedieMax(double medieMax)
    {
        this.medieMax = medieMax;
    }

    public String getProfil()
    {
        return profil;
    }

    public void setProfil(String profil)
    {
        this.profil = profil;
    }

    public Predicate<Liceu> buildPredicate(String search)
    {
        Predicate<Liceu> pred;
        if (profil != null)
        {
            pred = l -> l.containsBasicSp(profil) && (medieMin == -1 || l.getProfile().get(profil).getMedie() > medieMin) && (medieMax == -1 || l.getProfile().get(profil).getMedie() < medieMax);
        }
        else
        {
            pred = l -> (medieMin == -1 || l.getMaxMedie() > medieMin) && (medieMax == -1 || l.getMaxMedie() < medieMax);
        }

        return pred.and(l -> l.getName().toLowerCase().contains(search));
    }
}
