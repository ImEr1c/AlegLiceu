package com.imer1c.alegliceu.manager;

import java.util.Objects;

public class ProfilSmallData {

    private final long liceuId;
    private final String sp;

    public ProfilSmallData(long liceuId, String sp)
    {
        this.liceuId = liceuId;
        this.sp = sp;
    }

    public long getLiceuId()
    {
        return liceuId;
    }

    public String getSp()
    {
        return sp;
    }

    @Override
    public boolean equals(Object o)
    {
        if (!(o instanceof ProfilSmallData that))
        {
            return false;
        }
        return liceuId == that.liceuId && Objects.equals(sp, that.sp);
    }

    @Override
    public int hashCode()
    {
        return Objects.hash(liceuId, sp);
    }
}
