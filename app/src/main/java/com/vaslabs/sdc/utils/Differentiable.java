package com.vaslabs.sdc.utils;

/**
 * Created by vnicolao on 10/05/15.
 */
public interface Differentiable extends Comparable<Differentiable> {
    double differantiate(Differentiable differentiable);
}
