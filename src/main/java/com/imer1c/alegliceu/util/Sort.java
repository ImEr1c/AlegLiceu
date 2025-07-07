package com.imer1c.alegliceu.util;

import com.imer1c.alegliceu.manager.Liceu;

import java.util.Comparator;
import java.util.function.Function;

public enum Sort {
    NULL("Sortează", null),
    MEDIE_CRESCATOR("Medie Crescător", profil -> profil == null ? Comparator.comparingDouble(Liceu::getMaxMedie) :
            Comparator.comparingDouble(l -> l.getMedieForProfil(profil))),
    MEDIE_DESCRESCATOR("Medie Descrescător", profil -> profil == null ? (o1, o2) -> Double.compare(o2.getMaxMedie(), o1.getMaxMedie()) :
            (o1, o2) -> Double.compare(o2.getMedieForProfil(profil), o1.getMedieForProfil(profil)));

    private final Function<String, Comparator<Liceu>> comparatorBuilder;
    private final String name;

    Sort(String name, Function<String, Comparator<Liceu>> comparatorBuilder)
    {
        this.name = name;
        this.comparatorBuilder = comparatorBuilder;
    }

    public Comparator<Liceu> build(String filteredProfil)
    {
        return this.comparatorBuilder.apply(filteredProfil);
    }

    @Override
    public String toString()
    {
        return name;
    }
}
