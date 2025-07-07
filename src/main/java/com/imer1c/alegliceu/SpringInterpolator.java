package com.imer1c.alegliceu;

import javafx.animation.Interpolator;

public class SpringInterpolator extends Interpolator {

    public static SpringInterpolator INTERPOLATOR_0 = new SpringInterpolator(0);

    private final int bounciness;

    public SpringInterpolator(int bounciness) {
        this.bounciness = bounciness;
    }

    @Override
    protected double curve(double progress) {
        return 1 - Math.exp(-5 * progress) * Math.cos(bounciness * progress);
    }
}
