package com.vaslabs.sdc.math;

import java.util.List;

/**
 * Created by vnicolaou on 24/10/15.
 */
public class SDCMathUtils {
    public static float sumBuffer(List<Float> buffer) {
        float sum = 0;
        for (float value : buffer) {
            sum += value;
        }
        return sum;
    }
}
