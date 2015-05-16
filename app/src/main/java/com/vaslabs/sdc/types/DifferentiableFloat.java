package com.vaslabs.sdc.types;

import com.vaslabs.sdc.utils.Differentiable;

/**
 * Created by vnicolao on 16/05/15.
 */
public final class DifferentiableFloat implements Differentiable<DifferentiableFloat> {

    public final float value;

    public DifferentiableFloat(float value) {
        this.value = value;
    }

    @Override
    public double differantiate(DifferentiableFloat differentiable) {
        if (!(differentiable instanceof DifferentiableFloat))
            throw new IllegalArgumentException("Should be DifferentiableFloat");

        return this.value - ((DifferentiableFloat)differentiable).value;
    }

    @Override
    public int compareTo(DifferentiableFloat differentiable) {
        double difference = this.differantiate(differentiable);
        if (difference > 0)
            return 1;
        if (difference < 0)
            return -1;
        return 0;
    }

    @Override
    public int hashCode() {
        return Float.floatToIntBits(value);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        DifferentiableFloat that = (DifferentiableFloat) o;

        return Float.compare(that.value, value) == 0;
    }
}
